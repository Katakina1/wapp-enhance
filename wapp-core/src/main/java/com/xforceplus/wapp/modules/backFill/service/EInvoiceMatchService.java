package com.xforceplus.wapp.modules.backFill.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.xml.internal.bind.v2.TODO;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.InvoiceUtil;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.backFill.model.InvoiceMain;
import com.xforceplus.wapp.modules.backFill.model.VerificationBack;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.daoExt.ElectronicInvoiceDao;
import com.xforceplus.wapp.repository.daoExt.MatchDao;
import com.xforceplus.wapp.repository.daoExt.XfRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.service.TransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;


/**
 *
 * t_dx_record_invoice
 * qs_type 5 pdf上传
 * 		4 手工签收
 * 		3 导入签收
 * 		2 app签收
 * 		1 扫描仪签收
 * 		0 扫码签收
 * qs_status
 * 		1 签收成功
 * 		0 签收失败
 *
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description 专用电票匹配入库
 * @create 2021-09-16 19:45
 **/
@Service
@Slf4j

public class EInvoiceMatchService {
    @Autowired
    private MatchDao matchDao;


    @Autowired
    private ElectronicUploadRecordDetailService electronicUploadRecordDetailService;

    @Autowired
    private ElectronicUploadRecordService electronicUploadRecordService;

    @Autowired
    private ElectronicInvoiceDao electronicInvoiceDao;

    @Autowired
    private XfRecordInvoiceDao recordInvoiceDao;

    @Autowired
    private InvoiceFileService invoiceFileService;

    private static final SimpleDateFormat sdf=new SimpleDateFormat();

    @Autowired
    private TransactionalService transactionalService;

    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;


    @Value("${wapp.org-check:false}")
    private boolean needOrgCheck;

    static {
        sdf.applyPattern("yyyy-MM-dd");
    }


    @Transactional
    public void matchResultAfterVerify(VerificationBack verificationBack, SealedMessage.Header header) {
        String taskId = verificationBack.getTaskId();
        final TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity = electronicUploadRecordDetailService.getByVerifyTaskId(taskId);
        if (!verificationBack.isOK()) {
            verifyFailure(verificationBack.getMessage(), header, electronicUploadRecordDetailEntity);
            return;
        }
        try {
            verifySucceed(verificationBack, header, electronicUploadRecordDetailEntity);
        } catch (EnhanceRuntimeException e) {
            verifyFailure(e.getMessage(), header, electronicUploadRecordDetailEntity);
        }
    }

    void verifyFailure(String message, SealedMessage.Header header, TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity) {
        log.warn("发票验真异步结果》》验真失败:header:{},失败消息{}", header, message);
        //失败计数
        electronicUploadRecordService.increaseFailure(electronicUploadRecordDetailEntity.getBatchNo());
        electronicUploadRecordDetailEntity.setReason(message);
        electronicUploadRecordDetailEntity.setStatus(false);
        electronicUploadRecordDetailService.updateById(electronicUploadRecordDetailEntity);
    }

