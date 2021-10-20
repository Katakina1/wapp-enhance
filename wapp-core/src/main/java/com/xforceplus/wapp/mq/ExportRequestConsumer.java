package com.xforceplus.wapp.mq;

import com.xforceplus.wapp.export.IExportHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

import static com.xforceplus.wapp.export.IExportHandler.KEY_OF_HANDLER_NAME;


/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 10:36
 **/
@Component
@Slf4j
public class ExportRequestConsumer {


    final Map<String, IExportHandler> exportHandlers = new HashMap<>();

    public ExportRequestConsumer(ApplicationContext applicationContext) {
        final Map<String, IExportHandler> beansOfType = applicationContext.getBeansOfType(IExportHandler.class);
        beansOfType.forEach((k, v) -> exportHandlers.put(v.handlerName(), v));
    }

    @JmsListener(destination = "${activemq.queue-name.export-request}")
    public void doListen(Message<String> message, TextMessage textMessage) {

        final MessageHeaders headers = message.getHeaders();
        final String s = headers.get(KEY_OF_HANDLER_NAME, String.class);
        String messageId = "";
        try {
            messageId = textMessage.getJMSMessageID();
        } catch (JMSException e) {
            log.error("获取消息ID失败:" + e.getMessage(), e);
        }
        if (StringUtils.isBlank(s)) {
            log.info("导出请求:{},未指定导出处理器,messageId:{}", message.getPayload(), messageId);
        } else {
            final IExportHandler iExportHandler = exportHandlers.get(s);
            if (iExportHandler != null) {
                iExportHandler.doExport(message, messageId);
            } else {
                log.info("处理器名称:[{}]未匹配到相应处理器", s);
            }
        }
    }
}
