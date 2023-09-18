package com.xforceplus.wapp.modules.statement.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红字结算单表-协议
 * 导出实体
 */
@Data
public class AgreementExportListDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@ExcelProperty("结算单号")
    private String settlementNo;

    @ExcelProperty("协议号")
    private String businessNo;

    /**
     * 红字信息表编号
     */
    @ExcelProperty("红字信息表编号")
    private String redNotificationNo;

    @ExcelProperty("供应商编码")
    private String sellerNo;

    @ExcelProperty("购方编码")
    private String purchaserNo;

    @ExcelProperty("购方名称")
    private String purchaserName;

    @ExcelProperty("不含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithoutTax;

    @ExcelProperty("税额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxAmount;

    @ExcelProperty("含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithTax;

    @ExcelIgnore
    private Integer settlementStatus;

	@ExcelProperty("结算单状态")
	private String settlementStatusStr;

}