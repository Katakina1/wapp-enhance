package com.xforceplus.wapp.modules.backFill.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-15 20:18
 **/
@Setter
@Getter
public class InvoiceMain {

    /**
     * 发票类型 ce-电普、se-电专
     */
    private String invoiceType;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 开票日期
     */
    private String paperDrewDate;
    /**
     * 购方名称
     */
    private String purchaserName;
    /**
     * 购方税号
     */
    private String purchaserTaxNo;
    /**
     * 购方地址 电话
     */
    private String purchaserAddrTel;
    /**
     * 购房银行名称 账号
     */
    private String purchaserBankInfo;
    /**
     * 销方名称
     */
    private String sellerName;
    /**
     * 销方税号
     */
    private String sellerTaxNo;
    /**
     * 销方地址 电话
     */
    private String sellerAddrTel;
    /**
     * 销方银行名称 账号
     */
    private String sellerBankInfo;
    /**
     * 不含税金额
     */
    private String amountWithoutTax;
    /**
     * 税额
     */
    private String taxAmount;
    /**
     * 含税金额
     */
    private String amountWithTax;
    /**
     * 机器码
     */
    private String machineCode;
    /**
     * 校验码
     */
    private String checkCode;
    /**
     * 开票人
     */
    private String drawerName;
    /**
     * 复合人
     */
    private String checkerName;
    /**
     * 收款人
     */
    private String cashierName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 成品油标志  1-成平油发票
     */
    private String cpyStatus;
    /**
     * 通行费标志 1-可底扣通行费
     */
    private String ctStatus;
    /**
     * 销货清单标志  1-有销货清单
     */
    private String goodsListFlag;

    /**
     * 查验次数 (通道二不支持)
     */
    private String checkNumber;

    /**
     * 查验时间 yyyy-MM-dd HH:mm:ss
     */
    private String checkTime;

    /**
     * 地区代码
     */
    private String dqCode; //地区代码
    /**
     * 地区名称
     */
    private String dqName;//地区名称
    /**
     * 发票ofd文件下载地址
     */
    private String ofdDownloadUrl; //发票ofd文件下载地址
    /**
     * 发票pdf文件下载地址
     */
    private String pdfDownloadUrl; //发票pdf文件下载地址
    /**
     * 发票ofd文件预览地址
     */
    private String ofdPreviewUrl; //发票ofd文件预览地址

    /**
     * 发票图片jpeg文件地址
     */
    private String ofdImageUrl;

    /**
     * "status":" ", //发票状态 1-正常、0-作废
     */
    private String status;

    /**
     * "redFlag":" ", //红冲状态 0-未红冲、1-已被红冲
     */
    private String redFlag;

    public static final String ALREADY_RED = "1";

    public static final String NOT_RED = "0";
}
