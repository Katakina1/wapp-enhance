package com.xforceplus.wapp.modules.entryaccount.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.common.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: ChenHang
 * @Date: 2023/8/9 17:27
 */
@Data
public class TDxSummonsRMSDto implements Serializable {

    @ExcelIgnore
    private Long id;

    /**
     * 公司代码
     */
    @ExcelProperty(value = "公司代码", index = 0)
    @ApiModelProperty("公司代码")
    private String companyCode;
    /**
     * jv
     */
    @ExcelProperty(value = "Jv", index = 1)
    @ApiModelProperty("jv")
    private String jvcode;
    /**
     * 发票代码
     */
    @ExcelProperty(value = "发票代码", index = 2)
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    /**
     * 发票号码
     */
    @ExcelProperty(value = "发票号码", index = 3)
    @ApiModelProperty("发票号码")
    private String invoiceNo;

    /**
     * uuid 发票代码 + 发票号码
     */
    @ApiModelProperty("uuid 发票代码 + 发票号码")
    @ExcelIgnore
    private String uuid;
    /**
     * 扫描日期
     */
    @ApiModelProperty("扫描日期")
    @ExcelIgnore
    private Date scanTime;
    /**
     * 所属期
     */
    @ExcelProperty(value = "税款所属期", index = 4)
    private String taxPeriod;
    /**
     * 供应商号
     */
    @ExcelProperty(value = "供应商号", index = 5)
    @ApiModelProperty("供应商号")
    private String venderid;
    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称", index = 6)
    @ApiModelProperty("供应商名称")
    private String vendername;
    /**
     * Stroe#
     */
    @ExcelProperty(value = "Stroe#", index = 7)
    @ApiModelProperty("Stroe#")
    private String stroe;
    /**
     * 税额
     */
    @ApiModelProperty("税额")
    @ExcelIgnore
    private BigDecimal taxAmount;
    public String getTaxAmount() {
        if(ObjectUtils.isEmpty(taxAmount)) {
            return "";
        } else {
            return taxAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    @ExcelProperty(value = "税额", index = 8)
    private String taxAmountStr;
    public String getTaxAmountStr() {
        if(ObjectUtils.isEmpty(taxAmount)) {
            return "";
        } else {
            return taxAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    /**
     * 税码由沃尔玛侧提供
     */
    @ExcelProperty(value = "税码", index = 9)
    @ApiModelProperty("税码由沃尔玛侧提供")
    private String taxCode;
    /**
     * 税率
     */
    @ApiModelProperty("税率")
    @ExcelIgnore
    private BigDecimal taxRate;
    public String getTaxRate() {
        if(ObjectUtils.isEmpty(taxRate)) {
            return "";
        } else {
            return taxRate.setScale(0, BigDecimal.ROUND_UP).toString();
        }
    }
    @ExcelProperty(value = "税率", index = 10)
    private String taxRateStr;
    public String getTaxRateStr() {
        if(ObjectUtils.isEmpty(taxRate)) {
            return "";
        } else {
            return taxRate.setScale(0, BigDecimal.ROUND_UP).toString();
        }
    }
    /**
     * 含税金额(税价合计)
     */
    @ApiModelProperty("含税金额(税价合计)")
    @ExcelIgnore
    private BigDecimal totalAmount;
    public String getTotalAmount() {
        if(ObjectUtils.isEmpty(totalAmount)) {
            return "";
        } else {
            return totalAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    @ExcelProperty(value = "价税合计", index = 11)
    private String totalAmountStr;
    public String getTotalAmountStr() {
        if(ObjectUtils.isEmpty(totalAmount)) {
            return "";
        } else {
            return totalAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    /**
     * 凭证号(Voucher)
     */
    @ExcelProperty(value = "凭证号", index = 12)
    @ApiModelProperty("凭证号(Voucher)")
    private String certificateNo;
    /**
     * 凭证入账日期
     */
    @ApiModelProperty("凭证入账日期")
    @ExcelProperty(value = "凭证入账日期", index = 13)
    private Date certificateTime;

    public String getCertificateTime(Date certificateTime) {
        if (ObjectUtils.isEmpty(certificateTime)) {
            return "";
        }
        return DateUtils.format(certificateTime, DateUtils.DATE_TIME_PATTERN);
    }

    /**
     * 开票日期
     */
    @ExcelProperty(value = "开票日期", index = 13)
    @ApiModelProperty("开票日期")
    private String invoiceDate;
    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 14)
    @ApiModelProperty("备注")
    private String remark;
    /**
     * 业务类型
     */
    @ExcelProperty(value = "业务类型", index = 15)
    @ApiModelProperty("业务类型")
    private String businessType;
    /**
     * 扫描人
     */
    @ExcelProperty(value = "扫描人", index = 16)
    @ApiModelProperty("扫描人")
    private String scanUser;
    /**
     * 购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-
     */
    @ApiModelProperty("购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-")
    @ExcelProperty(value = "购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-", index = 17)
    private String isImmovables;
    /**
     * 大类(指商品类,资产类,费用类)
     */
    @ExcelProperty(value = "大类(指商品类,资产类,费用类)", index = 18)
    @ApiModelProperty("大类(指商品类,资产类,费用类)")
    private String largeCategory;
    /**
     * 不含税金额(成本金额)
     */
    @ApiModelProperty("不含税金额(成本金额)")
    @ExcelIgnore
    private BigDecimal invoiceAmount;
    public String getInvoiceAmount() {
        if(ObjectUtils.isEmpty(invoiceAmount)) {
            return "";
        } else {
            return invoiceAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    @ExcelProperty(value = "成本金额", index = 19)
    private String invoiceAmountStr;
    public String getInvoiceAmountStr() {
        if(ObjectUtils.isEmpty(invoiceAmount)) {
            return "";
        } else {
            return invoiceAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    /**
     * 购方名称
     */
    @ExcelProperty(value = "购方名称", index = 20)
    @ApiModelProperty("购方名称")
    private String gfName;
    /**
     * GL发票
     */
    @ExcelProperty(value = "GL发票", index = 21)
    @ApiModelProperty("GL发票")
    private String glInvoice;
    /**
     * 购方税号
     */
    @ExcelIgnore
    @ApiModelProperty("购方税号")
    private String gfTaxNo;
    /**
     * epsNo
     */
    @ExcelIgnore
    @ApiModelProperty("epsNo")
    private String epsNo;
    /**
     * 发票类型
     */
    @ExcelIgnore
    @ApiModelProperty("发票类型")
    private String invoiceType;
    /**
     * 更新时间
     */
    @ExcelIgnore
    @ApiModelProperty("更新时间")
    private Date updateTime;
    /**
     * 创建时间
     */
    @ExcelIgnore
    @ApiModelProperty("创建时间")
    private Date createTime;

}
