package com.xforceplus.wapp.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.enums.syslog.PreInvoiceRedNoSysStatusEnum;
import com.xforceplus.wapp.modules.deduct.service.DeductPreInvoiceService;
import com.xforceplus.wapp.modules.syslog.util.SysLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-14 14:51
 **/
@Component
@Slf4j
public class DeductRedNotificationConsumer {

    private final ObjectMapper objectMapper;

    private final DeductPreInvoiceService deductPreInvoiceService;

    public DeductRedNotificationConsumer(ObjectMapper objectMapper, DeductPreInvoiceService deductPreInvoiceService) {
        this.objectMapper = objectMapper;
        this.deductPreInvoiceService = deductPreInvoiceService;
    }

    @JmsListener(destination = "${activemq.queue-name.deduct-notification}")
    public void doConsume(Message<String> message, TextMessage textMessage) {

        try {
            String messageId = textMessage.getJMSMessageID();
            log.info("当前消息:{},payload:{}", messageId, message.getPayload());
        } catch (JMSException e) {
            log.error("获取消息ID失败:" + e.getMessage(), e);
        }

        try {
            DeductRedNotificationEvent deductRedNotificationEvent = objectMapper.readValue(message.getPayload(), DeductRedNotificationEvent.class);
            SysLogUtil.sendPreInvoiceRedNoLog(deductRedNotificationEvent, PreInvoiceRedNoSysStatusEnum.RECEIVE.getCode());
            deductPreInvoiceService.consume(deductRedNotificationEvent);
        } catch (Exception e) {
            log.error("红字信息流转消息异常", e);
        }

    }
}
