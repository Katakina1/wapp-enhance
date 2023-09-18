package com.xforceplus.wapp.modules.entryaccount.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class CustomsSummonsExportDto {

    /**
     * 主键id
     */
    @ExcelProperty("序号")
    @ColumnWidth(15)
    private Long id;
    /**
     * 公司代码
     */
    @ExcelProperty("公司代码")
    private String companyCode;

    /**
     * jv
     */
    @ExcelProperty("JV")
    private String jvcode;
    /**
     * 发票号码(海关缴款书号)
     */
    @ExcelProperty("发票号码")
    private String invoiceNo;
    /**
     * 供应商号
     */
    @ExcelProperty("供应商号")
    private String venderid;
    /**
     * 供应商名称
     */
    @ExcelProperty("供应商名称")
    private String vendername;
    /**
     * 税额
     */
    @ExcelProperty("税额")
    private BigDecimal taxAmount;
    /**
     * 税码由沃尔玛侧提供
     */
    @ExcelProperty("TaxCode")
    private String taxCode;
    /**
     * 税率
     */
    @ExcelProperty("税率")
    private BigDecimal taxRate;
    /**
     * 不含税金额(税价合计)
     */
    @ExcelProperty("税价合计")
    private BigDecimal totalAmount;
    /**
     * 凭证号(Voucher)
     */
    @ExcelProperty("Voucher#")
    private String certificateNo;
    /**
     * 开票日期
     */
    @ExcelProperty("开票日期")
    private String invoiceDate;
    /**
     * 业务类型
     */
    @ExcelProperty("业务类型")
    private String businessType;
    /**
     * 组别
     */
    @ExcelProperty("组别")
    private String groupCode;
    /**
     * 费用类客科目
     */
    @ExcelProperty("费用类客科目")
    private String costSubject;
    /**
     * 不含税金额(成本金额)
     */
    @ExcelProperty("成本金额")
    private BigDecimal invoiceAmount;
    /**
     * 可抵扣固定资产进项税金(税额)
     */
    @ExcelProperty("可抵扣固定资产进项税金")
    private BigDecimal incomeTaxAmount;
    /**
     * 扫描人
     */
    @ExcelProperty("扫描人")
    private String scanUser;
    /**
     * 购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-
     */
    @ExcelProperty("购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-")
    private String isImmovables;
    /**
     * 大类(指商品类,资产类,费用类)
     */
    @ExcelProperty("大类(指商品类,资产类,费用类)")
    private String largeCategory;
    /**
     * 购方名称
     */
    @ExcelProperty("购方名称")
    private String gfName;
    /**
     * 购方税号
     */
    @ExcelProperty("购方税号")
    private String gfTaxNo;
    /**
     * 发票类型
     */
    @ExcelProperty("发票类型")
    private String invoiceType;
    /**
     * 否
     */
    @ExcelProperty("GL发票")
    private String glInvoice;
    /**
     * 税款所属期
     */
    @ExcelProperty("税款所属期")
    private String taxPeriod;
    /**
     * 入账状态未入账、企业所得税税前扣除、企业所得税不扣除、撤销入账
     */
    @ExcelProperty("入账状态")
    private String accountStatus;
    /**
     * 更新时间
     */
    @ExcelProperty("更新时间")
    private Date updateTime;
    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private Date createTime;

}
