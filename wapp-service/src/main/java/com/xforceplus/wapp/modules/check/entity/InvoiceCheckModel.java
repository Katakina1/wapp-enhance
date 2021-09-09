package com.xforceplus.wapp.modules.check.entity;

import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bobby
 * @date 2018/4/19
 * 查验
 */
@Setter
@Getter
@ToString
public final class InvoiceCheckModel {


    private Integer id;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 处理时间
     */
    private String handleDate;

    /**
     * 处理结果
     */
    private String handleCode;

    /**
     * 处理过程
     */
    private String handleCourse;

    /**
     * 描述
     */
    private String checkMassege;

    /**
     * 查验账号
     */
    private String checkUser;

    /**
     * 开票日期
     */
    private String invoiceDate;

    /**
     * 购方名称
     */
    private String buyerName;

    /**
     * 价税合计
     */
    private Double totalAmount;

    /**
     * 发票金额
     */
    private Double invoiceAmount;

    /**
     * 税额
     */
    private Double taxAmount;

    /**
     * 类型
     */
    private String invoiceType;

    /**
     * 校验码
     */
    private String checkCode;


    /**
     * detailId
     */
    private Integer detailId;

    /**
     * 价税金额大写
     */
    private String stringTotalAmount;

    /**
     * 查验响应实体
     */
    private ResponseInvoice responseInvoice;

    private String xfName;

}
