package com.xforceplus.wapp.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mashaopeng@xforceplus.com
 */
@UtilityClass
public class StaticString {
    public final String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public String formatDate(String dateStr) {
         StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(dateStr) || dateStr.length() < 4) {
            return dateStr == null ? "" : dateStr;
        }
        builder.append("-");
        builder.append(dateStr, 0, 4);
        builder.append("-");
        if (dateStr.length() >= 6) {
            builder.append(dateStr, 4, 6);
            builder.append("-");
        }
        if (dateStr.length() >= 8) {
            builder.append(dateStr, 6, 8);
            builder.append("-");
        }
        return builder.substring(1, builder.length() - 1);
    }
}
