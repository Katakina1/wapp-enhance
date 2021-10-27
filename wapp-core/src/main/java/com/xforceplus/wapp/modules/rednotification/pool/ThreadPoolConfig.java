package com.xforceplus.wapp.modules.rednotification.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class ThreadPoolConfig {

    @Bean
    ThreadPoolExecutor redNotificationThreadPool(){
        int corePoolSize =  Runtime.getRuntime().availableProcessors();
        log.info("当前机器cpu核数:{}",corePoolSize);
        return  new ThreadPoolExecutor(corePoolSize , corePoolSize,30,TimeUnit.SECONDS,new LinkedBlockingQueue<>());
    }
}
