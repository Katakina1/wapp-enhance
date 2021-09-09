package com.xforceplus.wapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@MapperScan(basePackages = {"com.xforceplus.wapp.dao","com.xforceplus.wapp.modules.*.dao"})
@EnableScheduling
@EnableTransactionManagement
public class WappHostApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(com.xforceplus.wapp.WappHostApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(com.xforceplus.wapp.WappHostApplication.class);
	}
}
