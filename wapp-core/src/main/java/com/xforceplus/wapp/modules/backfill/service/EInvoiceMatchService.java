package com.xforceplus.wapp.modules.backfill.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.evat.common.constant.consist.CommonApiProperties;
import com.xforceplus.evat.common.constant.consist.GlobalConstants;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.backfill.model.InvoiceMain;
import com.xforceplus.wapp.modules.backfill.model.UploadFileResult;
import com.xforceplus.wapp.modules.backfill.model.VerificationBack;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.modules.xforceapi.HttpClientUtils;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfNoneBusinessUploadDetailDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.daoExt.ElectronicInvoiceDao;
import com.xforceplus.wapp.repository.daoExt.MatchDao;
import com.xforceplus.wapp.repository.daoExt.XfRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.service.TransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

    private static final Pattern RED_NOTIFICATION_NO_P = Pattern.compile("^.*信息表编号:?([0-9]{16}).*$");

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
    @Lazy
    private VerificationService verificationService;

    @Autowired
    private TXfNoneBusinessUploadDetailDao tXfNoneBusinessUploadDetailDao;

    @Autowired
    private FileService fileService;
    @Autowired
    private CommonApiProperties commonApiProperties;

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
        successEntity.setInvoiceRemark(invoiceMain.getRemark());
        successEntity.setInvoiceDate(invoiceMain.getPaperDrewDate());
        successEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
        if (StringUtils.isNotEmpty(invoiceMain.getOfdImageUrl())) {
            try {
                String base64 = verificationService.getBase64ByUrl(invoiceMain.getInvoiceCode() + invoiceMain.getInvoiceNo());
                if (StringUtils.isNotEmpty(base64)) {
                    String uploadFile = fileService.uploadFile(Base64.decode(base64), UUID.randomUUID().toString().replace("-", "") + ".jpeg", invoiceMain.getSellerTaxNo());
                    UploadFileResult uploadFileImageResult = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
                    if (null != uploadFileImageResult) {
                        successEntity.setUploadId(uploadFileImageResult.getData().getUploadId());
                        successEntity.setUploadPath(uploadFileImageResult.getData().getUploadPath());
                    }
                }

            } catch (IOException e) {
                log.error("非商下载税局OFD图片失败:{}", e);
            }

        }
        Map<String, Object> map = new HashMap<>();
        String uuid = invoiceMain.getInvoiceCode() + "" + invoiceMain.getInvoiceNo();
        map.put("uuid", uuid);
        List<InvoiceEntity> list1 = matchDao.ifExist(map);

        if (!CollectionUtils.isEmpty(list1)) {
            String flowType = list1.get(0).getFlowType();
            if (StringUtils.isNotEmpty(flowType) && !"7".equals(flowType)) {
                successEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
                successEntity.setReason("该发票不是非商发票");
                successEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                noneBusinessService.updateById(successEntity);
                return;
            }
        }
        TAcOrgEntity purEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getPurchaserTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_WALMART);
        TAcOrgEntity sellerEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getSellerTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_SUPPLIER);
        TDxRecordInvoiceEntity recordInvoice = new TDxRecordInvoiceEntity();
        recordInvoice.setInvoiceCode(invoiceMain.getInvoiceCode());
        recordInvoice.setInvoiceNo(invoiceMain.getInvoiceNo());
        recordInvoice.setInvoiceAmount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
        recordInvoice.setDkInvoiceamount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
        recordInvoice.setInvoiceDate(DateUtils.convertStringToDate(invoiceMain.getPaperDrewDate()));
        recordInvoice.setTotalAmount(new BigDecimal(invoiceMain.getAmountWithTax()));
        recordInvoice.setTaxAmount(new BigDecimal(invoiceMain.getTaxAmount()));
        recordInvoice.setTaxRate(new BigDecimal(invoiceDetails.get(0).getTaxRate()));
        long taxRateCount = invoiceDetails.stream().map(InvoiceDetail::getTaxRate).distinct().count();
        if (taxRateCount > 1) {
            recordInvoice.setTaxRate(null);
        }
        recordInvoice.setInvoiceType(InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode()));
        recordInvoice.setGfName(invoiceMain.getPurchaserName());
        recordInvoice.setGfTaxNo(invoiceMain.getPurchaserTaxNo());
        recordInvoice.setGfAddressAndPhone(invoiceMain.getPurchaserAddrTel());
        recordInvoice.setGfBankAndNo(invoiceMain.getPurchaserBankInfo());
        recordInvoice.setCheckCode(invoiceMain.getCheckCode());
        recordInvoice.setXfName(invoiceMain.getSellerName());
        recordInvoice.setXfTaxNo(invoiceMain.getSellerTaxNo());
        recordInvoice.setXfAddressAndPhone(invoiceMain.getSellerAddrTel());
        recordInvoice.setXfBankAndNo(invoiceMain.getSellerBankInfo());