    void verifySucceed(VerificationBack verificationBack, SealedMessage.Header header, TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity) {
        final InvoiceMain invoiceMain = verificationBack.getResult().getInvoiceMain();
        final List<InvoiceDetail> invoiceDetails = verificationBack.getResult().getInvoiceDetails();

        TXfElecUploadRecordEntity recordEntity = this.electronicUploadRecordService.getByBatchNo(electronicUploadRecordDetailEntity.getBatchNo());
        List<Supplier<Boolean>> successSuppliers = new ArrayList<>();

        final OrgEntity orgEntity = this.electronicInvoiceDao.selectGfByJvCode(recordEntity.getJvCode());
        electronicUploadRecordDetailEntity.setInvoiceNo(invoiceMain.getInvoiceNo());
        electronicUploadRecordDetailEntity.setInvoiceCode(invoiceMain.getInvoiceCode());
        electronicUploadRecordDetailEntity.setStatus(true);
        validateOrg(invoiceMain,recordEntity,orgEntity);
        validateTax(invoiceMain,invoiceDetails);

        //TODO 结果存储-大象记录表
        Map<String, Object> map = new HashMap<>();
        map.put("venderid", recordEntity.getVendorId());
        map.put("jvcode", recordEntity.getJvCode());
        map.put("invoiceNo", invoiceMain.getInvoiceNo());
        map.put("invoiceCode", invoiceMain.getInvoiceCode());
        map.put("invoiceAmount", invoiceMain.getAmountWithoutTax());
        map.put("invoiceDate", invoiceMain.getPaperDrewDate());
        map.put("totalAmount", invoiceMain.getAmountWithTax());
        map.put("taxAmount", invoiceMain.getTaxAmount());
        map.put("taxRate", invoiceDetails.get(0).getTaxRate());
        map.put("gfName", orgEntity.getOrgname());
        map.put("invoiceType", InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode()));
        map.put("gfTaxno", invoiceMain.getPurchaserTaxNo());
        map.put("checkNo", invoiceMain.getCheckCode());
        map.put("xfName", invoiceMain.getSellerName());
        map.put("xfTaxNo", invoiceMain.getSellerTaxNo());
        map.put("companyCode", orgEntity.getCompany());
//        底账来源  0-采集 1-查验 2-录入
        map.put("sourceSystem","1");

//        发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
        map.put("invoiceStatus", convertStatus(invoiceMain.getStatus(),invoiceMain.getRedFlag()));
        map.put("remark",invoiceMain.getRemark());
        //从备注里截取红字信息编号
        if(Float.valueOf(invoiceMain.getAmountWithoutTax()) <0){
            String redNo = StringUtils.substringBetween(invoiceMain.getRemark(),"信息表编号","单据编号").trim();
            if(StringUtils.isNotEmpty(redNo)){
                map.put("redNoticeNumber",redNo.trim());
            }
        }
        successSuppliers.add(() -> {
                    //结果存储-记录表
                    //成功计数
                    electronicUploadRecordService.increaseSucceed(electronicUploadRecordDetailEntity.getBatchNo());
                    //保存发票号码代码到上传详情
                    electronicUploadRecordDetailService.updateById(electronicUploadRecordDetailEntity);
                    invoiceFileService.save(electronicUploadRecordDetailEntity, invoiceMain);
                    return true;
                }
        );
        int result = this.saveOrUpdateRecordInvoice(map);
        //只有红票才入库
        if(new BigDecimal(invoiceMain.getAmountWithoutTax()).compareTo(BigDecimal.ZERO) < 0){
            saveOrUpdateInvoice(map);
        }
        if (result == 0) {
            // 新增发票情况下才会将明细入库
            successSuppliers.add(() -> {
                saveOrUpdateRecordInvoiceDetail(invoiceDetails, invoiceMain.getInvoiceNo(), invoiceMain.getInvoiceCode());
                return true;
            });
        }

