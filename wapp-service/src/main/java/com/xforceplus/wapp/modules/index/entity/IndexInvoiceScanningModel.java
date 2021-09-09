package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-发票扫描
 */
@Getter
@Setter
@ToString
public final class IndexInvoiceScanningModel {

    private Long id;

    /**
     * 发票类型（01-增值税专用发票
     * 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票）
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
     * 扫描流水号
     */
    private String invoiceSerialNo;

    /**
     * 购方税号
     */
    private String gfTaxNo;

    /**
     * 购方名称
     */
    private String gfName;

    /**
     * 销方税号
     */
    private String xfTaxNo;

    /**
     * 销方名称
     */
    private String xfName;

    /**
     * 金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 价税合计
     */
    private BigDecimal totalAmount;

    /**
     * 开票时间
     */
    private String invoiceDate;

    /**
     * 扫描账号
     */
    private String userAccount;

    /**
     * 扫描人
     */
    private String userName;

    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收，5-pdf上传）
     */
    private String qsType;

    /**
     * 签收结果(0-签收失败 1-签收成功）
     */
    private String qsStatus;

    /**
     * 是否有效（0-有效 1-无效）
     */
    private String valid;

    /**
     * 发票唯一标识 组成：发票代码+发票号码
     */
    private String uuid;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 修改时间
     */
    private String updateDate;

    /**
     * 签收时间
     */
    private String qsDate;

    /**
     * 扫描图片唯一识别码
     */
    private String scanId;

    /**
     * 扫描备注
     */
    private String notes;

    /**
     * 校验码
     */
    private String checkCode;
}
