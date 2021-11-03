package com.xforceplus.wapp.config;


import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MybatisRowLockPlugin implements Interceptor {

    private long time;


    //方法拦截
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //通过StatementHandler获取执行的sql
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql().trim();

        if (sql.startsWith("INSERT") && skipRowlock( sql)){
            //在第一个（ 前面插入
            int index = sql.indexOf("(");
            String mSql = sql.substring(0,index)+" with (rowlock) " + sql.substring(index);

            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, mSql);
        }else if (sql.startsWith("UPDATE") && skipRowlock(sql)){
            // 在set 前面添加
            int index = sql.indexOf("SET");
            String mSql = sql.substring(0,index)+" with (rowlock) " + sql.substring(index);

            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, mSql);
        }
//        long start = System.currentTimeMillis();
        Object proceed = invocation.proceed();
//        long end = System.currentTimeMillis();
//        if ((end - start) > time) {
//            System.out.println("本次数据库操作是慢查询，sql是:" + sql);
//        }
        return proceed;
    }

    /**
     *   跳过rowlock
     * @param sql
     * @return
     */
    boolean skipRowlock(String sql){
        return !(sql.contains("rowlock") || sql.contains("ROWLOCK"));
    }


    //获取到拦截的对象，底层也是通过代理实现的，实际上是拿到一个目标代理对象
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    //获取设置的阈值等参数
    @Override
    public void setProperties(Properties properties) {
        this.time = Long.parseLong(properties.getProperty("time"));
    }
}

