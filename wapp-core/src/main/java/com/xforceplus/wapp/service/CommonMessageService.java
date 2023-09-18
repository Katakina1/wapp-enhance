package com.xforceplus.wapp.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.enums.syslog.PreInvoiceRedNoSysStatusEnum;
import com.xforceplus.wapp.modules.messagecontrol.service.MessageControlService;
import com.xforceplus.wapp.modules.syslog.util.SysLogUtil;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.util.TransactionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-20 10:54
 **/
@Service
@Slf4j
public class CommonMessageService {
    @Autowired
    private ActiveMqProducer activeMqProducer;
    @Autowired
    private MessageControlService messageControlService;
    @Value("${activemq.queue-name.export-success-queue-gfone}")
    private String gfoneQueue;
    @Value("${activemq.queue-name.deduct-notification}")
    private String deductNotificationEventQueue;

    public void sendMessage(TDxMessagecontrolEntity messagecontrolEntity){
        messagecontrolEntity.setOperationStatus("0");
        messagecontrolEntity.setCreateTime(new Date());
        messageControlService.save(messagecontrolEntity);
        activeMqProducer.send(gfoneQueue, JSON.toJSONString(Collections.singletonMap("message",messagecontrolEntity)));
    }

    /**
     * 红字信息表状态事件处理
     * @param eventEnum 事件枚举
     * @param entity 红字信息表详情
     */
    public void sendMessage(DeductRedNotificationEventEnum eventEnum, TXfRedNotificationEntity entity) {
        if (StringUtils.isNotBlank(entity.getPid())) {
            DeductRedNotificationEvent.DeductRedNotificationModel deductRedNotificationModel = new DeductRedNotificationEvent.DeductRedNotificationModel();
            deductRedNotificationModel.setPreInvoiceId(CommonUtil.toLong(entity.getPid()));
            deductRedNotificationModel.setRedNotificationId(entity.getId());
            deductRedNotificationModel.setRedNotificationNo(entity.getRedNotificationNo());
            deductRedNotificationModel.setApplyRequired(true);
            sendMessage(eventEnum, deductRedNotificationModel);
        }
    }

    /**
     * 预制发票作废删除事件发送
     * @param preInvoiceIdList 预制发票id集合
     */
    public void sendPreInvoiceDiscardMessage(List<Long> preInvoiceIdList) {
        sendPreInvoiceMessage(preInvoiceIdList, DeductRedNotificationEventEnum.PRE_INVOICE_DISCARD);
    }

    /**
     * 预制发票已上传发票
     * @param preInvoiceIdList 预制发票id集合
     */
    public void sendPreInvoiceBackFillMessage(List<Long> preInvoiceIdList) {
        sendPreInvoiceMessage(preInvoiceIdList, DeductRedNotificationEventEnum.UPLOAD_RED_INVOICE);
    }

    /**
     * 预制发票已删除
     * @param preInvoiceIdList 预制发票id集合
     */
    public void sendPreInvoiceDeleteMessage(List<Long> preInvoiceIdList) {
        sendPreInvoiceMessage(preInvoiceIdList, DeductRedNotificationEventEnum.PRE_INVOICE_DELETE);
    }

    /**
     * 预制发票关联红票删除
     * @param preInvoiceIdList 预制发票id集合
     */
    public void sendInvoiceDeleteMessage(List<Long> preInvoiceIdList) {
        sendPreInvoiceMessage(preInvoiceIdList, DeductRedNotificationEventEnum.DELETE_RED_INVOICE);
    }

    private void sendPreInvoiceMessage(List<Long> preInvoiceIdList, DeductRedNotificationEventEnum eventEnum) {
        if (CollectionUtil.isNotEmpty(preInvoiceIdList)) {
            preInvoiceIdList.forEach(preInvoiceId ->
                    sendMessage(preInvoiceId, eventEnum)
            );
        }
    }

    private void sendMessage(Long preInvoiceId, DeductRedNotificationEventEnum eventEnum) {
        DeductRedNotificationEvent.DeductRedNotificationModel deductRedNotificationModel = new DeductRedNotificationEvent.DeductRedNotificationModel();
        deductRedNotificationModel.setPreInvoiceId(preInvoiceId);
        sendMessage(eventEnum, deductRedNotificationModel);
    }


    /**
     * 红字信息表状态事件处理
     * @param eventEnum 事件枚举
     * @param model body
     */
    public void sendMessage(DeductRedNotificationEventEnum eventEnum, DeductRedNotificationEvent.DeductRedNotificationModel model) {
        log.info("sendRedNotificationEventMessage info:[{}]-[{}]", JSON.toJSONString(model), eventEnum);
        DeductRedNotificationEvent deductRedNotificationEvent = new DeductRedNotificationEvent();
        deductRedNotificationEvent.setEvent(eventEnum);
        deductRedNotificationEvent.setTimestamp(System.currentTimeMillis());
        deductRedNotificationEvent.setBody(model);

        //改为事务提交成功之后再发送事件
        TransactionUtils.invokeAfterCommitIfExistOrImmediately(() -> {
            SysLogUtil.sendPreInvoiceRedNoLog(deductRedNotificationEvent, PreInvoiceRedNoSysStatusEnum.SEND.getCode());
            activeMqProducer.send(deductNotificationEventQueue, JSON.toJSONString(deductRedNotificationEvent));
        });
    }
}
