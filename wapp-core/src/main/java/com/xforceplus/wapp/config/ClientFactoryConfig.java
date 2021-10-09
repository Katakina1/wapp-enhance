package com.xforceplus.wapp.config;

import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.apollo.client.netty.IMessageListener;
import com.xforceplus.apollo.client.netty.MCFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-09 15:44
 **/
@Configuration
public class ClientFactoryConfig {
    /**
     * 集成平台地址
     */
    @Value("${wapp.xf.host.http}")
    private String janusPath;
    /**
     * 沃尔玛授权码
     */
    @Value("${wapp.xf.authentication}")
    private String authentication;

    @Bean
    HttpClientFactory getHttpClientFactory() {

        return HttpClientFactory.getHttpClientFactory(janusPath, authentication);
    }

    @Bean
    public MCFactory mcFactory(@Value("${wapp.xf.client-id}") String clientId, @Value("${wapp.xf.host.tcp}") String tcpHost, @Value("${wapp.xf.port.tcp}") int port
            , IMessageListener messageListeners) {
        final MCFactory mcFactory = MCFactory.getInstance(clientId, tcpHost, port);
        mcFactory.registerListener(messageListeners);
        return mcFactory;
    }
}
