package com.xforceplus.wapp.modules.deduct.model;

import java.util.List;

/**
 * 类描述： 蓝票匹配结果
 *
 * @ClassName InvoiceMatchResult
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 15:10
 */
public class InvoiceMatchResult {


    /**
     * 发票主信息
     */
    class InvoiceMainInfo{
        private String invoiceNo;
        private String invoiceCode;
        private List<InvoiceItemInfo> invoiceItemInfoList;
    }
    class InvoiceItemInfo{

    }

}
