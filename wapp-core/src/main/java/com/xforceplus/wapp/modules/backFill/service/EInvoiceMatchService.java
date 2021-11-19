package com.xforceplus.wapp.modules.backFill.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.backFill.model.InvoiceMain;
import com.xforceplus.wapp.modules.backFill.model.UploadFileResult;
import com.xforceplus.wapp.modules.backFill.model.VerificationBack;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;


/**
 * t_dx_record_invoice
 * qs_type 5 pdf上传
 * 4 手工签收
 * 3 导入签收
 * 2 app签收
 * 1 扫描仪签收
 * 0 扫码签收
 * qs_status
 * 1 签收成功
 * 0 签收失败
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

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    private static final SimpleDateFormat sdf = new SimpleDateFormat();

    @Autowired
    private TransactionalService transactionalService;

    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;
    @Autowired
    private NoneBusinessService noneBusinessService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TXfSettlementDao tXfSettlementDao;

    @Value("${wapp.org-check:false}")
    private boolean needOrgCheck;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private FileService fileService;

    static {
        sdf.applyPattern("yyyy-MM-dd");
    }


    @Transactional
    public void matchResultAfterVerify(VerificationBack verificationBack, SealedMessage.Header header) {
        String taskId = verificationBack.getTaskId();
        final TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity = electronicUploadRecordDetailService.getByVerifyTaskId(taskId);
        if (null != electronicUploadRecordDetailEntity) {
            if (!verificationBack.isOK()) {
                verifyFailure(verificationBack.getMessage(), header, electronicUploadRecordDetailEntity);
                return;
            }
            try {
                verifySucceed(verificationBack, header, electronicUploadRecordDetailEntity);
            } catch (EnhanceRuntimeException e) {
                verifyFailure(e.getMessage(), header, electronicUploadRecordDetailEntity);
            }
            //查询非商上传的电票验真数据
        } else {
            final TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity = noneBusinessService.getObjByVerifyTaskId(taskId);
            if (null == tXfNoneBusinessUploadDetailEntity) {
                return;
            }
            if (!verificationBack.isOK()) {
                verifyNoneBusFailed(verificationBack, header, tXfNoneBusinessUploadDetailEntity);
            } else {
                verifyNoneBusSucess(verificationBack, header, tXfNoneBusinessUploadDetailEntity);
            }
        }

    }

    private void verifyNoneBusSucess(VerificationBack verificationBack, SealedMessage.Header header, TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity) {
        final InvoiceMain invoiceMain = verificationBack.getResult().getInvoiceMain();
        final List<InvoiceDetail> invoiceDetails = verificationBack.getResult().getInvoiceDetails();
        TXfNoneBusinessUploadDetailEntity successEntity = new TXfNoneBusinessUploadDetailEntity();
        successEntity.setId(tXfNoneBusinessUploadDetailEntity.getId());
        successEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_SUCCESSE);
        successEntity.setReason(verificationBack.getMessage());
        successEntity.setInvoiceCode(invoiceMain.getInvoiceCode());
        successEntity.setInvoiceNo(invoiceMain.getInvoiceNo());
        successEntity.setInvoiceDate(invoiceMain.getPaperDrewDate());
        successEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(invoiceMain.getOfdImageUrl())) {
            try {
                String base64 = verificationService.getBase64ByUrl(invoiceMain.getInvoiceCode() + invoiceMain.getInvoiceNo());

                String uploadFile = fileService.uploadFile(Base64.decode(base64), UUID.randomUUID().toString().replace("-", "") + ".jpeg",invoiceMain.getSellerTaxNo());
                UploadFileResult uploadFileImageResult = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
                if (null != uploadFileImageResult) {
                    successEntity.setUploadId(uploadFileImageResult.getData().getUploadId());
                    successEntity.setUploadPath(uploadFileImageResult.getData().getUploadPath());
                }
            } catch (IOException e) {
                log.error("非商下载税局OFD图片失败:{}", e);
            }

        }

        TAcOrgEntity purEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getPurchaserTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_WALMART);
        TAcOrgEntity sellerEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getSellerTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_SUPPLIER);
        Map<String, Object> map = new HashMap<>();
        if (null != sellerEntity) {
            map.put("venderid", sellerEntity.getOrgCode());
            map.put("xfName", sellerEntity.getOrgName());
        }
        if (null != purEntity) {
            map.put("jvcode", purEntity.getOrgCode());
            map.put("gfName", purEntity.getOrgName());
        }else {
            successEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
            successEntity.setReason("购方信息未维护");
            successEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
            noneBusinessService.updateById(successEntity);
            return;
        }
        noneBusinessService.updateById(successEntity);
        map.put("invoiceNo", invoiceMain.getInvoiceNo());
        map.put("invoiceCode", invoiceMain.getInvoiceCode());
        map.put("invoiceAmount", invoiceMain.getAmountWithoutTax());
        map.put("invoiceDate", invoiceMain.getPaperDrewDate());
        map.put("totalAmount", invoiceMain.getAmountWithTax());
        map.put("taxAmount", invoiceMain.getTaxAmount());
        map.put("taxRate", invoiceDetails.get(0).getTaxRate());
        map.put("invoiceType", InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode()));
        map.put("gfTaxno", invoiceMain.getPurchaserTaxNo());
        map.put("checkNo", invoiceMain.getCheckCode());
        map.put("xfName", invoiceMain.getSellerName());
        map.put("xfTaxNo", invoiceMain.getSellerTaxNo());
        map.put("cipherText", invoiceMain.getCipherText());
        map.put("goodsListFlag", invoiceMain.getGoodsListFlag());
        map.put("invoiceStatus", InvoiceUtil.getInvoiceStatus(invoiceMain.getStatus()));
        //非商发票不进手工认证，提交后才进入手工认证
        map.put("noDeduction", '1');
        List<Supplier<Boolean>> successSuppliers = new ArrayList<>();
        String uuid = invoiceMain.getInvoiceCode() + "" + invoiceMain.getInvoiceNo();
        map.put("uuid", uuid);
        List<InvoiceEntity> list1 = matchDao.ifExist(map);
        if (CollectionUtils.isEmpty(list1)) {
            if ("04".equals(CommonUtil.getFplx((String) invoiceMain.getInvoiceCode()))) {
                electronicInvoiceDao.saveInvoicePP(map);
            } else {
                this.electronicInvoiceDao.saveInvoice(map);
            }
            // 新增发票情况下才会将明细入库
            successSuppliers.add(() -> {
                saveOrUpdateRecordInvoiceDetail(invoiceDetails, invoiceMain.getInvoiceNo(), invoiceMain.getInvoiceCode());
                return true;
            });
        } else {
            map.put("id", list1.get(0).getId());
            if ("04".equals(CommonUtil.getFplx((String) invoiceMain.getInvoiceCode()))) {
                matchDao.allUpdatePP(map);
            } else {
                matchDao.allUpdate(map);
            }
        }

    }

    /**
     * 非商验真失败
     *
     * @param verificationBack
     * @param tXfNoneBusinessUploadDetailEntity
     */
    void verifyNoneBusFailed(VerificationBack verificationBack, SealedMessage.Header header, TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity) {
        log.warn("非商发票验真异步结果》》验真失败:header:{},失败消息{}", header, verificationBack.getMessage());
        TXfNoneBusinessUploadDetailEntity failEntity = new TXfNoneBusinessUploadDetailEntity();
        failEntity.setId(tXfNoneBusinessUploadDetailEntity.getId());
        failEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
        failEntity.setReason(verificationBack.getMessage());
        noneBusinessService.updateById(failEntity);
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
        //validateOrg(invoiceMain, recordEntity, orgEntity);
        validateTax(invoiceMain, invoiceDetails);
        //校验购销对
        QueryWrapper<TXfSettlementEntity> settlementWrapper = new QueryWrapper<>();
        settlementWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, electronicUploadRecordDetailEntity.getSettlementNo());
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(settlementWrapper);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("未找到对应的结算单");
        }
        String invoiceType = InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode());

