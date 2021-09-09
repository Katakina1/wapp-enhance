package com.xforceplus.wapp.modules.job.service;

import java.util.Map;

/**
 * Created by Daily.zhang on 2018/04/20.
 */
public interface OcrSerivce {
    /**
     *
     * @param userid   用户Id     建议手机号，用于区分用户
     * @param uuid  图片标识     可为随机字符串
     * @param picBase64         Base64图片信息
     * @return Map<String, String>
     * returnCode:   返回代码
     * returnMessage:返回描述
     * buyerTaxNo:   购买方税号
     * invoiceType:  发票类型
     * invoiceAmount:发票金额
     * invoiceNo:   发票号码
     * invoiceDate: 开票日期
     * invoiceCode: 发票代码
     * verifyCode:  校验码
     */
    Map<String, String> discernInvoice(String userid, String uuid, String picBase64);
}