//        底账来源  0-采集 1-查验 2-录入
        recordInvoice.setSourceSystem("1");
        recordInvoice.setGoodsListFlag(invoiceMain.getGoodsListFlag());
        recordInvoice.setMachinecode(invoiceMain.getMachineCode());
//        发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
        recordInvoice.setInvoiceStatus(convertStatus(invoiceMain.getStatus(), invoiceMain.getRedFlag()));
        recordInvoice.setRemark(invoiceMain.getRemark());
        recordInvoice.setDxhyMatchStatus("0");
        recordInvoice.setDetailYesorno("1");
        recordInvoice.setFlowType("7");
        recordInvoice.setTpStatus("0");
        recordInvoice.setIsDel(IsDealEnum.NO.getValue());
        recordInvoice.setUuid(Objects.toString(invoiceMain.getInvoiceCode(), "") + invoiceMain.getInvoiceNo());
        recordInvoice.setCreateDate(new Date());
        //电子发票改为签收状态
        if (InvoiceTypeEnum.isElectronic(InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode()))) {
            //非商电票不进入直接认证，提交后才进入
            recordInvoice.setQsStatus("0");
            recordInvoice.setQsDate(new Date());
            recordInvoice.setQsType("5");
        }
        if (null != sellerEntity) {
            recordInvoice.setVenderid(sellerEntity.getOrgCode());
            map.put("venderid", sellerEntity.getOrgCode());
            map.put("xfName", sellerEntity.getOrgName());
        }
        if (null != purEntity) {
            recordInvoice.setJvcode(purEntity.getOrgCode());
            map.put("jvcode", purEntity.getOrgCode());
            map.put("gfName", purEntity.getOrgName());
            map.put("companyCode", purEntity.getCompanyCode());
            recordInvoice.setCompanyCode(purEntity.getCompanyCode());
        } else {
            successEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
            successEntity.setReason("购方信息未维护");
            successEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
            noneBusinessService.updateById(successEntity);
            return;
        }
        this.saveOrUpdateInvoice(recordInvoice);
        //特殊发票需要打上标识，发票备注字段不能为空
        if (!CollectionUtils.isEmpty(invoiceDetails)) {
            try {
                String goodsTaxNos =invoiceDetails.stream().map(InvoiceDetail::getGoodsTaxNo).distinct().collect(Collectors.joining(","));
                JSONObject params = new JSONObject();
                params.put("goodsTaxNo",goodsTaxNos);
                log.info("根据税收分类编码查询发票：{}",goodsTaxNos);
                String response = HttpClientUtils.postJson(commonApiProperties.getBaseHost()+commonApiProperties.getInvoiceTaxMappingUrl(),params.toJSONString());
                log.info("根据税收分类编码查询发票返回：{}",response);
                List<TInvoiceTaxMappingEntity> invoiceTaxMappingList = JSON.parseArray(response, TInvoiceTaxMappingEntity.class);
                if(!CollectionUtils.isEmpty(invoiceTaxMappingList)){
                    map.put("specialFlag", invoiceTaxMappingList.get(0).getInvoiceType());
                    if(StringUtils.isEmpty(invoiceMain.getRemark())){
                        successEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
                        successEntity.setReason(GlobalConstants.SPECIAL_INVOICE_REMARK_EMPTY_TIP);
                        successEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                    }
                }
            } catch (Exception e) {
                log.error("根据税收分类编码查询发票异常",e);
            }
        }
        // 更新非商税率、税码
        updateNoneTaxCode(tXfNoneBusinessUploadDetailEntity, successEntity, recordInvoice);
        noneBusinessService.updateById(successEntity);
        map.put("remark", invoiceMain.getRemark());
        map.put("detailYesorno", "1");
        map.put("invoiceNo", invoiceMain.getInvoiceNo());
        map.put("invoiceCode", invoiceMain.getInvoiceCode());
        map.put("invoiceAmount", invoiceMain.getAmountWithoutTax());
        map.put("invoiceDate", invoiceMain.getPaperDrewDate());
        map.put("totalAmount", invoiceMain.getAmountWithTax());
        map.put("taxAmount", invoiceMain.getTaxAmount());
        map.put("taxRate", invoiceDetails.get(0).getTaxRate());
        if (taxRateCount > 1) {
            map.put("taxRate", null);
        }
        map.put("invoiceType", InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode()));
        map.put("gfTaxno", invoiceMain.getPurchaserTaxNo());
        map.put("gfAdress", invoiceMain.getPurchaserAddrTel());
        map.put("gfBank", invoiceMain.getPurchaserBankInfo());
        map.put("xfAdress", invoiceMain.getSellerAddrTel());
        map.put("xfBank", invoiceMain.getSellerBankInfo());
        map.put("checkNo", invoiceMain.getCheckCode());
        map.put("xfName", invoiceMain.getSellerName());
        map.put("xfTaxNo", invoiceMain.getSellerTaxNo());
        map.put("cipherText", invoiceMain.getCipherText());
        map.put("goodsListFlag", invoiceMain.getGoodsListFlag());
        map.put("invoiceStatus", convertStatus(invoiceMain.getStatus(), invoiceMain.getRedFlag()));
        //非商发票不进手工认证，提交后才进入手工认证
        map.put("noDeduction", '1');
        // 底账来源  0-采集 1-查验 2-录入
        map.put("sourceSystem", "1");
        map.put("xfAddressAndPhone", invoiceMain.getSellerAddrTel());
        map.put("xfBankAndNo", invoiceMain.getSellerBankInfo());
        map.put("gfAddressAndPhone", invoiceMain.getPurchaserAddrTel());
        map.put("gfBankAndNo", invoiceMain.getPurchaserBankInfo());
        map.put("detailYesorno", "1");
        map.put("qs_status", "1");
        //CpyStatus 成平油标志 0-非成品油发票 1-成品油发票
        map.put("isOil", "1".equals(invoiceMain.getCpyStatus())?1:0);
        if (CollectionUtils.isEmpty(list1)) {
            if ("04".equals(CommonUtil.getFplx((String) invoiceMain.getInvoiceCode()))) {
                electronicInvoiceDao.saveInvoicePP(map);
            } else {
                this.electronicInvoiceDao.saveInvoice(map);
            }
            // 新增发票情况下才会将明细入库
            saveOrUpdateRecordInvoiceDetail(invoiceDetails, invoiceMain.getInvoiceNo(), invoiceMain.getInvoiceCode());
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
     * 更新非商税率、税码
     *
     * @param tXfNoneBusinessUploadDetailEntity
     * @param successEntity
     * @param recordInvoice
     */
    public void updateNoneTaxCode(TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity, TXfNoneBusinessUploadDetailEntity successEntity,
                                   TDxRecordInvoiceEntity recordInvoice) {
        if (recordInvoice.getTaxRate() == null) {
            return;
        }
        try {
            List<TXfNoneBusinessUploadDetailTaxCodeDto> taxCodeList = tXfNoneBusinessUploadDetailDao.queryTaxCodeList();
            String taxCodeDictKey = tXfNoneBusinessUploadDetailEntity.getBussinessType() + "_" + recordInvoice.getTaxRate().intValue();
            String taxCode =
                    taxCodeList.stream().filter(s -> s.getValue().equals(taxCodeDictKey)).map(TXfNoneBusinessUploadDetailTaxCodeDto::getLabel).distinct().collect(Collectors.joining(
                            ""));
            successEntity.setTaxCode(taxCode);
            successEntity.setTaxRate(recordInvoice.getTaxRate().intValue() + "");
        } catch (Exception e) {
            log.error("error={}", e);
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
        electronicUploadRecordDetailEntity.setReason(message);
        electronicUploadRecordDetailEntity.setStatus(0);
        electronicUploadRecordDetailService.updateById(electronicUploadRecordDetailEntity);
        electronicUploadRecordService.increaseFailure(electronicUploadRecordDetailEntity.getBatchNo());
    }

    void verifySucceed(VerificationBack verificationBack, SealedMessage.Header header, TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity) {
        final InvoiceMain invoiceMain = verificationBack.getResult().getInvoiceMain();
        final List<InvoiceDetail> invoiceDetails = verificationBack.getResult().getInvoiceDetails();

        TXfElecUploadRecordEntity recordEntity = this.electronicUploadRecordService.getByBatchNo(electronicUploadRecordDetailEntity.getBatchNo());
        List<Supplier<Boolean>> successSuppliers = new ArrayList<>();

        final OrgEntity orgEntity = this.electronicInvoiceDao.selectGfByJvCode(recordEntity.getJvCode());
        electronicUploadRecordDetailEntity.setInvoiceNo(invoiceMain.getInvoiceNo());
        electronicUploadRecordDetailEntity.setInvoiceCode(invoiceMain.getInvoiceCode());
        electronicUploadRecordDetailEntity.setStatus(1);
        //校验购销对
        validateOrg(invoiceMain, recordEntity, orgEntity);
        //校验税率
        validateTax(invoiceMain, invoiceDetails, electronicUploadRecordDetailEntity);
        if (StringUtils.isNotEmpty(electronicUploadRecordDetailEntity.getSettlementNo())) {
            QueryWrapper<TXfSettlementEntity> settlementWrapper = new QueryWrapper<>();
            settlementWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, electronicUploadRecordDetailEntity.getSettlementNo());
            TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(settlementWrapper);
            if (tXfSettlementEntity == null) {
                throw new EnhanceRuntimeException("未找到对应的结算单");
            }
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
        }
        String invoiceType = InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode());
        invoiceMain.setInvoiceType(invoiceType);

        //从备注里截取红字信息编号
        TDxRecordInvoiceEntity recordInvoice = new TDxRecordInvoiceEntity();
        if (new BigDecimal(invoiceMain.getAmountWithoutTax()).compareTo(BigDecimal.ZERO) < 0 && new BigDecimal(invoiceDetails.get(0).getTaxRate()).compareTo(BigDecimal.ZERO) != 0 &&
                (invoiceType.equals(InvoiceTypeEnum.SPECIAL_INVOICE.getValue()) || invoiceType.equals(InvoiceTypeEnum.E_SPECIAL_INVOICE.getValue()))) {
            Matcher matcher = RED_NOTIFICATION_NO_P.matcher(invoiceMain.getRemark().replaceAll("[\r\n]", ""));
            if (matcher.find()) {
                recordInvoice.setRedNoticeNumber(matcher.group(1));
            } else {
                log.warn("发票回填--解析红字信息编号失败！[{}]-[{}]", invoiceMain.getInvoiceNo(), invoiceMain.getInvoiceCode());
                throw new EnhanceRuntimeException("解析红字信息编号失败");
            }
        }
        recordInvoice.setVenderid(recordEntity.getVendorId());
        recordInvoice.setJvcode(recordEntity.getJvCode());
        recordInvoice.setInvoiceCode(invoiceMain.getInvoiceCode());
        recordInvoice.setInvoiceNo(invoiceMain.getInvoiceNo());
        recordInvoice.setInvoiceAmount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
        recordInvoice.setDkInvoiceamount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
        recordInvoice.setInvoiceDate(DateUtils.convertStringToDate(invoiceMain.getPaperDrewDate()));
        recordInvoice.setTotalAmount(new BigDecimal(invoiceMain.getAmountWithTax()));
        recordInvoice.setTaxAmount(new BigDecimal(invoiceMain.getTaxAmount()));
        recordInvoice.setTaxRate(new BigDecimal(invoiceDetails.get(0).getTaxRate()));
        long taxRateCount = invoiceDetails.stream().map(InvoiceDetail::getTaxRate).distinct().count();
        if (taxRateCount > 1) {
            recordInvoice.setTaxRate(null);
        }
        recordInvoice.setInvoiceType(invoiceMain.getInvoiceType());
        recordInvoice.setGfName(invoiceMain.getPurchaserName());
        recordInvoice.setGfTaxNo(invoiceMain.getPurchaserTaxNo());
        recordInvoice.setGfAddressAndPhone(invoiceMain.getPurchaserAddrTel());
        recordInvoice.setGfBankAndNo(invoiceMain.getPurchaserBankInfo());
        recordInvoice.setCheckCode(invoiceMain.getCheckCode());
        recordInvoice.setXfName(invoiceMain.getSellerName());
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
		// 加if判断是满足：WALMART-3368，WALMART-3371 ,WALMART-3411 begin
		if (electronicUploadRecordDetailEntity.getBusinessType() != null && 
				(electronicUploadRecordDetailEntity.getBusinessType() == 0 || electronicUploadRecordDetailEntity.getBusinessType() == 1)) {//红冲,换票
			recordInvoice.setFlowType("");
		}
		// 加if判断是满足：WALMART-3368，WALMART-3371,WALMART-3411 end

        recordInvoice.setTpStatus("0");
        recordInvoice.setIsDel(IsDealEnum.NO.getValue());
        recordInvoice.setUuid(Objects.toString(invoiceMain.getInvoiceCode(), "") + invoiceMain.getInvoiceNo());
        recordInvoice.setCreateDate(new Date());
        //特殊发票需要打上标识
        if (!CollectionUtils.isEmpty(invoiceDetails)) {
            try {
                String goodsTaxNos =invoiceDetails.stream().map(InvoiceDetail::getGoodsTaxNo).distinct().collect(Collectors.joining(","));
                JSONObject params = new JSONObject();
                params.put("goodsTaxNo",goodsTaxNos);
                log.info("根据税收分类编码查询发票{}",goodsTaxNos);
                String response = HttpClientUtils.postJson(commonApiProperties.getBaseHost()+commonApiProperties.getInvoiceTaxMappingUrl(),params.toJSONString());
                log.info("根据税收分类编码查询发票返回{}",response);
                List<TInvoiceTaxMappingEntity> invoiceTaxMappingList = JSON.parseArray(response, TInvoiceTaxMappingEntity.class);
                if(!CollectionUtils.isEmpty(invoiceTaxMappingList)){
                    recordInvoice.setSpecialFlag(invoiceTaxMappingList.get(0).getInvoiceType());
                    if(StringUtils.isEmpty(invoiceMain.getRemark())){
                        log.info("特殊发票备注栏为空{}",invoiceMain.getInvoiceNo()+invoiceMain.getInvoiceCode());
                    }
                }
            } catch (Exception e) {
                log.error("根据税收分类编码查询发票异常",e);
            }
        }
        //CpyStatus 成平油标志 0-非成品油发票 1-成品油发票
        recordInvoice.setIsOil("1".equals(invoiceMain.getCpyStatus())?1:0);
        //电子发票改为签收状态
        if (InvoiceTypeEnum.isElectronic(invoiceType)) {
            recordInvoice.setQsStatus("1");
            recordInvoice.setQsDate(new Date());
            recordInvoice.setQsType("5");
            //只有电票才直接插入扫描表
            this.saveOrUpdateInvoice(recordInvoice);
        }
        successSuppliers.add(() -> {
                    //结果存储-记录表
                    //保存发票号码代码到上传详情
                    electronicUploadRecordDetailService.updateById(electronicUploadRecordDetailEntity);
                    //成功计数
                    electronicUploadRecordService.increaseSucceed(electronicUploadRecordDetailEntity.getBatchNo());
                    if (electronicUploadRecordDetailEntity.getFileType() != null) {
                        invoiceFileService.save(electronicUploadRecordDetailEntity, invoiceMain);
                    }
                    return true;
                }
        );
        int result = this.saveOrUpdateRecordInvoice(recordInvoice);

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

    private void validateTax(InvoiceMain invoiceMain, List<InvoiceDetail> invoiceDetails, TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity) {
        final BigDecimal taxAmountSum = invoiceDetails.stream().map(x -> {
                    BigDecimal taxRate = new BigDecimal(x.getTaxRate());
                    return taxRate.compareTo(BigDecimal.ONE) >= 0 ? taxRate.movePointLeft(2).multiply(new BigDecimal(x.getAmountWithoutTax())) : taxRate.multiply(new BigDecimal(x.getAmountWithoutTax()));
                }
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
        //如果是用于红票上传和红票蓝冲，不校验税差
        if (electronicUploadRecordDetailEntity.getBusinessType() != null && electronicUploadRecordDetailEntity.getBusinessType() == 0) {
            return;
        }
        if (BigDecimal.ZERO.compareTo(new BigDecimal(invoiceMain.getAmountWithTax())) > 0) {
            return;
        }
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
        wrapper.eq(TDxRecordInvoiceEntity.UUID, recordInvoice.getUuid());
        TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectOne(wrapper);
        int result = 0;
        try {
            //判断uuid是否存在
            if (entity == null) {
                //不存在录入
                flag = tDxRecordInvoiceDao.insert(recordInvoice) > 0;
            } else {
                result = 1;
                //存在数据
                TDxRecordInvoiceEntity newEntity = new TDxRecordInvoiceEntity();
                BeanUtil.copyProperties(recordInvoice, newEntity);
                newEntity.setId(entity.getId());
                newEntity.setIsDel(IsDealEnum.NO.getValue());
                newEntity.setLastUpdateDate(new Date());
                //加if判断是满足：WALMART-3368，WALMART-3371 ,WALMART-3411 begin
                if(StringUtils.isNotBlank(entity.getFlowType())) {
                	newEntity.setFlowType(entity.getFlowType());
                }
              //加if判断是满足：WALMART-3368，WALMART-3371 ,WALMART-3411 end
                tDxRecordInvoiceDao.updateById(newEntity);
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
    public void saveOrUpdateRecordInvoiceDetail(List<InvoiceDetail> details, String invoiceNo, String invoiceCode) {
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
                detail.setGoodsNum(x.getGoodsTaxNo());
                //过滤发票明细 "(详见销货清单)", "（详见销货清单）", "(详见销货清单）", "（详见销货清单)", "原价合计", "折扣额合计"
                if (!StringUtils.equalsAnyIgnoreCase(x.getCargoName(), "(详见销货清单)", "（详见销货清单）", "(详见销货清单）", "（详见销货清单)", "原价合计", "折扣额合计")) {
                    recordDetails.add(detail);
                }
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
        BeanUtil.copyProperties(recordInvoiceEntity, entity);
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
                entity.setId(tDxInvoiceEntity.getId());
                tDxInvoiceDao.updateById(entity);
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
        if (Objects.equals(redFlag, InvoiceMain.ALREADY_RED) || Objects.equals(redFlag, InvoiceMain.ALREADY_ALL_RED)) {
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