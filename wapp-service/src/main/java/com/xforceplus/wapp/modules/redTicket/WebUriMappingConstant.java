package com.xforceplus.wapp.modules.redTicket;

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
     * 开红票资料列表
     */
    public static final String URI_OPEN_RED_TICKET_LIST = MODULES_ROOT + "openRedTicket/list/queryPaged";
    /**
     * 打印开红票资料列表(销方)
     */
    public static final String URI_OPEN_RED_TICKET_LIST_PRINTING = MODULES_ROOT + "openRedTicket/list/queryPrintingPaged";
    /**
     * 上传开红票资料列表(销方)
     */
    public static final String URI_OPEN_RED_TICKET_LIST_UPLOAD = MODULES_ROOT + "openRedTicket/list/queryUploadPaged";

    /**
     * 审核开红票资料列表
     */
    public static final String URI_OPEN_RED_TICKET_EXAMINE_LIST = MODULES_ROOT + "openRedTicket/list/examineQueryPaged";
    /**
     * 上传红字通知单页面查询
     */
    public static final String URI_OPEN_RED_TICKET_UPLOAD_NOTICES_LIST = MODULES_ROOT + "upload/list/uploadNoticeQueryPaged";
    /**
     * 开红票资料明细（退货）
     */
    public static final String URI_OPEN_RED_TICKET_RETURN_DETAIL = MODULES_ROOT + "openRedTicket/return/redTicketDetail";
    /**
     * 查询退货列表by id
     */
    public static final String URI_OPEN_RED_TICKET_RETURN_LIST_BY_SERIAL_NUMBER = MODULES_ROOT + "openRedTicket/list/queryByRedTicketDataSerialNumber";

    /**
     * 查询蓝票列表by代号
     */
    public static final String URI_OPEN_RED_TICKET_RETURN_LIST_BY_INVOICECODE_INVOICENO = MODULES_ROOT + "openRedTicket/list/queryByInvoiceCodeAndNo";


    /**
     * 查询蓝票明细列表by红票序列号
     */
    public static final String URI_OPEN_RED_TICKET_RETURN_INVOICE_DETAIL_LIST_BY_SERIAL_NUMBER = MODULES_ROOT + "openRedTicket/list/queryInvoiceDetailByNumber";


    /**
     * 查询合并明细列表by红票序列号
     */
    public static final String URI_OPEN_RED_TICKET_RETURN_MERGE_LIST_BY_SERIAL_NUMBER = MODULES_ROOT + "openRedTicket/list/queryMergeDetailByNumber";




    /**
     * 开红票资料明细（协议）
     */
    public static final String URI_OPEN_RED_TICKET_AGREEMENT_DETAIL = MODULES_ROOT + "openRedTicket/agreement/redTicketDetail";

    /**
     * 查询协议列表by红票序列号
     */
    public static final String URI_OPEN_RED_TICKET_AGREEMENT_QUERY_BY_BUMBER = MODULES_ROOT + "openRedTicket/list/queryAgreementByRedTicketDataSerialNumber";


    /**
     * 根据查看上传资料
     */
    public static final String URI_OPEN_RED_TICKET_QUERY_IMG = MODULES_ROOT + "openRedTicket/queryImg";
    /**
     * 根据查看红字通知单
     */
    public static final String URI_OPEN_RED_TICKET_QUERY_RED_ROTICE_IMG = MODULES_ROOT + "openRedTicket/queryRedNoticeImg";

    /**
     * 红票资料上传
     */
    public static final String URI_OPEN_RED_TICKET_DATA_UPLOAD = MODULES_ROOT + "openRedTicket/data/upload";
    /**
     * 红字通知单上传
     */
    public static final String URI_OPEN_RED_TICKET_RED_NOTIES_UPLOAD = MODULES_ROOT + "openRedTicket/data/uploadRed";
    /**
     * 红字通知单上传
     */
    public static final String URI_OPEN_RED_TICKET_RED_NOTIES_UPLOAD_BATH = MODULES_ROOT + "openRedTicket/data/uploadRedBatch";


    /**
     * TOKEN过期
     */
    public static final String URI_OPEN_RED_TICKET_FOR_IMAGE_CHECK = MODULES_ROOT + "checkImageToken";

    /**
     * TOKEN过期
     */
    public static final String URI_OPEN_RED_TICKET_FOR_FILE_CHECK = MODULES_ROOT + "downLoadFileToken";

    /**
     * 获取资料
     */
    public static final String URI_OPEN_RED_TICKET_GET_IMAGE_ALL = MODULES_ROOT + "openRedTicket/getImageForAll";
    /**
     * 获取红字通知单
     */
    public static final String URI_OPEN_RED_TICKET_GET_IMAGE_NOTICE = MODULES_ROOT + "openRedTicket/getImageForNotice";
    /**
     * 下载文件到本地
     */
    public static final String URI_OPEN_RED_TICKET_DOWN_LOAD_FILE = MODULES_ROOT + "downLoadFile";

    /**
     * 根据类型获取 类型名-code的对应关系
     */
    public static final String URI_PARAM_MAP_BY_TYPE = MODULES_ROOT + "param/getParamMap";


    /**
     * 保存不同意信息
     */
    public static final String URI_OPEN_RED_TICKET_SAVE_EXAMINE_REMARKS = MODULES_ROOT + "openRedTicket/saveExamineRemarksById";

    /**
     * 查询红票
     */
    public static final String URI_RDE_TICKET_LIST = MODULES_ROOT + "redTicket/selectRedTicketList";

    /**
     * 检验红票
     */
    public static final String URI_RDE_RED_TICKET_INVOICE = MODULES_ROOT + "redTicket/invoice/query";

    /**
     *保存红票
     */
    public static final String URI_RDE_RED_SAVE_TICKET_INVOICE = MODULES_ROOT + "redTicket/saveRedTicket";
    /**
     *查询红票by id
     */
    public static final String URI_RDE_RED_SELECT_TICKET_INVOICE_BY_ID = MODULES_ROOT + "redTicket/selectRedTicketById";
    /**
     * 可红冲发票信息列表
     */
    public static final String URI_INVOICE_LIST = MODULES_ROOT + "invoice/list/query";

    /**
     * 可红冲发票明细信息列表
     */
    public static final String URI_INVOICE_DETAILS_LIST = MODULES_ROOT + "invoiceDetails/list/query";

    /**
     * 可红冲发票明细信息列表(根据名称查询)
     */
    public static final String URI_INVOICE_DETAILSBYNAME_LIST = MODULES_ROOT + "invoiceDetailsByName/list/query";

    /**
     * 生成红票数据
     */
    public static final String URI_GENERATE_REDTICKET_DATA = MODULES_ROOT + "generateRedTicketData/list/insert";

    /**
     * 可红冲发票明细信息列表
     */
    public static final String URI_CANREDRUSH_INVOICE_DETAILS_LIST = MODULES_ROOT + "canRedRushInvoiceDetails/list/query";
    public static final String URI_INVOICE_OPEN_RED_QUERY_XL = MODULES_ROOT + "openRedInvoiceQuery/queryXL";
    /**
     * 查询类型
     */
    public static final String URI_INVOICE_OPEN_RED_QUERY_RED_TICKET_TYPE = MODULES_ROOT + "openRedInvoiceQuery/queryOpenRedTicketType";
    /**
     * 导出红票资料pdf
     */
    public static final String URI_INVOICE_OPEN_RED_QUERY_EXPORT_DATA = MODULES_ROOT + "openRedTicket/openRedDateResultListExport";







    /**
     * 开红票资料列表(供)
     */
    public static final String URI_OPEN_RED_TICKET_SUPPLIER_LIST = MODULES_ROOT + "openRedTicketSupplier/list/queryPaged";
    /**
     * 开红票资料列表(供)
     */
    public static final String URI_CANCEL_RED_RUSH_INFORMATION = MODULES_ROOT + "cancelRedRushInformation/list/update";
    /**
     * 折让类型红票匹配明细
     */
    public static final String URI_RED_TICKET_DISCOUNT_DETAIL = MODULES_ROOT + "RedTicket/discount/redTicketDetail";


    /**
     * 打印查询红票
     */
    public static final String URI_INVOICE_OPEN_RED_QUERY_PRINT_COVER = MODULES_ROOT + "redTicket/selectRedTicketPrintCoverList";
    /**
     * 同意开红票资料
     */
    public static final String URI_INVOICE_OPEN_RED_QUERY_RED_TICKET_AGREE = MODULES_ROOT + "openRedTicket/updateMatchStatus";
    /**
     * 导出审核清单
     */
    public static final String URI_INVOICE_EXPORT_EXAMINE_DATA =  "/export/exportDataExamine";
    /**
     * 发送邮件通知税务组
     */
    public static final String URI_INVOICE_SEND_MESSAGE_DATA =  MODULES_ROOT+"openRedTicket/sendMessage";



}
