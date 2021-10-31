package com.xforceplus.wapp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
@MapperScan({"com.xforceplus.wapp.repository","com.xforceplus.wapp.modules.sys.dao"})
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

//    @Bean
//    public SqlServerOperationInterceptor sqlServerOperationInterceptor(){
//        return new SqlServerOperationInterceptor();
//    }
}