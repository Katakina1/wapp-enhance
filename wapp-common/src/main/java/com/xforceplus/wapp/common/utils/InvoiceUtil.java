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
                case "cb":
                    return "07";
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
                //qc-电子发票（普通发票），qs-电子发票（增值税专用发票）
                case "qc":
                    return "16";
                case "qs":
                    return "18";
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

    /**
     * 发票类型转换
     *
     * @param invoiceType 发票类型
     * @param invoiceCode 发票代码
     * @return
     */
    public static String getInvoiceStatus(String invoiceStatus) {
        if (Objects.nonNull(invoiceStatus)) {
            switch (invoiceStatus) {
                case "0":
                    return "2";
                case "1":
                    return "0";
                default:
                    return invoiceStatus;
            }
        }
        return invoiceStatus;
    }
}
