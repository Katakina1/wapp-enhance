package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.client.TaxWareInvoice;
import com.xforceplus.wapp.client.TaxWareInvoiceDetail;
import com.xforceplus.wapp.client.TaxWareInvoiceRsp;
import com.xforceplus.wapp.client.TaxWareInvoiceVO;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationLogEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    RedNotificationLogRequestService redNotificationLogRequestService;


    @Value("${wapp.integration.action.terminals}")
    private String getTerminalAction;

    @Value("${wapp.integration.action.rednotification}")
    private String applyRedAction;

//    @Value("${wapp.integration.action.invoice-all-elements}")
//    private String invoiceAllElementsAction;

    private final Map<String, String> defaultHeader;


    private static final String VERIFICATION_LEVEL = "1";
    private static final String SUCCESSFUL_PROCESS_FLAG = "1";


    public TaxWareService(@Value("${wapp.xf.tenant-id:1203939049971830784}")
                                  String tenantId) {
        defaultHeader = new HashMap<>();
        defaultHeader.put("rpcType", "http");
//        defaultHeader.put("x-app-client", "janus");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("tenantCode", tenantId);
        defaultHeader.put("accept-encoding", "");
    }

    Gson gson = new Gson();

    public GetTerminalResponse getTerminal(String taxNo) {
        try {
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            paramMeterMap.put("taxNo", taxNo);
            final String get = httpClientFactory.get(getTerminalAction, paramMeterMap, defaultHeader);
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
            final String post = httpClientFactory.post(applyRedAction, defaultHeader, reqJson, "");
            log.info("申请结果:{}", post);
            return gson.fromJson(post, TaxWareResponse.class);
        } catch (IOException e) {
            log.error("申请发起失败:" + e.getMessage(), e);
            throw new RRException("申请发起失败:" + e.getMessage());
        }
    }

//    public Optional<Tuple2<TaxWareInvoice, List<TaxWareInvoiceDetail>>> getInvoiceAllElements(TaxWareInvoiceVO vo) {
//        long start = System.currentTimeMillis();
//        try {
//            String post = httpClientFactory.post(invoiceAllElementsAction, defaultHeader, gson.toJson(vo), StringUtils.EMPTY);
//            log.debug("获取税件发票全要素信息,返回值:{}", post);
//            TaxWareInvoiceRsp taxWareInvoiceRsp = gson.fromJson(post, TaxWareInvoiceRsp.class);
//            if (Objects.nonNull(taxWareInvoiceRsp) && "TXWRBT0001".equals(taxWareInvoiceRsp.getCode())) {
//                TaxWareInvoice invoiceMain = taxWareInvoiceRsp.getResult().getInvoiceMain();
//                List<TaxWareInvoiceDetail> invoiceDetails = taxWareInvoiceRsp.getResult().getInvoiceDetails();
//                //TODO 明细缺少关联ID
//                log.info("获取税件发票全要素信息耗时{}ms", System.currentTimeMillis() - start);
//                return Optional.of(Tuple.of(invoiceMain, invoiceDetails));
//            }
//        } catch (IOException e) {
//            log.error("获取税件发票全要素信息异常[{}]:" + e.getMessage(), e);
//        }
//        log.info("获取税件发票全要素信息耗时{}ms", System.currentTimeMillis() - start);
//        return Optional.empty();
//    }


    public void handle(RedMessage redMessage) {
        // 更新流水表(每一个红字信息一条记录)
        QueryWrapper<TXfRedNotificationLogEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq(TXfRedNotificationLogEntity.SERIAL_NO, redMessage.getSerialNo());
        List<TXfRedNotificationLogEntity> requestLogList = redNotificationLogRequestService.getBaseMapper().selectList(queryWrapper);
        Map<Long, TXfRedNotificationLogEntity> requestLogMap = requestLogList.stream().collect(Collectors.toMap(TXfRedNotificationLogEntity::getApplyId, e -> e));


        List<RedMessageInfo> resultInfos = redMessage.getRedApplyResultList();
        Map<String, RedMessageInfo> redMessageInfoMap = resultInfos.stream().filter(e -> StringUtils.isNotBlank(e.getPid())).collect(Collectors.toMap(RedMessageInfo::getPid, e -> e));
        if (!redMessageInfoMap.isEmpty()) {
            // 更新红字信息表主表
            List<String> pidList = new ArrayList<>(redMessageInfoMap.keySet());
            QueryWrapper<TXfRedNotificationEntity> redNotificationEntityQueryWrapper = new QueryWrapper();
            redNotificationEntityQueryWrapper.in(TXfRedNotificationEntity.ID, pidList);
            List<TXfRedNotificationEntity> tXfRedNotificationEntities = redNotificationMainService.getBaseMapper().selectList(redNotificationEntityQueryWrapper);
            Map<Long, TXfRedNotificationEntity> redNotificationEntityMap = tXfRedNotificationEntities.stream().collect(Collectors.toMap(TXfRedNotificationEntity::getId, e -> e));


            for (String applyId : redMessageInfoMap.keySet()) {
                Long id = Long.parseLong(applyId);
                RedMessageInfo redMessageInfo = redMessageInfoMap.get(applyId);
                TXfRedNotificationLogEntity tXfRedNotificationLogEntity = requestLogMap.get(id);
                TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationEntityMap.get(id);
                //如果已经申请 。不替换
                if (tXfRedNotificationEntity != null && RedNoApplyingStatus.APPLIED.getValue().equals(tXfRedNotificationEntity.getApplyingStatus())) {
                    log.info(String.format("红字信息{}申请状态为已申请, 本次编号:{},不需要处理", tXfRedNotificationEntity.getId(), redMessageInfo.getRedNotificationNo()));

                }

                if (SUCCESSFUL_PROCESS_FLAG.equals(redMessageInfo.getProcessFlag())) {
                    tXfRedNotificationEntity.setRedNotificationNo(redMessageInfo.getRedNotificationNo());
                    tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.APPLIED.getValue());
                    redNotificationMainService.updateById(tXfRedNotificationEntity);
                    tXfRedNotificationLogEntity.setProcessRemark("申请成功");
                    tXfRedNotificationLogEntity.setStatus(2);
                    redNotificationLogRequestService.updateById(tXfRedNotificationLogEntity);
                } else {
                    tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.APPLYING.getValue());
                    redNotificationMainService.updateById(tXfRedNotificationEntity);
                    tXfRedNotificationLogEntity.setProcessRemark(redMessageInfo.getProcessRemark());
                    tXfRedNotificationLogEntity.setStatus(3);
                    redNotificationLogRequestService.updateById(tXfRedNotificationLogEntity);
                }
            }

        }
    }
}
