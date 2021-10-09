package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;

/**
 * 集成平台结果处理
 */
public interface IntegrationResultHandler {
    boolean handle(SealedMessage sealedMessage);
    String requestName();
}
