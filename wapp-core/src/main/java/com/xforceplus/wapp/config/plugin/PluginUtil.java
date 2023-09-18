package com.xforceplus.wapp.config.plugin;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-04-24 17:55
 **/
public class PluginUtil {

    public static boolean skipRowlock(String sql) {
        return (sql.contains("rowlock") || sql.contains("ROWLOCK") || sql.contains("nolock") || sql.contains("NOLOCK"));
    }
}