        transactionalService.execute(successSuppliers);
    }

    private void validateOrg(InvoiceMain invoiceMain,TXfElecUploadRecordEntity recordEntity,OrgEntity gfOrg){
        if (needOrgCheck) {
            final String jvCode = recordEntity.getJvCode();
            OrgEntity orgEntity = matchDao.getXfMessage(recordEntity.getVendorId());
            final String xfTaxNo = orgEntity.getTaxno();
            StringBuilder stringBuilder = new StringBuilder();
            if (!Objects.equals(invoiceMain.getSellerTaxNo(), xfTaxNo)) {
                stringBuilder.append("发票销方税号与供应商主体税号不一致;");
            }

            if (!Objects.equals(invoiceMain.getPurchaserTaxNo(),gfOrg.getTaxno())){
                stringBuilder.append("发票购方税号与所选订单不一致;");
            }

            final String toString = stringBuilder.toString();
            if (StringUtils.isNotBlank(toString)) {
                throw new EnhanceRuntimeException(toString);
            }
        }
    }

    private void validateTax(InvoiceMain invoiceMain, List<InvoiceDetail> invoiceDetails) {
        final BigDecimal taxAmountSum = invoiceDetails.stream().map(x -> {
                    BigDecimal taxRate = new BigDecimal(x.getTaxRate());
                    return taxRate.compareTo(BigDecimal.ONE) > 0 ? taxRate.movePointLeft(2).multiply(new BigDecimal(x.getAmountWithoutTax())) : taxRate.multiply(new BigDecimal(x.getAmountWithoutTax()));
                }
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (taxAmountSum.subtract(new BigDecimal(invoiceMain.getTaxAmount())).abs().compareTo(new BigDecimal("0.05")) > 0) {
            throw new EnhanceRuntimeException("发票税差超过0.05");
        }
    }

    /**
     *
     * @param map
     * @return 0 插入，1更新
     */
    public int saveOrUpdateRecordInvoice(Map<String, Object> map) {
        final Object invoiceCode = map.get("invoiceCode");
        String code = invoiceCode.toString();
        String no = map.get("invoiceNo").toString();
        String uuid = code + "" + no;
        boolean flag = false;
        map.put("uuid", uuid);
        List<InvoiceEntity> list1 = matchDao.ifExist(map);
        String jvcode = (String) map.get("jvcode");
//        String companyCode = matchDao.getCompanyCode(jvcode);
//        map.put("companyCode", companyCode);
        OrgEntity orgEntity = matchDao.getXfMessage((String) map.get("venderid"));
        int result=0;
        try {
            //判断uuid是否存在
            if (CollectionUtils.isEmpty(list1)) {
                //不存在
                //录入

                map.put("xfTaxNo", orgEntity.getTaxno());
                map.put("xfName", orgEntity.getOrgname());
                if ("04".equals(CommonUtil.getFplx((String) invoiceCode))) {
                    flag = electronicInvoiceDao.saveInvoicePP(map) > 0;
                } else {
                    flag = this.electronicInvoiceDao.saveInvoice(map) > 0;
                }

            } else {
                result=1;
                //存在数据
                String source = list1.get(0).getSystemSource();
                String matchstatus = list1.get(0).getDxhyMatchStatus();
                String tpStatus = list1.get(0).getTpStatus();
                String flowType = list1.get(0).getFlowType();
                String hostStatus = list1.get(0).getHostStatus();
                BigDecimal invoiceAmount = list1.get(0).getInvoiceAmount();

                if (invoiceAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new EnhanceRuntimeException("该发票金额小于0，不能匹配！");
                }
                if ("0".equals(hostStatus) || "10".equals(hostStatus) || "1".equals(hostStatus) || "13".equals(hostStatus) || StringUtils.isEmpty(hostStatus)) {


                    if (StringUtils.isEmpty(flowType) || "1".equals(flowType)) {

                        if (list1.get(0).getXfTaxNo().equals(orgEntity.getTaxno())) {
                            if (("0".equals(matchstatus) || "6".equals(matchstatus)) && (!"1".equals(tpStatus))) {
                                //判断来源
                                if ("0".equals(source)) {
                                    //采集
                                    if (list1.get(0).getGfTaxNo() == null || !list1.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                                        throw new EnhanceRuntimeException("该发票的购方税号与所选购方名称不一致！");
                                    }
                                    //判断是否有明细
                                    if ("0".equals(list1.get(0).getDetailYesorno()) && list1.get(0).getTaxRate() == null) {
                                        //无税率插入税率
                                        flag = matchDao.update(list1.get(0).getId(), new BigDecimal((String) map.get("taxRate"))) > 0;

                                    } else if ("1".equals(list1.get(0).getDetailYesorno()) && list1.get(0).getTaxRate() == null) {
                                        //有明细无税率
                                        throw new EnhanceRuntimeException("该发票不是单一税率发票！");
                                    }
                                } else if ("2".equals(source)) {
                                    // 录入
                                    //覆盖
                                    map.put("id", list1.get(0).getId());
                                    if ("04".equals(CommonUtil.getFplx((String) invoiceCode))) {
                                        flag = matchDao.allUpdatePP(map) > 0;
                                    } else {
                                        flag = matchDao.allUpdate(map) > 0;
                                    }


                                } else {
                                    if (list1.get(0).getGfTaxNo() == null || !list1.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                                        throw new EnhanceRuntimeException("该发票的购方税号与所选购方名称不一致！");
                                    }
                                    //更新抵扣金额
                                    Object invoiceAmounts = map.get("invoiceAmount");
                                    matchDao.updateDkAmount(new BigDecimal(String.valueOf(invoiceAmounts)), (String) map.get("uuid"));
                                }
                            } else {
                                throw new EnhanceRuntimeException("该发票已匹配或者待退票！");
                            }
                        } else {
                            throw new EnhanceRuntimeException("该发票销方税号不符！");
                        }
                    } else {
                        throw new EnhanceRuntimeException("该发票不是商品发票！");

                    }
                } else {
                    throw new EnhanceRuntimeException("该发票已在沃尔玛匹配！");
                }
            }
        } catch (Exception e) {
            log.error("录入发票:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("录入发票失败:" + e.getMessage());
        } finally {
            log.info("更新/插入结果:{},{}-{}", flag, invoiceCode, map.get("invoiceNo"));
        }
        return result;
    }

    /**
     * 保存明细
     * @param details
     * @param invoiceNo
     * @param invoiceCode
     */
    private void saveOrUpdateRecordInvoiceDetail(List<InvoiceDetail> details,String invoiceNo,String invoiceCode){
        if (!CollectionUtils.isEmpty(details)){
            List<TDxRecordInvoiceDetailEntity> recordDetails =new ArrayList<>();
            for (int i = 0; i < details.size(); i++) {
                final InvoiceDetail  x= details.get(i);
                TDxRecordInvoiceDetailEntity detail=new TDxRecordInvoiceDetailEntity();
                detail.setInvoiceNo(invoiceNo);
                detail.setInvoiceCode(invoiceCode);
                detail.setUuid(invoiceCode+invoiceNo);
                detail.setDetailNo(String.valueOf(i+1));
                detail.setDetailAmount(x.getAmountWithoutTax());
                detail.setGoodsName(x.getCargoName());
                detail.setTaxAmount(x.getTaxAmount());
                detail.setTaxRate(x.getTaxRate());
                detail.setNum(x.getQuantity());
                detail.setUnit(x.getQuantityUnit());
                detail.setUnitPrice(x.getUnitPrice());
                detail.setModel(x.getItemSpec());
                recordDetails.add(detail);
            }
            recordInvoiceDao.saveRecordInvoiceDetail(recordDetails);
        }
    }

    /**
     *
     * @param map
     * @return 扫描表 0 插入，1更新
     */
    public int saveOrUpdateInvoice(Map<String, Object> map) {
        final Object invoiceCode = map.get("invoiceCode");
        String code = invoiceCode.toString();
        String no = map.get("invoiceNo").toString();
        String uuid = code + "" + no;
        QueryWrapper<TDxInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxInvoiceEntity.UUID,uuid);
        TDxInvoiceEntity tDxInvoiceEntity = tDxInvoiceDao.selectOne(wrapper);
        String jvcode = (String) map.get("jvcode");
        OrgEntity orgEntity = matchDao.getXfMessage((String) map.get("venderid"));
        boolean flag = false;
        int result=0;
        try {
            //判断uuid是否存在
            if (tDxInvoiceEntity != null) {
                //不存在
                //录入
                map.put("xfTaxNo", orgEntity.getTaxno());
                map.put("xfName", orgEntity.getOrgname());
                if ("04".equals(CommonUtil.getFplx((String) invoiceCode))) {
                    //TODO
                } else {

                }
                TDxInvoiceEntity entity = JSONObject.parseObject(JSONObject.toJSONString(map), TDxInvoiceEntity.class);
                flag = tDxInvoiceDao.insert(entity) >0;
            } else {
                result=1;
                //存在数据
                String flowType = tDxInvoiceEntity.getFlowType();
                BigDecimal invoiceAmount = tDxInvoiceEntity.getInvoiceAmount();
                if (invoiceAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new EnhanceRuntimeException("该发票金额小于0，不能匹配！");
                }
                //TODO

            }
        } catch (Exception e) {
            log.error("录入发票:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("录入发票失败:" + e.getMessage());
        } finally {
            log.info("更新/插入结果:{},{}-{}", flag, invoiceCode, map.get("invoiceNo"));
        }
        return result;
    }

    private static String  convertStatus(String status,String redFlag){
        if (Objects.equals(redFlag,InvoiceMain.ALREADY_RED)){
            return "3";
        }
        switch (status){
            case "1":
                return "0";
            case "0":
                return "2";
        }
        return "4";
    }


}