package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedMessage;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedRevokeMessageResult;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RollbackRedNotificationIntegrationResultHandler implements IntegrationResultHandler{

    public static final String REQUEST_NAME = "red_revocation";

    @Autowired
    TaxWareService taxWareService;

    /**
     * 具体结果处理方法
     *
     * @param sealedMessage 回调参数
     * @return
     */
    @Override
    public boolean handle(SealedMessage sealedMessage) {
        log.info("撤销回调结果payload obj:{},header:{}",sealedMessage.getPayload().getObj(),sealedMessage.getHeader());
        RedRevokeMessageResult redRevokeMessageResult = JsonUtil.fromJson(sealedMessage.getPayload().getObj().toString(), RedRevokeMessageResult.class);
        taxWareService.handleRollBack(redRevokeMessageResult);
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
