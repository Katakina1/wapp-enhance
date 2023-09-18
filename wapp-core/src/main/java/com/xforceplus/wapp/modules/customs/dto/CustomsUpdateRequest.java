package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;


@Data
public class CustomsUpdateRequest {

    @ApiModelProperty("操作类型1修改税款金额2修改所属期3修改凭证号4手工录入")
    private String type;

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty("有效税款金额")
    private BigDecimal effectiveTaxAmount;

    @ApiModelProperty("所属期")
    private String taxPeriod;

    @ApiModelProperty("凭证号")
    private String voucherNo;

    @ApiModelProperty("缴款单位税号")
    private String companyTaxNo;

    @ApiModelProperty("缴款单位名称")
    private String companyName;

    @ApiModelProperty("缴款书号码")
    private String customsNo;

    @ApiModelProperty("填发日期")
    private String paperDrawDate;

    @ApiModelProperty("凭证入账日期")
    private Date voucherAccountTime;

    @ApiModelProperty("国税入账日期")
    private Date accountTime;
}
