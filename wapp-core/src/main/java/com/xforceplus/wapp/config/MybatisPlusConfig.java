package com.xforceplus.wapp.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.xforceplus.wapp.config.plugin.MybatisRowLockPlugin;
import com.xforceplus.wapp.config.plugin.PaginationExtInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;

@EnableTransactionManagement
@Configuration
@MapperScan({"com.xforceplus.wapp.repository", "com.xforceplus.wapp.modules.sys.dao", "com.xforceplus.wapp.modules.xforceapi.dao"})
public class MybatisPlusConfig{

    /**
     * 新增修改使用行锁
     * 不能放到PaginationInterceptor 后面， "with"关键字不识别
     * @return
     */
    @Bean
    public MybatisRowLockPlugin mybatisRowLockPlugin() {
        return new MybatisRowLockPlugin();
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
    	PaginationInterceptor interceptor = new PaginationInterceptor();
    	interceptor.setLimit(99999);
        return interceptor;
    }

    @Bean
    public SqlExplainInterceptor sqlExplainInterceptor() {
        return new SqlExplainInterceptor();
    }

}