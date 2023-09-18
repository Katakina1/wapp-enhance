package com.xforceplus.wapp.modules.backfill.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.enums.invoice.InvoiceAuthStatusEnum;
import com.xforceplus.wapp.enums.invoice.InvoicePaymentStatusEnum;
import com.xforceplus.wapp.enums.invoice.InvoiceReceiptStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveTypeEnum;
import com.xforceplus.wapp.modules.audit.enums.AuditStatusEnum;
import com.xforceplus.wapp.modules.audit.service.InvoiceAuditService;
import com.xforceplus.wapp.modules.audit.vo.InvoiceAuditVO;
import com.xforceplus.wapp.modules.backfill.model.*;
import com.xforceplus.wapp.modules.backfill.tools.BackFillCheckTools;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.daoExt.ElectronicUploadRecordDao;
import com.xforceplus.wapp.repository.daoExt.MatchDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.service.CommRedNotificationService;
import com.xforceplus.wapp.util.CoopFullHalfAngleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.constants.Constants.*;
import static com.xforceplus.wapp.modules.sys.util.UserUtil.getUserId;


/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-15 10:00
 **/
@Service
@Slf4j
public class BackFillService {

    @Value("${wapp.integration.customer-no}")
    private String customerNo;

    @Autowired
    private HttpClientFactory httpClientFactory;

    @Value("${wapp.integration.action.ofd}")
    private String ofdAction;

    private final Map<String, String> defaultHeader;

    @Value("${wapp.integration.tenant-code}")
    private String tenantCode;

    /**
     * 沃尔玛租户ID
     */
    private final String tenantId;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private InvoiceAuditService invoiceAuditService;

    @Autowired
    private TXfElecUploadRecordDetailDao electronicUploadRecordDetailDao;

    @Autowired
    private ElectronicUploadRecordDao electronicUploadRecordDao;

    @Autowired
    private IDSequence idSequence;

    @Autowired
    private DiscernService discernService;

    @Autowired
    private FileService fileService;

    @Autowired
    private MatchDao matchDao;

    @Autowired
    private SucceedInvoiceMapper succeedInvoiceMapper;

    @Autowired
    private TXfPreInvoiceDao preInvoiceDao;

    @Autowired
    private TXfSettlementDao tXfSettlementDao;

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;

    @Autowired
    private RecordInvoiceService recordInvoiceService;

    @Autowired
    private RedNotificationOuterService redNotificationOuterService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private TXfElecUploadRecordDao tXfElecUploadRecordDao;

    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private LockClient lockClient;
    @Autowired
    private StatementServiceImpl statementService;
    @Autowired
    private TXfBillDeductInvoiceDetailDao tXfBillDeductInvoiceDetailDao;

    public BackFillService(@Value("${wapp.integration.tenant-id}")
                                   String tenantId) {
        this.tenantId = tenantId;
        defaultHeader = new HashMap<>();
        defaultHeader.put("rpcType", "http");
        defaultHeader.put("x-app-client", "janus");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("accept-encoding", "");
    }

