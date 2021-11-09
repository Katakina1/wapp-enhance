package com.xforceplus.wapp.modules.backFill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.modules.backFill.model.*;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.daoExt.ElectronicUploadRecordDao;
import com.xforceplus.wapp.repository.daoExt.MatchDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


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
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(settlementNo)){
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


    public R commitVerify(BackFillCommitVerifyRequest request) {
        R r = checkCommitRequest(request);
        if (R.FAIL.equals(r.getCode())) {
            return r;
        }
        String batchNo = UUID.randomUUID().toString().replace("-", "");
        TXfElecUploadRecordEntity recordEntity = new TXfElecUploadRecordEntity();
        recordEntity.setCreateTime(new Date());
        recordEntity.setTotalNum(request.getVerifyBeanList().size());
        recordEntity.setUpdateTime(recordEntity.getCreateTime());
        recordEntity.setBatchNo(batchNo);
        recordEntity.setCreateUser(String.valueOf(request.getOpUserId()));
        recordEntity.setId(idSequence.nextId());
        recordEntity.setJvCode(request.getJvCode());
        recordEntity.setVendorId(request.getVendorId());
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
            detailEntity.setCreateUser(String.valueOf(request.getOpUserId()));
            detailEntity.setSettlementNo(request.getSettlementNo());
            detailEntity.setInvoiceCode(backFillVerifyBean.getInvoiceCode());
            detailEntity.setInvoiceNo(backFillVerifyBean.getInvoiceNo());
            detailEntity.setPaperDrewDate(backFillVerifyBean.getPaperDrewDate());
            detailEntity.setAmount(new BigDecimal(backFillVerifyBean.getAmount()));
            detailEntity.setCheckCode(backFillVerifyBean.getCheckCode());
            detailEntity.setCreateTime(new Date());
            detailEntity.setCreateUser(request.getOpUserId().toString());
            try {
                VerificationResponse verificationResponse = verificationService.verify(verificationRequest);
                log.info("纸票发票回填--发票验真同步返回结果：{}", JSON.toJSONString(verificationResponse));

                if (verificationResponse.isOK()) {
                    final String verifyTaskId = verificationResponse.getResult();
                    detailEntity.setXfVerifyTaskId(verifyTaskId);
                    detailEntity.setStatus(true);
                } else {
                    log.warn("发票代码:{},发票号码：{}，发票验真请求失败:{}", backFillVerifyBean.getInvoiceCode(), backFillVerifyBean.getInvoiceNo(), verificationResponse.getMessage());
                    detailEntity.setStatus(false);
                    detailEntity.setReason(verificationResponse.getMessage());
                    recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                }
            } catch (EnhanceRuntimeException e) {
                recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                detailEntity.setStatus(false);
                detailEntity.setReason(e.getMessage());
            }
            tXfElecUploadRecordDao.updateById(recordEntity);
            electronicUploadRecordDetailDao.insert(detailEntity);
        }
        return R.ok(batchNo);
    }

    public VerificationResponse parseOfd(byte[] ofd, String batchNo,TXfElecUploadRecordDetailEntity detailEntity) {
        OfdParseRequest request = new OfdParseRequest();
        request.setOfdEncode(Base64.encodeBase64String(ofd));
        request.setTenantCode(tenantCode);
        // 仅解析和验签
        request.setType("1");
        try {
            defaultHeader.put("serialNo", batchNo);
            final String responseBody = httpClientFactory.post(ofdAction, defaultHeader, JSONObject.toJSONString(request), "");
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
            defaultHeader.put("serialNo", businessNo);
            final String responseBody = httpClientFactory.post(ofdAction, defaultHeader, JSONObject.toJSONString(request), "");
            log.info("发送ofd解析结果:{}", responseBody);
            return JSONObject.parseObject(responseBody, OfdResponse.class);
        } catch (IOException e) {
            log.error("ofd解析请求发起失败:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("ofd解析请求发起失败:" + e.getMessage());
        }
    }

    public String uploadAndVerify(SpecialElecUploadDto specialElecUploadDto) {
        List<byte[]> pdfs = specialElecUploadDto.getPdfs();
        if (pdfs == null) {
            pdfs = Collections.emptyList();
        }
        List<byte[]> ofds = specialElecUploadDto.getOfds();
        if (ofds == null) {
            ofds = Collections.emptyList();
        }
        final int totalNum = ofds.size() + pdfs.size();
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
                detailEntity.setSettlementNo(specialElecUploadDto.getSettlementNo());
                try {
                    final VerificationResponse verificationResponse = this.parseOfd(ofd, batchNo,detailEntity);
                    if (verificationResponse.isOK()) {
                        final String verifyTaskId = verificationResponse.getResult();
                        detailEntity.setXfVerifyTaskId(verifyTaskId);
                        detailEntity.setStatus(true);
                        //文件上传
                        uploadFile(ofd, 0, detailEntity);
                    } else {
                        detailEntity.setStatus(false);
                        detailEntity.setReason(verificationResponse.getMessage());
                        recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                    }
                } catch (EnhanceRuntimeException e) {
                    recordEntity.setFailureNum(recordEntity.getFailureNum() + 1);
                    detailEntity.setStatus(false);
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
                    detailEntity.setStatus(true);
                    detailEntity.setCreateUser(String.valueOf(specialElecUploadDto.getUserId()));
                    detailEntity.setFileType(true);
                    detailEntity.setSettlementNo(specialElecUploadDto.getSettlementNo());
                    detailEntity.setCreateTime(new Date());
                    //文件上传
                    uploadFile(m.getValue(), Constants.FILE_TYPE_PDF, detailEntity);
                    this.electronicUploadRecordDetailDao.insert(detailEntity);

                }
                final int failureSize = pdfs.size() - discernTaskMap.size();
                if (failureSize > 0) {
                    recordEntity.setFailureNum(recordEntity.getFailureNum() + failureSize);
                }
            }
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
    private void uploadFile(byte[] file, Integer fileType, TXfElecUploadRecordDetailEntity detailEntity) {

        try {

            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString());
            fileName.append(".");
            if (fileType.equals(Constants.FILE_TYPE_OFD)) {
                fileName.append(Constants.SUFFIX_OF_OFD);
            } else {
                fileName.append(Constants.SUFFIX_OF_PDF);
            }

            String uploadResult = fileService.uploadFile(file, fileName.toString());

            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);

            UploadFileResultData data = uploadFileResult.getData();

            detailEntity.setFileType(fileType.equals(Constants.FILE_TYPE_OFD));
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
            if (detailEntity.getStatus()) {
                final String invoiceNo = detailEntity.getInvoiceNo();
                final String invoiceCode = detailEntity.getInvoiceCode();
                final List<InvoiceEntity> invoices = matchDao.invoiceQueryList(Collections.singletonMap("uuid", invoiceCode + invoiceNo));
                if (!CollectionUtils.isEmpty(invoices)) {
                    final InvoiceEntity invoiceEntity = invoices.get(0);
                    final UploadResult.SucceedInvoice succeedInvoice = succeedInvoiceMapper.toSucceed(invoiceEntity);
                    if (detailEntity.getFileType() != null) {
                        if (detailEntity.getFileType()) {
                            succeedInvoice.setFileType(Constants.SUFFIX_OF_OFD);
                        } else {
                            succeedInvoice.setFileType(Constants.SUFFIX_OF_PDF);
                        }
                    }
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
            //非零税率根据红字编号匹配，零税率根据金额匹配
            boolean flag = BigDecimal.ZERO.compareTo(tXfPreInvoiceEntities.get(0).getTaxRate()) == 0;
            for (TXfPreInvoiceEntity preInvoiceEntity : tXfPreInvoiceEntities) {
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
                        //电子发票改为签收状态
                        if (InvoiceTypeEnum.isElectronic(backFillVerifyBean.getInvoiceType())) {
                            tDxRecordInvoiceEntity.setQsStatus("1");
                        }
                        tDxRecordInvoiceDao.update(tDxRecordInvoiceEntity, updateWrapper);
                    }
                }
            }
            log.info("红票回填后匹配--修改结算单状态");
            QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, request.getSettlementNo());
            TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
            String businessStatus = "";
            if (tXfSettlementEntity != null) {
                if (tXfPreInvoiceEntities.stream().allMatch(t -> TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode().equals(t.getPreInvoiceStatus()))) {
                    tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode());
                    businessStatus = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getDesc();
                } else {
                    tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode());
                    businessStatus = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getDesc();
                }
                tXfSettlementEntity.setUpdateTime(updateDate);
                tXfSettlementDao.updateById(tXfSettlementEntity);
                operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.UPLOAD_INVOICE, businessStatus, UserUtil.getUserId(), UserUtil.getUserName());
            } else {
                throw new EnhanceRuntimeException("未找到结算单");
            }
        } else {
            log.info("发票蓝冲:invoiceNo:{},invoiceCode:{}", request.getOriginInvoiceNo(), request.getOriginInvoiceCode());

            if (org.apache.commons.lang3.StringUtils.isBlank(request.getOriginInvoiceNo())) {
                throw new EnhanceRuntimeException("原红字发票号码不能为空");
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(request.getOriginInvoiceCode())) {
                throw new EnhanceRuntimeException("原红字发票代码不能为空");
            }
            //校验金额
            if (!CollectionUtils.isEmpty(request.getVerifyBeanList())) {
                QueryWrapper<TDxRecordInvoiceEntity> invoiceWrapper = new QueryWrapper<>();
                invoiceWrapper.eq(TDxRecordInvoiceEntity.UUID, request.getOriginInvoiceCode() + request.getOriginInvoiceNo());
                TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectOne(invoiceWrapper);
                if (invoiceEntity != null) {
                    BigDecimal amount = request.getVerifyBeanList().stream().map(t -> new BigDecimal(t.getAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (amount.add(invoiceEntity.getInvoiceAmount()).compareTo(BigDecimal.ZERO) != 0) {
                        throw new EnhanceRuntimeException("您上传的发票合计金额与代开金额不一致，请确认后再保存");
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
            TXfPreInvoiceEntity preInvoiceEntity = new TXfPreInvoiceEntity();
            preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
            preInvoiceEntity.setUpdateTime(updateDate);
            preInvoiceDao.update(preInvoiceEntity, updateWrapper);

            //作废扫描表发票
            TDxInvoiceEntity tDxInvoiceEntity = new TDxInvoiceEntity();
            tDxInvoiceEntity.setIsdel(IsDealEnum.YES.getValue());
            tDxInvoiceEntity.setUpdateDate(updateDate);
            UpdateWrapper<TDxInvoiceEntity> invoiceWrapper = new UpdateWrapper<>();
            wrapper.eq(TDxInvoiceEntity.UUID,request.getOriginInvoiceCode()+request.getOriginInvoiceNo());
            tDxInvoiceDao.update(tDxInvoiceEntity,invoiceWrapper);
            //修改结算单状态
            recordInvoiceService.updateSettlement(request.getSettlementNo(),request.getOriginInvoiceCode(),request.getOriginInvoiceNo());


        }

        return R.ok("匹配成功");
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
