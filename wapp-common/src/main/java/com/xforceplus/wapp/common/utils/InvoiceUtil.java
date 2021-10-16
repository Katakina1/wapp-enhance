package com.xforceplus.wapp.common.utils;


import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * 发票相关工具类
 *
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-18 11:22
 **/
public class InvoiceUtil {
    /**
     * 发票类型转换
     *
     * @param invoiceType 发票类型
     * @param invoiceCode 发票代码
     * @return
     */
    public static String getInvoiceType(String invoiceType, String invoiceCode) {
        if (Objects.nonNull(invoiceType)) {
            switch (invoiceType) {
                case "se":
                    return "08";
                case "ce":
                    return "10";
                case "ju":
                    return "11";
                case "c":
                    return "04";
                case "s":
                    return "01";
                case "v":
                    return "03";
                default:
                    return CommonUtil.getFplx(invoiceCode);
            }
        }
        return CommonUtil.getFplx(invoiceCode);
    }


    public static String dateToStrLong(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }
}
