package com.xforceplus.wapp.modules.check.checkutils;

/**
 * @author Bobby
 * @date 2018/4/27
 * mybatis参数验证
 */
public final class CheckUtils {


    /**
     * 非空
     */
    public static boolean isNotEmpty(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof String) {
            return !((String) o).trim().equals("");
        }
        return true;

    }
}
