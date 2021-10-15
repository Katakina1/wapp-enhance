package com.xforceplus.wapp;

import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.apollo.client.netty.MCFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Configuration;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-14 20:33
 **/

@Configuration
@ConditionalOnProperty(value = "wapp.integration.enabled", havingValue = "false")
@MockBeans(
        value = {
                @MockBean(HttpClientFactory.class),
                @MockBean(MCFactory.class)
        }
)
public class ClientFactoryMockConfig {

}
