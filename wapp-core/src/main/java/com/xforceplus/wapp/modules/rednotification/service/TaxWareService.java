package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.InvoiceOrigin;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.QueryModel;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationExportPdfRequest;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import com.xforceplus.wapp.modules.rednotification.util.HttpUtils;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationLogEntity;
import com.xforceplus.wapp.service.CommPreInvoiceService;
import com.xforceplus.wapp.service.CommSettlementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 调用税件
 */
@Service
@Slf4j
public class TaxWareService {

    @Autowired
    private HttpClientFactory httpClientFactory;
    @Autowired
    RedNotificationMainService redNotificationMainService;

    @Autowired
    RedNotificationLogService redNotificationLogService;
    @Autowired
    CommPreInvoiceService commPreInvoiceService;
    @Autowired
    ThreadPoolExecutor redNotificationThreadPool;



    @Value("${wapp.integration.action.terminals}")
    private String getTerminalAction;
    @Value("${wapp.integration.action.rednotification}")
    private String applyRedAction;
    @Value("${wapp.integration.action.rollback}")
    private String rollbackAction;
    @Value("${wapp.integration.action.genredpdf}")
    private String genredpdfAction;

    @Value("${wapp.integration.tenant-id:1203939049971830784}")
    public String tenantId;
    @Value("${wapp.integration.tenant-name:Walmart}")
    public String tenantName;
    @Value("${wapp.integration.authentication}")
    public String authentication;
    @Value("${wapp.integration.host.http}")
    public String host;

    private final Map<String, String> defaultHeader;


    private static final String VERIFICATION_LEVEL = "1";
    private static final String SUCCESSFUL_PROCESS_FLAG = "1";


    public TaxWareService(@Value("${wapp.integration.tenant-id:1203939049971830784}")  String tenantId) {
        defaultHeader =  new HashMap<>();
        defaultHeader.put("rpcType", "http");
//        defaultHeader.put("x-app-client", "janus");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("tenantId", tenantId);
        defaultHeader.put("tenantCode", tenantId);
        defaultHeader.put("accept-encoding","");
    }

    Gson gson = new Gson();

    public GetTerminalResponse getTerminal(String taxNo) {
        try {
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            paramMeterMap.put("taxNo",taxNo);

            Map<String, String> requestHeaderMap = getRequestHeaderMap(defaultHeader);
            // 集成平台没传递，邮件报警
            requestHeaderMap.put("serialNo",taxNo);
            final String get = httpClientFactory.get(getTerminalAction,paramMeterMap,requestHeaderMap);
            log.info("获取终端结果:{}", get);
            return gson.fromJson(get, GetTerminalResponse.class);
        } catch (IOException e) {
            log.error("获取终端结果发起失败:" + e.getMessage(), e);
            throw new RRException("获取终端结果发起失败:" + e.getMessage());
        }
    }

    @Retryable(value = Exception.class,maxAttempts = 1,backoff = @Backoff(delay = 500,multiplier = 1.5))
    public TaxWareResponse applyRedInfo(ApplyRequest applyRequest) {
        try {
            String reqJson = gson.toJson(applyRequest);
            log.info("申请请求:{}", reqJson);
            Map<String, String> requestHeaderMap = getRequestHeaderMap(defaultHeader);

            // 集成平台没传递，邮件报警
            requestHeaderMap.put("serialNo",applyRequest.getSerialNo());
            final String post = httpClientFactory.post(applyRedAction,requestHeaderMap,reqJson,"");
            log.info("申请结果:{}", post);
            TaxWareResponse taxWareResponse = gson.fromJson(post, TaxWareResponse.class);
            if (taxWareResponse.getCode()==null && taxWareResponse.getTraceId()==null ){
                throw new RRException("申请发起失败");
            }
            return taxWareResponse;
        } catch (IOException e) {
            log.error("申请发起失败:" + e.getMessage(), e);
            throw new RRException("申请发起失败:" + e.getMessage());
        }
    }



