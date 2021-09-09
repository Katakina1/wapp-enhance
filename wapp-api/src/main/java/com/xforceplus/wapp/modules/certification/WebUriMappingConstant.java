package com.xforceplus.wapp.modules.certification;

/**
 * 请求地址
 * @author Colin.hu
 * @date 4/13/2018
 */
public final class WebUriMappingConstant {

    private WebUriMappingConstant() {
    }

    private static final String MODULES_ROOT = "/modules/";

    /**
     * 导入认证
     */
    public static final String URI_INVOICE_CERTIFICATION_IMPORT = MODULES_ROOT + "certification/import";
    public static final String URI_CERTIFICATION_EXPORT_TEMP = "export/certificationTemplate";
    public static final String URI_CERTIFICATION_AUTH_SUBMIT = MODULES_ROOT +"certification/authSumit";


    /**
     * 导入勾选
     */
    public static final String URI_INVOICE_CERTIFICATION_CHECK_IMPORT = MODULES_ROOT + "certification/checkImport";
    public static final String URI_CERTIFICATION_EXPORT_CHECK_TEMP = "export/certificationCheckTemplate";
    public static final String URI_CERTIFICATION_CHECK_SUBMIT = MODULES_ROOT +"certification/submit";

    /**
     * 认证查询
     */
    public static final String URI_INVOICE_CERTIFICATION_LIST = MODULES_ROOT + "certification/query/queryPaged";
    public static final String URI_INVOICE_CERTIFICATION_EXPORT = "export/certification";
}
