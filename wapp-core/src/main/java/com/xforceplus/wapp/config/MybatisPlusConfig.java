package com.xforceplus.wapp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@EnableTransactionManagement
@Configuration
@MapperScan({"com.xforceplus.wapp.repository","com.xforceplus.wapp.modules.sys.dao"})
public class MybatisPlusConfig {

    /**
     * 新增修改使用行锁
     * @return
     */
    @Bean
    public MybatisRowLockPlugin mybatisRowLockPlugin() {
        MybatisRowLockPlugin mybatisRowLockPlugin = new MybatisRowLockPlugin();
        //设置参数，比如阈值等，可以在配置文件中配置，这里直接写死便于测试
        Properties properties = new Properties();
        //这里设置慢查询阈值为1毫秒，便于测试
        properties.setProperty("time", "1");
        mybatisRowLockPlugin.setProperties(properties);
        return mybatisRowLockPlugin;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public SqlExplainInterceptor sqlExplainInterceptor(){
        return new SqlExplainInterceptor();
    }


//    @Bean
//    public SqlServerOperationInterceptor sqlServerOperationInterceptor(){
//        return new SqlServerOperationInterceptor();
//    }



}