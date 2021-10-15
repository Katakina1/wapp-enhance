package com.xforceplus.wapp.modules.blackwhitename.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SpecialCompanyQueryRequest implements Serializable {

    @ApiModelProperty(value = "税号")
    @NotBlank(message = "税号不能为空")
    private String taxNo;

    @ApiModelProperty(value = "抬头名称")
    private String taxName;

    @ApiModelProperty(value = "开户行")
    private String bank;

    @ApiModelProperty(value = "银行账号")
    private String account;

    @ApiModelProperty(value = "限额")
    @Digits(integer = 6, fraction = 2, message = "限额额度最大为100000 保留两位小数")
    private BigDecimal quota;

}
