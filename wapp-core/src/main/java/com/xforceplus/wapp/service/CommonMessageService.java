package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.modules.messagecontrol.service.MessageControlService;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-20 10:54
 **/
@Service
public class CommonMessageService {
    @Autowired
    private ActiveMqProducer activeMqProducer;
    @Autowired
    private MessageControlService messageControlService;
    @Value("${activemq.queue-name.export-success-queue-gfone}")
    private String gfoneQueue;

    public void sendMessage(TDxMessagecontrolEntity messagecontrolEntity){
        messagecontrolEntity.setOperationStatus("0");
        messagecontrolEntity.setCreateTime(new Date());
        messageControlService.save(messagecontrolEntity);
        activeMqProducer.send(gfoneQueue, JSON.toJSONString(Collections.singletonMap("message",messagecontrolEntity)));
    }
}
