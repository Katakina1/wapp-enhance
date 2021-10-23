package com.xforceplus.wapp.modules.rednotification.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class RegexUtils {

    public static boolean composedByNumberOrLetter(String str) {
        return Objects.nonNull(str) && str.matches("[a-zA-Z0-9]+");
    }

    public static boolean isInvoiceCode(String str) {
        return Objects.nonNull(str) && (str.length() == 10 || str.length() == 12) && str.matches("[0-9]+");
    }

    public static boolean isInvoiceNo(String str) {
        return Objects.nonNull(str) && str.length() == 8 && str.matches("[0-9]+");
    }

    public static boolean checkApplyDateFormat(String applyStartDate){
        if(StringUtils.isNotEmpty(applyStartDate)){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            try {
                dateFormat.setLenient(false);
                dateFormat.parse(applyStartDate);
                return true;
            } catch (ParseException e) {

            }
        }
        return false;
    }

}
