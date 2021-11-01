package com.xforceplus.wapp.config;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-30 14:46
 **/
@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
        , @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
//@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlServerOperationInterceptor extends AbstractSqlParserHandler implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object params =  args[1];
        final BoundSql boundSql = ms.getBoundSql(params);
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        sqlParser(metaObject);
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }

        final String sql = boundSql.getSql();
        log.info("sql:{}",sql);
        switch (ms.getSqlCommandType()){
            case UPDATE:
                break;
            case DELETE:
                break;
            case INSERT:
                break;
            case SELECT:

                break;
        }



        // 先判断是不是SELECT操作  (2019-04-10 00:37:31 跳过存储过程)
//        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
//        if (SqlCommandType.SELECT == mappedStatement.getSqlCommandType()
//                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
//            return invocation.proceed();
//        }
        return invocation.proceed();
    }
}
