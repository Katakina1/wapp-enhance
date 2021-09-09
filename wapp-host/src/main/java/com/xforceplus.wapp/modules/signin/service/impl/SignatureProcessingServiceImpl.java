package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceUploadService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.dao.SignatureProcessingDao;
import com.xforceplus.wapp.modules.signin.dao.SweepCodeDao;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.enumflord.SignInEnum;
import com.xforceplus.wapp.modules.signin.service.SignatureProcessingService;

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

import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.INVOICE_TYPE;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.valueOf;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * CreateBy leal.liang on 2018/4/19.
 **/
@Service
@Transactional
public class SignatureProcessingServiceImpl implements SignatureProcessingService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SignatureProcessingServiceImpl.class);

    private final String code="0001";

    private final static String GF_TAX_NO="91110108MA004CPN95";

    private SignatureProcessingDao signatureProcessingDao;


    private final EinvoiceUploadService einvoiceUploadService;

    private SweepCodeDao sweepCodeDao;

    private final SystemConfig systemConfig;

    @Autowired
    public SignatureProcessingServiceImpl(SignatureProcessingDao signatureProcessingDao,  EinvoiceUploadService einvoiceUploadService, SweepCodeDao sweepCodeDao, SystemConfig systemConfig) {
        this.signatureProcessingDao = signatureProcessingDao;
        this.einvoiceUploadService = einvoiceUploadService;
        this.sweepCodeDao = sweepCodeDao;
        this.systemConfig = systemConfig;
    }


    @Override
    public List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query) {
        return signatureProcessingDao.getRecordIncoiceList(schemaLabel,query);
    }

    @Override
    public int queryTotal(String schemaLabel, Query query) {
        return signatureProcessingDao.queryTotal(schemaLabel,query);
    }

    @Override
    public List<OptionEntity> searchGf(String schemaLabel, Long userId) {
        return signatureProcessingDao.searchGf(schemaLabel,userId);
    }

    @Override
    public List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params) {
        return signatureProcessingDao.queryAllList(schemaLabel,params);
    }

    @Override
    public Boolean deleteByuuid(String schemaLabel, String uuid,UserEntity user) {
        //保存备份数据
        RecordInvoiceEntity r=signatureProcessingDao.selectInvoice(schemaLabel,uuid);
        //查询备份表是否存在该uuid的发票数据
        Long copyDataId=signatureProcessingDao.getCopyId(schemaLabel,uuid);
        if(copyDataId!=null){
            //存在uuid 数据 更新备份表
            signatureProcessingDao.updateCopyData(schemaLabel,r,uuid,user);
        }else{
            //不存在uuid数据 插入备份数据
            signatureProcessingDao.saveCopyData(schemaLabel,r,user);
        }
        //删除扫描表数据
        Boolean c=signatureProcessingDao.deleteInvice(schemaLabel,uuid);
        einvoiceUploadService.delInvoiceImgIncludeSFTP(schemaLabel,null,uuid);
        if(c){
            return Boolean.TRUE;
        }
       return Boolean.FALSE;
    }

    @Override
    public Boolean checkedInvoice(String schemaLabel, Map<String, Object> params) {

        String uuid= (String)params.get("invoiceCode")+params.get("invoiceNo");
        RecordInvoiceEntity rq=sweepCodeDao.getIncoiceData(schemaLabel,uuid);
        if(rq!=null){
           return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean updateInvoice(String schemaLabel, String uuid, UserEntity user, Map<String ,Object> params) throws RRException {

        RecordInvoiceEntity rq=sweepCodeDao.getIncoiceData(schemaLabel, uuid);
        //只有查验成功数据才能去判断是否有权限签收
       Boolean checkPowerResult=einvoiceUploadService.checkUserTaxNoPower(schemaLabel, Long.valueOf(user.getUserid()),rq.getGfTaxNo());
        if(checkPowerResult){
            comparisonData(params, rq);
            rq.setQs(SignInEnum.NUMBER_ONE.getValue());
            rq.setQsStatus(SignInEnum.NUMBER_ONE.getValue());
            //更新抵账表签收数据
            signatureProcessingDao.updateInvoice(schemaLabel,uuid);
            //更新扫描表签收信息
            signatureProcessingDao.updateDxInvoiceData(schemaLabel,uuid,rq);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    public ResponseInvoice checkPlainInvoice(String schemaLabel, Map<String, Object> params) throws Exception {
       
    	 final RequestData requestData = new RequestData();
       
    	 /*购方税号*/
        requestData.setBuyerTaxNo(valueOf(params.get("gfTaxNo")));
         /*发票类型*/
        requestData.setInvoiceType(CommonUtil.getFplx(valueOf(params.get("invoiceCode"))));
        /* 发票代码*/
        requestData.setInvoiceCode(valueOf(params.get("invoiceCode")));
        /*发票号码*/
        requestData.setInvoiceNo(valueOf(params.get("invoiceNo")));
        /*开票日期*/
        requestData.setInvoiceDate(valueOf(params.get("invoiceDate")));

        //普票的
        if (INVOICE_TYPE.contains(CommonUtil.getFplx(valueOf(params.get("invoiceCode"))))) {
            requestData.setCheckCode(valueOf((params.get("checkCode"))));

        } else {
            requestData.setInvoiceAmount(valueOf(params.get("invoiceAmount")));
        }

        
        ResponseInvoice r=null;
        if(r!=null && code.equals(r.getResultCode())){

            //构建抵账表数据
            InvoiceCollectionInfo ic=buildInvoiceCollectionInfo(r);
            //保存抵账表数据
            sweepCodeDao.saveIncoiceData(schemaLabel,ic);
            //构建明细表数据
            List<InvoiceDetailInfo> list=buildDetailList(r);
            //保存明细数据
            for (InvoiceDetailInfo ind:list){
                sweepCodeDao.saveIncoiceData3(schemaLabel,ind);
            }

        }
        return r;
    }
    
    /**
     * 参数初始化
     */

    private RequestData initRequestData(Map<String, Object> params) {
        final RequestData requestData = new RequestData();
         /*购方税号*/
        requestData.setBuyerTaxNo(systemConfig.getBuyerTaxNo());
       // requestData.setBuyerTaxNo(valueOf(params.get("gfTaxNo")));
               /*发票类型*/
        requestData.setInvoiceType(CommonUtil.getFplx(valueOf(params.get("invoiceCode"))));
                  /* 发票代码*/
        requestData.setInvoiceCode(valueOf(params.get("invoiceCode")));
        /*发票号码*/
        requestData.setInvoiceNo(valueOf(params.get("invoiceNo")));
        /*开票日期*/
        requestData.setInvoiceDate(valueOf(params.get("invoiceDate")));

        if (INVOICE_TYPE.contains(CommonUtil.getFplx(valueOf(params.get("invoiceCode"))))) {
            requestData.setCheckCode(valueOf((params.get("invoiceAmount"))));

        } else {
            requestData.setInvoiceAmount(valueOf(params.get("invoiceAmount")));
        }


        return requestData;
    }

    @Override
    public RecordInvoiceEntity getDataByuuid(String schemaLabel, Map<String, Object> params) {
        return signatureProcessingDao.getDataByuuid(schemaLabel,params);
    }

    private void comparisonData(Map<String, Object> params, RecordInvoiceEntity rq) throws RRException {
        //开票日期
        final String invoiceDate =  new SimpleDateFormat(SHORT_DATE_FORMAT).format(rq.getInvoiceDate());
        if(!(params.get("invoiceDate").equals(invoiceDate))) {
            throw new RRException("开票日期与实际不符");
        }

        //购方税号
        if(!(params.get("gfTaxNo").equals(rq.getGfTaxNo()))) {
            throw new RRException("购方税号与实际不符");
        }

        //销方税号
        if(!(params.get("xfTaxNo").equals(rq.getXfTaxNo()))) {
            throw new RRException("销方税号与实际不符");
        }

        //发票金额
        if(new BigDecimal(String.valueOf(params.get("invoiceAmount"))).compareTo(rq.getInvoiceAmount()) != 0) {
            throw new RRException("发票金额与实际不符");
        }

        //发票税额
        if(new BigDecimal(String.valueOf(params.get("taxAmount"))).compareTo(rq.getTaxAmount()) != 0) {
            throw new RRException("发票税额与实际不符");
        }

        //价税合计金额
        if(new BigDecimal(String.valueOf(params.get("totalAmount"))).compareTo(/*BigDecimal.valueOf*/(rq.getTotalAmount())) != 0) {  // double - bigdecimal  6/7
            throw new RRException("价税合计金额与实际不符");
        }
    }


    /**
     * 普票等调查验接口构建扫描表数据
     * @param r
     * @param params
     * @return
     * @throws ParseException
     */
    private RecordInvoiceEntity creatInvoiceData(ResponseInvoice r,Map<String, Object> params,Boolean checkResult) throws Exception {
        RecordInvoiceEntity rie=new RecordInvoiceEntity();
        if(code.equals(r.getResultCode())){
            if(checkResult){
                rie.setQs(SignInEnum.NUMBER_ONE.getValue());
                rie.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                rie.setQsType(SignInEnum.QS_SWEEP_CODE.getValue());
                rie.setInvoiceCode(r.getInvoiceCode());
                rie.setInvoiceNo(r.getInvoiceNo());
                rie.setInvoiceType(r.getInvoiceType());
                SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
                rie.setInvoiceDate(formatter2.parse(r.getInvoiceDate()));
                rie.setInvoiceAmount(new BigDecimal(r.getInvoiceAmount()));
                rie.setTaxAmount(new BigDecimal(r.getTaxAmount()));
                rie.setCheckCode(r.getCheckCode().substring(r.getCheckCode().length()-6));
                rie.setNotes(r.getResultTip());
                rie.setGfTaxNo(r.getBuyerTaxNo());
                rie.setXfTaxNo(r.getSalerTaxNo());
                rie.setGfName(r.getBuyerName());
                rie.setXfName(r.getSalerName());
                rie.setNotes(r.getResultTip());
                rie.setTaxAmount(new BigDecimal(r.getTaxAmount()));
                rie.setTotalAmount(/*Double.valueOf*/new BigDecimal(r.getTotalAmount())); // double - bigdecimal  7/7
                rie.setUuid(r.getInvoiceCode()+r.getInvoiceNo());
                r.setResultTip("签收成功！");
            }else{
                rie.setQs(SignInEnum.NUMBER_ZERO.getValue());
                rie.setQsStatus(SignInEnum.QS_FAIL.getValue());
                rie.setSignInDate(new Date());
                rie.setNotes("无税号权限，签收失败！");
                rie.setInvoiceType((String) params.get("invoiceType"));
                rie.setCheckCode((String) params.get("checkCode"));
                rie.setInvoiceAmount(new BigDecimal(String.valueOf(params.get("invoiceAmount"))));
                rie.setInvoiceCode((String) params.get("invoiceCode"));
                rie.setInvoiceNo((String) params.get("invoiceNo"));
                SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
                rie.setInvoiceDate(format.parse((String) params.get("invoiceDate")));
                rie.setUuid(String.valueOf(params.get("invoiceCode"))+String.valueOf(params.get("invoiceNo")));
                r.setResultTip("无税号权限，签收失败！");
            }

        }else{
            rie.setQs(SignInEnum.NUMBER_ZERO.getValue());
            rie.setQsStatus(SignInEnum.QS_FAIL.getValue());
            rie.setSignInDate(new Date());
            rie.setNotes(r.getResultTip());
            rie.setInvoiceType((String) params.get("invoiceType"));
            rie.setCheckCode((String) params.get("checkCode"));
            if(params.get("invoiceAmount") != null) {
                rie.setInvoiceAmount(new BigDecimal(String.valueOf(params.get("invoiceAmount"))));
            }
            rie.setInvoiceCode((String) params.get("invoiceCode"));
            rie.setInvoiceNo((String) params.get("invoiceNo"));
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
            rie.setInvoiceDate(format.parse((String) params.get("invoiceDate")));
            rie.setUuid(String.valueOf(params.get("invoiceCode"))+String.valueOf(params.get("invoiceNo")));
        }
        return rie;
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
        if(SignInEnum.INVOICE_STATUS_YES.getValue().equals(responseInvoice.getIsCancelled())){
            invoiceCollectionInfo.setInvoiceStatus(SignInEnum.NUMBER_ZERO.getValue());
        }else {
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
        Date invoiceDateTime=null;
        try {
            invoiceDateTime = new SimpleDateFormat("yyyyMMdd").parse(responseInvoice.getInvoiceDate());
        } catch (ParseException e) {
            invoiceDateTime=null;

        }
        invoiceCollectionInfo.setInvoiceDate(invoiceDateTime);
        //备注
        invoiceCollectionInfo.setRemark(responseInvoice.getRemark());
        //校验码
        invoiceCollectionInfo.setCheckCode(responseInvoice.getCheckCode());
        //有明细
        invoiceCollectionInfo.setDetailYesorno(SignInEnum.NUMBER_ONE.getValue());
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
