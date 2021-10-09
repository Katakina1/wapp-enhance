package com.xforceplus.wapp.listener;

import com.xforceplus.apollo.client.netty.IMessageListener;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.handle.IntegrationResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Pub/Sub listener
 * Pub/Sub documentation
 * https://wiki.xforceplus.com/display/IP/TCP+Gateway+SDK
 * <p>
 *
 * @version 1.0
 * @author: zhaochao@xforceplus.com
 * @date: 2021-9-15 19:10:24
 */
@Component
@Slf4j
public class MessageListener implements IMessageListener {

    private final Map<String, IntegrationResultHandler> map = new HashMap<>();

    public MessageListener(List<IntegrationResultHandler> handlers) {
        for (IntegrationResultHandler handler : handlers) {
            map.put(handler.requestName(), handler);
        }
    }


    @Override
    public boolean onMessage(SealedMessage sealedMessage) {
        final SealedMessage.Header messageHeader = sealedMessage.getHeader();
        final String requestName = messageHeader.getRequestName();
        return Optional.ofNullable(map.get(requestName)).map(x -> x.handle(sealedMessage)).orElseGet(
                () ->
                {
                    log.info("requestName:[{}] not found", requestName);
                    return Boolean.FALSE;
                }
        );
    }
}
