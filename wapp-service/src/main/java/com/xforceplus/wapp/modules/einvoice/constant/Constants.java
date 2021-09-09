package com.xforceplus.wapp.modules.einvoice.constant;

import java.util.Arrays;
import java.util.List;

/**
 *
 * 电票上传常量配置
 *
 * Date 4/26/2018.
 *
 * @author marvin.zhong
 */
public final class Constants {
    private Constants() {

    }

    /**
     * 特殊的电票代码
     */
    public static final List<String> SPECIAL_ELECTRON_INVOICE = Arrays.asList("144031539110", "131001570151", "133011501118", "111001571071");

    /**
     * 电子发票类型
     */
    public static final String COMMON_ELECTRON_INVOICE_TYPE = "10";

    /**
     * 通行费电子发票类型
     */
    public static final String TOLL_ELECTRON_INVOICE_TYPE = "14";

    /**
     * 查验成功返回标志
     */
    public static final String CHECK_INVOICE_SUCCESS_CODE = "0001";

    /**
     * 查验失败返回标志
     */
    public static final String CHECK_INVOICE_BACK_FAIL_CODE = "0000";

    /**
     * 查验失败此发票是否无效 Y----无效
     */
    public static final String CHECK_BACK_INVOICE_Y = "Y";

    /**
     * 发票状态 0-正常
     */
    public static final String INVOICE_STATUS_ZERO = "0";

    /**
     * 发票状态 2-作废
     */
    public static final String INVOICE_STATUS_TWO = "2";

    /**
     * 发票是否有明细  1 有明细
     */
    public static final String INVOICE_DETAIL_YES = "1";

    /**
     * 发票是否有明细  0 无明细
     */
    public static final String INVOICE_DETAIL_NO = "0";

    /**
     * 查验返回的签收失败标志
     */
    public static final String CHECK_INVOICE_FAIL_CODE = "2";

    /**
     * 签收失败
     */
    public static final String INVOICE_QS_STATUS_FAIL_ZERO = "0";

    /**
     * 签收成功
     */
    public static final String INVOICE_QS_STATUS_SUCCESS_ONE = "1";

    /**
     * 底账发票来源----查验
     */
    public static final String INVOICE_SOURCE_SYSTEM = "1";

    /**
     * 电票是否有效 1： 有效
     */
    public static final String INVOICE_VALID_ONE = "1";
    /**
     * 电票的上传方式----扫码签收
     */
    public static final String ELECTRON_INVOICE_QS_TYPE_ZERO = "0";
    /**
     * 电票的上传方式----扫描仪签收
     */
    public static final String ELECTRON_INVOICE_QS_TYPE_ONE = "1";
    /**
     * 电票的上传方式----app签收
     */
    public static final String ELECTRON_INVOICE_QS_TYPE_TWO = "2";
    /**
     * 电票的上传方式----导入签收
     */
    public static final String ELECTRON_INVOICE_QS_TYPE_THREE = "3";
    /**
     * 电票的上传方式----手工签收
     */
    public static final String ELECTRON_INVOICE_QS_TYPE_FOUR = "4";
    /**
     * 电票的上传方式----pdf上传
     */
    public static final String ELECTRON_INVOICE_QS_TYPE_FIVE = "5";

    /**
     * 当查验的返回的为空时默认的购方税号
     */
    public static final String DEFAULT_ELECTRON_INVOICE_GF_TAX_NO = "123456789012345";

    /**
     * 签收成功
     */
    public static final String INVOICE_QS_SUCCESS_ZH = "签收成功！";

    /**
     * 签收失败
     */
    public static final String INVOICE_QS_FAIL_ZH = "签收失败！";

    /**
     * 签收失败 -- 发票作废
     */
    public static final String INVOICE_QS_FAIL_Z_ZH = "该发票已作废,签收失败！";

    /**
     * 签收失败 -- 无税号权限
     */
    public static final String INVOICE_QS_FAIL_NO_TAX = "无税号权限,签收失败！";

    /**
     * 重复上传
     */
    public static final String UPLOAD_REPEAT_INVOICE = "重复上传！";

    /**
     * 通行费发票，签收失败
     */
    public static final String TOLL_INVOICE_QS_FAIL = "通行费发票，签收失败！";

    /**
     * 通行费发票无底账，签收失败
     */
    public static final String TOLL_INVOICE_QS_FAIL_NO_RECORD = "通行费发票无底账，签收失败！";

    /**
     * 查询返回错误提示
     */
    public static final String CHECK_RESULT_TIP_ERROR = "系统错误！请稍后重试！";

    /**
     * 解析问价的map含有的key
     */
    public static final String MAP_KEY_FILE_NAME = "fileName";

    /**
     * zip压缩文件
     */
    public static final String ZIP_FILE = ".zip";

    /**
     * 解析文件信息
     */
    public static final String ZIP_FILE_POINT = "zip";

    /**
     * rar压缩文件
     */
    public static final String RAR_FILE = ".rar";

    /**
     * pdf文件
     */
    public static final String PDF_FILE = "pdf";

    /**
     * 电子发票代码长度
     */
    public static final int INVOICE_CODE_LENGTH = 12;

    /**
     * 电子发票代码开头
     */
    public static final String INVOICE_CODE_START_WITH = "0";

    /**
     * 电子发票代码结尾
     */
    public static final String INVOICE_CODE_END_WITH_ELEVEN = "11";

    /**
     * 电子发票代码--通行费发票代码结尾
     */
    public static final String INVOICE_CODE_END_WITH_TWELVE = "12";

    /**
     * 初始化发票代码类型
     */
    public static final String INVOICE_CODE_INIT_TYPE = "0";

    /**
     * 日期格式化格式1
     */
    public static final String INVOICE_KPRQ_FORMAT = "yyyy-MM-dd";

    /**
     * 日期格式化格式2
     */
    public static final String INVOICE_KPRQ_FORMAT_ZH = "yyyy年MM月dd日";

    /**
     * 日期格式化格式3
     */
    public static final String FILE_DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * 截取文件名称
     */
    public static final String SUBSTR_REGEX_FOR_FILE = "\\";

}
