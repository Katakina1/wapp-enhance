package com.xforceplus.wapp.common.utils;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-12 16:38
 **/
public class JsonUtil {
    public static <T> T fromJson(String json ,Class<T> clz){
        return JSON.parseObject(json,clz);
    }

    public static <T> List<T> fromJsonList(String json , Class<T> clz){
        return JSON.parseArray(json,clz);
    }

    public static String toJsonStr(Object object){
        return JSON.toJSONString(object);
    }
}