    /**
     * 红字信息撤销
     * @param revokeRequest
     * @return
     */
    public TaxWareResponse rollback(RevokeRequest revokeRequest) {
        // 获取在线终端
        if (StringUtils.isEmpty(revokeRequest.getDeviceUn())){
            GetTerminalResponse terminalResponse = getTerminal(revokeRequest.getApplyTaxCode());
            if (Objects.equals(TaxWareCode.SUCCESS,terminalResponse.getCode())) {
                for (GetTerminalResponse.ResultDTO.TerminalListDTO item : terminalResponse.getResult().getTerminalList()) {
                    GetTerminalResponse.ResultDTO.DeviceDTO deviceDTO = !CollectionUtils.isEmpty(item.getOnlineDeviceList()) ? item.getOnlineDeviceList().get(0) : null;
                    if (deviceDTO != null) {
                        revokeRequest.setDeviceUn(deviceDTO.getDeviceUn());
                        revokeRequest.setTerminalUn(item.getTerminalUn());
                        break;
                    }
                }
            }
        }

        //补充终端
        if (revokeRequest.getDeviceUn() == null){
            throw new RRException(String.format("未获取到在线终端%s",revokeRequest.getApplyTaxCode()));
        }


        try {
            String reqJson = gson.toJson(revokeRequest);
//            final String post = httpClientFactory.post(rollbackAction,defaultHeader,reqJson,"");
            // 集成平台没传递，邮件报警
            Map<String, String> requestHeaderMap = getRequestHeaderMap(defaultHeader);
            requestHeaderMap.put("serialNo",revokeRequest.getSerialNo());
            HttpUtils.pack(requestHeaderMap,rollbackAction, this.authentication) ;
            log.info("撤销请求:{}", reqJson);
            final String post = HttpUtils.doPutHttpRequest(host,requestHeaderMap,reqJson) ;
//            final String post2 = HttpUtils.doPutJsonSkipSsl(host,defaultHeader,reqJson) ;
            log.info("撤销结果:{}", post);
            return gson.fromJson(post, TaxWareResponse.class);
        } catch (Exception e) {
            log.error("撤销发起失败:" + e.getMessage(), e);
            throw new RRException("撤销发起失败:" + e.getMessage());
        }
    }

    /**
     * 创建新Map ,不实用静态map传递，不仅流水号会覆盖，还会造成 ConcurrentModificationException
     * Map<String, String> defaultHeader
     */
    Map<String, String> getRequestHeaderMap(Map<String, String> defaultHeader){
        HashMap<String, String> headerMap = Maps.newHashMap();
        headerMap.putAll(defaultHeader);
        return headerMap;
    }



    /**
     * 红字信息生成pdf
     * @param request
     * @return
     */
    public TaxWareResponse generatePdf(RedNotificationGeneratePdfRequest request) {
        try {
            String reqJson = gson.toJson(request);
            log.info("生成pdf请求:{}", reqJson);
            // 集成平台没传递，邮件报警
            defaultHeader.put("serialNo",request.getSerialNo());
            final String post = httpClientFactory.post(genredpdfAction,defaultHeader,reqJson,"");
            log.info("生成pdf结果:{}", post);
            return gson.fromJson(post, TaxWareResponse.class);
        } catch (IOException e) {
            log.error("生成pdf发起失败:" + e.getMessage(), e);
            throw new RRException("生成pdf发起失败:" + e.getMessage());
        }
    }


