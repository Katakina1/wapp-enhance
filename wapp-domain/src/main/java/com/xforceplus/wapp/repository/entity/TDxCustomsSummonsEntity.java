package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 传票清单
 * @Author: ChenHang
 * @Date: 2023/7/4 17:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_dx_customs_summons")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TDxCustomsSummonsEntity extends BaseEntity{

    /**
     * 主键id
     */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 公司代码
     */
    @ApiModelProperty("公司代码")
    @TableField("company_code")
    private String companyCode;

    /**
     * jv
     */
    @ApiModelProperty("jv")
    @TableField("jvcode")
    private String jvcode;
    /**
     * 发票号码(海关缴款书号)
     */
    @ApiModelProperty("发票号码(海关缴款书号)")
    @TableField("invoice_no")
    private String invoiceNo;
    /**
     * 供应商号
     */
    @ApiModelProperty("供应商号")
    @TableField("venderid")
    private String venderid;
    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    @TableField("vendername")
    private String vendername;
    /**
     * 税额
     */
    @ApiModelProperty("税额")
    @TableField("tax_amount")
    private BigDecimal taxAmount;
    /**
     * 税码由沃尔玛侧提供
     */
    @ApiModelProperty("税码由沃尔玛侧提供")
    @TableField("tax_code")
    private String taxCode;
    /**
     * 税率
     */
    @ApiModelProperty("税率")
    @TableField("tax_rate")
    private BigDecimal taxRate;
    /**
     * 含税金额(税价合计)
     */
    @ApiModelProperty("含税金额(税价合计)")
    @TableField("total_amount")
    private BigDecimal totalAmount;
    /**
     * 凭证号(Voucher)
     */
    @ApiModelProperty("凭证号(Voucher)")
    @TableField("certificate_no")
    private String certificateNo;
    /**
     * 开票日期
     */
    @ApiModelProperty("开票日期")
    @TableField("invoice_date")
    private String invoiceDate;
    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    @TableField("business_type")
    private String businessType;
    /**
     * 组别
     */
    @ApiModelProperty("组别")
    @TableField("group_code")
    private String groupCode;
    /**
     * 费用类客科目
     */
    @ApiModelProperty("费用类客科目")
    @TableField("cost_subject")
    private String costSubject;
    /**
     * 不含税金额(成本金额)
     */
    @ApiModelProperty("不含税金额(成本金额)")
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;
    /**
     * 可抵扣固定资产进项税金(税额)
     */
    @ApiModelProperty("可抵扣固定资产进项税金(税额)")
    @TableField("income_tax_amount")
    private BigDecimal incomeTaxAmount;
    /**
     * 扫描人
     */
    @ApiModelProperty("扫描人")
    @TableField("scan_user")
    private String scanUser;
    /**
     * 大类(指商品类,资产类,费用类)
     */
    @ApiModelProperty("大类(指商品类,资产类,费用类)")
    @TableField("large_category")
    private String largeCategory;
    /**
     * 购方名称
     */
    @ApiModelProperty("购方名称")
    @TableField("gf_name")
    private String gfName;
    /**
     * 购方税号
     */
    @ApiModelProperty("购方税号")
    @TableField("gf_tax_no")
    private String gfTaxNo;
    /**
     * 发票类型
     */
    @ApiModelProperty("发票类型")
    @TableField("invoice_type")
    private String invoiceType;
    /**
     * 否
     */
    @ApiModelProperty("否")
    @TableField("gl_invoice")
    private String glInvoice;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "update_time")
    private Date updateTime;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty("认证状态 2-未勾选 4-已勾选 9-撤销勾选成功(与主表的勾选状态一致)")
    @TableField(value = "is_check")
    private String isCheck;
    /**
     * 税款所属期
     */
    @TableField(exist = false)
    private String taxPeriod;
    /**
     * 入账状态未入账、企业所得税税前扣除、企业所得税不扣除、撤销入账
     */
    @TableField(exist = false)
    private String accountStatus;
    /**
     * 凭证入账时间
     */
    @TableField(exist = false)
    @ApiModelProperty("凭证入账时间")
    private String voucherAccountTime;
    /**
     * 缴款书合同号(PO号)
     */
    @TableField(exist = false)
    @ApiModelProperty("缴款书合同号(PO号)")
    private String contractNo;

}
