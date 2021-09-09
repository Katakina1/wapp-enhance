package com.xforceplus.wapp.modules.enterprise;

/**
 * 请求地址
 * Created by vito.xing on 2018/4/12
 */
public final class WebUriMappingConstant {

    private static final String MODULES_ROOT = "/modules/";

    /**
     * 企业信息列表
     */
    public static final String URI_ENTERPRISE_INFO_LIST = MODULES_ROOT + "enterpriseinfo/list/queryPaged";

    /**
     * 企业黑名单信息列表
     */
    public static final String URI_ENTERPRISE_BLACK_LIST = MODULES_ROOT + "enterpriseblack/list/queryPaged";

    /**
     * 删除黑名单企业
     */
    public static final String URI_DELETE_BLACK_ENTERPRISE = MODULES_ROOT + "enterpriseblack/delete";

    /**
     * 根据企业Id获取企业信息
     */
    public static final String URI_GET_ENTERPRISE_INFO_BY_ID = MODULES_ROOT + "enterprise/getEnterpriseById/{enterpriseId}";

    /**
     * 根据黑名单企业Id获取黑名单企业信息
     */
    public static final String URI_GET_ENTERPRISE_BLACK_BY_ID = MODULES_ROOT + "enterpriseblack/getBlackEnterpriseById/{blackEnterpriseId}";


    /**
     * 修改黑名单中的企业信息
     */
    public static final String URI_UPDATE_BLACK_ENTERPRISE = MODULES_ROOT + "enterpriseblack/update";

    /**
     * 新增黑名单企业
     */
    public static final String URI_SAVE_BLACK_ENTERPRISE = MODULES_ROOT + "enterpriseblack/save";

    /**
     * 从excel文件导入黑名单企业
     */
    public static final String URI_IMPORT_BLACK_ENTERPRISE = MODULES_ROOT + "enterpriseblack/import";

    /**
     *  企业黑名单模板下载URL
     */
    public static final String URI_DOWNLOAD_TEMPLATE_BLACK_ENTERPRISE =    "export/enterpriseblack/downloadTemplate";

    /**
     * 商品列表
     */
    public static final String URI_GOODS_LIST = MODULES_ROOT + "goods/list/queryPaged";

    /**
     * 税收分类编码列表
     */
    public static final String URI_TAX_CODE_LIST = MODULES_ROOT + "taxcode/list/queryPaged";

    /**
     * 删除商品
     */
    public static final String URI_DELETE_GOODS = MODULES_ROOT + "goods/delete";

    /**
     * 更新商品信息
     */
    public static final String URI_UPDATE_GOODS = MODULES_ROOT + "goods/update";

    /**
     * 根据商品Id获取商品信息
     */
    public static final String URI_GET_GOODS_BY_ID = MODULES_ROOT + "goods/getGoodsById/{goodsId}";


    /**
     * 新增黑名单商品
     */
    public static final String URI_SAVE_BLACK_GOODS = MODULES_ROOT + "goodsblack/save";

    /**
     * 新增商品信息
     */
    public static final String URI_SAVE_GOODS = MODULES_ROOT + "goods/save";

    /**
     * 从excel文件导入黑名单商品
     */
    public static final String URI_IMPORT_BLACK_GOODS = MODULES_ROOT + "goodsblack/import";

    /**
     * 商品黑名单模板下载URL
     */
    public static final String URI_DOWNLOAD_TEMPLATE_BLACK_GOODS = "export/goodsblack/downloadTemplate";


    /**
     * 从excel文件导入商品税收分类编码
     */
    public static final String URI_IMPORT_GOODS = MODULES_ROOT + "goods/import";

    /**
     * 商品税收分类模板下载URL
     */
    public static final String URI_DOWNLOAD_TEMPLATE_GOODS_TAX_CODE = "export/goodstaxcode/downloadTemplate";


}
