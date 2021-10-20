package com.xforceplus.wapp.mq;

import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MQ 发送消息
 */
@Service
public class ActiveMqProducer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void send(String queueName, String msg) {
        if(StringUtils.isBlank(queueName)){
            throw new EnhanceRuntimeException("queueName not null");
        }
        if(StringUtils.isBlank(msg)){
            throw new EnhanceRuntimeException("msg not null");
        }
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(queueName), msg);
    }

    public void send(String queueName, String msg, Map<String,Object> headers) {
        if(StringUtils.isBlank(queueName)){
            throw new EnhanceRuntimeException("queueName not null");
        }
        if(StringUtils.isBlank(msg)){
            throw new EnhanceRuntimeException("msg not null");
        }
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(queueName), msg,headers);
    }
}
