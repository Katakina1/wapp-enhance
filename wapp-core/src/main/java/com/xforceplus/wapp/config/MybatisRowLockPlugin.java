package com.xforceplus.wapp.config;


import com.aisinopdf.text.P;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MybatisRowLockPlugin extends AbstractSqlParserHandler implements Interceptor {

    private long time;

    public static final String NO_LOCK=" with (nolock) ";


    //方法拦截
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //通过StatementHandler获取执行的sql
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // SQL 解析
        this.sqlParser(metaObject);

        // 先判断是不是SELECT操作  (2019-04-10 00:37:31 跳过存储过程)
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql().trim();
        if (skipRowlock(sql)){
            return invocation.proceed();
        }
        switch (mappedStatement.getSqlCommandType()) {
            case UPDATE: {
                // 在set 前面添加
                int index = sql.indexOf("SET");
                if (index<0){
                    index = sql.indexOf("set");
                }
                sql = sql.substring(0, index) + " with (rowlock) " + sql.substring(index);

//                Field field = boundSql.getClass().getDeclaredField("sql");
//                field.setAccessible(true);
//                field.set(boundSql, sql);
                break;
            }
            case INSERT: {
                int valueIndex = sql.indexOf("value");
                if (valueIndex < 0) {
                    valueIndex = sql.indexOf("VALUE");
                }

                //在第一个（ 前面插入
                int index = sql.indexOf("(");
                if (valueIndex > -1 && valueIndex < index) {
                    index = valueIndex;
                }
                sql = sql.substring(0, index) + " with (rowlock) " + sql.substring(index);

//                Field field = boundSql.getClass().getDeclaredField("sql");
//                field.setAccessible(true);
//                field.set(boundSql, mSql);
                break;
            }
            case SELECT: {
                // 在set 前面添加
                int fromIndex = sql.indexOf("from");
                if (fromIndex<0) {
                    fromIndex = sql.indexOf("FROM");
                }

                int index = sql.indexOf("WHERE");
                if (index < 0) {
                    index = sql.indexOf("where");
                }

                if (index < 0) {
                    break;
                }

                String tableArea = sql.substring(fromIndex, index);
                if (tableArea.contains("join") || tableArea.contains("JOIN")) {
//                    final String[] split = tableArea.split("(\\s+)((LEFT?|RIGIT?|INNER?|OUTER?|left?|rigit?|inner?|outer?)\\s+(join|JOIN))");
//                    Pattern pattern=Pattern.compile("(\\s+)((LEFT|RIGHT)\\s+)?((INNER|OUTER)\\s+)?JOIN",Pattern.CASE_INSENSITIVE);
//                    final Matcher matcher = pattern.matcher(sql);
//                    if (matcher.find()) {
//                        final String group = matcher.group();
//                        System.out.println("group = " + group);
//                    }
//                    if (split.length > 0) {
//                        String finalSql = sql.substring(0, fromIndex);
//                        finalSql = finalSql + split[0] + NO_LOCK;
//                        sql = finalSql + sql.substring(fromIndex+split[0].length());
//                    }

                }else {
                    sql = sql.substring(0, index) + NO_LOCK + sql.substring(index);
                }

                break;
            }
        }
        metaObject.setValue("delegate.boundSql.sql", sql);
        return invocation.proceed();
    }



    /**
     * 跳过rowlock
     *
     * @param sql
     * @return
     */
    boolean skipRowlock(String sql) {
        return (sql.contains("rowlock") || sql.contains("ROWLOCK") || sql.contains("nolock") || sql.contains("NOLOCK"));
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

//    public static void main(String[] args) {
//        match("from aaa  inner  join bbb b  a.a=b.b inner join B");
//
//    }

//    private static boolean match(String sql){
//        Pattern pattern=Pattern.compile("(\\s+)((LEFT|RIGHT)\\s+)?((INNER|OUTER)\\s+)?JOIN",Pattern.CASE_INSENSITIVE);
////        Pattern pattern=Pattern.compile("/(\\s+)(((LEFT\\s)?|(RIGIT\\s)?|(INNER\\s)?|(OUTER\\s)?|(left\\s)?|(rigit\\s)?|(inner\\s)?|(outer\\s)?)\\s+(join|JOIN))/i");
//        final Matcher matcher = pattern.matcher(sql);
//        if (matcher.find()) {
//            final String group = matcher.group();
//            System.out.println("group = " + group);
//        }
//        return true;
//
//    }
}

