package com.xforceplus.wapp.config.plugin;

import java.sql.Connection;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.xforceplus.wapp.config.vistor.NoLockSQLServerASTVisitorAdapter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-04-24 17:52
 **/
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PaginationExtInterceptor extends PaginationInterceptor {
    @Override
    protected void queryTotal(String sql, MappedStatement mappedStatement, BoundSql boundSql, IPage<?> page, Connection connection) {
//        if (!PluginUtil.skipRowlock(sql)) {
//            int index = sql.indexOf("WHERE");
//            if (index < 0) {
//                index = sql.indexOf("where");
//            }
//            if (index > 0) {
//                sql = sql.substring(0, index) + NO_LOCK + sql.substring(index);
//            } else {
//                sql = sql + NO_LOCK;
//            }
//        }

        sql = NoLockSQLServerASTVisitorAdapter.newInstance(sql).doVisitGetSql();

        super.queryTotal(sql, mappedStatement, boundSql, page, connection);
    }
}
