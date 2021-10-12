package com.xforceplus.wapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-09-08 17:25
 **/
@EnableSwagger2
@SpringBootApplication
public class WappApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(WappApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WappApplication.class);
    }
}
