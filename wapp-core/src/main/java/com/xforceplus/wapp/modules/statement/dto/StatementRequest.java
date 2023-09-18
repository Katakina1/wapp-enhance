package com.xforceplus.wapp.modules.statement.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Valid
public class StatementRequest {
	@ApiModelProperty("页数")
	Long current;
	@ApiModelProperty("条数")
	Long size;
	@ApiModelProperty("查询类型 1.索赔、2.协议、3.EPD")
	@NotNull(message = "必须要选择一个")
	Integer type;
	@ApiModelProperty("结算单状态")
	Integer settlementStatus;
	@ApiModelProperty("结算单号")
	String settlementNo;
	@ApiModelProperty("购方编号，供应商编号")
	String purchaserNo;
	@ApiModelProperty("发票类型（枚举值与结果实体枚举值相同）")
	String invoiceType;
	@ApiModelProperty("单据号（索赔、协议、EPD）")
	String businessNo;
	@ApiModelProperty("税率")
	String taxRate;
}
