package com.xforceplus.wapp.config.vistor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;
import com.xforceplus.wapp.config.plugin.PluginUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-04-26 19:25
 **/
public class NoLockSQLServerASTVisitorAdapter extends SQLServerASTVisitorAdapter {
    public static final String NO_LOCK=" with (nolock) ";
    private final String sql;
    private boolean modifyAlias;

    public NoLockSQLServerASTVisitorAdapter(String sql){
        modifyAlias =true;
        this.sql=sql;
    }
    @Override
    public boolean visit(SQLExprTableSource x) {
        if (modifyAlias) {
            if (!PluginUtil.skipRowlock(sql)) {
                String alias = x.getAlias();
                if (StringUtils.isNotBlank(alias)) {
                    alias += NO_LOCK;
                } else {
                    alias = NO_LOCK;
                }
                x.setAlias(alias);
            }
            modifyAlias = false;
        }
        return super.visit(x);
    }

    public String doVisitGetSql(){
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlserver);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(this);
        }
        return SQLUtils.toSQLString(stmtList,DbType.sqlserver, new SQLUtils.FormatOption(true, false));
    }

    public static NoLockSQLServerASTVisitorAdapter newInstance(String sql){
        return new NoLockSQLServerASTVisitorAdapter(sql);
    }


}
