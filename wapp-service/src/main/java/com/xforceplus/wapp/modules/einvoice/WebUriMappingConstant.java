package com.xforceplus.wapp.modules.einvoice;

/**
 * 请求地址
 * Created by Marvin.zhong on 2018/4/18
 */
public final class WebUriMappingConstant {

    private static final String ELECTRON_ROOT = "/electron/";

    /**
     * 上传电子发票
     */
    public static final String ELECTRON_INVOICE_UPLOAD = ELECTRON_ROOT + "upload";

    /**
     * 保存电票
     */
    public static final String ELECTRON_INVOICE_SAVE = ELECTRON_ROOT + "save";

    /**
     * 删除电票
     */
    public static final String ELECTRON_INVOICE_DELETE = ELECTRON_ROOT + "delete";

    /**
     * 查询电票信息准备保存
     */
    public static final String ELECTRON_INVOICE_UPDATE_SELECT = ELECTRON_ROOT + "selectToUpdate";

    /**
     * 修改的信息保存
     */
    public static final String ELECTRON_INVOICE_UPDATE_SAVE = ELECTRON_ROOT + "update/save";

    /**
     * 获取图片
     */
    public static final String ELECTRON_INVOICE_GET_IMAGE = ELECTRON_ROOT + "getImage";

    /**
     * 获取图片--all
     */
    public static final String ELECTRON_INVOICE_GET_IMAGE_ALL = ELECTRON_ROOT + "getImageForAll";

    /**
     * TOKEN过期
     */
    public static final String ELECTRON_INVOICE_FOR_IMAGE_CHECK = ELECTRON_ROOT + "checkImageToken";

    /**----------------------------电票查询--------------------------------------------*/

    public static final String ELECTRON_INVOICE_QUERY_LIST = ELECTRON_ROOT + "select/list";

    /**----------------------------导出excel-------------------------------------*/

    public static final String ELECTRON_INVOICE_EXPORT = ELECTRON_ROOT + "export";
    /**
     * 模板路径
     */
    public static final String ELECTRON_INVOICE_EXPORT_TEMPLET = "export/electron/ElectronInvoice.xlsx";

}
