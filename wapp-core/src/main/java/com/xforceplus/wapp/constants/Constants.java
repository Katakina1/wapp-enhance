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
     * 上传文件类型-xml
     */
    public static final int FILE_TYPE_XML = 4;
	
	 /**
     * pdf后缀名
     */
    public static final String SUFFIX_OF_PDF = "pdf";
    /**
     * odf后缀名
     */
    public static final String SUFFIX_OF_OFD = "ofd";

    /**
     * xml后缀名
     */
    public static final String SUFFIX_OF_XML = "xml";


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
    /**
     * 非商验真状态成功
     */
    String VERIFY_NONE_BUSINESS_SUCCESSE="2";
    /**
     * 非商验真状态失败
     */
    String VERIFY_NONE_BUSINESS_FAIL="1";

    /**
     * 非商验真中
     */
    String VERIFY_NONE_BUSINESS_DOING="0";
    /**
     * 非商验签中
     */
    String SIGN_NONE_BUSINESS_DOING="0";
    /**
     * 非商验签成功
     */
    String SIGN_NONE_BUSINESS_SUCCESS="2";
    /**
     * 非商验签失败
     */
    String SIGIN_NONE_BUSINESS_FAIL="1";
    /**
     * 非商是否提交标识 未提交
     */
    String SUBMIT_NONE_BUSINESS_UNDO_FLAG="0";
    /**
     * 非商是否提交标识 已提交
     */
    String SUBMIT_NONE_BUSINESS_DONE_FLAG="1";

    String ZERO_STR = "0";

    String ONE_STR = "1";

    String TWO_STR = "2";

    /**
     * 结算单撤销时撤销红字轮训key
     */
    String DESTROY_SETTLEMENT_RED_NO_PRE = "destroy_settlement_red_no_pre_";
}
