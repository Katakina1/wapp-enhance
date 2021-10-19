package com.xforceplus.wapp.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:11
 **/
@Configuration
public class JacksonConfiguration {

    @Bean
    public Module longToString(){
        SimpleModule simpleModule=new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return simpleModule;
    }
}
