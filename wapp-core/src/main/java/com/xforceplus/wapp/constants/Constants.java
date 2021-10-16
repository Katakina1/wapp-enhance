package com.xforceplus.wapp.constants;

public interface Constants {
    /**
     * 上传文件类型-ofd
     */
    public static final int FILE_TYPE_OFD = 0;
    /**
     * 上传文件类型-pdf
     */
    public static final int FILE_TYPE_PDF = 1;
	
	 /**
     * pdf后缀名
     */
    public static final String SUFFIX_OF_PDF = "pdf";
    /**
     * odf后缀名
     */
    public static final String SUFFIX_OF_OFD = "ofd";

    /**
     * 导入类型 01 对应po单
     */
    String WAPP_IMPORT_PO_TYPE="01";

    /**
     * 导入类型 02 对应索赔单
     */
    String WAPP_IMPORT_CLAIM_TYPE="02";
    /**
     * 发票匹配类型 -电票
     */
    String WAPP_MACH_EINVOICE_TYPE="1";

    /**
     * 扫描匹配成功
     */
    String SCAN_MATCH_STATUS_SUCCESS="1";
}
