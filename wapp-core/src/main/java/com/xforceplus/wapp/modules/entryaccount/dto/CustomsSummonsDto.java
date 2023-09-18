package com.xforceplus.wapp.modules.entryaccount.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.customs.AccountStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 传票清单
 * @Author: ChenHang
 * @Date: 2023/7/4 17:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomsSummonsDto{

    /**
     * 主键id
     */
    @ApiModelProperty("主键")
    @ExcelProperty(value = "序号", index = 0)
    private Long id;
    /**
     * 公司代码
     */
    @ApiModelProperty("公司代码")
    @ExcelProperty(value = "公司代码", index = 1)
    private String companyCode;

    /**
     * jv
     */
    @ApiModelProperty("jv")
    @ExcelProperty(value = "JV", index = 2)
    private String jvcode;
    /**
     * 海关缴款书号码
     */
    @ApiModelProperty("海关缴款书号码")
    @ExcelProperty(value = "海关缴款书号码", index = 3)
    private String invoiceNo;
    /**
     * 供应商号
     */
    @ApiModelProperty("供应商号")
    @ExcelProperty(value = "供应商号", index = 4)
    private String venderid;
    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    @ExcelProperty(value = "供应商名称", index = 5)
    private String vendername;
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
    @ExcelProperty(value = "税额", index = 6)
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
    @ApiModelProperty("税码由沃尔玛侧提供")
    @ExcelProperty(value = "TaxCode", index = 7)
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
    @ExcelProperty(value = "税率", index = 8)
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
    @ExcelProperty(value = "税价合计", index = 9)
    private String totalAmountStr;
    public String getTotalAmountStr() {
        if(ObjectUtils.isEmpty(totalAmount)) {
            return "";
        } else {
            return totalAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    /**
     * 凭证号(Voucher#)
     */
    @ApiModelProperty("凭证号(Voucher)")
    @ExcelProperty(value = "Voucher#", index = 10)
    private String certificateNo;
    /**
     * 开票日期
     */
    @ExcelIgnore
    @ApiModelProperty("开票日期")
    private String invoiceDate;

    @ExcelProperty(value = "填发日期", index = 11)
    private String paperDrewDate;

    public String getPaperDrewDate() {
       // 如果开票日期不为空则只保留前年月日及yyyy-MM-dd格式
        if (StringUtils.isNotEmpty(invoiceDate) && invoiceDate.length() > 10) {
            return invoiceDate.substring(0, 10);
        }
       return invoiceDate;
    }
    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    @ExcelProperty(value = "业务类型", index = 12)
    private String businessType;
    /**
     * 组别
     */
    @ApiModelProperty("组别")
    @ExcelProperty(value = "组别", index = 13)
    private String groupCode;
    /**
     * 费用类科目
     */
    @ApiModelProperty("费用类科目")
    @ExcelProperty(value = "费用类科目", index = 14)
    private String costSubject;
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

    @ExcelProperty(value = "成本金额", index = 15)
    private String invoiceAmountStr;
    public String getInvoiceAmountStr() {
        if(ObjectUtils.isEmpty(invoiceAmount)) {
            return "";
        } else {
            return invoiceAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    /**
     * 可抵扣固定资产进项税金(税额)
     */
    @ApiModelProperty("可抵扣固定资产进项税金(税额)")
    @ExcelIgnore
    private BigDecimal incomeTaxAmount;
    public String getIncomeTaxAmount() {
        if(ObjectUtils.isEmpty(incomeTaxAmount)) {
            return "";
        } else {
            return incomeTaxAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    @ExcelProperty(value = "可抵扣固定资产进项税金", index = 16)
    private String incomeTaxAmountStr;
    public String getIncomeTaxAmountStr() {
        if(ObjectUtils.isEmpty(incomeTaxAmount)) {
            return "";
        } else {
            return incomeTaxAmount.setScale(2, BigDecimal.ROUND_UP).toString();
        }
    }
    /**
     * 扫描人
     */
    @ApiModelProperty("扫描人")
    @ExcelProperty(value = "扫描人", index = 17)
    private String scanUser;
    /**
     * 购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-
     */
    @ApiModelProperty("购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-")
    @ExcelProperty(value = "购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-", index = 18)
    private String isImmovables;
    /**
     * 大类(指商品类,资产类,费用类)
     */
    @ApiModelProperty("大类(指商品类,资产类,费用类)")
    @ExcelProperty(value = "大类(指商品类,资产类,费用类)", index = 19)
    private String largeCategory;
    /**
     * 购方名称
     */
    @ApiModelProperty("购方名称")
    @ExcelProperty(value = "购方名称", index = 20)
    private String gfName;
    /**
     * 购方税号
     */
    @ApiModelProperty("购方税号")
    @ExcelProperty(value = "购方税号", index = 21)
    private String gfTaxNo;
    /**
     * 税款所属期
     */
    @ApiModelProperty(value = "税款所属期")
    @ExcelProperty(value = "税款所属期", index = 22)
    private String taxPeriod;

    /**
     * 调整税款所属期时间格式为 yyyy-MM
     * @return
     */
    public String getTaxPeriod() {
        if (StringUtils.isNotEmpty(taxPeriod) && taxPeriod.length() == 6){
            return DateUtils.toFormatDateMM(taxPeriod);
        }
        return taxPeriod;
    }

    /**
     * 发票类型
     */
    @ApiModelProperty("发票类型")
    @ExcelProperty(value = "发票类型", index = 23)
    private String invoiceType;
    /**
     * GL发票
     */
    @ApiModelProperty(value = "GL发票(否)")
    @ExcelProperty(value = "GL发票", index = 24)
    private String glInvoice;
    /**
     * 入账状态未入账、企业所得税税前扣除、企业所得税不扣除、撤销入账
     */
    @ApiModelProperty(value = "入账状态")
    @ExcelIgnore
    private String accountStatus;
    @ExcelProperty(value = "国税入账标识", index = 25)
    private String accountStatusStr;
    // 00, "未入账",01, "入账中",02,"入账企业所得税税前扣除",03,"入账企业所得税不扣除",04,"入账失败",05,"入账撤销中",06,"入账撤销",07,"入账撤销失败"
    public String getAccountStatusStr() {
        if (StringUtils.isNotEmpty(accountStatus)) {
            return AccountStatusEnum.getValue(accountStatus);
        }
        return accountStatus;
    }

    /**
     * 凭证入账时间
     */
    @ApiModelProperty("凭证入账时间")
    @ExcelProperty(value = "凭证入账日期", index = 26)
    private String voucherAccountTime;
    public String getVoucherAccountTime() {
        // 如果开票日期不为空则只保留前年月日及yyyy-MM-dd格式
        if (StringUtils.isNotEmpty(voucherAccountTime) && voucherAccountTime.length() > 10) {
            return voucherAccountTime.substring(0, 10);
        }
        return voucherAccountTime;
    }
    /**
     * 缴款书合同号(PO号)
     */
    @ApiModelProperty("缴款书合同号(PO号)")
    @ExcelProperty(value = "PO单号", index = 27)
    private String contractNo;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @ExcelIgnore
    private Date updateTime;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @ExcelIgnore
    private Date createTime;

}
