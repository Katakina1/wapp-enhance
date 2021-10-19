package com.xforceplus.wapp.export;

import org.springframework.messaging.Message;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 10:35
 **/
public interface IExportHandler {
    void doExport(Message<String> message,String messageId);

    String handlerName();
}
