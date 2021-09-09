package com.xforceplus.wapp.modules.check;

/**
 * @author Bobby
 * @date 2018/4/19
 * 发票查验
 */
public class WebUriMappingConstant {
    private static final String MODULES_ROOT = "/modules/";

    private static final String EXPORT_ROOT = "/export/";


    /**
     * 查验历史列表
     */
    public static final String URI_INVOICE_CHECK_MODULES_INVOICE_HAND_CHECK = MODULES_ROOT + "invoice/check/handCheck";

    /**
     * 查验历史列表
     */
    public static final String URI_INVOICE_CHECK_MODULES_HISTORY_LIST = MODULES_ROOT + "invoice/check/history";
    /**
     * 查验历史详情
     */
    public static final String URI_INVOICE_CHECK_MODULES_HISTORY_DETAIL = MODULES_ROOT + "invoice/check/detail";
    /**
     * 查验历史-查验
     */
    public static final String URI_INVOICE_CHECK_MODULES_INVOICE_CHECK = MODULES_ROOT + "invoice/check/doCheck";
    /**
     * 查验历史删除
     */
    public static final String URI_INVOICE_CHECK_MODULES_HISTORY_DELETE = MODULES_ROOT + "invoice/check/delete";
    /**
     * 查验历史导出
     */
    public static final String URI_INVOICE_CHECK_MODULES_HISTORY_EXPORT = EXPORT_ROOT + "invoice/check/export";
    /**
     * 查验统计
     */
    public static final String URI_INVOICE_CHECK_MODULES_STATISTICS = MODULES_ROOT + "invoice/check/statistics";

    /**
     * 发票查验删除
     */
    public static final String URI_INVOICE_CHECK_DELETE = MODULES_ROOT + "invoice/checkDelete";
}
