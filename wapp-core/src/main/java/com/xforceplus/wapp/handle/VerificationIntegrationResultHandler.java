package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 14:41
 **/
@Component
@Slf4j
public class VerificationIntegrationResultHandler implements IntegrationResultHandler{

    public static final String REQUEST_NAME = "invoiceVerifyUploadResult";

    /**
     * 具体结果处理方法
     *
     * @param sealedMessage 回调参数
     * @return
     */
    @Override
    public boolean handle(SealedMessage sealedMessage) {
        log.info("payload obj:{}",sealedMessage.getPayload().getObj());
        return false;
    }

    /**
     * @return 业务名称
     */
    @Override
    public String requestName() {
        return REQUEST_NAME;
    }
}
