package com.xforceplus.wapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-09-08 17:25
 **/
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication
public class WappApplication  {
    public static void main(String[] args) {
        SpringApplication.run(WappApplication.class, args);
    }

}
