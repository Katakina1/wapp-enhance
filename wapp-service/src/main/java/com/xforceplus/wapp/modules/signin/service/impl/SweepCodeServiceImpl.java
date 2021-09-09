package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceUploadService;
import com.xforceplus.wapp.modules.signin.dao.SignatureProcessingDao;
import com.xforceplus.wapp.modules.signin.dao.SweepCodeDao;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.enumflord.SignInEnum;
import com.xforceplus.wapp.modules.signin.service.SweepCodeService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * CreateBy leal.liang on 2018/4/17.
 **/
@Service
@Transactional
public class SweepCodeServiceImpl implements SweepCodeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SweepCodeServiceImpl.class);

    /**
     * 查验接口处理成功的返回状态码
     */
    private final String code = "0001";

    /**
     * 调用查验接口默认的购方税号
     */
    private final String GF_TAX_NO = "91110108MA004CPN95";



    private SignatureProcessingDao signatureProcessingDao;

    private final EinvoiceUploadService einvoiceUploadService;

    @Autowired
    private SweepCodeDao sweepCodeDao;

    @Autowired
    public SweepCodeServiceImpl( SignatureProcessingDao signatureProcessingDao, EinvoiceUploadService einvoiceUploadService) {
        this.signatureProcessingDao = signatureProcessingDao;
        this.einvoiceUploadService = einvoiceUploadService;
    }


    @Override
    public RecordInvoiceEntity ReceiptInvoice(String schemaLabel, Map<String, Object> params) throws Exception {
        //设置调用接口请求需要的参数
        RequestData requestData = new RequestData();
        requestData.setBuyerTaxNo(GF_TAX_NO);
        requestData.setInvoiceAmount(String.valueOf(params.get("invoiceAmount")));
        requestData.setCheckCode((String) params.get("checkCode"));
        requestData.setInvoiceCode((String) params.get("invoiceCode"));
        requestData.setInvoiceNo((String) params.get("invoiceNo"));
        requestData.setInvoiceDate((String) params.get("invoiceDate"));
        requestData.setInvoiceType((String) params.get("invoiceType"));
        //获取返回对象
        ResponseInvoice r = null;
        UserEntity user = (UserEntity) params.get("user");
        //创建签收查询的实体类对象
        RecordInvoiceEntity rie = new RecordInvoiceEntity();
        if (code.equals(r.getResultCode())) {
            //只有查验成功数据才能去判断是否有权限签收
            Boolean checkPowerResult = einvoiceUploadService.checkUserTaxNoPower(schemaLabel, Long.valueOf(user.getUserid()), r.getBuyerTaxNo());
            if (checkPowerResult) {
                //获取抵账表数据
                InvoiceCollectionInfo ic = buildInvoiceCollectionInfo(r);
                //保存抵账表数据
                sweepCodeDao.saveIncoiceData(schemaLabel, ic);
                //构建明细表数据
                List<InvoiceDetailInfo> list = buildDetailList(r);
                //保存明细数据
                for (InvoiceDetailInfo ind : list) {
                    sweepCodeDao.saveIncoiceData3(schemaLabel, ind);
                }
                //查验结果一致 构建扫描表数据
                rie.setQs("1");
                rie.setUserName(user.getUsername());
                rie.setUserNum(user.getLoginname());
                rie.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                rie.setQsType(SignInEnum.QS_SWEEP_CODE.getValue());
                rie.setInvoiceCode(r.getInvoiceCode());
                rie.setInvoiceNo(r.getInvoiceNo());
                rie.setInvoiceType((String) params.get("invoiceType"));
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
                rie.setInvoiceDate(formatter2.parse(r.getInvoiceDate()));
                rie.setInvoiceAmount(new BigDecimal(r.getInvoiceAmount()));
                rie.setTaxAmount(new BigDecimal(r.getTaxAmount()));
                rie.setCheckCode(r.getCheckCode().substring(r.getCheckCode().length() - 6));
                rie.setNotes(r.getResultTip());
                rie.setGfTaxNo(r.getBuyerTaxNo());
                rie.setXfTaxNo(r.getSalerTaxNo());
                rie.setGfName(r.getBuyerName());
                rie.setXfName(r.getSalerName());
                rie.setNotes(r.getResultTip());
                rie.setTotalAmount(Double.valueOf(r.getTotalAmount()));
                rie.setUuid(r.getInvoiceCode() + r.getInvoiceNo());
                //保存扫描表
                sweepCodeDao.saveIncoiceData2(schemaLabel, rie);
            } else {
                //查验失败封装扫描表数据
                rie.setQs("0");
                rie.setUserName(user.getUsername());
                rie.setUserNum(user.getLoginname());
                rie.setQsStatus(SignInEnum.QS_FAIL.getValue());
                rie.setQsType(SignInEnum.QS_SWEEP_CODE.getValue());
                rie.setSignInDate(new Date());
                rie.setNotes("无签收权限，签收失败!");
                rie.setInvoiceType((String) params.get("invoiceType"));
                rie.setGfTaxNo(GF_TAX_NO);
                rie.setCheckCode((String) params.get("checkCode"));
                rie.setInvoiceCode((String) params.get("invoiceCode"));
                rie.setInvoiceNo((String) params.get("invoiceNo"));
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                rie.setInvoiceDate(format.parse((String) params.get("invoiceDate")));
                rie.setUuid(String.valueOf(params.get("invoiceCode")) + String.valueOf(params.get("invoiceNo")));
                if (StringUtils.isNotEmpty(r.getTaxAmount())) {
                    rie.setTaxAmount(new BigDecimal(r.getTaxAmount()));
                }
                if (StringUtils.isNotEmpty(r.getTotalAmount())) {
                    rie.setTotalAmount(Double.valueOf(r.getTotalAmount()));
                }
                if (StringUtils.isNotEmpty(r.getInvoiceAmount())) {
                    rie.setInvoiceAmount(new BigDecimal(r.getInvoiceAmount()));
                }
            }

        } else {
            //查验失败封装扫描表数据
            rie.setQs("0");
            rie.setUserName(user.getUsername());
            rie.setUserNum(user.getLoginname());
            rie.setQsStatus(SignInEnum.QS_FAIL.getValue());
            rie.setQsType(SignInEnum.QS_SWEEP_CODE.getValue());
            rie.setSignInDate(new Date());
            rie.setNotes(r.getResultTip());
            rie.setInvoiceType((String) params.get("invoiceType"));
            rie.setGfTaxNo(GF_TAX_NO);
            rie.setCheckCode((String) params.get("checkCode"));
            rie.setInvoiceCode((String) params.get("invoiceCode"));
            rie.setInvoiceNo((String) params.get("invoiceNo"));
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            rie.setInvoiceDate(format.parse((String) params.get("invoiceDate")));
            rie.setUuid(String.valueOf(params.get("invoiceCode")) + String.valueOf(params.get("invoiceNo")));
            //保存扫描表
            sweepCodeDao.saveIncoiceData2(schemaLabel, rie);
        }
        return rie;
    }

    @Override
    public RecordInvoiceEntity ReceiptInvoiceTwo(String schemaLabel, Map<String, Object> params) throws Exception {
        String uuid = (String) params.get("invoiceCode") + params.get("invoiceNo");
        //根据uuid获取数据
        RecordInvoiceEntity rq = sweepCodeDao.getIncoiceData(schemaLabel, uuid);
        UserEntity user = (UserEntity) params.get("user");
        if (rq != null) {
            //查验登录人是否有权限签收数据
            Boolean checkPowerResult = einvoiceUploadService.checkUserTaxNoPower(schemaLabel, Long.valueOf(user.getUserid()), rq.getGfTaxNo());
            if (checkPowerResult) {
                //查询抵账表成功 更改抵账表签收的相关数据
                rq.setQs("1");
                rq.setNotes("签收成功！");
                rq.setUserName(user.getUsername());
                rq.setUserNum(user.getLoginname());
                rq.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                rq.setQsType(SignInEnum.QS_SWEEP_CODE.getValue());
                sweepCodeDao.updateDataByUuid(schemaLabel, uuid);
                sweepCodeDao.saveIncoiceData2(schemaLabel, rq);
            } else {
                //查询抵账表成功 但无签收权限 封装保存扫描表数据
                rq = new RecordInvoiceEntity();
                rq.setQs("0");
                rq.setQsStatus(SignInEnum.QS_FAIL.getValue());
                rq.setQsType(SignInEnum.QS_SWEEP_CODE.getValue());
                rq.setSignInDate(new Date());
                rq.setNotes("无签收权限，签收失败！");
                rq.setUserName(user.getUsername());
                rq.setUserNum(user.getLoginname());
                rq.setCheckCode((String) params.get("checkCode"));
                if(params.get("invoiceAmount") != null) {
                    rq.setInvoiceAmount(new BigDecimal(String.valueOf(params.get("invoiceAmount"))));
                }
                rq.setInvoiceCode((String) params.get("invoiceCode"));
                rq.setInvoiceType((String) params.get("invoiceType"));
                rq.setInvoiceNo((String) params.get("invoiceNo"));
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                rq.setInvoiceDate(format.parse((String) params.get("invoiceDate")));
                rq.setUuid(uuid);
            }

        } else {
            //查询抵账表失败 封装保存扫描表数据
            rq = new RecordInvoiceEntity();
            rq.setQs("0");
            rq.setQsStatus(SignInEnum.QS_FAIL.getValue());
            rq.setQsType(SignInEnum.QS_SWEEP_CODE.getValue());
            rq.setSignInDate(new Date());
            rq.setNotes("发票不存在！");
            rq.setUserName(user.getUsername());
            rq.setUserNum(user.getLoginname());
            rq.setCheckCode((String) params.get("checkCode"));
            if(params.get("invoiceAmount") != null) {
                rq.setInvoiceAmount(new BigDecimal(String.valueOf(params.get("invoiceAmount"))));
            }
            rq.setInvoiceCode((String) params.get("invoiceCode"));
            rq.setInvoiceType((String) params.get("invoiceType"));
            rq.setInvoiceNo((String) params.get("invoiceNo"));
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            rq.setInvoiceDate(format.parse((String) params.get("invoiceDate")));
            rq.setUuid(uuid);
            sweepCodeDao.saveIncoiceData2(schemaLabel, rq);
        }

        return rq;
    }


    @Override
    public Long getInvoiceId(String schemaLabel, String uuid) {
        return sweepCodeDao.getInvoiceId(schemaLabel, uuid);
    }

    @Override
    public Boolean deleteInvoiceData(String schemaLabel, String uuid, UserEntity user) {
        sweepCodeDao.deleteDataByUuid(schemaLabel, uuid);
        //保存备份数据
        RecordInvoiceEntity r = signatureProcessingDao.selectInvoice(schemaLabel, uuid);
        Long copyDataId = signatureProcessingDao.getCopyId(schemaLabel, uuid);
        if (copyDataId != null) {
            signatureProcessingDao.updateCopyData(schemaLabel, r, uuid, user);
        } else {
            signatureProcessingDao.saveCopyData(schemaLabel, r, user);
        }
        //删除扫描表信息
        sweepCodeDao.deleteById(schemaLabel, uuid);
        return Boolean.TRUE;
    }

    @Override
    public Long getInvoiceData(String schemaLabel, String uuid) {
        return sweepCodeDao.getInvoiceData(schemaLabel, uuid);
    }

    /**
     * 构建抵账主体数据
     *
     * @param responseInvoice 抵账主体数据
     * @return 主体数据
     */
    private InvoiceCollectionInfo buildInvoiceCollectionInfo(ResponseInvoice responseInvoice) {
        //定义返回值
        final InvoiceCollectionInfo invoiceCollectionInfo = new InvoiceCollectionInfo();
        //购方税号
        invoiceCollectionInfo.setGfTaxNo(responseInvoice.getBuyerTaxNo());
        //购方名称
        invoiceCollectionInfo.setGfName(responseInvoice.getBuyerName());
        //发票类型
        invoiceCollectionInfo.setInvoiceType(responseInvoice.getInvoiceType());
        //发票代码
        invoiceCollectionInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
        //发票号码
        invoiceCollectionInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
        //发票状态
        if (SignInEnum.INVOICE_STATUS_YES.getValue().equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus(SignInEnum.NUMBER_ZERO.getValue());
        } else {
            invoiceCollectionInfo.setInvoiceStatus(SignInEnum.NUMBER_TWO.getValue());
        }
        //价税合计
        invoiceCollectionInfo.setTotalAmount(responseInvoice.getTotalAmount());
        //金额
        invoiceCollectionInfo.setInvoiceAmount(responseInvoice.getInvoiceAmount());
        //税额
        invoiceCollectionInfo.setTaxAmount(responseInvoice.getTaxAmount());
        //销方税号
        invoiceCollectionInfo.setXfTaxNo(responseInvoice.getSalerTaxNo());
        //销方名称
        invoiceCollectionInfo.setXfName(responseInvoice.getSalerName());
        //销方地址
        invoiceCollectionInfo.setXfAddressAndPhone(responseInvoice.getSalerAddressPhone());
        //销方银行帐号
        invoiceCollectionInfo.setXfBankAndNo(responseInvoice.getSalerAccount());
        //购方银行帐号
        invoiceCollectionInfo.setGfBankAndNo(responseInvoice.getBuyerAccount());
        //购方地址
        invoiceCollectionInfo.setGfAddressAndPhone(responseInvoice.getBuyerAddressPhone());
        //开票日期
        final DateTime invoiceDateTime = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(responseInvoice.getInvoiceDate());
        invoiceCollectionInfo.setInvoiceDate(invoiceDateTime.toDate());
        //备注
        invoiceCollectionInfo.setRemark(responseInvoice.getRemark());
        //校验码
        invoiceCollectionInfo.setCheckCode(responseInvoice.getCheckCode());
        //有明细
        invoiceCollectionInfo.setDetailYesorno("1");
        //机器编号
        invoiceCollectionInfo.setMachinecode(responseInvoice.getMachineNo());
        //返回
        return invoiceCollectionInfo;
    }


    /**
     * 构建抵账表发票明细
     *
     * @param responseInvoice 响应实体
     * @return 抵账表发票明细
     */
    private List<InvoiceDetailInfo> buildDetailList(ResponseInvoice responseInvoice) {
        //构建返回值
        final List<InvoiceDetailInfo> invoiceDetailInfoList = newArrayList();

        //明细
        final List<InvoiceDetail> invoiceDetailList = responseInvoice.getDetailList();

        invoiceDetailList.forEach(invoiceDetail -> {
            final InvoiceDetailInfo invoiceDetailInfo = new InvoiceDetailInfo();
            //税额
            invoiceDetailInfo.setTaxAmount(invoiceDetail.getTaxAmount());
            //货物或应税劳务名称
            invoiceDetailInfo.setGoodsName(invoiceDetail.getGoodsName());
            //发票号码
            invoiceDetailInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
            //发票代码
            invoiceDetailInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
            //数量
            invoiceDetailInfo.setNum(invoiceDetail.getNum());
            //明细序号
            invoiceDetailInfo.setDetailNo(invoiceDetail.getDetailNo());
            //单价
            invoiceDetailInfo.setUnitPrice(invoiceDetail.getUnitPrice());
            //类型
            invoiceDetailInfo.setLx(invoiceDetail.getLx());
            //uuid唯一标识(发票代码+发票号码)
            invoiceDetailInfo.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
            //通行日期起
            invoiceDetailInfo.setTxrqq(invoiceDetail.getTxrqq());
            //通行日期止
            invoiceDetailInfo.setTxrqz(invoiceDetail.getTxrqz());
            //税率
            invoiceDetailInfo.setTaxRate(invoiceDetail.getTaxRate());
            //单位
            invoiceDetailInfo.setUnit(invoiceDetail.getUnit());
            //金额
            invoiceDetailInfo.setDetailAmount(invoiceDetail.getDetailAmount());
            //规格型号
            invoiceDetailInfo.setModel(invoiceDetail.getSpecificationModel());
            //车牌号
            invoiceDetailInfo.setCph(invoiceDetail.getCph());
            //放入集合
            invoiceDetailInfoList.add(invoiceDetailInfo);
        });
        return invoiceDetailInfoList;
    }
}
