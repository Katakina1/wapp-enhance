package com.xforceplus.wapp.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;

import java.util.List;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 异常断言
 * @date : 2022/09/12 9:39
 **/
public class Asserts {

    public static void isNull(Object obj, String message) {
        if (obj == null) {
            throw new EnhanceRuntimeException(message);
        }
    }

    public static void isFalse(boolean b, String message) {
        if (!b) {
            throw new EnhanceRuntimeException(message);
        }
    }

    public static void isTrue(boolean b, String message) {
        if (b) {
            throw new EnhanceRuntimeException(message);
        }
    }

    public static <T> void isEmpty(List<T> list, String message) {
        if (CollectionUtil.isEmpty(list)) {
            throw new EnhanceRuntimeException(message);
        }
    }

}
