package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import com.xforceplus.wapp.modules.rednotification.util.HttpUtils;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
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
            final String get = httpClientFactory.get(getTerminalAction,paramMeterMap,defaultHeader);
            log.info("获取终端结果:{}", get);
            return gson.fromJson(get, GetTerminalResponse.class);
        } catch (IOException e) {
            log.error("获取终端结果发起失败:" + e.getMessage(), e);
            throw new RRException("获取终端结果发起失败:" + e.getMessage());
        }
    }

    public TaxWareResponse applyRedInfo(ApplyRequest applyRequest) {
        try {
            String reqJson = gson.toJson(applyRequest);
            final String post = httpClientFactory.post(applyRedAction,defaultHeader,reqJson,"");
            log.info("申请结果:{}", post);
            return gson.fromJson(post, TaxWareResponse.class);
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
        try {
            String reqJson = gson.toJson(revokeRequest);
//            final String post = httpClientFactory.post(rollbackAction,defaultHeader,reqJson,"");
            HttpUtils.pack(defaultHeader,rollbackAction, this.authentication) ;
            final String post = HttpUtils.doPutHttpRequest(host,defaultHeader,reqJson) ;
            final String post2 = HttpUtils.doPutJsonSkipSsl(host,defaultHeader,reqJson) ;
            log.info("撤销结果:{}", post);
            return gson.fromJson(post, TaxWareResponse.class);
        } catch (Exception e) {
            log.error("撤销发起失败:" + e.getMessage(), e);
            throw new RRException("撤销发起失败:" + e.getMessage());
        }
    }


    /**
     * 红字信息生成pdf
     * @param request
     * @return
     */
    public TaxWareResponse generatePdf(RedNotificationGeneratePdfRequest request) {
        try {
            String reqJson = gson.toJson(request);
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
                    redNotificationMainService.updateById(tXfRedNotificationEntity);
                    tXfRedNotificationLogEntity.setProcessRemark("申请成功");
                    tXfRedNotificationLogEntity.setStatus(2);
                    redNotificationLogService.updateById(tXfRedNotificationLogEntity);
                }else {
                    tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.APPLYING.getValue());
                    redNotificationMainService.updateById(tXfRedNotificationEntity);
                    tXfRedNotificationLogEntity.setProcessRemark(redMessageInfo.getProcessRemark());
                    tXfRedNotificationLogEntity.setStatus(3);
                    redNotificationLogService.updateById(tXfRedNotificationLogEntity);
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
            record.setId(logEntity.getId());
            if (Objects.equals(TaxWareCode.SUCCESS,redRevokeMessageResult.getCode())){
                logEntity.setStatus(2);
                logEntity.setProcessRemark("撤销成功");
                //修改红字信息 已撤销并解锁数据
                record.setApproveStatus(ApproveStatus.ALREADY_ROLL_BACK.getValue());
                record.setLockFlag(1);

            }else {
                logEntity.setStatus(3);
                logEntity.setProcessRemark(redRevokeMessageResult.getMessage());
                //解锁数据
                record.setLockFlag(1);
                // 通知调用方

            }
            redNotificationMainService.updateById(record);

        }



    }


}
