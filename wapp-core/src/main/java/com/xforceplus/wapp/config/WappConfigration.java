package com.xforceplus.wapp.config;

import com.xforceplus.wapp.sequence.IDSequence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-14 13:39
 **/
@Configuration
public class WappConfigration {

    @Bean
    public IDSequence idSequence(@Value("${wapp.datacenter:0}") long dataCenter) {
        return new IDSequence(dataCenter,false );
    }
}
