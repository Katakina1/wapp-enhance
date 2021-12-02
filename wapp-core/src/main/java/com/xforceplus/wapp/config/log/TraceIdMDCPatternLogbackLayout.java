package com.xforceplus.wapp.config.log;

import ch.qos.logback.classic.PatternLayout;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-15 16:54
 **/
public class TraceIdMDCPatternLogbackLayout extends PatternLayout {
    public TraceIdMDCPatternLogbackLayout() {
    }

    static {
        defaultConverterMap.put("X", LogbackMDCPatternConverter.class.getName());
    }
}
