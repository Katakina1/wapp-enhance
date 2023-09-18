package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 发票审核页面
 *
 * @TableName t_xf_invoice_audit
 */
@TableName(value = "t_xf_invoice_audit")
@Data
public class InvoiceAudit implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发票UUID（发票代码+发票号码）
     */
    private String invoiceUuid;

    /**
     * 审核状态 0未审核 1审核通过 2审核不通过
     */
    private String auditStatus;

    /**
     * 审核人
     */
    private String auditUser;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 结算单号
     */
    private String settlementNo;

    /**
     * 备注
     */
    private String remark;

    private String auditRemark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 提交审核人
     */
    private String createUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 删除标记
     */
    private String deleteFlag;

    /**
     * 是否自动审核 1-是
     */
    private Integer autoFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}