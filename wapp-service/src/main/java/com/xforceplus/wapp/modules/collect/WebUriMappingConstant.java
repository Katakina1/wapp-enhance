package com.xforceplus.wapp.modules.collect;

/**
 * 请求地址
 * @author Colin.hu
 * @date 4/11/2018
 */
public final class WebUriMappingConstant {

    private WebUriMappingConstant() {
    }

    private static final String MODULES_ROOT = "/modules/";

    /**
     * 发票采集列表
     */
    public static final String URI_INVOICE_COLLECTION_LIST = MODULES_ROOT + "collect/list/queryPaged";
    public static final String URI_INVOICE_COLLECTION_INVOICE_INFO = MODULES_ROOT + "collect/getInvoiceInfo";
    public static final String URI_INVOICE_COLLECTION_TAX_NAME = MODULES_ROOT + "collect/getGfName";
    public static final String URI_INVOICE_COLLECTION_LIST_EXPORT = "export/invoiceCollectionList";

    /**
     * 异常发票采集
     */
    public static final String URI_ABNORMAL_INVOICE_COLLECTION = MODULES_ROOT + "collect/abnormal/queryPaged";
    public static final String URI_ABNORMAL_INVOICE_COLLECTION_EXPORT = "export/abnormalInvoiceCollectionList";

    /**
     * 未补明细发票
     */
    public static final String URI_DETAILED_INVOICE_COLLECTION = MODULES_ROOT + "collect/detailed/queryPaged";
    public static final String URI_DETAILED_INVOICE_COLLECTION_EXPORT = "export/detailedInvoiceCollectionList";
    public static final String URI_DETAILED_INVOICE_MANUAL_INSPECTION = MODULES_ROOT + "collect/detailed/manualInspection";

    /**
     * 根据类型获取 类型名-code的对应关系
     */
    public static final String URI_PARAM_MAP_BY_TYPE = MODULES_ROOT + "param/getParamMap";
}
