package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 对接RMS非商入账 生成的传票清单
 * @Author: ChenHang
 * @Date: 2023/7/14 16:39
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_dx_summons_rms")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TDxSummonsRMSEntity  extends BaseEntity{

    /**
     * 主键id
     */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 发票号码
     */
    @ApiModelProperty("发票号码")
    @TableField("invoice_no")
    private String invoiceNo;
    /**
     * 发票代码
     */
    @ApiModelProperty("发票代码")
    @TableField("invoice_code")
    private String invoiceCode;
    /**
     * uuid 发票代码 + 发票号码
     */
    @ApiModelProperty("uuid 发票代码 + 发票号码")
    @TableField("uuid")
    private String uuid;
    /**
     * 发票类型
     */
    @ApiModelProperty("发票类型")
    @TableField("invoice_type")
    private String invoiceType;
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
     * 扫描人
     */
    @ApiModelProperty("扫描人")
    @TableField("scan_user")
    private String scanUser;
    /**
     * 扫描日期
     */
    @ApiModelProperty("扫描日期")
    @TableField("scan_time")
    private Date scanTime;
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
     * Stroe#
     */
    @ApiModelProperty("Stroe#")
    @TableField("stroe")
    private String stroe;
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
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;
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
     * epsNo
     */
    @ApiModelProperty("epsNo")
    @TableField("eps_no")
    private String epsNo;
    /**
     * GL发票
     */
    @ApiModelProperty("GL发票")
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
    /**
     * 含税金额(税价合计)
     */
    @ApiModelProperty("含税金额(税价合计)")
    @TableField("total_amount")
    private BigDecimal totalAmount;
    /**
     * 凭证入账日期
     */
    @ApiModelProperty("凭证入账日期")
    @TableField("certificate_time")
    private Date certificateTime;
    /**
     * 购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-
     */
    @ApiModelProperty("购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-")
    @TableField("is_immovables")
    private String isImmovables;
    /**
     * 不含税金额(成本金额)
     */
    @ApiModelProperty("不含税金额(成本金额)")
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    @TableField(exist = false)
    private String taxPeriod;

}