    public R commitVerifyCheck(Long id,String settlementNo) {
        if(id != null){
            TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectById(id);
            if (entity != null) {
                if (DateUtils.isCurrentMonth(entity.getInvoiceDate()) && !InvoiceTypeEnum.isElectronic(entity.getInvoiceType())) {
                    return R.fail("当前红票可以作废，请直接删除后，再重新上传");
                }
            } else {
                return R.fail("未查到发票");
            }
        }
        if(StringUtils.isNotEmpty(settlementNo)){
            QueryWrapper<TXfPreInvoiceEntity> preinvoiceWrapper = new QueryWrapper<>();
            preinvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, settlementNo);
            preinvoiceWrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.DESTROY.getCode());
            preinvoiceWrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
            List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = preInvoiceDao.selectList(preinvoiceWrapper);
            if (CollectionUtils.isEmpty(tXfPreInvoiceEntities)) {
                return R.fail("根据结算单号未找到预制发票");
            }
            if (tXfPreInvoiceEntities.stream().anyMatch(  t -> TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode().equals(t.getPreInvoiceStatus()) || TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode().equals(t.getPreInvoiceStatus()) ||
                    (BigDecimal.ZERO.compareTo(t.getTaxRate()) != 0&& StringUtils.isEmpty(t.getRedNotificationNo())))) {
                R r = new R();
                r.setCode("XFWAPP0002");
                r.setMessage("当前红字信息表由购方发起申请或审核，暂未完成；\r\n" +
                        "完成后，您可以继续添加发票！\r\n" +
                        "请及时关注票据状态！或联系购货方联系");
                return r;
            }
            long countPre = tXfPreInvoiceEntities.stream().filter(t -> TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(t.getPreInvoiceStatus())).count();
            return R.ok(countPre);
        }
        return R.ok("校验通过");
    }

    public R commitVerify(BackFillCommitVerifyRequest request){
        R r = checkCommitRequest(request);
        if (R.FAIL.equals(r.getCode())) {
            return r;
        }
        return this.commitInvoiceVerify(request);
    }

    public R commitInvoiceVerify(BackFillCommitVerifyRequest request) {
        String batchNo = UUID.randomUUID().toString().replace("-", "");
        TXfElecUploadRecordEntity recordEntity = new TXfElecUploadRecordEntity();
        recordEntity.setCreateTime(new Date());
        recordEntity.setTotalNum(request.getVerifyBeanList().size());
        recordEntity.setUpdateTime(recordEntity.getCreateTime());
        recordEntity.setBatchNo(batchNo);
        recordEntity.setCreateUser(String.valueOf(UserUtil.getUserId()));
        recordEntity.setId(idSequence.nextId());
        recordEntity.setJvCode(request.getJvCode());
        recordEntity.setVendorId(UserUtil.getUser().getUsercode());
        recordEntity.setGfName(request.getGfName());
        recordEntity.setFailureNum(0);
        recordEntity.setSucceedNum(0);
        //先入库初始化，防止pdf识别/验真过早的回调返回
        // 不用考虑事务
        this.electronicUploadRecordDao.save(recordEntity);
        log.info("纸票发票回填--发起验真请求");
        for (BackFillVerifyBean backFillVerifyBean : request.getVerifyBeanList()) {
            VerificationRequest verificationRequest = new VerificationRequest();
            verificationRequest.setAmount(backFillVerifyBean.getAmount());
            verificationRequest.setCheckCode(backFillVerifyBean.getCheckCode());
            verificationRequest.setCustomerNo(customerNo);
            verificationRequest.setInvoiceCode(backFillVerifyBean.getInvoiceCode());
            verificationRequest.setInvoiceNo(backFillVerifyBean.getInvoiceNo());
            verificationRequest.setPaperDrewDate(backFillVerifyBean.getPaperDrewDate());

            TXfElecUploadRecordDetailEntity detailEntity = new TXfElecUploadRecordDetailEntity();
            detailEntity.setBatchNo(batchNo);
            detailEntity.setId(idSequence.nextId());
            detailEntity.setCreateUser(String.valueOf(UserUtil.getUserId()));
            detailEntity.setSettlementNo(request.getSettlementNo());
            detailEntity.setInvoiceCode(backFillVerifyBean.getInvoiceCode());
            detailEntity.setInvoiceNo(backFillVerifyBean.getInvoiceNo());
            detailEntity.setPaperDrewDate(backFillVerifyBean.getPaperDrewDate());
            detailEntity.setAmount(new BigDecimal(backFillVerifyBean.getAmount()));
            detailEntity.setCheckCode(backFillVerifyBean.getCheckCode());
            detailEntity.setCreateTime(new Date());
            detailEntity.setStatus(2);
            try {
                VerificationResponse verificationResponse = verificationService.verify(verificationRequest);
                log.info("纸票发票回填--发票验真同步返回结果：{}", JSON.toJSONString(verificationResponse));

                if (verificationResponse.isOK()) {
                    final String verifyTaskId = verificationResponse.getResult();
                    detailEntity.setXfVerifyTaskId(verifyTaskId);
                    detailEntity.setStatus(3);
                } else {
                    log.warn("发票代码:{},发票号码：{}，发票验真请求失败:{}", backFillVerifyBean.getInvoiceCode(), backFillVerifyBean.getInvoiceNo(), verificationResponse.getMessage());
                    detailEntity.setStatus(0);
                    detailEntity.setReason(verificationResponse.getMessage());
                    recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                }
            } catch (EnhanceRuntimeException e) {
                recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                detailEntity.setStatus(0);
                detailEntity.setReason(e.getMessage());
            }
            tXfElecUploadRecordDao.updateById(recordEntity);
            electronicUploadRecordDetailDao.insert(detailEntity);
        }
        return R.ok(batchNo);
    }

    public R upload(MultipartFile[] files, String gfName,String jvCode,String vendorid, String settlementNo,Integer businessType) {
        if (files.length == 0) {
            return R.fail("请选择您要上传的电票文件(pdf/ofd/xml)");
        }
        if (files.length > 10) {
            return R.fail("最多一次性上传10个文件");
        }
        List<byte[]> ofd = new ArrayList<>();
        List<byte[]> pdf = new ArrayList<>();
        List<byte[]> xml = new ArrayList<>();
        try {
            Set<String> fileNames=new LinkedHashSet<>();
            for (int i = 0; i < files.length; i++) {
                final MultipartFile file = files[i];
                final String filename = file.getOriginalFilename();
                if(!fileNames.add(filename)){
                    return R.fail("文件["+filename+"]重复上传！");
                }
                final String suffix = filename.substring(filename.lastIndexOf(".") + 1);
                if (org.apache.commons.lang.StringUtils.isNotBlank(suffix)) {
                    switch (suffix.toLowerCase()) {
                        case Constants.SUFFIX_OF_OFD:
                            //OFD处理
                            ofd.add(IOUtils.toByteArray(file.getInputStream()));
                            break;
                        case Constants.SUFFIX_OF_PDF:
                            // PDF 处理
                            pdf.add(IOUtils.toByteArray(file.getInputStream()));
                            break;
                        case Constants.SUFFIX_OF_XML:
                            // PDF 处理
                            xml.add(IOUtils.toByteArray(file.getInputStream()));
                            break;
                        default:
                            throw new EnhanceRuntimeException("文件:[" + filename + "]类型不正确,应为:[ofd/pdf/xml]");
                    }
                } else {
                    throw new EnhanceRuntimeException("文件:[" + filename + "]后缀名不正确,应为:[ofd/pdf/xml]");
                }
            }

            SpecialElecUploadDto dto = new SpecialElecUploadDto();
            dto.setOfds(ofd);
            dto.setJvCode(jvCode);
            dto.setUserId(getUserId());
            dto.setGfName(gfName);
            dto.setPdfs(pdf);
            dto.setVendorId(vendorid);
            dto.setSettlementNo(settlementNo);
            dto.setBusinessType(businessType);
            dto.setXmls(xml);
            log.info("电票发票上传--识别入参：{}",JSONObject.toJSONString(dto));
            final String batchNo = this.uploadAndVerify(dto);

            return R.ok(batchNo);
        } catch (Exception e) {
            log.error("上传过程中出现异常:" + e.getMessage(), e);
            return R.fail("上传过程中出现错误，请重试");
        }
    }



    public VerificationResponse parseOfd(byte[] ofd, String batchNo,TXfElecUploadRecordDetailEntity detailEntity) {
        OfdParseRequest request = new OfdParseRequest();
        request.setOfdEncode(Base64.encodeBase64String(ofd));
        request.setTenantCode(tenantCode);
        // 仅解析和验签
        request.setType("1");
        try {
            Map<String,String> header=new HashMap<>(defaultHeader);
            header.put("serialNo", batchNo);
            final String responseBody = httpClientFactory.post(ofdAction, header, JSONObject.toJSONString(request), "");
            log.info("发送ofd解析结果:{}", responseBody);
            final OfdResponse ofdResponse = JSONObject.parseObject(responseBody, OfdResponse.class);
            if (ofdResponse.isOk()) {
                final OfdResponse.OfdResponseResult result = ofdResponse.getResult();
                final InvoiceMain invoiceMain = result.getInvoiceMain();

                detailEntity.setInvoiceCode(invoiceMain.getInvoiceCode());
                detailEntity.setInvoiceNo(invoiceMain.getInvoiceNo());
                detailEntity.setPaperDrewDate(invoiceMain.getPaperDrewDate());
                detailEntity.setAmount(new BigDecimal(invoiceMain.getAmountWithoutTax()));
                detailEntity.setCheckCode(invoiceMain.getCheckCode());

                VerificationRequest verificationRequest = new VerificationRequest();
                verificationRequest.setAmount(invoiceMain.getAmountWithoutTax());
                verificationRequest.setCheckCode(invoiceMain.getCheckCode());
                verificationRequest.setCustomerNo(customerNo);
                verificationRequest.setInvoiceCode(invoiceMain.getInvoiceCode());
                verificationRequest.setInvoiceNo(invoiceMain.getInvoiceNo());
                verificationRequest.setPaperDrewDate(invoiceMain.getPaperDrewDate());
                return verificationService.verify(verificationRequest);
            } else {
                log.info("ofd解析失败:{}", ofdResponse.getMessage());
                throw new EnhanceRuntimeException("ofd解析失败:" + ofdResponse.getMessage());
            }
        } catch (IOException e) {
            log.error("ofd解析请求发起失败:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("ofd解析请求发起失败:" + e.getMessage());
        }
    }

    public OfdResponse signOfd(byte[] ofd, String businessNo) {
        OfdParseRequest request = new OfdParseRequest();
        request.setOfdEncode(Base64.encodeBase64String(ofd));
        request.setTenantCode(tenantCode);
        // 仅解析和验签
        request.setType("1");
        try {
            Map<String,String> header=new HashMap<>(defaultHeader);
            header.put("serialNo", UUID.randomUUID().toString());
            final String responseBody = httpClientFactory.post(ofdAction, header, JSONObject.toJSONString(request), "");
            log.info("发送ofd解析结果:{}", responseBody);
            return JSONObject.parseObject(responseBody, OfdResponse.class);
        } catch (IOException e) {
            log.error("ofd解析请求发起失败:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("ofd解析请求发起失败:" + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String uploadAndVerify(SpecialElecUploadDto specialElecUploadDto) {
        List<byte[]> pdfs = specialElecUploadDto.getPdfs();
        if (pdfs == null) {
            pdfs = Collections.emptyList();
        }
        List<byte[]> ofds = specialElecUploadDto.getOfds();
        if (ofds == null) {
            ofds = Collections.emptyList();
        }
        List<byte[]> xmls = specialElecUploadDto.getXmls();
        if (xmls == null) {
            xmls = Collections.emptyList();
        }
        final int totalNum = ofds.size() + pdfs.size()+ xmls.size();
        String batchNo = UUID.randomUUID().toString().replace("-", "");
        TXfElecUploadRecordEntity recordEntity = new TXfElecUploadRecordEntity();
        recordEntity.setCreateTime(new Date());
        recordEntity.setTotalNum(totalNum);
        recordEntity.setUpdateTime(recordEntity.getCreateTime());
        recordEntity.setBatchNo(batchNo);
        recordEntity.setCreateUser(String.valueOf(specialElecUploadDto.getUserId()));
        recordEntity.setId(idSequence.nextId());
        recordEntity.setJvCode(specialElecUploadDto.getJvCode());
        recordEntity.setVendorId(specialElecUploadDto.getVendorId());
        recordEntity.setGfName(specialElecUploadDto.getGfName());
        recordEntity.setFailureNum(0);
        recordEntity.setSucceedNum(0);
        //先入库初始化，防止pdf识别/验真过早的回调返回
        // 不用考虑事务
        this.electronicUploadRecordDao.save(recordEntity);
        try {
            for (byte[] ofd : ofds) {
                TXfElecUploadRecordDetailEntity detailEntity = new TXfElecUploadRecordDetailEntity();
                detailEntity.setBatchNo(batchNo);
                detailEntity.setId(idSequence.nextId());
                detailEntity.setCreateUser(String.valueOf(specialElecUploadDto.getUserId()));
                detailEntity.setCreateTime(new Date());
                detailEntity.setFileType(FILE_TYPE_OFD);
                detailEntity.setSettlementNo(specialElecUploadDto.getSettlementNo());
                detailEntity.setBusinessType(specialElecUploadDto.getBusinessType());
                try {
                    final VerificationResponse verificationResponse = this.parseOfd(ofd, batchNo,detailEntity);
                    if (verificationResponse.isOK()) {
                        final String verifyTaskId = verificationResponse.getResult();
                        detailEntity.setXfVerifyTaskId(verifyTaskId);
                        detailEntity.setStatus(3);
                        //文件上传
                        uploadFile(ofd, FILE_TYPE_OFD, detailEntity,recordEntity.getVendorId());
                    } else {
                        detailEntity.setStatus(0);
                        detailEntity.setReason(verificationResponse.getMessage());
                        recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                    }
                } catch (EnhanceRuntimeException e) {
                    recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                    detailEntity.setStatus(0);
                    detailEntity.setReason(e.getMessage());
                }

                this.electronicUploadRecordDetailDao.insert(detailEntity);
            }

            if (!CollectionUtils.isEmpty(pdfs)) {
                final Map<String, byte[]> discernTaskMap = discernService.discern(pdfs);

                for (Map.Entry<String, byte[]> m : discernTaskMap.entrySet()) {
                    TXfElecUploadRecordDetailEntity detailEntity = new TXfElecUploadRecordDetailEntity();
                    detailEntity.setBatchNo(batchNo);
                    detailEntity.setId(idSequence.nextId());
                    detailEntity.setXfDiscernTaskId(m.getKey());
                    detailEntity.setStatus(2);
                    detailEntity.setCreateUser(String.valueOf(specialElecUploadDto.getUserId()));
                    detailEntity.setFileType(FILE_TYPE_PDF);
                    detailEntity.setSettlementNo(specialElecUploadDto.getSettlementNo());
                    detailEntity.setCreateTime(new Date());
                    detailEntity.setBusinessType(specialElecUploadDto.getBusinessType());
                    //文件上传
                    uploadFile(m.getValue(), Constants.FILE_TYPE_PDF, detailEntity,recordEntity.getVendorId());
                    this.electronicUploadRecordDetailDao.insert(detailEntity);

                }
                final int failureSize = pdfs.size() - discernTaskMap.size();
                if (failureSize > 0) {
                    recordEntity.setFailureNum(recordEntity.getFailureNum() + failureSize);
                }
            }
            xmls.forEach( xml->{
                TXfElecUploadRecordDetailEntity detailEntity = new TXfElecUploadRecordDetailEntity();
                detailEntity.setBatchNo(batchNo);
                detailEntity.setId(idSequence.nextId());
                detailEntity.setCreateUser(String.valueOf(specialElecUploadDto.getUserId()));
                detailEntity.setCreateTime(new Date());
                detailEntity.setFileType(FILE_TYPE_XML);
                detailEntity.setSettlementNo(specialElecUploadDto.getSettlementNo());
                detailEntity.setBusinessType(specialElecUploadDto.getBusinessType());
                uploadFile(xml, Constants.FILE_TYPE_XML, detailEntity, recordEntity.getVendorId());
                verificationService.analysisAndVerify(xml, batchNo, recordEntity.getVendorId(), detailEntity.getUploadId(), taskId -> {
                    detailEntity.setXfVerifyTaskId(taskId);
                    detailEntity.setStatus(3);
                    //文件上传
                }, err -> {
                    detailEntity.setStatus(0);
                    detailEntity.setReason(err);
                    recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                });
                this.electronicUploadRecordDetailDao.insert(detailEntity);
            });
        } finally {
            if (recordEntity.getFailureNum() > 0) {
                electronicUploadRecordDao.increaseFailureSpecialNum(recordEntity.getBatchNo(), recordEntity.getFailureNum());
            }
        }
        return batchNo;
    }


    /**
     * 上传至文件服务器
     *
     * @param file
     * @param fileType
     * @param detailEntity
     */
    private void uploadFile(byte[] file, Integer fileType, TXfElecUploadRecordDetailEntity detailEntity,String venderId) {

        try {

            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString());
            fileName.append(".");
            if (fileType.equals(FILE_TYPE_OFD)) {
                fileName.append(Constants.SUFFIX_OF_OFD);
            } else if (fileType.equals(Constants.FILE_TYPE_PDF)) {
                fileName.append(Constants.SUFFIX_OF_PDF);
            }else if (fileType.equals(Constants.FILE_TYPE_XML)) {
                fileName.append(Constants.SUFFIX_OF_XML);
            }

            String uploadResult = fileService.uploadFile(file, fileName.toString(),venderId);

            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);

            UploadFileResultData data = uploadFileResult.getData();

            detailEntity.setUploadId(data.getUploadId());
            detailEntity.setUploadPath(data.getUploadPath());

        } catch (Exception e) {

            log.error("调用文件服务器失败:{}", e);
            throw new RRException("调用文件服务器失败:" + e.getMessage());
        }

    }

    public UploadResult getUploadResult(String batchNo) {
        final TXfElecUploadRecordEntity recordEntity = this.electronicUploadRecordDao.selectByBatchNo(batchNo);
        if (recordEntity == null) {
            log.info("不存在的批次号:{}", batchNo);
            throw new RRException("不存在的批次号:" + batchNo);
        }
        UploadResult uploadResult = new UploadResult();
        // 还在处理中
        if (recordEntity.getSucceedNum() + recordEntity.getFailureNum() != recordEntity.getTotalNum()) {
            uploadResult.setStep(0);
            return uploadResult;
        }
        QueryWrapper<TXfElecUploadRecordDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfElecUploadRecordDetailEntity.BATCH_NO, batchNo);
        final List<TXfElecUploadRecordDetailEntity> detailEntities = this.electronicUploadRecordDetailDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(detailEntities) || detailEntities.size() != recordEntity.getTotalNum()) {
            log.info("selectByBatchNo total:{},size:{}", recordEntity.getTotalNum(), detailEntities == null ? 0 : detailEntities.size());
            uploadResult.setStep(0);
            return uploadResult;
        }

        uploadResult.setStep(1);
        uploadResult.setFailureNum(recordEntity.getFailureNum());
        uploadResult.setSucceedNum(recordEntity.getSucceedNum());


        List<UploadResult.SucceedInvoice> invoiceEntities = new ArrayList<>();

        List<UploadResult.FailureInvoice> failureInvoices = new ArrayList<>();

        for (TXfElecUploadRecordDetailEntity detailEntity : detailEntities) {
            if (detailEntity.getStatus().equals(1)) {
                final String invoiceNo = detailEntity.getInvoiceNo();
                final String invoiceCode = detailEntity.getInvoiceCode();
                final List<InvoiceEntity> invoices = matchDao.invoiceQueryList(Collections.singletonMap("uuid", invoiceCode + invoiceNo));
                if (!CollectionUtils.isEmpty(invoices)) {
                    final InvoiceEntity invoiceEntity = invoices.get(0);
                    final UploadResult.SucceedInvoice succeedInvoice = succeedInvoiceMapper.toSucceed(invoiceEntity);
                    succeedInvoice.setFileType(Objects.toString(detailEntity.getFileType(), null));
                    invoiceEntities.add(succeedInvoice);
                }
            } else {
                UploadResult.FailureInvoice failureInvoice = new UploadResult.FailureInvoice();
                failureInvoice.setInvoiceNo(detailEntity.getInvoiceNo());
                failureInvoice.setInvoiceCode(detailEntity.getInvoiceCode());
                failureInvoice.setInvoiceDate(detailEntity.getPaperDrewDate());
                failureInvoice.setInvoiceAmount(detailEntity.getAmount());
                failureInvoice.setCheckCode(detailEntity.getCheckCode());
                failureInvoice.setMsg(detailEntity.getReason());
                failureInvoices.add(failureInvoice);
            }
        }

        uploadResult.setSucceedInvoices(invoiceEntities);

        uploadResult.setFailureInvoices(failureInvoices);

        return uploadResult;
    }

    @Deprecated
    @Transactional
    public R matchPreInvoice(BackFillMatchRequest request) {
        if (StringUtils.isEmpty(request.getSettlementNo())) {
            return R.fail("结算单号不能为空");
        }
        if (CollectionUtils.isEmpty(request.getVerifyBeanList())) {
            return R.fail("上传发票不能为空");
        }
        QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, request.getSettlementNo());
        wrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
        wrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.DESTROY.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = preInvoiceDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(tXfPreInvoiceEntities)) {
            return R.fail("根据结算单号未找到预制发票");
        }
        Date updateDate = new Date();
        if ("0".equals(request.getInvoiceColor())) {
            //红冲
            if(request.getVerifyBeanList().stream().anyMatch(t -> new BigDecimal(t.getAmount()).compareTo(BigDecimal.ZERO) > 0)){
                return R.fail("上传的发票金额必须小于零");
            }
            if (!CollectionUtils.isEmpty(request.getVerifyBeanList())) {
                boolean isElec = request.getVerifyBeanList().stream().allMatch(t -> InvoiceTypeEnum.isElectronic(t.getInvoiceType()));
                boolean isNotElec = request.getVerifyBeanList().stream().noneMatch(t -> InvoiceTypeEnum.isElectronic(t.getInvoiceType()));
                if(!(isElec || isNotElec)){
                    return R.fail("红票不允许纸电混合");
                }
            }
            int successCount = 0;
            List<Long> successPreInvoiceIdList = Lists.newArrayList();
            //非零税率根据红字编号匹配，零税率根据金额匹配
            for (TXfPreInvoiceEntity preInvoiceEntity : tXfPreInvoiceEntities) {
                boolean flag = BigDecimal.ZERO.compareTo(preInvoiceEntity.getTaxRate()) == 0;
                for (BackFillVerifyBean backFillVerifyBean : request.getVerifyBeanList()) {
                    boolean equalsRedNo = preInvoiceEntity.getRedNotificationNo().equals(backFillVerifyBean.getRedNoticeNumber());
                    boolean equalsAmount = preInvoiceEntity.getAmountWithoutTax().compareTo(new BigDecimal(backFillVerifyBean.getAmount())) == 0;
                    if ((!flag && equalsRedNo) || (flag && equalsAmount)) {
                        if (!flag && StringUtils.isEmpty(preInvoiceEntity.getRedNotificationNo())) {
                            log.info("预制发票的红字信息编号不能为空");
                            continue;
                        }
                        log.info("发票回填后匹配--回填预制发票数据");
                        preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode());
                        preInvoiceEntity.setInvoiceCode(backFillVerifyBean.getInvoiceCode());
                        preInvoiceEntity.setInvoiceNo(backFillVerifyBean.getInvoiceNo());
                        preInvoiceEntity.setCheckCode(backFillVerifyBean.getCheckCode());
                        preInvoiceEntity.setMachineCode(backFillVerifyBean.getMachinecode());
                        preInvoiceEntity.setPaperDrewDate(backFillVerifyBean.getPaperDrewDate());
                        preInvoiceEntity.setUpdateTime(updateDate);
                        preInvoiceDao.updateById(preInvoiceEntity);

                        successPreInvoiceIdList.add(preInvoiceEntity.getId());
                        if(!flag){
                            log.info("发票回填后匹配--核销已申请的红字信息表编号入参：{}", preInvoiceEntity.getRedNotificationNo());
                            Response<String> update = redNotificationOuterService.update(preInvoiceEntity.getRedNotificationNo(), ApproveStatus.ALREADY_USE);
                            log.info("发票回填后匹配--核销已申请的红字信息表编号响应：{}", JSONObject.toJSONString(update));
                        }
                        log.info("红票回填后匹配--修改发票状态并加上结算单号");
                        UpdateWrapper<TDxRecordInvoiceEntity> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq(TDxRecordInvoiceEntity.UUID, backFillVerifyBean.getInvoiceCode() + backFillVerifyBean.getInvoiceNo());
                        TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
                        tDxRecordInvoiceEntity.setSettlementNo(request.getSettlementNo());
                        tDxRecordInvoiceEntity.setInvoiceStatus(InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode());
                        tDxRecordInvoiceEntity.setStatusUpdateDate(updateDate);
                        tDxRecordInvoiceEntity.setIsDel(IsDealEnum.NO.getValue());
                        tDxRecordInvoiceDao.update(tDxRecordInvoiceEntity, updateWrapper);
                        successCount++;
                    }
                }
            }
            if(successCount == 0){
                return R.fail("红票的红字信息表未匹配到对应的预制发票");
            }
            log.info("红票回填后匹配--修改结算单状态");
            QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, request.getSettlementNo());
            TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
            // 原结算单状态
            Integer originalSettlementStatus = Optional.ofNullable(tXfSettlementEntity).map(TXfSettlementEntity::getSettlementStatus).orElse(null);
            String businessStatus = "";
            if (tXfSettlementEntity != null) {
                OperateLogEnum deductOpLogEnum = null;
                if (tXfPreInvoiceEntities.stream().allMatch(t -> TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode().equals(t.getPreInvoiceStatus()))) {
                    tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode());
                    businessStatus = TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getDesc();
                    // 由部分开具/待开票变为已开具
                    deductOpLogEnum = (TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode().equals(originalSettlementStatus) || TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(originalSettlementStatus)) ? OperateLogEnum.SETTLEMENT_UPLOAD_ALL_RED_INVOICE : null;
                } else {
                    tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode());
                    businessStatus = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getDesc();
                    // 由待开票变为部分开票
                    deductOpLogEnum = TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(originalSettlementStatus) ? OperateLogEnum.SETTLEMENT_UPLOAD_PART_RED_INVOICE : null;
                }
                tXfSettlementEntity.setUpdateTime(updateDate);
                tXfSettlementDao.updateById(tXfSettlementEntity);
                // 添加日志
                operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.UPLOAD_INVOICE, businessStatus, "",getUserId(), UserUtil.getUserName());
                operateLogService.addDeductLog(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getBusinessType(), TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()), deductOpLogEnum, "", UserUtil.getUserId(), UserUtil.getUserName());
                // 发送消息
                commonMessageService.sendPreInvoiceBackFillMessage(successPreInvoiceIdList);
            } else {
                throw new EnhanceRuntimeException("未找到结算单");
            }
        } else {
            //蓝冲
            log.info("发票蓝冲:invoiceNo:{},invoiceCode:{}", request.getOriginInvoiceNo(), request.getOriginInvoiceCode());
            Asserts.isTrue(StringUtils.isBlank(request.getOriginInvoiceNo()), "原红字发票号码不能为空");
            Asserts.isTrue(StringUtils.isBlank(request.getOriginInvoiceCode()), "原红字发票代码不能为空");

            //校验金额
            if (!CollectionUtils.isEmpty(request.getVerifyBeanList())) {
                QueryWrapper<TDxRecordInvoiceEntity> invoiceWrapper = new QueryWrapper<>();
                invoiceWrapper.eq(TDxRecordInvoiceEntity.UUID, request.getOriginInvoiceCode() + request.getOriginInvoiceNo());
                TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectOne(invoiceWrapper);
                if (invoiceEntity != null) {
                    if (!invoiceAuditService.passAudit(invoiceEntity.getUuid())) {
                        throw new EnhanceRuntimeException("通过审核后才能蓝冲。");
                    }
                    BigDecimal amount = request.getVerifyBeanList().stream().map(t -> new BigDecimal(t.getAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (amount.add(invoiceEntity.getInvoiceAmount()).compareTo(BigDecimal.ZERO) != 0) {
                        throw new EnhanceRuntimeException("您上传的发票合计金额与代开金额不一致，请确认后再保存");
                    }
                    Set<String> uuid = request.getVerifyBeanList().stream().map(it -> it.getInvoiceCode() + it.getInvoiceNo()).collect(Collectors.toSet());
                    List<TDxRecordInvoiceEntity> list = new LambdaQueryChainWrapper<>(tDxRecordInvoiceDao).in(TDxRecordInvoiceEntity::getUuid, uuid).list();
                    Optional<String> first = list.stream().map(it -> {
                        if (!"1".equalsIgnoreCase(it.getQsStatus())) {
                            return "您上传的发票未被签收，请确认后再保存";
                        }
                        if (!"7".equalsIgnoreCase(it.getFlowType())) {
                            return "您上传的发票业务类型不是直接认证，请确认后再保存";
                        }
                        if (!"4".equalsIgnoreCase(it.getAuthStatus())) {
                            return "您上传的发票认证状态不是已认证，请确认后再保存";
                        }
                        return StringUtils.EMPTY;
                    }).filter(StringUtils::isNotBlank).findFirst();
                    if (first.isPresent()) {
                        throw new EnhanceRuntimeException(first.get());
                    }
                    if (list.size() != uuid.size()) {
                        throw new EnhanceRuntimeException("未找到蓝票");
                    }
                } else {
                    throw new EnhanceRuntimeException("未找到蓝冲的发票");
                }
            }
            // 保存红蓝关系
            blueInvoiceRelationService.saveBatch(request.getOriginInvoiceNo(), request.getOriginInvoiceCode(), request.getVerifyBeanList());

            log.info("蓝票回填后匹配--修改发票状态和预制发票状态和结算单状态");
            recordInvoiceService.blue4RedInvoice(request.getOriginInvoiceNo(), request.getOriginInvoiceCode());

            //作废预制发票
            UpdateWrapper<TXfPreInvoiceEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, request.getSettlementNo());
            updateWrapper.eq(TXfPreInvoiceEntity.INVOICE_CODE,request.getOriginInvoiceCode());
            updateWrapper.eq(TXfPreInvoiceEntity.INVOICE_NO,request.getOriginInvoiceNo());
            List<TXfPreInvoiceEntity> preInvoiceEntityList = preInvoiceDao.selectList(updateWrapper);
            if (CollectionUtil.isNotEmpty(preInvoiceEntityList)) {
                TXfPreInvoiceEntity preInvoiceEntity = new TXfPreInvoiceEntity();
                preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
                preInvoiceEntity.setUpdateTime(updateDate);
                preInvoiceDao.update(preInvoiceEntity, updateWrapper);

                commonMessageService.sendPreInvoiceDiscardMessage(preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList()));
            }

            // 作废扫描表发票
            if (StringUtils.isNotBlank(request.getOriginInvoiceCode()) && StringUtils.isNotBlank(request.getOriginInvoiceNo())) {
                TDxInvoiceEntity tDxInvoiceEntity = new TDxInvoiceEntity();
                tDxInvoiceEntity.setIsdel(IsDealEnum.YES.getValue());
                tDxInvoiceEntity.setUpdateDate(updateDate);
                tDxInvoiceEntity.setDelDate(updateDate);
                tDxInvoiceEntity.setRefundReason("matchPreInvoice detele");
                UpdateWrapper<TDxInvoiceEntity> invoiceWrapper = new UpdateWrapper<>();
                wrapper.eq(TDxInvoiceEntity.UUID, request.getOriginInvoiceCode() + request.getOriginInvoiceNo());
                tDxInvoiceDao.update(tDxInvoiceEntity, invoiceWrapper);
            }

            //修改结算单状态
            recordInvoiceService.updateSettlement(request.getSettlementNo(),null);

            // 重新查询结算单
            QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, request.getSettlementNo());
            TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
            // 添加日志
            operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.BLUE_FLUSH_INVOICE, Optional.ofNullable(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus())).map(TXfSettlementStatusEnum::getDesc).orElse(""), "",getUserId(), UserUtil.getUserName());
            OperateLogEnum deductOpLogEnum = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode().equals(tXfSettlementEntity.getSettlementStatus()) ? OperateLogEnum.SETTLEMENT_BLUE_FLUSH_PART_RED_INVOICE : OperateLogEnum.SETTLEMENT_BLUE_FLUSH_ALL_RED_INVOICE;
            operateLogService.addDeductLog(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getBusinessType(), TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()), deductOpLogEnum , "", UserUtil.getUserId(), UserUtil.getUserName());
        }

        return R.ok("匹配成功");
    }

    /**
     * 红票蓝冲审核申请
     * @param settlementId 结算单ID
     * @param invoiceAuditVO 审核信息
     */
    public R blueFlushAuditApply(Long settlementId, InvoiceAuditVO invoiceAuditVO) {
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(tXfSettlementEntity, "结算单信息不存在");

        Asserts.isTrue(CollectionUtil.isEmpty(invoiceAuditVO.getUuids()), "审核信息不能为空");
        Asserts.isFalse(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(tXfSettlementEntity.getBusinessType()), "只有协议结算单下红票需要蓝冲审核");

        List<InvoiceAudit> invoiceAudits = invoiceAuditService.search(Sets.newHashSet(invoiceAuditVO.getUuids()));
        if (CollectionUtil.isNotEmpty(invoiceAudits)) {
            InvoiceAudit invoiceAudit = invoiceAudits.get(0);
            Asserts.isTrue(AuditStatusEnum.AUDIT_FAIL.getValue().equals(invoiceAudit.getAuditStatus()), "当前红票已发起过蓝冲申请，购方已驳回，不可再次发起");
            Asserts.isTrue(AuditStatusEnum.AUDIT_PASS.getValue().equals(invoiceAudit.getAuditStatus()), "当前红票已发起过蓝冲申请，购方已审核通过，不可再次发起");
            Asserts.isTrue(AuditStatusEnum.NOT_AUDIT.getValue().equals(invoiceAudit.getAuditStatus()), "当前红票已发起过蓝冲申请，购方正在审核中，不可再次发起");
        }

        boolean add = invoiceAuditService.add(tXfSettlementEntity.getSettlementNo(), invoiceAuditVO.getUuids(), invoiceAuditVO.getRemark(), NumberUtils.INTEGER_ZERO);
        Asserts.isFalse(add, "提交审核失败，请稍后重试");

        if (!TXfSettlementStatusEnum.WAIT_CHECK.getCode().equals(tXfSettlementEntity.getSettlementStatus())) {
            // 将结算单更新为待审核状态
            statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.WAIT_CHECK, invoiceAuditVO.getRemark(),
                    SettlementApproveTypeEnum.BLUE_FLUSH, SettlementApproveStatusEnum.APPROVING, null);
        }
        return R.ok();
    }

    /**
     * 发票上传匹配、蓝冲 - 新版
     */
    @Transactional(rollbackFor = Exception.class)
    public R matchPreInvoiceV2(BackFillMatchRequest request) {
        Asserts.isTrue(StringUtils.isEmpty(request.getSettlementNo()), "结算单号不能为空");
        Asserts.isTrue(CollectionUtil.isEmpty(request.getVerifyBeanList()), "上传发票不能为空");

        QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, request.getSettlementNo());
        wrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
        wrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.DESTROY.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = preInvoiceDao.selectList(wrapper);
        Asserts.isTrue(CollectionUtil.isEmpty(tXfPreInvoiceEntities), "根据结算单号未找到预制发票");

        if (Constants.ZERO_STR.equals(request.getInvoiceColor())) {
            matchPreInvoiceRed(request, tXfPreInvoiceEntities);
        } else {
            matchPreInvoiceBlue(request, tXfPreInvoiceEntities);
        }
        return R.ok(null, "匹配成功");
    }

    /**
     * 上传红票
     */
    private void matchPreInvoiceRed(BackFillMatchRequest request, List<TXfPreInvoiceEntity> tXfPreInvoiceEntities) {
        //红冲
        Asserts.isTrue(request.getVerifyBeanList().stream().anyMatch(t -> new BigDecimal(t.getAmount()).compareTo(BigDecimal.ZERO) > 0), "上传的发票金额必须小于零");

        if (CollectionUtil.isNotEmpty(request.getVerifyBeanList())) {
            boolean isElec = request.getVerifyBeanList().stream().allMatch(t -> InvoiceTypeEnum.isElectronic(t.getInvoiceType()));
            boolean isNotElec = request.getVerifyBeanList().stream().noneMatch(t -> InvoiceTypeEnum.isElectronic(t.getInvoiceType()));
            Asserts.isFalse(isElec || isNotElec, "红票不允许纸电混合");
        }
        int successCount = 0;
        final Date updateDate = new Date();
        List<Long> successPreInvoiceIdList = Lists.newArrayList();
        //非零税率根据红字编号匹配，零税率根据金额匹配
        for (TXfPreInvoiceEntity preInvoiceEntity : tXfPreInvoiceEntities) {
            // 0税率
            boolean flag = BigDecimal.ZERO.compareTo(preInvoiceEntity.getTaxRate()) == 0;
            for (BackFillVerifyBean backFillVerifyBean : request.getVerifyBeanList()) {
                boolean equalsRedNo = preInvoiceEntity.getRedNotificationNo().equals(backFillVerifyBean.getRedNoticeNumber());
                boolean equalsAmount = preInvoiceEntity.getAmountWithoutTax().compareTo(new BigDecimal(backFillVerifyBean.getAmount())) == 0;
                // 非0税率（专票）==》红字信息表编号需要相同； 0税率（普票）==》金额需要相同
                boolean checkFlag = (!flag && equalsRedNo) || (flag && equalsAmount);
                if (checkFlag) {
                    if (!flag && StringUtils.isEmpty(preInvoiceEntity.getRedNotificationNo())) {
                        log.info("预制发票的红字信息编号不能为空");
                        continue;
                    }
                    log.info("发票回填后匹配--回填预制发票数据:[{}]", preInvoiceEntity.getId());
                    preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode());
                    preInvoiceEntity.setInvoiceCode(backFillVerifyBean.getInvoiceCode());
                    preInvoiceEntity.setInvoiceNo(backFillVerifyBean.getInvoiceNo());
                    preInvoiceEntity.setCheckCode(backFillVerifyBean.getCheckCode());
                    preInvoiceEntity.setMachineCode(backFillVerifyBean.getMachinecode());
                    preInvoiceEntity.setPaperDrewDate(backFillVerifyBean.getPaperDrewDate());
                    preInvoiceEntity.setUpdateTime(updateDate);
                    preInvoiceDao.updateById(preInvoiceEntity);

                    successPreInvoiceIdList.add(preInvoiceEntity.getId());
                    if (!flag) {
                        log.info("发票回填后匹配--核销已申请的红字信息表编号入参：{}", preInvoiceEntity.getRedNotificationNo());
                        Response<String> update = redNotificationOuterService.update(preInvoiceEntity.getRedNotificationNo(), ApproveStatus.ALREADY_USE);
                        log.info("发票回填后匹配--核销已申请的红字信息表编号响应：{}", JSONObject.toJSONString(update));
                    }
                    log.info("红票回填后匹配--修改发票状态并加上结算单号:[{}]-[{}]", backFillVerifyBean.getInvoiceNo(), backFillVerifyBean.getInvoiceCode());
                    UpdateWrapper<TDxRecordInvoiceEntity> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq(TDxRecordInvoiceEntity.UUID, backFillVerifyBean.getInvoiceCode() + backFillVerifyBean.getInvoiceNo());
                    
                    TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
                    tDxRecordInvoiceEntity.setSettlementNo(request.getSettlementNo());
                    tDxRecordInvoiceEntity.setInvoiceStatus(InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode());
                    tDxRecordInvoiceEntity.setStatusUpdateDate(updateDate);
                    tDxRecordInvoiceEntity.setIsDel(IsDealEnum.NO.getValue());
                    tDxRecordInvoiceEntity.setFlowType("5"); //flowType 是新红票 WALMART-3368
                    tDxRecordInvoiceDao.update(tDxRecordInvoiceEntity, updateWrapper);
                    successCount++;
                }
            }
        }
        Asserts.isTrue(successCount == 0, "红票的红字信息表未匹配到对应的预制发票");

        log.info("红票回填后匹配--修改结算单状态:[{}]", request.getSettlementNo());
        QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, request.getSettlementNo());
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
        Asserts.isNull(tXfSettlementEntity, "未找到结算单");
        Integer originSettlementStatus = tXfSettlementEntity.getSettlementStatus();

        String businessStatus;
        OperateLogEnum deductOpLogEnum;
        if (tXfPreInvoiceEntities.stream().allMatch(t -> TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode().equals(t.getPreInvoiceStatus()))) {
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode());
            businessStatus = TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getDesc();
            deductOpLogEnum = OperateLogEnum.SETTLEMENT_UPLOAD_ALL_RED_INVOICE;
        } else {
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode());
            businessStatus = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getDesc();
            deductOpLogEnum = OperateLogEnum.SETTLEMENT_UPLOAD_PART_RED_INVOICE;
        }
        if (!originSettlementStatus.equals(tXfSettlementEntity.getSettlementStatus())) {
            // 更新结算单状态，清除审核状态
            tXfSettlementEntity.setApproveStatus(SettlementApproveStatusEnum.DEFAULT.getCode());
        }
        tXfSettlementEntity.setUpdateTime(updateDate);
        tXfSettlementDao.updateById(tXfSettlementEntity);
        // 日志添加
        operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.UPLOAD_INVOICE, businessStatus, "",getUserId(), UserUtil.getUserName());
        operateLogService.addDeductLog(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getBusinessType(), TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()), deductOpLogEnum, "", UserUtil.getUserId(), UserUtil.getUserName());
        // 发送消息
        commonMessageService.sendPreInvoiceBackFillMessage(successPreInvoiceIdList);
    }

    /**
     * 上传蓝票
     */
    private void matchPreInvoiceBlue(BackFillMatchRequest request, List<TXfPreInvoiceEntity> tXfPreInvoiceEntities) {
        //蓝冲
        log.info("发票蓝冲:invoiceNo:{},invoiceCode:{}", request.getOriginInvoiceNo(), request.getOriginInvoiceCode());
        // 1. 蓝票未提交匹配状态
        Asserts.isFalse(StringUtils.isNotBlank(request.getOriginInvoiceNo()), "原红字发票号码不能为空");
        Asserts.isFalse(StringUtils.isNotBlank(request.getOriginInvoiceCode()), "原红字发票代码不能为空");

        //校验金额
        QueryWrapper<TDxRecordInvoiceEntity> invoiceWrapper = new QueryWrapper<>();
        invoiceWrapper.eq(TDxRecordInvoiceEntity.UUID, request.getOriginInvoiceCode() + request.getOriginInvoiceNo());
        TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectOne(invoiceWrapper);
        Asserts.isNull(invoiceEntity, "未找到蓝冲的发票");
        if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(tXfPreInvoiceEntities.get(0).getSettlementType())) {
            Asserts.isFalse(invoiceAuditService.passAudit(invoiceEntity.getUuid()), "通过审核后才能蓝冲。");
        }

        Set<String> uuid = request.getVerifyBeanList().stream().map(it -> it.getInvoiceCode() + it.getInvoiceNo()).collect(Collectors.toSet());
        // 查询是否已存在审核
        List<InvoiceAudit> search = invoiceAuditService.search(uuid);
        Asserts.isFalse(CollectionUtil.isEmpty(search) || search.stream().allMatch(audit -> AuditStatusEnum.AUDIT_FAIL.getValue().equals(audit.getAuditStatus())), "请勿重复提交蓝票");

        List<TDxRecordInvoiceEntity> list = new LambdaQueryChainWrapper<>(tDxRecordInvoiceDao).in(TDxRecordInvoiceEntity::getUuid, uuid).list();

        // 购销企业名称及税号的一致性校验（若抬头信息中存在空格，自动去空格后校验；若名称中存在括号，括号不区分中英文，不强制校验括号的中英文格式）
        list.forEach(invoice -> BackFillCheckTools.checkPurchaserAndSeller(tXfPreInvoiceEntities.get(0), invoice));

        // 税率相同，含税金额相同
        BigDecimal totalAmountWithTax = list.stream().map(TDxRecordInvoiceEntity::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Asserts.isFalse(totalAmountWithTax.add(invoiceEntity.getTotalAmount()).compareTo(BigDecimal.ZERO) == 0, "您上传的发票价税合计金额与代开金额不一致，请确认后再保存");

        Set<BigDecimal> taxRateSet = list.stream().map(entity -> Optional.ofNullable(entity.getTaxRate()).orElse(new BigDecimal("-1"))).collect(Collectors.toSet());
        Asserts.isFalse(taxRateSet.size() == 1 && taxRateSet.contains(Optional.ofNullable(invoiceEntity.getTaxRate()).orElse(new BigDecimal("-2"))), "您上传的发票税率与红票税率不一致，请确认后再保存");
        // 1.蓝票不能被匹配（组合生成结算单）
        List<Long> invoiceIdList = list.stream().map(TDxRecordInvoiceEntity::getId).collect(Collectors.toList());
        LambdaQueryWrapper<TXfBillDeductInvoiceDetailEntity> queryWrapper = Wrappers.lambdaQuery(TXfBillDeductInvoiceDetailEntity.class)
                .in(TXfBillDeductInvoiceDetailEntity::getInvoiceId, invoiceIdList)
                .eq(TXfBillDeductInvoiceDetailEntity::getStatus, NumberUtils.INTEGER_ZERO);
        Asserts.isTrue(tXfBillDeductInvoiceDetailDao.selectCount(queryWrapper) > 0, "蓝票需未提交匹配关系");
        Optional<String> first = list.stream().map(it -> {
            // 2.蓝票纸票未扫描，-电票未签收(电票查验成功自动签收)-  签收状态qsStatus
            // 3.蓝票未付款  付款状态
            // 4.蓝票未认证  认证状态
            // 5.蓝票发票类型不等于“商品” 发票类型
            // 以上均满足才能进行关联
            if (!InvoiceTypeEnum.isElectronic(it.getInvoiceType()) && InvoiceReceiptStatusEnum.YES.getCode().equalsIgnoreCase(it.getQsStatus())) {
                return "您上传的发票需要未签收，请确认后再保存";
            }
            if (InvoicePaymentStatusEnum.YES.getCode().equalsIgnoreCase(it.getBpmsPayStatus())) {
                return "您上传的发票需要未付款，请确认后再保存";
            }
            if (InvoiceAuthStatusEnum.SUCCESS_AUTH.code().equalsIgnoreCase(it.getAuthStatus())) {
                return "您上传的发票需要未认证，请确认后再保存";
            }
            if (InvoiceExchangeTypeEnum.SP.getCode().equalsIgnoreCase(it.getFlowType())) {
                return "您上传的发票类型需要是非“商品”类型，请确认后再保存";
            }
            return StringUtils.EMPTY;
        }).filter(StringUtils::isNotBlank).findFirst();
        if (first.isPresent()) {
            throw new EnhanceRuntimeException(first.get());
        }
        Asserts.isFalse(list.size() == uuid.size(), "未找到蓝票");
        // 保存红蓝关系
        blueInvoiceRelationService.saveBatch(request.getOriginInvoiceNo(), request.getOriginInvoiceCode(), request.getVerifyBeanList());

        // 蓝票需要审核认证（自动） 待认证成功再处理结算单和预制发票、发票状态，同时生成新的预制发票
        invoiceAuditService.saveOrUpdate(request.getSettlementNo(), Lists.newArrayList(uuid), "蓝冲自动审核", NumberUtils.INTEGER_ONE);
        // 修改蓝票关联结算单
        UpdateWrapper<TDxRecordInvoiceEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(TDxRecordInvoiceEntity.UUID, uuid);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
        tDxRecordInvoiceEntity.setSettlementNo(request.getSettlementNo());
        tDxRecordInvoiceEntity.setInvoiceStatus(InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode());
        tDxRecordInvoiceEntity.setStatusUpdateDate(new Date());
        tDxRecordInvoiceEntity.setIsDel(IsDealEnum.NO.getValue());
        tDxRecordInvoiceEntity.setFlowType("7"); //flowType 是直接认证 WALMART-3368
        tDxRecordInvoiceDao.update(tDxRecordInvoiceEntity, updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void autoAuthBlueFlush(InvoiceAudit invoiceAudit) {
        log.info("蓝冲发票自动审核begin:[{}]", JSON.toJSONString(invoiceAudit));
        // 查询发票
        LambdaQueryWrapper<TDxRecordInvoiceEntity> queryWrapper = Wrappers.lambdaQuery(TDxRecordInvoiceEntity.class)
                .eq(TDxRecordInvoiceEntity::getUuid, invoiceAudit.getInvoiceUuid());
        TDxRecordInvoiceEntity recordInvoiceEntity = tDxRecordInvoiceDao.selectOne(queryWrapper);
        Asserts.isNull(recordInvoiceEntity, "未找到蓝票");

        // 判断是否认证结束
        Asserts.isFalse(InvoiceAuthStatusEnum.SUCCESS_AUTH.code().equalsIgnoreCase(recordInvoiceEntity.getAuthStatus())
                || InvoiceAuthStatusEnum.FAIL_AUTH.code().equalsIgnoreCase(recordInvoiceEntity.getAuthStatus()),
                String.format("发票[%s]还未认证", recordInvoiceEntity.getUuid()));
        // 查询结算单
        LambdaQueryWrapper<TXfSettlementEntity> settlementQueryWrapper = Wrappers.lambdaQuery(TXfSettlementEntity.class)
                .eq(TXfSettlementEntity::getSettlementNo, recordInvoiceEntity.getSettlementNo());
        TXfSettlementEntity settlementEntity = tXfSettlementDao.selectOne(settlementQueryWrapper);

        boolean payment = false;
        if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(settlementEntity.getBusinessType())) {
            // 协议结算单蓝票  校验发票付款状态
            payment = Constants.ONE_STR.equals(recordInvoiceEntity.getBpmsPayStatus());
        }

        final Date updateDate = new Date();
        // 是否直接认证
        boolean directAuth = InvoiceAuthStatusEnum.SUCCESS_AUTH.code().equalsIgnoreCase(recordInvoiceEntity.getAuthStatus())
                && InvoiceExchangeTypeEnum.ZJRZ.getCode().equalsIgnoreCase(recordInvoiceEntity.getFlowType());
        if (directAuth && !payment) {
            // 直接认证 ， 蓝冲成功
            invoiceAuditService.audit(Lists.newArrayList(invoiceAudit.getInvoiceUuid()), AuditStatusEnum.AUDIT_PASS.getValue(), "认证成功");
            // 查询红蓝关系， 判断是否都已经审核通过
            TXfBlueRelationEntity blueRelation = blueInvoiceRelationService.getByBlueInfo(recordInvoiceEntity.getInvoiceNo(), recordInvoiceEntity.getInvoiceCode());
            Asserts.isNull(blueRelation, "不存在红蓝关系");

            String settlementNo = invoiceAudit.getSettlementNo();
            String redInvoiceNo = blueRelation.getRedInvoiceNo();
            String redInvoiceCode = blueRelation.getRedInvoiceCode();
            String redUuid = redInvoiceCode + redInvoiceNo;

            String key = "blue_flush_auth_auto:" + redUuid;
            lockClient.tryLock(key, () -> blueFlushDeal(settlementEntity.getId(), settlementNo, redInvoiceNo, redInvoiceCode, redUuid), -1, 1);
        } else {
            // 非直接认证/认证失败/已付款 蓝冲异常
            invoiceAuditService.audit(Lists.newArrayList(invoiceAudit.getInvoiceUuid()), AuditStatusEnum.AUDIT_FAIL.getValue(), "蓝冲异常");
            // 蓝票更新成异常状态  异常状态的可以删除
            UpdateWrapper<TDxRecordInvoiceEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in(TDxRecordInvoiceEntity.UUID, invoiceAudit.getInvoiceUuid());
            TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
            tDxRecordInvoiceEntity.setSettlementNo(invoiceAudit.getSettlementNo());
            tDxRecordInvoiceEntity.setInvoiceStatus(InvoiceStatusEnum.ANTU_STATUS_EXCEPTION.getCode());
            tDxRecordInvoiceEntity.setStatusUpdateDate(updateDate);
            tDxRecordInvoiceDao.update(tDxRecordInvoiceEntity, updateWrapper);
        }
    }

    /**
     * 蓝票直接认证成功后判断红票是否完全被红冲，并处理
     */
    private void blueFlushDeal(Long settlementId, String settlementNo, String redInvoiceNo, String redInvoiceCode, String redUuid) {
        List<TXfBlueRelationEntity> redBlueRelationList = blueInvoiceRelationService.getByRedInfo(redInvoiceNo, redInvoiceCode);
        Asserts.isFalse(CollectionUtil.isNotEmpty(redBlueRelationList), "不存在红蓝关系");

        Set<String> uuidList = redBlueRelationList.stream().map(entity -> entity.getBlueInvoiceCode() + entity.getBlueInvoiceNo()).collect(Collectors.toSet());
        List<InvoiceAudit> invoiceAuditList = invoiceAuditService.search(uuidList);
        if (!invoiceAuditList.stream().allMatch(entity -> AuditStatusEnum.AUDIT_PASS.getValue().equalsIgnoreCase(entity.getAuditStatus()))) {
            log.info("蓝冲使用的蓝票还未全部认证成功:[{}]-[{}]", redInvoiceNo, redInvoiceCode);
            return;
        }
        // 将红票更新成蓝冲状态
        recordInvoiceService.blue4RedInvoice(redInvoiceNo, redInvoiceCode);

        // 作废预制发票
        UpdateWrapper<TXfPreInvoiceEntity> preInvoiceWrapper = new UpdateWrapper<>();
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, invoiceAuditList.get(0).getSettlementNo());
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.INVOICE_CODE, redInvoiceCode);
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.INVOICE_NO, redInvoiceNo);
        TXfPreInvoiceEntity redPreInvoiceEntity = preInvoiceDao.selectOne(preInvoiceWrapper);

        TXfPreInvoiceEntity preInvoiceEntityU = new TXfPreInvoiceEntity();
        preInvoiceEntityU.setId(redPreInvoiceEntity.getId());
        preInvoiceEntityU.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
        preInvoiceEntityU.setUpdateTime(new Date());
        preInvoiceDao.updateById(preInvoiceEntityU);

        commonMessageService.sendPreInvoiceDiscardMessage(Lists.newArrayList(redPreInvoiceEntity.getId()));
        // 生成新的预制发票
        preinvoiceService.makeNewRedPreInvoice(redPreInvoiceEntity);

        //作废扫描表发票
        TDxInvoiceEntity tDxInvoiceEntity = new TDxInvoiceEntity();
        tDxInvoiceEntity.setIsdel(IsDealEnum.YES.getValue());
        tDxInvoiceEntity.setUpdateDate(new Date());
        UpdateWrapper<TDxInvoiceEntity> invoiceWrapper = new UpdateWrapper<>();
        invoiceWrapper.eq(TDxInvoiceEntity.UUID, redUuid);
        tDxInvoiceDao.update(tDxInvoiceEntity, invoiceWrapper);
        TXfSettlementEntity originSettlement = tXfSettlementDao.selectById(settlementId);
        log.info("结算单原状态:[{}]-[{}]", originSettlement.getSettlementNo(), originSettlement.getSettlementStatus());
        if (!TXfSettlementStatusEnum.WAIT_CHECK.getCode().equals(originSettlement.getSettlementStatus())) {
            // 结算单还处于待审核状态下，说明还有其他红字发票需要审核蓝冲 无需修改状态
            recordInvoiceService.updateSettlement(settlementNo, originSettlement.getSettlementStatus());
        }

        // 重新查询结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        // 添加日志
        operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.BLUE_FLUSH_INVOICE, Optional.ofNullable(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus())).map(TXfSettlementStatusEnum::getDesc).orElse(""), "", getUserId(), UserUtil.getUserName());
        OperateLogEnum deductOpLogEnum = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode().equals(tXfSettlementEntity.getSettlementStatus()) ? OperateLogEnum.SETTLEMENT_BLUE_FLUSH_PART_RED_INVOICE : OperateLogEnum.SETTLEMENT_BLUE_FLUSH_ALL_RED_INVOICE;
        operateLogService.addDeductLog(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getBusinessType(), TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()), deductOpLogEnum, "", UserUtil.getUserId(), UserUtil.getUserName());
    }

    public R checkCommitRequest(BackFillCommitVerifyRequest request) {
        if (StringUtils.isEmpty(request.getSettlementNo())) {
            return R.fail("结算单号不能为空");
        }
        if (StringUtils.isEmpty(request.getInvoiceColor())) {
            return R.fail("发票颜色不能为空");
        }
        if ("0".equals(request.getInvoiceColor())) {
            if(request.getVerifyBeanList().stream().anyMatch(t -> new BigDecimal(t.getAmount()).compareTo(BigDecimal.ZERO) > 0)){
                return R.fail("上传的发票金额必须小于零");
            }
        } else {
            if (!CollectionUtils.isEmpty(request.getVerifyBeanList())) {
                if(request.getVerifyBeanList().stream().anyMatch(t -> new BigDecimal(t.getAmount()).compareTo(BigDecimal.ZERO) < 0)){
                    return R.fail("上传的发票金额必须大于零");
                }
                if (StringUtils.isEmpty(request.getOriginInvoiceCode())) {
                    return R.fail("被蓝冲发票代码不能为空");
                }
                if (StringUtils.isEmpty(request.getOriginInvoiceNo())) {
                    return R.fail("被蓝冲发票号码不能为空");
                }
                QueryWrapper<TDxRecordInvoiceEntity> invoiceWrapper = new QueryWrapper<>();
                invoiceWrapper.eq(TDxRecordInvoiceEntity.UUID, request.getOriginInvoiceCode() + request.getOriginInvoiceNo());
                TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectOne(invoiceWrapper);
                if (invoiceEntity != null) {
                    if (InvoiceTypeEnum.isElectronic(invoiceEntity.getInvoiceType())) {
                        if(request.getVerifyBeanList().stream().anyMatch(t -> !InvoiceTypeEnum.isElectronic(t.getInvoiceType()))){
                            throw new EnhanceRuntimeException("原发票为电票，蓝冲的必须也为电票");
                        }
                    }
                } else {
                    return R.fail("未找到蓝冲的发票");
                }
            }
        }
        return R.ok("校验成功");
    }
}

