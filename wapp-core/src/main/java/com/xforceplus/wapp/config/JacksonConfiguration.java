package com.xforceplus.wapp.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

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

//        simpleModule.addDeserializer(String.class, new JsonDeserializer<String>() {
//            @Override
//            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//                final String valueAsString = p.getValueAsString();
//                if (StringUtils.isBlank(valueAsString)){
//                    return null;
//                }
//                return valueAsString;
//            }
//        });
        return simpleModule;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        builder.featuresToDisable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        return builder;
    }
}
