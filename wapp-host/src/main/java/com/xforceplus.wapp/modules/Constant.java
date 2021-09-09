package com.xforceplus.wapp.modules;

/**
 * 系统全局常量
 * @author Colin.hu
 * @date 5/4/2018
 */
public final class Constant {

    private Constant() {
    }

    public static final String CHARSET_UTF = "UTF-8";
    public static final String SIGN_HMACSHA1 = "HmacSHA1";
    public static final String DEFAULT_SHORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_FORMAT = "yyyyMMdd";
    public static final String LONG_DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String IMG_TYPE_PNG = "png";
    public static final String IMG_TYPE_JPG = "jpg";
    public static final String FILE_TYPE_ZIP = ".zip";

    //excel最大导入数量
    public static final int MAX_IMPORT_SIZE = 500;

    //请求超时时间
    public static final Integer SOCKET_TIME_OUT = 300000;

    public static final String FONT = "宋体";

    //发票采集列表导出模板位置
    public static final String EXPORT_INVOICE_COLLECTION = "export/collect/invoiceCollectionList.xlsx";

    //异常发票采集导出模板位置
    public static final String EXPORT_ABNORMAL_INVOICE_COLLECTION = "export/collect/abnormalInvoiceCollectionList.xlsx";

    //未补明细发票导出模板位置
    public static final String EXPORT_NO_DETAILED = "export/collect/noDetailedInvoiceList.xlsx";

    //抵账表存在明细
    public static final String DETAIL_YES_OR_NO = "1";

    //普票类型
    public static final String[] TOME_INVOICE_TYPE = {"04","10","11"};

    //9位序列号
    public static final int RAND_SER = 9;

}
