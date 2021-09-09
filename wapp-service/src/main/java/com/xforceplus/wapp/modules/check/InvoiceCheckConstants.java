package com.xforceplus.wapp.modules.check;

import org.springframework.stereotype.Component;

/**
 * @author Bobby
 * @date 2018/4/23
 */
@Component

public final class InvoiceCheckConstants {

    /**
     * 调用成功
     */
    public static final String INVOICE_CHECK_CYJG_CODE_SUCCESS = "0000";

    /**
     * 调用失败
     */
    public static final String INVOICE_CHECK_CYJG_CODE_FAILURE = "9999";

    /**
     * 验证收发一致
     */
    public static final String INVOICE_CHECK_CYJG_CODE_SFYZ = "0001";

    /**
     * 验证收发一致
     */
    public static final String INVOICE_TYPE_VEHICLE = "03";


    /**
     * 发票类型
     */
    public static final String INVOICE_TYPE = "04,10,11,14";

    /**
     * response code
     */

    /**
     * 发票查验成功
     */
    public static final String RESPONSE_CODE_SUCCESS = "000";
    /**
     * 发票已存在
     */
    public static final String RESPONSE_CODE_EXIST = "001";
    /**
     * 服务器端错误
     */
    public static final String RESPONSE_CODE_INNER_ERROR = "002";

    /**
     * 接口调用错误
     */
    public static final String RESPONSE_CODE_REMOTE_SERVER_ERROR = "003";

    /**
     * 无权限处理
     */
    public static final String RESPONSE_CODE_AUTH_ERROR = "004";
}
