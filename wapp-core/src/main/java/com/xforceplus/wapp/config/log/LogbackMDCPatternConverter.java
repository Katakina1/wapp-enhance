package com.xforceplus.wapp.config.log;

import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-15 16:51
 **/

public class LogbackMDCPatternConverter extends MDCConverter {
    private static final String CONVERT_KEY = "tid";
    private boolean convert4TID = false;

    public LogbackMDCPatternConverter() {
    }

    public void start() {
        super.start();
        String[] key = OptionHelper.extractDefaultReplacement(this.getFirstOption());
        if (null != key && key.length > 0 && "tid".equals(key[0])) {
            this.convert4TID = true;
        }

    }

    public String convert(ILoggingEvent iLoggingEvent) {
        return this.convert4TID ? this.convertTID(iLoggingEvent) : super.convert(iLoggingEvent);
    }

    public String convertTID(ILoggingEvent iLoggingEvent) {
        return "TID: N/A";
    }
}