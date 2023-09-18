package com.xforceplus.wapp.config;

import com.xforceplus.wapp.interceptor.LoggerMDCTraceInterceptor;
import com.xforceplus.wapp.sequence.IDSequence;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-14 13:39
 **/
@Configuration
//@EnableConfigurationProperties({TaxRateProperties.class})
public class WappConfigration {

    @Bean
    public IDSequence idSequence(@Value("${wapp.datacenter:0}") long dataCenter) {
        return new IDSequence(dataCenter,false );
    }

    @Bean
    public Executor taskThreadPoolExecutor() {
        return new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20),
                new ThreadFactory() {
                    private final AtomicInteger number = new AtomicInteger(1);
                    @Override
                    public Thread newThread(@Nonnull Runnable r) {
                        return new Thread(r, "ScheduledTaskPoll-" + number.getAndIncrement());
                    }
                });
    }
  
//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder){
//        return builder.build();
//    }


    @Bean
    PropertyEditorRegistrar propertyEditorRegistrar(){
        return registry ->
                registry.registerCustomEditor(String.class,new StringTrimmerEditor(Boolean.TRUE));
    }

    @Bean
    ConfigurableWebBindingInitializer configurableWebBindingInitializer(List<PropertyEditorRegistrar> propertyEditorRegistrars
            , FormattingConversionService service
            , Validator validator
                                                                        ){
        final ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setPropertyEditorRegistrars(propertyEditorRegistrars.toArray(new PropertyEditorRegistrar[0]));
        initializer.setConversionService(service);
        initializer.setValidator(validator);
        return initializer;
    }

    @Bean
    public WebMvcConfigurer loggerMdcTraceInterceptorConfigurer(LoggerMDCTraceInterceptor loggerMDCTraceInterceptor){
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(loggerMDCTraceInterceptor).order(Ordered.HIGHEST_PRECEDENCE);
            }
        };
    }
}
