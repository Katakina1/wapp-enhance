package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedMessage;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 1：红字信息表申请回调
 * </pre>
 * @author lenovo
 *
 */
@Component
@Slf4j
public class ApplyRedNotificationIntegrationResultHandler implements IntegrationResultHandler{

    public static final String REQUEST_NAME = "red_apply";

    @Autowired
    TaxWareService taxWareService;

    /**
     * .具体结果处理方法
     *
     * @param sealedMessage 回调参数
     * @return
     */
    @Override
    public boolean handle(SealedMessage sealedMessage) {
        log.info("ApplyRedNotificationIntegrationResultHandler payload obj:{},header:{}",sealedMessage.getPayload().getObj(),sealedMessage.getHeader());
        RedMessage redMessage = JsonUtil.fromJson(sealedMessage.getPayload().getObj().toString(), RedMessage.class);
        taxWareService.handle(redMessage);
        return true;
    }

    /**
     * @return 业务名称
     */
    @Override
    public String requestName() {
        return REQUEST_NAME;
    }
}
