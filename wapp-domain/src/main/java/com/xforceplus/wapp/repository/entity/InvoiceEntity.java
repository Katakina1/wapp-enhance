package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * t_dx_invoice
 * @author 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_dx_invoice")
public class InvoiceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 发票类型（01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票）
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
    private Date invoiceDate;

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
     * 是否有效（1-有效 0-无效）
     */
    private String valid;

    /**
     * 发票唯一标识 组成：发票代码+发票号码
     */
    private String uuid;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    /**
     * 签收时间
     */
    private Date qsDate;

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

    /**
     * 供应商名称
     */
    private String vendername;

    /**
     * 装订号
     */
    private String bbindingno;

    /**
     * 装箱号
     */
    private String packingno;

    /**
     * 装订日期
     */
    private Date bbindingDate;

    /**
     * 是否录入装订号（0-未装订，1-已装订）
     */
    private String bindyesorno;

    /**
     * 是否录入装箱号（0-未录入，1-已录入）
     */
    private String packyesorno;

    /**
     * 生成退单号日期
     */
    private Date rebateDate;

    /**
     * 是否生成退单号（0-未生成，1-已生成）
     */
    private String rebateyesorno;

    /**
     * 是否录入邮包号（0-未录入，1-已录入）
     */
    private String expressnoyesorno;

    /**
     * 供应商号
     */
    private String venderid;

    /**
     * 退单号
     */
    private String rebateno;

    /**
     * 邮包号
     */
    private String rebateExpressno;

    /**
     * 退票理由
     */
    private String refundNotes;

    /**
     * 是否整组退票（0-未整组退，1-已整组退）
     */
    private String refundyesorno;

    /**
     * 打印发票代码
     */
    private String dyInvoiceCode;

    /**
     * 打印发票号码
     */
    private String dyInvoiceNo;

    /**
     * 文件类型
     */
    private String fileType;

    private String jvCode;

    private String companyCode;

    private String isdel;

    /**
     * 删除时间
     */
    private Date delDate;

    private String flowType;

    /**
     * 退票原因
     */
    private String refundReason;

    /**
     * 开票时间
     */
    private Date makeDate;

    private String venderidEdit;

    /**
     * 是否从页面导入  0--是  1 --否
     */
    private String isImport;

    /**
     * 是否有印章
     */
    private String isExistStamper;

    /**
     * 无印章描述
     */
    private String noExistStamperNotes;

    /**
     * 费用封面组号
     */
    private String costNo;

    /**
     * 申请单号
     */
    private String epsNo;

    private Date mailDate;

    private String mailCompany;

    private String refundCode;

    /**
     * 属于
     */
    private String belongsTo;

    private String aribaConfirmStatus;

    private Date aribaConfirmDate;

    private static final long serialVersionUID = 1L;
}