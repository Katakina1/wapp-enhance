package com.xforceplus.wapp.config.plugin;


import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import com.xforceplus.wapp.config.vistor.NoLockSQLServerASTVisitorAdapter;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MybatisRowLockPlugin extends AbstractSqlParserHandler implements Interceptor {

    public static final String NO_LOCK= NoLockSQLServerASTVisitorAdapter.NO_LOCK;


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
        if (PluginUtil.skipRowlock(sql)){
            return invocation.proceed();
        }
        switch (mappedStatement.getSqlCommandType()) {
            case UPDATE: { 
                // 在set 前面添加   2022-08-08新增，避免SQL已经写了rowlock
            	if (!(sql.indexOf("rowlock") > -1 || sql.indexOf("ROWLOCK") > -1)) {
            		int index = sql.indexOf(" SET ");
                    if (index < 0) {
                        index = sql.indexOf(" set ");
                    }
                    sql = sql.substring(0, index) + " with (rowlock) " + sql.substring(index);
            	}
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
                sql = NoLockSQLServerASTVisitorAdapter.newInstance(sql).doVisitGetSql();
                break;
            }
        }
        metaObject.setValue("delegate.boundSql.sql", sql);
        return invocation.proceed();
    }



    //获取到拦截的对象，底层也是通过代理实现的，实际上是拿到一个目标代理对象
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }



//    public static void main(String[] args) {
//        String sql="select * from (select * from t s left join a on a.b=t.b where 1=1) aa";
//
//
//        final String visitGetSql = new NoLockSQLServerASTVisitorAdapter(sql).doVisitGetSql();
//        System.out.println(visitGetSql);
//
//    }
}