   // 处理税件红字信息申请
    public void handle(RedMessage redMessage) {
        // 更新流水表(每一个红字信息一条记录)
        QueryWrapper<TXfRedNotificationLogEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq(TXfRedNotificationLogEntity.SERIAL_NO, redMessage.getSerialNo());
        List<TXfRedNotificationLogEntity>  requestLogList = redNotificationLogService.getBaseMapper().selectList(queryWrapper);
        Map<Long, TXfRedNotificationLogEntity> requestLogMap = requestLogList.stream().collect(Collectors.toMap(TXfRedNotificationLogEntity::getApplyId, e->e));


        List<RedMessageInfo> resultInfos = redMessage.getRedApplyResultList();
        Map<String, RedMessageInfo> redMessageInfoMap = resultInfos.stream().filter(e-> StringUtils.isNotBlank(e.getPid())).collect(Collectors.toMap(RedMessageInfo::getPid, e->e));
        if(!redMessageInfoMap.isEmpty()){
            // 更新红字信息表主表
            List<String> pidList = new ArrayList<>(redMessageInfoMap.keySet());
            QueryWrapper<TXfRedNotificationEntity> redNotificationEntityQueryWrapper = new QueryWrapper();
            redNotificationEntityQueryWrapper.in(TXfRedNotificationEntity.ID, pidList);
            List<TXfRedNotificationEntity> tXfRedNotificationEntities = redNotificationMainService.getBaseMapper().selectList(redNotificationEntityQueryWrapper);
            Map<Long, TXfRedNotificationEntity> redNotificationEntityMap = tXfRedNotificationEntities.stream().collect(Collectors.toMap(TXfRedNotificationEntity::getId, e->e));


            for (String applyId : redMessageInfoMap.keySet()) {
                Long id = Long.parseLong(applyId);
                RedMessageInfo redMessageInfo = redMessageInfoMap.get(applyId);
                TXfRedNotificationLogEntity tXfRedNotificationLogEntity = requestLogMap.get(id);
                TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationEntityMap.get(id);
                //如果已经申请 。不替换
                if ( tXfRedNotificationEntity!=null  && RedNoApplyingStatus.APPLIED.getValue().equals(tXfRedNotificationEntity.getApplyingStatus())){
                    log.info(String.format("红字信息{}申请状态为已申请, 本次编号:{},不需要处理", tXfRedNotificationEntity.getId(),redMessageInfo.getRedNotificationNo()));

                }

                if(SUCCESSFUL_PROCESS_FLAG.equals(redMessageInfo.getProcessFlag())){
                    tXfRedNotificationEntity.setRedNotificationNo(redMessageInfo.getRedNotificationNo());
                    tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.APPLIED.getValue());
//                    if (tXfRedNotificationEntity.getInvoiceOrigin().intValue()== InvoiceOrigin.IMPORT.getValue().intValue())
//                    {
//
//                    }
                    tXfRedNotificationEntity.setApproveStatus(ApproveStatus.APPROVE_PASS.getValue());

                    // 更新申请日期
                    tXfRedNotificationEntity.setInvoiceDate(DateUtils.getCurentIssueDate());
                    redNotificationMainService.updateById(tXfRedNotificationEntity);
                    tXfRedNotificationLogEntity.setProcessRemark("申请成功");
                    tXfRedNotificationLogEntity.setStatus(2);
                    redNotificationLogService.updateById(tXfRedNotificationLogEntity);
                    //commPreInvoiceService//
                    try{
                        if (tXfRedNotificationEntity.getPid()!=null){
                            commPreInvoiceService.fillPreInvoiceRedNotification(Long.parseLong(tXfRedNotificationEntity.getPid()),redMessageInfo.getRedNotificationNo());
                        }else {
                            log.info("回填手工导入的红字信息,申请流水号:{}",tXfRedNotificationEntity.getSerialNo());
                        }
                    }catch (Exception e){
                        log.error("回填预制发票异常",e);
                    }
                    // 尝试申请pdf链接
                    RedNotificationExportPdfRequest request = new  RedNotificationExportPdfRequest();
                    request.setAutoFlag(true);
                    request.setGenerateModel(0);
                    QueryModel queryModel = new QueryModel();
                    queryModel.setIncludes(Lists.newArrayList(tXfRedNotificationEntity.getId()));
                    queryModel.setIsAllSelected(false);
                    request.setQueryModel(queryModel);
                    redNotificationThreadPool.execute(
                            ()->{
                                redNotificationMainService.downloadPdf(request);
                            }
                    );



                }else {
                    tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
                    redNotificationMainService.updateById(tXfRedNotificationEntity);
                    tXfRedNotificationLogEntity.setProcessRemark(redMessageInfo.getProcessRemark());
                    tXfRedNotificationLogEntity.setStatus(3);
                    redNotificationLogService.updateById(tXfRedNotificationLogEntity);
                    commPreInvoiceService.applyPreInvoiceRedNotificationFail(Long.parseLong(tXfRedNotificationEntity.getPid()));
                }
            }

        }
    }

    /**
     * 处理税件 撤销
     * @param redRevokeMessageResult
     */
    public void handleRollBack(RedRevokeMessageResult redRevokeMessageResult) {
        RedRevokeMessage message = redRevokeMessageResult.getResult();
        String redNotificationNo;
        Long serialNo  ;
        if(Objects.nonNull(redRevokeMessageResult) && StringUtils.isNotBlank(redNotificationNo = message.getRedNotificationNo())){
             serialNo = Long.valueOf(message.getSerialNo());
             LambdaQueryWrapper<TXfRedNotificationLogEntity> objectQueryWrapper = new LambdaQueryWrapper<>();
             objectQueryWrapper.eq(TXfRedNotificationLogEntity::getSerialNo,serialNo).eq(TXfRedNotificationLogEntity::getRedNotificationNo,redNotificationNo);
             TXfRedNotificationLogEntity logEntity = redNotificationLogService.getOne(objectQueryWrapper);
             if (logEntity ==null ){
                 log.info("根据红字信息编号未查询到红字信息,流水号:{},红字信息表编号:{}",serialNo,redNotificationNo);
                 return;
             }

            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
            record.setId(logEntity.getApplyId());
            if (Objects.equals(TaxWareCode.SUCCESS,redRevokeMessageResult.getCode())){
                logEntity.setStatus(2);
                logEntity.setProcessRemark("撤销成功");
                //修改红字信息 已撤销并解锁数据
                record.setApproveStatus(ApproveStatus.ALREADY_ROLL_BACK.getValue());
                record.setLockFlag(1);
                // 状态变为已申请 状态变为已撤销
                record.setApplyingStatus(RedNoApplyingStatus.APPLIED.getValue());

            }else {
                logEntity.setStatus(3);
                logEntity.setProcessRemark(redRevokeMessageResult.getMessage());
                //解锁数据
                record.setLockFlag(1);
                record.setApplyRemark(redRevokeMessageResult.getMessage());
                // 通知调用方

            }
            redNotificationMainService.updateById(record);
            redNotificationLogService.updateById(logEntity);

        }



    }


}
