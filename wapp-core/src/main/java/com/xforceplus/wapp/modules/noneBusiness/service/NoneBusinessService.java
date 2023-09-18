package com.xforceplus.wapp.modules.noneBusiness.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.evat.common.constant.consist.CommonApiProperties;
import com.xforceplus.evat.common.constant.enums.*;
import com.xforceplus.evat.common.domain.JsonResult;
import com.xforceplus.evat.common.domain.file.FileStoreRequest;
import com.xforceplus.evat.common.domain.file.FileStoreResponse;
import com.xforceplus.evat.common.utils.CommonUtils;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.AuthStatusEnum;
import com.xforceplus.wapp.enums.BusinessTypeExportEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.backfill.model.*;
import com.xforceplus.wapp.modules.backfill.service.*;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportSizeDto;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.noneBusiness.convert.NoneBusinessConverter;
import com.xforceplus.wapp.modules.noneBusiness.dto.*;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.xforceapi.HttpClientUtils;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.repository.dao.TXfNoneBusinessUploadDetailDao;
import com.xforceplus.wapp.repository.daoExt.ElectronicInvoiceDao;
import com.xforceplus.wapp.repository.daoExt.MatchDao;
import com.xforceplus.wapp.repository.daoExt.XfRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 非商业务逻辑
 */
@Service
@Slf4j
public class NoneBusinessService extends ServiceImpl<TXfNoneBusinessUploadDetailDao, TXfNoneBusinessUploadDetailEntity> {
    @Autowired
    private BackFillService backFillService;

    @Autowired
    private FileService fileService;
    @Autowired
    private DiscernService discernService;
    @Value("${wapp.integration.customer-no}")
    private String customerNo;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private ActiveMqProducer activeMqProducer;

    @Value("${activemq.queue-name.export-request}")
    private String exportQueue;
    @Autowired
    private TXfNoneBusinessUploadDetailDao tXfNoneBusinessUploadDetailDao;
    @Autowired
    private XfRecordInvoiceDao recordInvoiceDao;

    @Value("${wapp.export.tmp}")
    private String tmp;

    @Autowired
    private ElectronicInvoiceDao electronicInvoiceDao;
    @Autowired
    private NoneBusinessConverter noneBusinessConverter;
    @Autowired
    private IDSequence idSequence;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RecordInvoiceService recordInvoiceService;
    @Autowired
    private TDxRecordInvoiceDetailDao tDxRecordInvoiceDetailDao;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;
    @Autowired
    private CommonApiProperties commonApiProperties;
    @Autowired
    private MatchDao matchDao;

    private static String KEY = "NOBUSINESS_SIGN_";

