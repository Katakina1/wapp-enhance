package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedMessage;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author mashaopeng@xforceplus.com
 */
@Component
@Slf4j
public class TaxCodeIntegrationResultHandler implements IntegrationResultHandler {

    /**
     * @return 业务名称
     */
    @Override
    public String requestName() {
        return "red_apply";
    }

    /**
     * 具体结果处理方法
     *
     * @param sealedMessage 回调参数
     */
    @Override
    public boolean handle(SealedMessage sealedMessage) {
        log.info("payload obj:{},header:{}", sealedMessage.getPayload().getObj(), sealedMessage.getHeader());
        return true;
    }
}
