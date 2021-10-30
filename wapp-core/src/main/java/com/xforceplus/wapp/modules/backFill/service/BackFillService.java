package com.xforceplus.wapp.modules.backFill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.backFill.model.*;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfElecUploadRecordDetailDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
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
public class BackFillService  {

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

    public BackFillService(@Value("${wapp.integration.tenant-id}")
                              String tenantId) {
        this.tenantId=tenantId;
        defaultHeader =  new HashMap<>();
        defaultHeader.put("rpcType", "http");
        defaultHeader.put("x-app-client", "janus");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("accept-encoding","");
    }


    public R commitVerify(BackFillCommitVerifyRequest request){
        R r = checkCommitRequest(request);
        if(R.FAIL.equals(r)){
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
            this.electronicUploadRecordDetailDao.insert(detailEntity);
        }
        return R.ok(batchNo);
    }

    public VerificationResponse parseOfd(byte[] ofd,String batchNo) {
        OfdParseRequest request = new OfdParseRequest();
        request.setOfdEncode(Base64.encodeBase64String(ofd));
        request.setTenantCode(tenantCode);
        // 仅解析和验签
        request.setType("1");
        try {
            defaultHeader.put("serialNo",batchNo);
            final String responseBody = httpClientFactory.post(ofdAction, defaultHeader, JSONObject.toJSONString(request), "");
            log.info("发送ofd解析结果:{}", responseBody);
            final OfdResponse ofdResponse = JSONObject.parseObject(responseBody, OfdResponse.class);
            if (ofdResponse.isOk()) {
                final OfdResponse.OfdResponseResult result = ofdResponse.getResult();
                final InvoiceMain invoiceMain = result.getInvoiceMain();
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

    public OfdResponse signOfd(byte[] ofd,String businessNo) {
        OfdParseRequest request = new OfdParseRequest();
        request.setOfdEncode(Base64.encodeBase64String(ofd));
        request.setTenantCode(tenantCode);
        // 仅解析和验签
        request.setType("1");
        try {
            defaultHeader.put("serialNo",businessNo);
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
                try {
                    final VerificationResponse verificationResponse = this.parseOfd(ofd,batchNo);
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
     * @param file
     * @param fileType
     * @param detailEntity
     */
    private void uploadFile(byte[] file,Integer fileType,TXfElecUploadRecordDetailEntity detailEntity) {
    	
    	 try {
    		 
    		StringBuffer fileName = new StringBuffer();
    		fileName.append(UUID.randomUUID().toString());
    		fileName.append(".");
    		if(fileType.equals(Constants.FILE_TYPE_OFD)) {
    			fileName.append(Constants.SUFFIX_OF_OFD);
    		}else {
    			fileName.append(Constants.SUFFIX_OF_PDF);
    		}
    		 
			String uploadResult  = fileService.uploadFile(file,fileName.toString());
			
			UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
			
			UploadFileResultData data = uploadFileResult.getData();
			
			 detailEntity.setFileType(fileType.equals(Constants.FILE_TYPE_OFD));
	         detailEntity.setUploadId(data.getUploadId());
	         detailEntity.setUploadPath(data.getUploadPath());
	         
		} catch (Exception e) {
			
			 log.error("调用文件服务器失败:{}",e);
	         throw new RRException("调用文件服务器失败:"+e.getMessage());
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
        wrapper.eq(TXfElecUploadRecordDetailEntity.BATCH_NO,batchNo);
        final List<TXfElecUploadRecordDetailEntity> detailEntities = this.electronicUploadRecordDetailDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(detailEntities) || detailEntities.size() != recordEntity.getTotalNum()) {
            log.info("selectByBatchNo total:{},size:{}",recordEntity.getTotalNum(),detailEntities==null?0:detailEntities.size());
            uploadResult.setStep(0);
            return uploadResult;
        }

        uploadResult.setStep(1);
        uploadResult.setFailureNum(recordEntity.getFailureNum());
        uploadResult.setSucceedNum(recordEntity.getSucceedNum());


        List<UploadResult.SucceedInvoice> invoiceEntities=new ArrayList<>();

        List<UploadResult.FailureInvoice> failureInvoices=new ArrayList<>();

        for (TXfElecUploadRecordDetailEntity detailEntity : detailEntities) {
            if (detailEntity.getStatus()){
                final String invoiceNo = detailEntity.getInvoiceNo();
                final String invoiceCode = detailEntity.getInvoiceCode();
                final List<InvoiceEntity> invoices = matchDao.invoiceQueryList(Collections.singletonMap("uuid", invoiceCode + invoiceNo));
                if (!CollectionUtils.isEmpty(invoices)){
                    final InvoiceEntity invoiceEntity = invoices.get(0);
                    final UploadResult.SucceedInvoice succeedInvoice = succeedInvoiceMapper.toSucceed(invoiceEntity);
                    if(detailEntity.getFileType() != null ){
                        if (detailEntity.getFileType()){
                            succeedInvoice.setFileType(Constants.SUFFIX_OF_OFD);
                        }else {
                            succeedInvoice.setFileType(Constants.SUFFIX_OF_PDF);
                        }
                    }
                    invoiceEntities.add(succeedInvoice);
                } else{
                    UploadResult.FailureInvoice failureInvoice = new UploadResult.FailureInvoice();
                    failureInvoice.setInvoiceNo(detailEntity.getInvoiceNo());
                    failureInvoice.setInvoiceCode(detailEntity.getInvoiceCode());
                    failureInvoice.setMsg(detailEntity.getReason());
                    failureInvoices.add(failureInvoice);
                }
            }
        }

        uploadResult.setSucceedInvoices(invoiceEntities);

        uploadResult.setFailureInvoices(failureInvoices);

        return uploadResult;
    }

    @Transactional
    public R matchPreInvoice(BackFillMatchRequest request){
        if(StringUtils.isEmpty(request.getSettlementNo())){
            return R.fail("结算单号不能为空");
        }
        if(CollectionUtils.isEmpty(request.getVerifyBeanList())){
            return R.fail("上传发票不能为空");
        }
        QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO,request.getSettlementNo());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = preInvoiceDao.selectList(wrapper);
        if(CollectionUtils.isEmpty(tXfPreInvoiceEntities)){
            return R.fail("根据结算单号未找到预制发票");
        }
        if("0".equals(request.getInvoiceColer())){
            for (TXfPreInvoiceEntity preInvoiceEntity : tXfPreInvoiceEntities) {
                if(StringUtils.isEmpty(preInvoiceEntity.getRedNotificationNo())){
                    return R.fail("预制发票的红字信息编号不能为空");
                }
                if(request.getVerifyBeanList().stream().anyMatch(t -> preInvoiceEntity.getRedNotificationNo().equals(t.getRedNoticeNumber()))){
                    log.info("发票回填后匹配--修改预制发票状态");
                    preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode());
                    preInvoiceDao.updateById(preInvoiceEntity);
                    log.info("发票回填后匹配--核销已申请的红字信息表编号入参：{}",preInvoiceEntity.getRedNotificationNo());
                    Response<String> update = redNotificationOuterService.update(preInvoiceEntity.getRedNotificationNo(), ApproveStatus.ALREADY_USE);
                    log.info("发票回填后匹配--核销已申请的红字信息表编号响应：{}",JSONObject.toJSONString(update));
                }else{
                    return R.fail("预制发票的红字信息编号匹配失败");
                }
            }
            for (BackFillVerifyBean backFillVerifyBean : request.getVerifyBeanList()) {
                log.info("红票回填后匹配--修改发票状态并加上结算单号");
                UpdateWrapper<TDxRecordInvoiceEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(TDxRecordInvoiceEntity.UUID,backFillVerifyBean.getInvoiceCode()+backFillVerifyBean.getInvoiceNo());
                TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
                tDxRecordInvoiceEntity.setSettlementNo(request.getSettlementNo());
                //电子发票改为签收状态
                if(InvoiceTypeEnum.isElectronic(backFillVerifyBean.getInvoiceType())){
                    tDxRecordInvoiceEntity.setQsStatus("1");
                }
                tDxRecordInvoiceDao.update(tDxRecordInvoiceEntity,updateWrapper);
            }
            log.info("红票回填后匹配--修改结算单状态");
            QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO,request.getSettlementNo());
            TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
            String businessStatus = "";
            if(tXfSettlementEntity != null){
                if(tXfPreInvoiceEntities.stream().allMatch(t -> TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode().equals(t.getPreInvoiceStatus()))){
                    tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode());
                    businessStatus = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getDesc();
                }else{
                    tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode());
                    businessStatus = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getDesc();
                }
                tXfSettlementDao.updateById(tXfSettlementEntity);
                operateLogService.add(tXfSettlementEntity.getId(),OperateLogEnum.UPLOAD_INVOICE,businessStatus, UserUtil.getUserId(),UserUtil.getUserName());
            }else{
                throw new EnhanceRuntimeException("未找到结算单");
            }
        }else{
            log.info("发票蓝冲:invoiceNo:{},invoiceCode:{}",request.getOriginInvoiceNo(),request.getOriginInvoiceCode());

            if (org.apache.commons.lang3.StringUtils.isBlank(request.getOriginInvoiceNo())){
                throw new EnhanceRuntimeException("原红字发票号码不能为空");
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(request.getOriginInvoiceCode())){
                throw new EnhanceRuntimeException("原红字发票代码不能为空");
            }

            // 保存红蓝关系
            blueInvoiceRelationService.saveBatch(request.getOriginInvoiceNo(), request.getOriginInvoiceCode(), request.getVerifyBeanList());

            log.info("蓝票回填后匹配--修改发票状态和预制发票状态和结算单状态");
            recordInvoiceService.blue4RedInvoice(request.getOriginInvoiceNo(),request.getOriginInvoiceCode());

            //作废预制发票
            UpdateWrapper<TXfPreInvoiceEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set(TXfPreInvoiceEntity.SETTLEMENT_NO,request.getSettlementNo());
            TXfPreInvoiceEntity preInvoiceEntity = new TXfPreInvoiceEntity();
            preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
            preInvoiceDao.update(preInvoiceEntity,updateWrapper);

        }

        return R.ok("匹配成功");
    }

    public R checkCommitRequest(BackFillCommitVerifyRequest request){
        if(StringUtils.isEmpty(request.getSettlementNo())){
            return R.fail("结算单号不能为空");
        }
        if(CollectionUtils.isEmpty(request.getVerifyBeanList())){
            return R.fail("上传发票不能为空");
        }
        if("0".equals(request.getInvoiceColer())){
            //红票上传校验
            QueryWrapper<TXfPreInvoiceEntity> preinvoiceWrapper = new QueryWrapper<>();
            preinvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO,request.getSettlementNo());
            List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = preInvoiceDao.selectList(preinvoiceWrapper);
            if(CollectionUtils.isEmpty(tXfPreInvoiceEntities)){
                return R.fail("根据结算单号未找到预制发票");
            }
            long count = tXfPreInvoiceEntities.stream().filter(t -> TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(t.getPreInvoiceStatus())).count();
            if(request.getVerifyBeanList().size() > count){
                return R.fail("您最多只需要上传"+count+"张发票，请确认后再试");
            }
            if(tXfPreInvoiceEntities.stream().anyMatch(t -> StringUtils.isEmpty(t.getRedNotificationNo()))){
                return R.fail("当前红字信息表由购方发起申请或审核，暂未完成；\r\n" +
                        "完成后，您可以继续添加发票！\r\n" +
                        "请及时关注票据状态！或联系购货方联系");
            }
        } else{
            if(StringUtils.isEmpty(request.getOriginInvoiceCode())){
                return R.fail("蓝冲发票代码不能为空");
            }
            if(StringUtils.isEmpty(request.getOriginInvoiceNo())){
                return R.fail("蓝冲发票号码不能为空");
            }
            QueryWrapper<TDxRecordInvoiceEntity> invoiceWrapper = new QueryWrapper<>();
            invoiceWrapper.eq(TDxRecordInvoiceEntity.UUID,request.getOriginInvoiceCode()+request.getOriginInvoiceNo());
            TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectOne(invoiceWrapper);
            if(invoiceEntity != null){
                BigDecimal  amount = request.getVerifyBeanList().stream().map(t -> new BigDecimal(t.getAmount())).reduce(BigDecimal.ZERO,BigDecimal :: add);
                if(amount.compareTo(invoiceEntity.getInvoiceAmount()) != 0){
                    return R.fail("您上传的发票合计金额与代开金额不一致，请确认后再提交");
                }
            }else{
                return R.fail("未找到蓝冲的发票");
            }
        }
        return R.ok("校验成功");
    }
}