    public void parseOfdFile(List<byte[]> ofd, TXfNoneBusinessUploadDetailEntity entity) {

        List<TXfNoneBusinessUploadDetailEntity> list = new ArrayList<>();
        ofd.stream().forEach(ofdEntity -> {
            TXfNoneBusinessUploadDetailEntity addEntity = new TXfNoneBusinessUploadDetailEntity();
            BeanUtil.copyProperties(entity, addEntity);
            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString());
            fileName.append(".");
            fileName.append(Constants.SUFFIX_OF_OFD);
            String uploadResult = null;
            try {
                //上传源文件到ftp服务器
                uploadResult = fileService.uploadFile(ofdEntity, fileName.toString(), "noneBusiness");
            } catch (IOException ex) {
                log.error("非商上传电票到文件服务器失败:{}", ex);
            }
            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
            UploadFileResultData data = uploadFileResult.getData();
            addEntity.setFileType(String.valueOf(Constants.FILE_TYPE_OFD));
            addEntity.setSourceUploadId(data.getUploadId());
            addEntity.setCreateUser(UserUtil.getLoginName());
            addEntity.setSourceUploadPath(data.getUploadPath());
            addEntity.setCreateTime(new Date());
            addEntity.setUpdateTime(new Date());
            addEntity.setUpdateUser(UserUtil.getLoginName());
            //发送验签
            OfdResponse response = backFillService.signOfd(ofdEntity, entity.getBussinessNo());
            //验签成功
            if (response.isOk()) {
                addEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
                final InvoiceMain invoiceMain = response.getResult().getInvoiceMain();
                if (invoiceMain != null && ("qs".equalsIgnoreCase(invoiceMain.getInvoiceType()) || "qc".equalsIgnoreCase(invoiceMain.getInvoiceType()))) {
                    addEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                    addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
                    addEntity.setReason("全电发票必须上传xml文件，请重新上传");
                    this.save(addEntity);
                    return;
                }
                if (StringUtils.isNotEmpty(response.getResult().getImageUrl())) {
                    String base64 = Base64.getEncoder().encodeToString(response.getResult().getImageUrl().getBytes());
                    redisTemplate.opsForValue().set(KEY + invoiceMain.getInvoiceCode() + invoiceMain.getInvoiceNo(), base64, 10, TimeUnit.MINUTES);
                }
                VerificationRequest verificationRequest = new VerificationRequest();
                verificationRequest.setAmount(invoiceMain.getAmountWithoutTax());
                verificationRequest.setCheckCode(invoiceMain.getCheckCode());
                verificationRequest.setCustomerNo(customerNo);
                verificationRequest.setInvoiceCode(invoiceMain.getInvoiceCode());
                verificationRequest.setInvoiceNo(invoiceMain.getInvoiceNo());
                verificationRequest.setPaperDrewDate(invoiceMain.getPaperDrewDate());
                VerificationResponse verificationResponse = verificationService.verify(verificationRequest);
                if (verificationResponse.isOK()) {
                    addEntity.setXfVerifyTaskId(verificationResponse.getResult());
                    addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
                } else {
                    addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
                }
                this.save(addEntity);
            } else {
                addEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
                addEntity.setRemark(response.getMessage());
                this.save(addEntity);
            }
        });
    }

    public TXfNoneBusinessUploadDetailEntity parseFile(MultipartFile multipartFile, TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity) {
        try {
            log.info("开始处理,batchNo={},FileName={}", tXfNoneBusinessUploadDetailEntity.getBatchNo(), multipartFile.getOriginalFilename());
            HashMap<String, String> map = new HashMap<>();
            map.put("systemOrig", SystemOrigEnum.WALMART_ENHANCE_NONEBUS.getSystemOrig());
            map.put("storageType", StorageTypeEnum.SAVE.getStorageType());
            // 源文件识别验真
            int fileType = -1;
            String suffix = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            String reqUrl = commonApiProperties.getBaseHost();
            String fileEncode = org.apache.commons.codec.binary.Base64.encodeBase64String(multipartFile.getBytes());
            if (Constants.SUFFIX_OF_OFD.equals(suffix)) {
                reqUrl += commonApiProperties.getOfdVerifyUrl();
                map.put("ofdEncode", fileEncode);
                fileType = Constants.FILE_TYPE_OFD;
            } else if (Constants.SUFFIX_OF_PDF.equals(suffix)) {
                reqUrl += commonApiProperties.getPdfSyncVerifyUrl();
                map.put("file", fileEncode);
                fileType = Constants.FILE_TYPE_PDF;
            } else if (Constants.SUFFIX_OF_XML.equals(suffix)) {
                reqUrl += commonApiProperties.getXmlVerifyUrl();
                map.put("xmlEncode", fileEncode);
                fileType = Constants.FILE_TYPE_XML;
            }
            String invoiceResultStr = HttpClientUtils.postJson2(reqUrl, JSONObject.toJSONString(map));
            if (StringUtils.isBlank(invoiceResultStr)) {
                tXfNoneBusinessUploadDetailEntity.setReason("验真失败：请求超时。");
                return tXfNoneBusinessUploadDetailEntity;
            }
            JsonResult<com.xforceplus.evat.common.domain.verify.VerificationResponse.VerificationResult> jsonResult = JSON.parseObject(invoiceResultStr, new TypeReference<JsonResult<com.xforceplus.evat.common.domain.verify.VerificationResponse.VerificationResult>>() {
            });
            tXfNoneBusinessUploadDetailEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
            tXfNoneBusinessUploadDetailEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);

            // 1. 验真失败
            if (jsonResult.isFail()) {
                log.info("验真失败:{}", jsonResult.getMessage());
                tXfNoneBusinessUploadDetailEntity.setReason(jsonResult.getMessage());
                return tXfNoneBusinessUploadDetailEntity;
            }

            com.xforceplus.evat.common.domain.verify.VerificationResponse.VerificationResult verificationResult
                    = jsonResult.getData();
            log.info("验真结果={}", JSON.toJSONString(verificationResult));
            com.xforceplus.evat.common.domain.verify.InvoiceMain invoiceMain = verificationResult.getInvoiceMain();
            tXfNoneBusinessUploadDetailEntity.setInvoiceCode(invoiceMain.getInvoiceCode());
            tXfNoneBusinessUploadDetailEntity.setInvoiceNo(invoiceMain.getInvoiceNo());
            tXfNoneBusinessUploadDetailEntity.setInvoiceRemark(invoiceMain.getRemark());
            tXfNoneBusinessUploadDetailEntity.setInvoiceDate(invoiceMain.getPaperDrewDate());
            // 2. 业务校验
            // 2.1 OFD文件校验是否为全电类型
            if (Constants.SUFFIX_OF_OFD.equals(suffix) && InvoiceTypeEnum.qdInvoices().contains(invoiceMain.getInvoiceType())) {
                tXfNoneBusinessUploadDetailEntity.setReason("全电发票必须上传xml文件，请重新上传。");
                return tXfNoneBusinessUploadDetailEntity;
            }
            // 2.2 重复发票上传校验
            List<TXfNoneBusinessUploadDetailEntity> list = getInvoice(tXfNoneBusinessUploadDetailEntity.getInvoiceNo()
                    , tXfNoneBusinessUploadDetailEntity.getInvoiceCode());
            long unNoCount = Optional.ofNullable(list).orElse(Collections.emptyList()).stream().filter(s -> s.getSubmitFlag().equals(Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG)).count();
            if (unNoCount > 0) {
                tXfNoneBusinessUploadDetailEntity.setReason("待提交已存在该发票，请删除后操作。");
                return tXfNoneBusinessUploadDetailEntity;
            }

            // 2.3 FlowType校验
            Optional<TDxRecordInvoiceEntity> tDxRecordInvoiceEntity = new LambdaQueryChainWrapper<>(recordInvoiceService.getBaseMapper())
                    .eq(StringUtils.isNotBlank(invoiceMain.getInvoiceCode()), TDxRecordInvoiceEntity::getInvoiceCode, invoiceMain.getInvoiceCode())
                    .eq(TDxRecordInvoiceEntity::getInvoiceNo, invoiceMain.getInvoiceNo()).oneOpt();
            if (tDxRecordInvoiceEntity.isPresent()) {
                String flowType = tDxRecordInvoiceEntity.get().getFlowType();
                if (StringUtils.isNotEmpty(flowType) && !FlowTypeEnum.FLOW_7.getCode().equals(flowType)) {
                    tXfNoneBusinessUploadDetailEntity.setReason("该发票不是非商发票");
                    return tXfNoneBusinessUploadDetailEntity;
                }
            }
            // 2.4 购方信息校验
            TAcOrgEntity purEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getPurchaserTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_WALMART);
            if (null == purEntity) {
                tXfNoneBusinessUploadDetailEntity.setReason("购方信息未维护");
                return tXfNoneBusinessUploadDetailEntity;
            }
            // TODO 文件处理
            // tXfNoneBusinessUploadDetailEntity.setSourceUploadId(data.getUploadId());
            // tXfNoneBusinessUploadDetailEntity.setSourceUploadPath(data.getUploadPath());
            // tXfNoneBusinessUploadDetailEntity.setUploadId(uploadFileImageResult.getData().getUploadId());
            // tXfNoneBusinessUploadDetailEntity.setUploadPath(uploadFileImageResult.getData().getUploadPath());

            tXfNoneBusinessUploadDetailEntity.setFileType(String.valueOf(fileType));
            tXfNoneBusinessUploadDetailEntity.setReason("上传成功。");
            // 业务逻辑处理
            handleData(tXfNoneBusinessUploadDetailEntity, verificationResult);
        } catch (IOException e) {
            tXfNoneBusinessUploadDetailEntity.setReason(e.getMessage());
        }
        return tXfNoneBusinessUploadDetailEntity;
    }

    public void handleData(TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity, com.xforceplus.evat.common.domain.verify.VerificationResponse.VerificationResult verificationResult) {
        com.xforceplus.evat.common.domain.verify.InvoiceMain invoiceMain = verificationResult.getInvoiceMain();
        List<com.xforceplus.evat.common.domain.verify.InvoiceDetail> invoiceDetails = verificationResult.getInvoiceDetails();
        // 1. 保存非商业务表
        tXfNoneBusinessUploadDetailEntity.setVerifyStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
        tXfNoneBusinessUploadDetailEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
        tXfNoneBusinessUploadDetailEntity.setCreateTime(new Date());
        tXfNoneBusinessUploadDetailEntity.setUpdateTime(new Date());
        // 1.1 获取非商税率、税码
        long taxRateCount = invoiceDetails.stream().map(com.xforceplus.evat.common.domain.verify.InvoiceDetail::getTaxRate).distinct().count();
        String taxRateStr = invoiceDetails.stream().map(com.xforceplus.evat.common.domain.verify.InvoiceDetail::getTaxRate).findFirst().orElse(null);
        if (taxRateCount > 1) {
            taxRateStr = null;
        }
        BigDecimal taxRate = CommonUtils.taxRateHandle(taxRateStr).orElse(null);
        if (taxRate != null) {
            List<TXfNoneBusinessUploadDetailTaxCodeDto> taxCodeList = tXfNoneBusinessUploadDetailDao.queryTaxCodeList();
            String taxCodeDictKey = tXfNoneBusinessUploadDetailEntity.getBussinessType() + "_" + taxRate.intValue();
            String taxCode =
                    taxCodeList.stream().filter(s -> s.getValue().equals(taxCodeDictKey)).map(TXfNoneBusinessUploadDetailTaxCodeDto::getLabel).distinct().collect(Collectors.joining(
                            ""));
            tXfNoneBusinessUploadDetailEntity.setTaxCode(taxCode);
            tXfNoneBusinessUploadDetailEntity.setTaxRate(taxRate.toPlainString());
        }

        // 1.2 处理源文件及预览文件
        FileStoreRequest fileStoreRequest = FileStoreRequest.builder()
                .invoiceNo(tXfNoneBusinessUploadDetailEntity.getInvoiceNo()).invoiceCode(tXfNoneBusinessUploadDetailEntity.getInvoiceCode())
                .paperDrewDate(tXfNoneBusinessUploadDetailEntity.getInvoiceDate())
                .fileType(XfFileType.INVOICE.getFileType()).fileName(tXfNoneBusinessUploadDetailEntity.getFileName()).genPreviewFile(true)
                .businessId(String.valueOf(tXfNoneBusinessUploadDetailEntity.getId())).businessType(BusinessTypeEnum.NONE_BUSINESS.getBusinessType())
                .venderId("noneBusiness").createUser(tXfNoneBusinessUploadDetailEntity.getCreateUser())
                .build();
        String reqUrl = commonApiProperties.getBaseHost() + commonApiProperties.getFileUploadV2Url();
        String reqResponse = HttpClientUtils.postJson(reqUrl, JSONObject.toJSONString(fileStoreRequest));
        JsonResult<FileStoreResponse> fileStoreResponseJsonResult = JSON.parseObject(reqResponse, new TypeReference<JsonResult<FileStoreResponse>>() {
        });
        if (fileStoreResponseJsonResult.isSuccess()) {
            FileStoreResponse fileStoreResponse = fileStoreResponseJsonResult.getData();
            tXfNoneBusinessUploadDetailEntity.setUploadPath(Optional.ofNullable(fileStoreResponse).map(FileStoreResponse::getPreviewFile).map(FileStoreResponse::getFilePath).orElse(""));
            tXfNoneBusinessUploadDetailEntity.setSourceUploadPath(Optional.ofNullable(fileStoreResponse).map(FileStoreResponse::getFilePath).orElse(""));
        }
        this.save(tXfNoneBusinessUploadDetailEntity);
        // 2. 保存扫描表
        saveTDxInvoice(invoiceMain);

        // 3. 保存底账表
        saveTDxRecordInvoice(verificationResult);


    }

    public void saveTDxRecordInvoice(com.xforceplus.evat.common.domain.verify.VerificationResponse.VerificationResult verificationResult) {
        com.xforceplus.evat.common.domain.verify.InvoiceMain invoiceMain = verificationResult.getInvoiceMain();
        List<com.xforceplus.evat.common.domain.verify.InvoiceDetail> invoiceDetails = verificationResult.getInvoiceDetails();
        TAcOrgEntity purEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getPurchaserTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_WALMART);
        TAcOrgEntity sellerEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getSellerTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_SUPPLIER);

        Map<String, Object> map = new HashMap<>();
        String uuid = Objects.toString(invoiceMain.getInvoiceCode(), "") + invoiceMain.getInvoiceNo();
        map.put("uuid", uuid);
        if (null != sellerEntity) {
            map.put("venderid", sellerEntity.getOrgCode());
            map.put("xfName", sellerEntity.getOrgName());
        }
        if (null != purEntity) {
            map.put("jvcode", purEntity.getOrgCode());
            map.put("gfName", purEntity.getOrgName());
            map.put("companyCode", purEntity.getCompanyCode());
        }
        map.put("remark", invoiceMain.getRemark());
        map.put("detailYesorno", "1");
        map.put("invoiceNo", invoiceMain.getInvoiceNo());
        map.put("invoiceCode", invoiceMain.getInvoiceCode());
        map.put("invoiceAmount", invoiceMain.getAmountWithoutTax());
        map.put("invoiceDate", invoiceMain.getPaperDrewDate());
        map.put("totalAmount", invoiceMain.getAmountWithTax());
        map.put("taxAmount", invoiceMain.getTaxAmount());
        map.put("taxRate", invoiceDetails.get(0).getTaxRate());
        long taxRateCount = invoiceDetails.stream().map(com.xforceplus.evat.common.domain.verify.InvoiceDetail::getTaxRate).distinct().count();
        if (taxRateCount > 1) {
            map.put("taxRate", null);
        }
        map.put("invoiceType", invoiceMain.getInvoiceType());
        map.put("gfTaxno", invoiceMain.getPurchaserTaxNo());
        map.put("gfAdress", invoiceMain.getPurchaserAddrTel());
        map.put("gfBank", invoiceMain.getPurchaserBankInfo());
        map.put("xfAdress", invoiceMain.getSellerAddrTel());
        map.put("xfBank", invoiceMain.getSellerBankInfo());
        map.put("checkNo", invoiceMain.getCheckCode());
        map.put("xfName", invoiceMain.getSellerName());
        map.put("xfTaxNo", invoiceMain.getSellerTaxNo());
        map.put("goodsListFlag", invoiceMain.getGoodsListFlag());
        map.put("invoiceStatus", convertStatus(invoiceMain.getStatus(), invoiceMain.getRedFlag()));
        //非商发票不进手工认证，提交后才进入手工认证
        map.put("noDeduction", '1');
        // 底账来源  0-采集 1-查验 2-录入
        map.put("sourceSystem", SourceSystemEnum.DZ_SOURCE_VERIFY.getCode());
        map.put("xfAddressAndPhone", invoiceMain.getSellerAddrTel());
        map.put("xfBankAndNo", invoiceMain.getSellerBankInfo());
        map.put("gfAddressAndPhone", invoiceMain.getPurchaserAddrTel());
        map.put("gfBankAndNo", invoiceMain.getPurchaserBankInfo());
        map.put("detailYesorno", "1");
        map.put("qs_status", "1");
        map.put("flowType", FlowTypeEnum.FLOW_7.getCode());
        // 更新
        Optional<TDxRecordInvoiceEntity> tDxRecordInvoiceEntity = new LambdaQueryChainWrapper<>(recordInvoiceService.getBaseMapper())
                .eq(StringUtils.isNotBlank(invoiceMain.getInvoiceCode()), TDxRecordInvoiceEntity::getInvoiceCode, invoiceMain.getInvoiceCode())
                .eq(TDxRecordInvoiceEntity::getInvoiceNo, invoiceMain.getInvoiceNo()).oneOpt();
        if (tDxRecordInvoiceEntity.isPresent()) {
            map.put("id", tDxRecordInvoiceEntity.get().getId());
            if ("04".equals(CommonUtil.getFplx((String) invoiceMain.getInvoiceCode()))) {
                matchDao.allUpdatePP(map);
            } else {
                matchDao.allUpdate(map);
            }
            return;
        }
        // 新增
        if ("04".equals(CommonUtil.getFplx((String) invoiceMain.getInvoiceCode()))) {
            electronicInvoiceDao.saveInvoicePP(map);
        } else {
            this.electronicInvoiceDao.saveInvoice(map);
        }
        // 新增发票情况下才会将明细入库
        List<InvoiceDetail> details = invoiceDetails.stream()
                .map(e -> {
                    InvoiceDetail d = new InvoiceDetail();
                    BeanUtils.copyProperties(e, d);
                    return d;
                }).collect(Collectors.toList());
        saveOrUpdateRecordInvoiceDetail(details, invoiceMain.getInvoiceNo(), invoiceMain.getInvoiceCode());
    }

    public void saveOrUpdateRecordInvoiceDetail(List<InvoiceDetail> details, String invoiceNo, String invoiceCode) {
        if (!org.springframework.util.CollectionUtils.isEmpty(details)) {
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
     * 保存扫描表
     *
     * @param invoiceMain
     */
    public void saveTDxInvoice(com.xforceplus.evat.common.domain.verify.InvoiceMain invoiceMain) {
        TAcOrgEntity purEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getPurchaserTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_WALMART);
        TAcOrgEntity sellerEntity = companyService.getOrgInfoByTaxNo(invoiceMain.getSellerTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_SUPPLIER);
        TDxInvoiceEntity tDxInvoice = new TDxInvoiceEntity();
        tDxInvoice.setInvoiceCode(invoiceMain.getInvoiceCode());
        tDxInvoice.setInvoiceNo(invoiceMain.getInvoiceNo());
        tDxInvoice.setInvoiceAmount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
        tDxInvoice.setInvoiceDate(DateUtils.convertStringToDate(invoiceMain.getPaperDrewDate()));
        tDxInvoice.setTotalAmount(new BigDecimal(invoiceMain.getAmountWithTax()));
        tDxInvoice.setTaxAmount(new BigDecimal(invoiceMain.getTaxAmount()));
        tDxInvoice.setInvoiceType(InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode()));
        tDxInvoice.setGfName(invoiceMain.getPurchaserName());
        tDxInvoice.setGfTaxNo(invoiceMain.getPurchaserTaxNo());
        tDxInvoice.setCheckCode(invoiceMain.getCheckCode());
        tDxInvoice.setXfName(invoiceMain.getSellerName());
        tDxInvoice.setXfTaxNo(invoiceMain.getSellerTaxNo());
        tDxInvoice.setFlowType(FlowTypeEnum.FLOW_7.getCode());
        tDxInvoice.setUuid(Objects.toString(invoiceMain.getInvoiceCode(), "") + invoiceMain.getInvoiceNo());
        tDxInvoice.setCreateDate(new Date());
        //电子发票改为签收状态
        if (com.xforceplus.wapp.enums.InvoiceTypeEnum.isElectronic(InvoiceUtil.getInvoiceType(invoiceMain.getInvoiceType(), invoiceMain.getInvoiceCode()))) {
            //非商电票不进入直接认证，提交后才进入
            tDxInvoice.setQsStatus("0");
            tDxInvoice.setQsDate(new Date());
            tDxInvoice.setQsType("5");
        }
        if (null != sellerEntity) {
            tDxInvoice.setVenderid(sellerEntity.getOrgCode());
        }
        if (null != purEntity) {
            tDxInvoice.setCompanyCode(purEntity.getCompanyCode());
        }

        QueryWrapper<TDxInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxInvoiceEntity.UUID, tDxInvoice.getUuid());
        TDxInvoiceEntity _tDxInvoiceEntity = tDxInvoiceDao.selectOne(wrapper);
        if (_tDxInvoiceEntity == null || _tDxInvoiceEntity.getId() == null) {
            tDxInvoice.setBindyesorno("0");
            tDxInvoice.setPackyesorno("0");
            tDxInvoiceDao.insert(tDxInvoice);
        } else {
            tDxInvoice.setId(_tDxInvoiceEntity.getId());
            tDxInvoiceDao.updateById(tDxInvoice);
        }
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

    public void parsePdfFile(List<byte[]> pdf, TXfNoneBusinessUploadDetailEntity entity) {
        List<TXfNoneBusinessUploadDetailEntity> list = new ArrayList<>();
        pdf.stream().forEach(pdfEntity -> {
            TXfNoneBusinessUploadDetailEntity addEntity = new TXfNoneBusinessUploadDetailEntity();
            BeanUtil.copyProperties(entity, addEntity);
            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString());
            fileName.append(".");
            fileName.append(Constants.SUFFIX_OF_PDF);
            String uploadResult = null;
            try {
                //上传源文件到ftp服务器
                uploadResult = fileService.uploadFile(pdfEntity, fileName.toString(), "noneBusiness");
            } catch (IOException ex) {
                log.error("非商上传电票到文件服务器失败:{}", ex);
            }
            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
            UploadFileResultData data = uploadFileResult.getData();
            addEntity.setFileType(String.valueOf(Constants.FILE_TYPE_PDF));
            addEntity.setSourceUploadId(data.getUploadId());
            addEntity.setCreateUser(UserUtil.getLoginName());
            addEntity.setSourceUploadPath(data.getUploadPath());
            addEntity.setUploadId(data.getUploadId());
            addEntity.setUploadPath(data.getUploadPath());
            addEntity.setCreateTime(new Date());
            addEntity.setUpdateTime(new Date());
            addEntity.setUpdateUser(UserUtil.getLoginName());
            //发送识别
            String taskId = discernService.discern(pdfEntity);
            //发送识别成功
            if (StringUtils.isNotEmpty(taskId)) {
                addEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_DOING);
                addEntity.setXfDiscernTaskId(taskId);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
            } else {
                addEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
            }
            list.add(addEntity);
            this.save(addEntity);

        });


    }

    public void parseXmlFile(List<byte[]> xml, TXfNoneBusinessUploadDetailEntity entity) {
        for (byte[] xmlByte : xml) {
            TXfNoneBusinessUploadDetailEntity addEntity = new TXfNoneBusinessUploadDetailEntity();
            BeanUtil.copyProperties(entity, addEntity);
            StringBuilder fileName = new StringBuilder();
            fileName.append(UUID.randomUUID());
            fileName.append(".");
            fileName.append(Constants.SUFFIX_OF_XML);
            String uploadResult = null;
            try {
                //上传源文件到ftp服务器
                uploadResult = fileService.uploadFile(xmlByte, fileName.toString(), "noneBusiness");
            } catch (IOException ex) {
                log.error("非商上传电票到文件服务器失败:{}", ex.getMessage(), ex);
            }
            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
            UploadFileResultData data = uploadFileResult.getData();
            addEntity.setFileType(String.valueOf(Constants.FILE_TYPE_XML));
            addEntity.setSourceUploadId(data.getUploadId());
            addEntity.setCreateUser(UserUtil.getLoginName());
            addEntity.setSourceUploadPath(data.getUploadPath());
            addEntity.setCreateTime(new Date());
            addEntity.setUpdateTime(new Date());
            addEntity.setUpdateUser(UserUtil.getLoginName());
            addEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
            addEntity.setId(idSequence.nextId());
            addEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_DOING);
            addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
            this.save(addEntity);
            //解析xml
            verificationService.analysisAndVerify(xmlByte, entity.getBatchNo(), "noneBusiness", addEntity.getSourceUploadId(), taskId -> {
                addEntity.setXfVerifyTaskId(taskId);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
            }, err -> {
                addEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
                addEntity.setReason(err);
            });
            this.updateById(addEntity);
        }
    }

    public TXfNoneBusinessUploadDetailEntity getObjByVerifyTaskId(String verifyTaskId) {
        QueryWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfNoneBusinessUploadDetailEntity.XF_VERIFY_TASK_ID, verifyTaskId);
        return getOne(wrapper);
    }

    public TXfNoneBusinessUploadDetailEntity getBySourceUploadId(String uploadId) {
        QueryWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfNoneBusinessUploadDetailEntity.SOURCE_UPLOAD_ID, uploadId);
        return getOne(wrapper);
    }

    public TXfNoneBusinessUploadDetailEntity getObjByDiscernTaskId(String taskId) {
        QueryWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfNoneBusinessUploadDetailEntity.XF_DISCERN_TASK_ID, taskId);
        return getOne(wrapper);
    }

    public boolean updateById(TXfNoneBusinessUploadDetailEntity entity) {
        if (null == entity.getId()) {
            return false;
        }
        LambdaUpdateChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());

        if (null != entity.getId()) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getId, entity.getId());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceCode())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceCode, entity.getInvoiceCode());
        }
        if (StringUtils.isNotBlank(entity.getStoreNo())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getStoreNo, entity.getStoreNo());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceNo())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, entity.getInvoiceNo());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceDate())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceDate, entity.getInvoiceDate());
        }
        if (StringUtils.isNotBlank(entity.getVerifyStatus())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getVerifyStatus, entity.getVerifyStatus());
        }
        if (StringUtils.isNotBlank(entity.getXfVerifyTaskId())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getXfVerifyTaskId, entity.getXfVerifyTaskId());
        }
        if (StringUtils.isNotBlank(entity.getReason())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getReason, entity.getReason());
        }
        if (StringUtils.isNotBlank(entity.getOfdStatus())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getOfdStatus, entity.getOfdStatus());
        }
        if (StringUtils.isNotBlank(entity.getUploadId())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getUploadId, entity.getUploadId());
        }
        if (StringUtils.isNotBlank(entity.getUploadPath())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getUploadPath, entity.getUploadPath());
        }
        if (StringUtils.isNotBlank(entity.getSourceUploadId())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getSourceUploadId, entity.getSourceUploadId());
        }
        if (StringUtils.isNotBlank(entity.getSourceUploadPath())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getSourceUploadPath, entity.getSourceUploadPath());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceRemark())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceRemark, entity.getInvoiceRemark());
        }
        if (StringUtils.isNotBlank(entity.getTaxRate())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getTaxRate, entity.getTaxRate());
        }
        if (StringUtils.isNotBlank(entity.getTaxCode())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getTaxCode, entity.getTaxCode());
        }
        if (StringUtils.isNotBlank(entity.getGoodsName())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getGoodsName, entity.getGoodsName());
        }

        return wrapper.update();
    }

    public Page<TXfNoneBusinessUploadDetailDto> page(Long current, Long size, TXfNoneBusinessUploadQueryDto dto) {
        if ("0".equals(dto.getQueryType())) {
            dto.setCreateUser(UserUtil.getLoginName());
        }
        if ("0".equals(dto.getQueryType())) {
            dto.setCreateUser(UserUtil.getLoginName());
        }
//        if(StringUtils.isNotEmpty(dto.getStoreNo())&&dto.getStoreNo().startsWith("0")){
//            dto.setStoreNo(dto.getStoreNo().substring(1,dto.getStoreNo().length()));
//        }
//        if(StringUtils.isNotEmpty(dto.getInvoiceStoreNo())&&dto.getInvoiceStoreNo().startsWith("0")){
//            dto.setInvoiceStoreNo(dto.getInvoiceStoreNo().substring(1,dto.getInvoiceStoreNo().length()));
//        }

        //创建时间、入库 >> end
        String createTimeEnd = dto.getCreateDateEnd();
        if (StringUtils.isNotBlank(createTimeEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(createTimeEnd, 1);
            dto.setCreateDateEnd(format);
        }
        Page<TXfNoneBusinessUploadDetailDto> page = tXfNoneBusinessUploadDetailDao.list(new Page<>(current, size), dto);
        return page;
    }

    public List<TXfNoneBusinessUploadDetailDto> bulidTaxCode(List<TXfNoneBusinessUploadDetailDto> list) {
        List<TXfNoneBusinessUploadDetailTaxCodeDto> taxCodeList = tXfNoneBusinessUploadDetailDao.queryTaxCodeList();
        Map<String, String> map = new HashMap<>();
        log.info("taxCodeList size{}", taxCodeList.size());
        if (CollectionUtils.isNotEmpty(taxCodeList)) {
            taxCodeList.stream().forEach(e -> {
                log.info("key vaue{}{}", e.getValue(), e.getLabel());
                map.put(e.getValue(), e.getLabel());
            });

        }
        list.stream().forEach(e -> {
            if (StringUtils.isNotEmpty(e.getTaxRate())) {
                BigDecimal decimal = new BigDecimal(e.getTaxRate());
                log.info("taxcode key{}", decimal.toBigInteger().toString());
                e.setTaxCode(map.get(e.getBussinessType() + "_" + decimal.toBigInteger().toString()));
            }

        });
        return list;

    }

    public List<TXfNoneBusinessUploadDetailDto> noPaged(TXfNoneBusinessUploadQueryDto dto) {
        if ("0".equals(dto.getQueryType())) {
            dto.setCreateUser(UserUtil.getLoginName());
        }
        //创建时间、入库 >> end
        String createTimeEnd = dto.getCreateDateEnd();
        if (StringUtils.isNotBlank(createTimeEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(createTimeEnd, 1);
            dto.setCreateDateEnd(format);
        }
        return this.tXfNoneBusinessUploadDetailDao.list(dto);
    }

    public List<TXfNoneBusinessUploadDetailDto> getByIds(List<Long> ids) {
        List<List<Long>> idlists = ListUtils.partition(ids, 300);
        List<TXfNoneBusinessUploadDetailDto> detailList = new ArrayList<>();
        // 数据量过大会导致sql超长, 进行分段查询
        for (List<Long> idlist : idlists) {
            List<TXfNoneBusinessUploadDetailDto> byIds = tXfNoneBusinessUploadDetailDao.getByIds(idlist);
            detailList.addAll(byIds);
        }
        return detailList;
    }

    public void down(List<TXfNoneBusinessUploadDetailEntity> list, FileDownRequest request) {
        String path = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String ftpPath = ftpUtilService.pathprefix + path;
        log.info("文件ftp路径{}", ftpPath);
        final File tempDirectory = FileUtils.getTempDirectory();
        File file = FileUtils.getFile(tempDirectory, path);
        file.mkdir();
        String downLoadFileName = path + ".zip";

        List<TXfNoneBusinessUploadDetailEntity> downloadList = list.stream().filter(it -> {
            if (1 == request.getOfd() && it.getFileType().equals(String.valueOf(Constants.FILE_TYPE_OFD))) {
                return true;
            } else if (1 == request.getPdf() && it.getFileType().equals(String.valueOf(Constants.FILE_TYPE_PDF))) {
                return true;
            }
            return 1 == request.getXml() && it.getFileType().equals(String.valueOf(Constants.FILE_TYPE_XML));
        }).collect(Collectors.toList());

        for (TXfNoneBusinessUploadDetailEntity fileEntity : downloadList) {

            final byte[] bytes = fileService.downLoadFile4ByteArray(fileEntity.getSourceUploadPath());
            try {
                String suffix = null;
                if (fileEntity.getFileType().equals(String.valueOf(Constants.FILE_TYPE_OFD))) {
                    suffix = "." + Constants.SUFFIX_OF_OFD;
                } else if (fileEntity.getFileType().equals(String.valueOf(Constants.FILE_TYPE_XML))) {
                    suffix = "." + Constants.SUFFIX_OF_XML;
                } else {
                    suffix = "." + Constants.SUFFIX_OF_PDF;
                }
                if (StringUtils.isEmpty(fileEntity.getInvoiceNo()) || (fileEntity.getInvoiceNo().length() != 20 && StringUtils.isEmpty(fileEntity.getInvoiceCode()))) {
                    FileUtils.writeByteArrayToFile(FileUtils.getFile(file, "附件_" + path + suffix), bytes);
                } else {
                    FileUtils.writeByteArrayToFile(FileUtils.getFile(file, fileEntity.getInvoiceNo() + "-" + (StringUtils.isNotBlank(fileEntity.getInvoiceCode()) ?
                            fileEntity.getInvoiceCode() : "") + suffix), bytes);
                }

            } catch (IOException e) {
                log.error("临时文件存储失败:" + e.getMessage(), e);
            }
        }
        try {
            ZipUtil.zip(file.getPath() + ".zip", file);
            String s = exportCommonService.putFile(ftpPath, tempDirectory.getPath() + "/" + downLoadFileName, downLoadFileName);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto dto = new ExceptionReportExportDto();
            dto.setUserId(userId);
            dto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(dto.getUserId().toString());
            excelExportlogEntity.setUserName(dto.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + downLoadFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            dto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "非商电票源文件下载成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("下载文件打包失败:" + e.getMessage(), e);
            throw new RRException("下载文件打包失败，请重试");
        }
    }

    public R export(List<TXfNoneBusinessUploadDetailDto> resultList, ValidSubmitRequest request) {

        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), "非商数据导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        FileInputStream inputStream = null;
        try {
            //创建一个sheet
            File file = FileUtils.getFile(tmp + ftpPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            resultList.stream().forEach(e -> {
                if (StringUtils.isEmpty(e.getAuthStatus())) {
                    e.setAuthStatus(AuthStatusEnum.AUTH_STATUS_UN.getCode());
                }
            });
            File excl = FileUtils.getFile(file, excelFileName);
            EasyExcel.write(tmp + ftpPath + "/" + excelFileName, TXfNoneBusinessUploadExportDto.class).sheet("sheet1").doWrite(noneBusinessConverter.exportMap(resultList));
            //推送sftp
            //String ftpFilePath = ftpPath + "/" + excelFileName;
            inputStream = FileUtils.openInputStream(excl);
            ftpUtilService.uploadFile(ftpPath, excelFileName, inputStream);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "非商电票结果导出成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("导出异常:{}", e);
            return R.fail("导出异常");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return R.ok();
    }

    /**
     * 根据发表代码号码删除已经提交的发票信息
     *
     * @param invoiceNo
     * @param invoiceCode
     */
    public boolean deleteSubmitInvoice(String invoiceNo, String invoiceCode) {
        LambdaUpdateChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());
        if (StringUtils.isEmpty(invoiceNo)) {
            return false;
        }
        wrapper.eq(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, invoiceNo);
        wrapper.eq(StringUtils.isNoneBlank(invoiceCode), TXfNoneBusinessUploadDetailEntity::getInvoiceCode, invoiceCode);
        wrapper.eq(TXfNoneBusinessUploadDetailEntity::getSubmitFlag, Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
        return wrapper.remove();
    }

    public Optional<TXfNoneBusinessUploadDetailEntity> getSubmitInvoice(String invoiceNo, String invoiceCode) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, invoiceNo)
                .eq(StringUtils.isNoneBlank(invoiceCode), TXfNoneBusinessUploadDetailEntity::getInvoiceCode, invoiceCode)
                .eq(TXfNoneBusinessUploadDetailEntity::getSubmitFlag, Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG)
                .oneOpt();
    }

    public List<TXfNoneBusinessUploadDetailEntity> getInvoice(String invoiceNo, String invoiceCode) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, invoiceNo)
                .eq(StringUtils.isNoneBlank(invoiceCode), TXfNoneBusinessUploadDetailEntity::getInvoiceCode, invoiceCode)
                .list();
    }

    /**
     * 将发票信息改为可认证
     *
     * @param uuid
     */
    public void updateInvoiceInfo(List<String> uuid) {
        if (uuid != null && uuid.size() > 0) {
            electronicInvoiceDao.updateNoDeduction(uuid);
            electronicInvoiceDao.updateDxNoDeduction(uuid);
        }


    }

    public SpecialCompanyImportSizeDto queryImportData(MultipartFile file) throws IOException {
        SpecialCompanyImportSizeDto sizeDto = new SpecialCompanyImportSizeDto();
        NonbusinessImportCompanyImportListener listener = new NonbusinessImportCompanyImportListener();
        EasyExcel.read(file.getInputStream(), TXfNoneBusinessUploadImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
            sizeDto.setErrorMsg("未解析到数据");
            return sizeDto;
        }

        log.info("导入数据解析条数:{}", listener.getRows());
        List<TXfNoneBusinessUploadDetailEntity> updateList = new ArrayList<>();
        List<String> uuidList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listener.getValidInvoices())) {
            //过滤出税号+6d去重后的数据
            List<TXfNoneBusinessUploadImportDto> supplierCodeList = listener.getValidInvoices().stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getInvoiceNo() + f.getInvoiceCode()))), ArrayList::new)
            );
            for (TXfNoneBusinessUploadImportDto tXfNoneBusinessUploadImportDto : supplierCodeList) {
                TXfNoneBusinessUploadDetailEntity obj = getOne(
                        new QueryWrapper<TXfNoneBusinessUploadDetailEntity>()
                                .lambda()
                                .eq(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, tXfNoneBusinessUploadImportDto.getInvoiceNo())
                                .eq(StringUtils.isNoneBlank(tXfNoneBusinessUploadImportDto.getInvoiceCode()), TXfNoneBusinessUploadDetailEntity::getInvoiceCode,
                                        tXfNoneBusinessUploadImportDto.getInvoiceCode())
                                .eq(TXfNoneBusinessUploadDetailEntity::getSubmitFlag, Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG)
                );
                if (Objects.isNull(obj)) {
                    tXfNoneBusinessUploadImportDto.setErrorMessage("未找到对应的已提交发票记录");
                    listener.getInvalidInvoices().add(tXfNoneBusinessUploadImportDto);
                } else {
                    TXfNoneBusinessUploadDetailEntity entity = new TXfNoneBusinessUploadDetailEntity();
                    entity.setId(obj.getId());
                    entity.setVoucherNo(tXfNoneBusinessUploadImportDto.getVoucherNo());
                    entity.setEntryDate(tXfNoneBusinessUploadImportDto.getEntryDate());
                    entity.setUpdateTime(new Date());
                    entity.setUpdateUser(UserUtil.getLoginName());

                    // 当业务类型为“GNFR“，需要填写好“凭证号”、“入账日期”、“Taxcode”，发票才能去到直接认证模块
                    if (BusinessTypeExportEnum.BUSINESS_TYPE_GNFR.getCode().equals(tXfNoneBusinessUploadImportDto.getBussinessType())) {
                        if (StringUtils.isAnyEmpty(tXfNoneBusinessUploadImportDto.getVoucherNo(), tXfNoneBusinessUploadImportDto.getEntryDate(),
                                tXfNoneBusinessUploadImportDto.getTaxCode())) {
                            tXfNoneBusinessUploadImportDto.setErrorMessage("业务类型为GNFR，需填写凭证号、入账日期、税码;");
                            listener.getInvalidInvoices().add(tXfNoneBusinessUploadImportDto);
                        } else if (StringUtils.isNoneBlank(obj.getVoucherNo(), obj.getEntryDate(), obj.getTaxCode())) {
                            tXfNoneBusinessUploadImportDto.setErrorMessage("凭证号、入账日期、税码已经存在，不允许重新导入");
                            listener.getInvalidInvoices().add(tXfNoneBusinessUploadImportDto);
                        } else {
                            entity.setTaxCode(tXfNoneBusinessUploadImportDto.getTaxCode());
                            updateList.add(entity);
                            uuidList.add(Objects.toString(obj.getInvoiceCode(), "") + obj.getInvoiceNo());
                        }
                    } else {
                        // 其他业务类型
                        if (StringUtils.isEmpty(obj.getVoucherNo()) && StringUtils.isEmpty(obj.getEntryDate())) {
                            updateList.add(entity);
                            uuidList.add(Objects.toString(obj.getInvoiceCode(), "") + obj.getInvoiceNo());
                        } else {
                            tXfNoneBusinessUploadImportDto.setErrorMessage("凭证号入账日期已经存在，不允许重新导入");
                            listener.getInvalidInvoices().add(tXfNoneBusinessUploadImportDto);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateBatchById(updateList);
                this.updateInvoiceInfo(uuidList);
            }
        }
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            File tmpFile = FileUtils.getFile(tmp);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File sourceFile = FileUtils.getFile(tmp, file.getOriginalFilename());
            EasyExcel.write(tmp + "/" + file.getOriginalFilename(), TXfNoneBusinessUploadImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String exportFileName = "导入失败原因" + String.valueOf(System.currentTimeMillis()) + ExcelExportUtil.FILE_NAME_SUFFIX;
            String ftpFilePath = ftpPath + "/" + exportFileName;
            FileInputStream inputStream = FileUtils.openInputStream(sourceFile);
            try {
                ftpUtilService.uploadFile(ftpPath, exportFileName, inputStream);
            } catch (Exception e) {
                log.error("上传ftp服务器异常:{}", e);
            }
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setConditions(file.getOriginalFilename());
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpFilePath);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "非商上传导入错误信息", exportCommonService.getSuccContent());

        }
        sizeDto.setImportCount(listener.getRows());
        sizeDto.setValidCDount(updateList.size());
        sizeDto.setUnValidCount(listener.getInvalidInvoices().size());
        return sizeDto;
    }

    public SpecialCompanyImportSizeDto uploadImportData(MultipartFile file) throws IOException {
        SpecialCompanyImportSizeDto sizeDto = new SpecialCompanyImportSizeDto();
        NonbusinessUploadImportListener listener = new NonbusinessUploadImportListener();
        EasyExcel.read(file.getInputStream(), TXfNoneBusinessUploadImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
            sizeDto.setErrorMsg("未解析到数据");
            return sizeDto;
        }
        log.info("导入数据解析条数:{}", listener.getRows());
        List<String> uuidList = new ArrayList<>();
        List<TXfNoneBusinessUploadDetailEntity> updateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listener.getValidInvoices())) {
            //过滤出税号+6d去重后的数据
            List<TXfNoneBusinessUploadImportDto> supplierCodeList = listener.getValidInvoices().stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getInvoiceNo() + f.getInvoiceCode()))), ArrayList::new)
            );

            for (TXfNoneBusinessUploadImportDto tXfNoneBusinessUploadImportDto : supplierCodeList) {
                TXfNoneBusinessUploadDetailEntity obj = getOne(
                        new QueryWrapper<TXfNoneBusinessUploadDetailEntity>()
                                .lambda()
                                .eq(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, tXfNoneBusinessUploadImportDto.getInvoiceNo())
                                .eq(TXfNoneBusinessUploadDetailEntity::getBatchNo, tXfNoneBusinessUploadImportDto.getBatchNo())
                                .eq(StringUtils.isNoneBlank(tXfNoneBusinessUploadImportDto.getInvoiceCode()), TXfNoneBusinessUploadDetailEntity::getInvoiceCode, tXfNoneBusinessUploadImportDto.getInvoiceCode())
                                .eq(TXfNoneBusinessUploadDetailEntity::getSubmitFlag, Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG)
                );
                if (Objects.isNull(obj)) {
                    tXfNoneBusinessUploadImportDto.setErrorMessage("未找到对应的待提交发票记录");
                    listener.getInvalidInvoices().add(tXfNoneBusinessUploadImportDto);
                    continue;
                }
                if (StringUtils.isNotBlank(tXfNoneBusinessUploadImportDto.getCompanyCode())) {
                    String invoiceCode = StringUtils.defaultIfBlank(tXfNoneBusinessUploadImportDto.getInvoiceCode(), "");
                    Optional<TDxRecordInvoiceEntity> tDxRecordInvoiceEntity = new LambdaQueryChainWrapper<>(recordInvoiceService.getBaseMapper())
                            //.eq(TDxRecordInvoiceEntity::getUuid, invoiceCode + e.getInvoiceNo())
                            .eq(StringUtils.isNoneBlank(invoiceCode), TDxRecordInvoiceEntity::getInvoiceCode, invoiceCode)
                            .eq(TDxRecordInvoiceEntity::getInvoiceNo, tXfNoneBusinessUploadImportDto.getInvoiceNo()).oneOpt();
                    if (!tDxRecordInvoiceEntity.isPresent()) {
                        tXfNoneBusinessUploadImportDto.setErrorMessage("未找到对应的底账发票记录");
                        listener.getInvalidInvoices().add(tXfNoneBusinessUploadImportDto);
                        continue;
                    }
                    QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
                    wrapper.eq(TAcOrgEntity.TAX_NO, tDxRecordInvoiceEntity.get().getGfTaxNo());
                    wrapper.eq(TAcOrgEntity.ORG_TYPE, "5");
                    List<TAcOrgEntity> tAcOrgEntityList = companyService.list(wrapper);
                    boolean anyMatchFlag = tAcOrgEntityList.stream().map(TAcOrgEntity::getOrgCode).anyMatch(s -> s.contains(tXfNoneBusinessUploadImportDto.getCompanyCode()));
                    if (!anyMatchFlag) {
                        tXfNoneBusinessUploadImportDto.setErrorMessage("填写的JV与税号" + tDxRecordInvoiceEntity.get().getGfTaxNo() + "不匹配，请确认后重试。");
                        listener.getInvalidInvoices().add(tXfNoneBusinessUploadImportDto);
                        continue;
                    }
                    LambdaUpdateChainWrapper<TDxRecordInvoiceEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(recordInvoiceService.getBaseMapper());
                    updateChainWrapper.eq(TDxRecordInvoiceEntity::getUuid, invoiceCode + tXfNoneBusinessUploadImportDto.getInvoiceNo());
                    updateChainWrapper.set(TDxRecordInvoiceEntity::getJvcode, tXfNoneBusinessUploadImportDto.getCompanyCode());
                    updateChainWrapper.update();
                }

                TXfNoneBusinessUploadDetailEntity entity = new TXfNoneBusinessUploadDetailEntity();
                entity.setId(obj.getId());
                entity.setStoreNo(tXfNoneBusinessUploadImportDto.getStoreNo());
                entity.setInvoiceStoreNo(tXfNoneBusinessUploadImportDto.getInvoiceStoreNo());
                entity.setStoreStart(tXfNoneBusinessUploadImportDto.getStoreStart());
                entity.setStoreEnd(tXfNoneBusinessUploadImportDto.getStoreEnd());
                entity.setRemark(tXfNoneBusinessUploadImportDto.getRemark());
                entity.setUpdateTime(new Date());
                entity.setUpdateUser(UserUtil.getLoginName());
                updateList.add(entity);
                uuidList.add(Objects.toString(obj.getInvoiceCode(), "") + obj.getInvoiceNo());
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateBatchById(updateList);
            }
        }
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            File tmpFile = FileUtils.getFile(tmp);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File sourceFile = FileUtils.getFile(tmp, file.getOriginalFilename());
            EasyExcel.write(tmp + "/" + file.getOriginalFilename(), TXfNoneBusinessUploadImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String exportFileName = "导入失败原因" + String.valueOf(System.currentTimeMillis()) + ExcelExportUtil.FILE_NAME_SUFFIX;
            String ftpFilePath = ftpPath + "/" + exportFileName;
            FileInputStream inputStream = FileUtils.openInputStream(sourceFile);
            try {
                ftpUtilService.uploadFile(ftpPath, exportFileName, inputStream);
            } catch (Exception e) {
                log.error("上传ftp服务器异常:{}", e);
            }
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setConditions(file.getOriginalFilename());
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpFilePath);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "非商上传导入错误信息", exportCommonService.getSuccContent());

        }
        sizeDto.setImportCount(listener.getRows());
        sizeDto.setValidCDount(updateList.size());
        sizeDto.setUnValidCount(listener.getInvalidInvoices().size());
        return sizeDto;
    }

    @Transactional
    public R deleteUploadDetail(Long id) {
        log.info("非商电票删除deleteUploadDetail id:{}", id);
        //根据要删除发票的id查询此发票的所有信息
        TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity = this.getById(id);
        if (tXfNoneBusinessUploadDetailEntity == null) {
            log.info("未查询到发票数据，请确认后重试");
            return R.fail("未查询到发票数据，请确认后重试");
        }
        if ("1".equals(tXfNoneBusinessUploadDetailEntity.getSubmitFlag())) {
            log.info("该数据已提交不可删除，请确认后重试");
            return R.fail("该数据已提交不可删除，请确认后重试");
        }
        List<TXfNoneBusinessUploadDetailEntity> list = getInvoice(tXfNoneBusinessUploadDetailEntity.getInvoiceNo(), tXfNoneBusinessUploadDetailEntity.getInvoiceCode());
        // 删除非商表
        this.removeById(id);
        deleteRecordInvoice(tXfNoneBusinessUploadDetailEntity, list);
        return R.ok(null, "删除成功。");
    }

    public void deleteRecordInvoice(TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity, List<TXfNoneBusinessUploadDetailEntity> list) {
        String uuid = (StringUtils.isBlank(tXfNoneBusinessUploadDetailEntity.getInvoiceCode()) ? "" : tXfNoneBusinessUploadDetailEntity.getInvoiceCode()) + tXfNoneBusinessUploadDetailEntity.getInvoiceNo();
        List<TXfNoneBusinessUploadDetailEntity> doneList = list.stream().filter(s -> s.getSubmitFlag().equals(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG)).collect(Collectors.toList());
        List<TXfNoneBusinessUploadDetailEntity> undoList = list.stream()
                .filter(s -> s.getSubmitFlag().equals(Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG))
                .filter(s -> !Objects.equals(s.getId(), tXfNoneBusinessUploadDetailEntity.getId()))
                .filter(s -> Objects.equals(Constants.VERIFY_NONE_BUSINESS_SUCCESSE, tXfNoneBusinessUploadDetailEntity.getVerifyStatus()))
                .collect(Collectors.toList());

        // 1. 存在已提交发票不删除;
        if (CollectionUtils.isNotEmpty(doneList)) {
            log.info("存在已提交的发票，不删除底账信息。");
            return;
        }
        //  2. 验真失败的发票不删除
        if (Objects.equals(Constants.VERIFY_NONE_BUSINESS_FAIL, tXfNoneBusinessUploadDetailEntity.getVerifyStatus())) {
            log.info("非商验真状态失败，不删除底账信息。");
            return;
        }
        // 3. 发票存在其他待提交且验真成功的发票
        if (CollectionUtils.isNotEmpty(undoList)) {
            log.info("当前发票存在其他待提交且验真成功的发票，不删除底账信息。");
            return;
        }
        log.info("删除底账信息、底账明细、扫描表信息...");
        // 删除底账表
        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put("uuid", uuid);
        // 删除底账明细表
        LambdaUpdateWrapper<TDxRecordInvoiceEntity> updateChainWrapper = new LambdaUpdateWrapper<>();
        updateChainWrapper.eq(TDxRecordInvoiceEntity::getUuid, uuid);
        updateChainWrapper.set(TDxRecordInvoiceEntity::getFlowType, "");
        updateChainWrapper.set(TDxRecordInvoiceEntity::getSourceSystem, SourceSystemEnum.DZ_SOURCE_ENTER.getCode());
        updateChainWrapper.set(TDxRecordInvoiceEntity::getNoDeduction, "1");
        recordInvoiceService.update(updateChainWrapper);
        // 删除扫描表
        tDxInvoiceDao.deleteByMap(deleteMap);
        // 删除文件
        fileService.deleteFile(tXfNoneBusinessUploadDetailEntity.getSourceUploadPath());
    }

}