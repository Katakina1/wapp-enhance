package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;

/**
 * 集成平台结果处理
 */
public interface IntegrationResultHandler {
    /**
     * 具体结果处理方法
     * @param sealedMessage 回调参数
     * @return
     */
    boolean handle(SealedMessage sealedMessage);

    /**
     * @return 业务名称
     */
    String requestName();
}
