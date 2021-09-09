package com.xforceplus.wapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@MapperScan(basePackages = {"com.xforceplus.wapp.modules.*.dao","com.xforceplus.*.dao"})
@EnableScheduling
@EnableTransactionManagement
public class WappApiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WappApiApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WappApiApplication.class);
	}
}
