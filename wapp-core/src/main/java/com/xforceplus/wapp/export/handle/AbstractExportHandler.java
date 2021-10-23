package com.xforceplus.wapp.export.handle;

import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.service.CommonMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-23 13:43
 **/
public abstract class AbstractExportHandler implements IExportHandler {

    @Autowired
    private CommonMessageService commonMessageService;

    @Override
    public void doExport(Message<String> message, String messageId) {

    }

    abstract protected void export(Message<String> message, String messageId);


    private void sendMessage(boolean success,String loginName){
        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        messagecontrolEntity.setTitle(title()+(success?"成功":"失败"));
        messagecontrolEntity.setUserAccount(loginName);
        commonMessageService.sendMessage(messagecontrolEntity);
    }

    abstract protected String title();
}
