package com.xforceplus.wapp.config;

import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.apollo.client.netty.IMessageListener;
import com.xforceplus.apollo.client.netty.MCFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-09 15:44
 **/
@Configuration
@ConditionalOnProperty(value = "wapp.integration.enabled",matchIfMissing = true, havingValue = "true")
public class ClientFactoryConfig {
    /**
     * 集成平台地址
     */
    @Value("${wapp.integration.host.http}")
    private String janusPath;
    /**
     * 沃尔玛授权码
     */
    @Value("${wapp.integration.authentication}")
    private String authentication;

    @Bean
    HttpClientFactory getHttpClientFactory() {

        return HttpClientFactory.getHttpClientFactory(janusPath, authentication);
    }

    @Bean
    public MCFactory mcFactory(@Value("${wapp.integration.client-id}") String clientId,
                               @Value("${wapp.integration.host.tcp}") String tcpHost,
                               @Value("${wapp.integration.port.tcp}") int port
            , IMessageListener messageListeners) {
        final MCFactory mcFactory = MCFactory.getInstance(clientId, tcpHost, port);
        mcFactory.registerListener(messageListeners);
        return mcFactory;
    }
}
