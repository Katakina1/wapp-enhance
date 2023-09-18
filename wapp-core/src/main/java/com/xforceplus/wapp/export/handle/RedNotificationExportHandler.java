package com.xforceplus.wapp.export.handle;

import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.service.CommRedNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-01-11 17:19
 **/
@Component
public class RedNotificationExportHandler implements IExportHandler {
    @Autowired
    private CommRedNotificationService commRedNotificationService;


    @Override
    public void doExport(Message<String> message, String messageId) {
        final String payload = message.getPayload();

    }

    @Override
    public String handlerName() {
        return ExportHandlerEnum.NOTIFICATION.name();
    }
}