//        if (!invoiceType.equals(tXfSettlementEntity.getInvoiceType())) {
//            throw new EnhanceRuntimeException("发票类型与结算单不一致");
//        }
        invoiceMain.setInvoiceType(invoiceType);
     /*   if (!invoiceMain.getPurchaserName().equals(tXfSettlementEntity.getPurchaserName())) {
            throw new EnhanceRuntimeException("购方名称不一致");
        }
        if (!invoiceMain.getPurchaserTaxNo().equals(tXfSettlementEntity.getPurchaserTaxNo())) {
            throw new EnhanceRuntimeException("购方税号不一致");
        }
        if (!invoiceMain.getSellerName().equals(tXfSettlementEntity.getSellerName())) {
            throw new EnhanceRuntimeException("销方名称不一致");
        }
        if (!invoiceMain.getSellerTaxNo().equals(tXfSettlementEntity.getSellerTaxNo())) {
            throw new EnhanceRuntimeException("销方税号不一致");
        }*/
        //从备注里截取红字信息编号
        TDxRecordInvoiceEntity recordInvoice = new TDxRecordInvoiceEntity();
        if (new BigDecimal(invoiceMain.getAmountWithoutTax()).compareTo(BigDecimal.ZERO) < 0  && new BigDecimal(invoiceDetails.get(0).getTaxRate()).compareTo(BigDecimal.ZERO) !=0){
            String redNo = StringUtils.substring(invoiceMain.getRemark(), invoiceMain.getRemark().indexOf("信息表编号") + 5, invoiceMain.getRemark().indexOf("信息表编号") + 21);
            if (StringUtils.isNotEmpty(redNo)) {
                String trim = redNo.trim();
                if (trim.matches("[0-9]+")) {
                    recordInvoice.setRedNoticeNumber(trim);
                } else {
                    log.error("发票回填--解析红字信息编号失败！");
                    throw new EnhanceRuntimeException("解析红字信息编号失败");
                }
            }
        }
        recordInvoice.setSettlementNo(electronicUploadRecordDetailEntity.getSettlementNo());
        recordInvoice.setVenderid(recordEntity.getVendorId());
        recordInvoice.setJvcode(recordEntity.getJvCode());
        recordInvoice.setInvoiceCode(invoiceMain.getInvoiceCode());
        recordInvoice.setInvoiceNo(invoiceMain.getInvoiceNo());
        recordInvoice.setInvoiceAmount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
        recordInvoice.setDkInvoiceamount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
        recordInvoice.setInvoiceDate(DateUtils.convertStringToDate(invoiceMain.getPaperDrewDate()));
        recordInvoice.setTotalAmount(new BigDecimal(invoiceMain.getAmountWithTax()));
        recordInvoice.setTaxAmount(new BigDecimal( invoiceMain.getTaxAmount()));
        recordInvoice.setTaxRate(new BigDecimal(invoiceDetails.get(0).getTaxRate()));
        recordInvoice.setInvoiceType(invoiceMain.getInvoiceType());
        recordInvoice.setGfName(invoiceMain.getPurchaserName());
        recordInvoice.setGfTaxNo(invoiceMain.getPurchaserTaxNo());
        recordInvoice.setGfAddressAndPhone(invoiceMain.getPurchaserAddrTel());
        recordInvoice.setGfBankAndNo(invoiceMain.getPurchaserBankInfo());
        recordInvoice.setCheckCode(invoiceMain.getCheckCode());
        recordInvoice.setXfName( invoiceMain.getSellerName());
        recordInvoice.setXfTaxNo(invoiceMain.getSellerTaxNo());
        recordInvoice.setXfAddressAndPhone(invoiceMain.getSellerAddrTel());
        recordInvoice.setXfBankAndNo(invoiceMain.getSellerBankInfo());
        recordInvoice.setCompanyCode(orgEntity == null ? null : orgEntity.getCompany());
