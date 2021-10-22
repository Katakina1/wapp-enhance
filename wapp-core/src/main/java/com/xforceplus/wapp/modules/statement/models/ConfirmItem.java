package com.xforceplus.wapp.modules.statement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("结算单确认明细列表")
public class ConfirmItem extends BaseConfirm {
    @ApiModelProperty("明细ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @ApiModelProperty("明细名称")
    private String itemName;
    @ApiModelProperty("单价")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal unitPrice;
    @ApiModelProperty("数量")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantity;
    @ApiModelProperty("不含税金额(待匹配金额)")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithoutTax;
    @ApiModelProperty("税率")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxRate;
}
