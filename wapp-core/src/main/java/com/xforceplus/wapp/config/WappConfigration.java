package com.xforceplus.wapp.config;

import com.xforceplus.wapp.sequence.IDSequence;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyEditor;
import java.util.List;

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

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }


    @Bean
    PropertyEditorRegistrar propertyEditorRegistrar(){
        return registry ->
                registry.registerCustomEditor(String.class,new StringTrimmerEditor(Boolean.TRUE));
    }

    @Bean
    ConfigurableWebBindingInitializer configurableWebBindingInitializer(List<PropertyEditorRegistrar> propertyEditorRegistrars){
        final ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setPropertyEditorRegistrars(propertyEditorRegistrars.toArray(new PropertyEditorRegistrar[0]));
        return initializer;
    }

}