//        底账来源  0-采集 1-查验 2-录入
        recordInvoice.setSourceSystem("1");
        recordInvoice.setGoodsListFlag(invoiceMain.getGoodsListFlag());
        recordInvoice.setMachinecode(invoiceMain.getMachineCode());
//        发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
        recordInvoice.setInvoiceStatus(convertStatus(invoiceMain.getStatus(), invoiceMain.getRedFlag()));
        recordInvoice.setRemark(invoiceMain.getRemark());
        recordInvoice.setDxhyMatchStatus("0");
        recordInvoice.setDetailYesorno("1");
        recordInvoice.setFlowType("1");
        recordInvoice.setTpStatus("0");
        recordInvoice.setIsDel(IsDealEnum.NO.getValue());
        recordInvoice.setUuid(invoiceMain.getInvoiceCode()+invoiceMain.getInvoiceNo());
        recordInvoice.setCreateDate(new Date());
        successSuppliers.add(() -> {
                    //结果存储-记录表
                    //成功计数
                    electronicUploadRecordService.increaseSucceed(electronicUploadRecordDetailEntity.getBatchNo());
                    //保存发票号码代码到上传详情
                    electronicUploadRecordDetailService.updateById(electronicUploadRecordDetailEntity);
                    if (electronicUploadRecordDetailEntity.getFileType() != null && StringUtils.isNotEmpty(invoiceMain.getOfdPreviewUrl())) {
                        invoiceFileService.save(electronicUploadRecordDetailEntity, invoiceMain);
                    }
                    return true;
                }
        );
        int result = this.saveOrUpdateRecordInvoice(recordInvoice);
        //只有红票才入库
        if (new BigDecimal(invoiceMain.getAmountWithoutTax()).compareTo(BigDecimal.ZERO) < 0) {
            this.saveOrUpdateInvoice(recordInvoice);
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

    private void validateOrg(InvoiceMain invoiceMain, TXfElecUploadRecordEntity recordEntity, OrgEntity gfOrg) {
        if (needOrgCheck) {
            final String jvCode = recordEntity.getJvCode();
            OrgEntity orgEntity = matchDao.getXfMessage(recordEntity.getVendorId());
            final String xfTaxNo = orgEntity.getTaxno();
            StringBuilder stringBuilder = new StringBuilder();
            if (!Objects.equals(invoiceMain.getSellerTaxNo(), xfTaxNo)) {
                stringBuilder.append("发票销方税号与供应商主体税号不一致;");
            }

            if (!Objects.equals(invoiceMain.getPurchaserTaxNo(), gfOrg.getTaxno())) {
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
     * @param recordInvoice
     * @return 0 插入，1更新
     */
    public int saveOrUpdateRecordInvoice(TDxRecordInvoiceEntity recordInvoice) {
        boolean flag = false;
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity.UUID,recordInvoice.getUuid());
        TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectOne(wrapper);
        int result = 0;
        try {
            //判断uuid是否存在
            if (entity == null) {
                //不存在录入
                flag = tDxRecordInvoiceDao.insert(recordInvoice) >0;
            } else {
                result = 1;
                //存在数据
                TDxRecordInvoiceEntity newEntity = new TDxRecordInvoiceEntity();
                newEntity.setId(entity.getId());
                newEntity.setIsDel(IsDealEnum.NO.getValue());
                tDxRecordInvoiceDao.updateById(entity);
            }
        } catch (Exception e) {
            log.error("录入发票:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("录入发票失败:" + e.getMessage());
        } finally {
            log.info("更新/插入结果:{},{}-{}", flag, recordInvoice.getInvoiceCode(), recordInvoice.getInvoiceNo());
        }
        return result;
    }

    /**
     * 保存明细
     *
     * @param details
     * @param invoiceNo
     * @param invoiceCode
     */
    private void saveOrUpdateRecordInvoiceDetail(List<InvoiceDetail> details, String invoiceNo, String invoiceCode) {
        if (!CollectionUtils.isEmpty(details)) {
            List<TDxRecordInvoiceDetailEntity> recordDetails = new ArrayList<>();
            for (int i = 0; i < details.size(); i++) {
                final InvoiceDetail x = details.get(i);
                TDxRecordInvoiceDetailEntity detail = new TDxRecordInvoiceDetailEntity();
                detail.setInvoiceNo(invoiceNo);
                detail.setInvoiceCode(invoiceCode);
                detail.setUuid(invoiceCode + invoiceNo);
                detail.setDetailNo(String.valueOf(i + 1));
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
     * @param recordInvoiceEntity
     * @return 扫描表 0 插入，1更新
     */
    public int saveOrUpdateInvoice(TDxRecordInvoiceEntity recordInvoiceEntity) {
        TDxInvoiceEntity entity = new TDxInvoiceEntity();
        BeanUtil.copyProperties(recordInvoiceEntity,entity);
        QueryWrapper<TDxInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxInvoiceEntity.UUID, entity.getUuid());
        TDxInvoiceEntity tDxInvoiceEntity = tDxInvoiceDao.selectOne(wrapper);
        boolean flag = false;
        int result = 0;
        try {
            //判断uuid是否存在
            if (tDxInvoiceEntity == null) {
                //不存在
                //录入
                entity.setBindyesorno("0");
                entity.setPackyesorno("0");
                flag = tDxInvoiceDao.insert(entity) > 0;
            } else {
                result = 1;
                //存在数据
                tDxInvoiceEntity.setIsdel(IsDealEnum.NO.getValue());
                tDxInvoiceDao.updateById(tDxInvoiceEntity);
            }
        } catch (Exception e) {
            log.error("录入发票:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("录入发票失败:" + e.getMessage());
        } finally {
            log.info("更新/插入结果:{},{}-{}", flag, recordInvoiceEntity.getInvoiceCode(), recordInvoiceEntity.getInvoiceNo());
        }
        return result;
    }

    private static String convertStatus(String status, String redFlag) {
        if (Objects.equals(redFlag, InvoiceMain.ALREADY_RED)) {
            return "3";
        }
        switch (status) {
            case "1":
                return "0";
            case "0":
                return "2";
        }
        return "4";
    }

}
