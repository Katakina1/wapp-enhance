package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;


@Data
public class CustomsSaveRequest {

    @ApiModelProperty("缴款书号码")
    @NotBlank(message = "报错！缴款书号码是必填项")
    @Length(max = 22,min = 22,message = "报错！缴款书号码的位数不是22位")
    private String customsNo;

    @ApiModelProperty("缴款单位税号")
    @NotBlank(message = "报错！缴款单位税号是必填项")
    @Pattern(regexp="^[A-Za-z0-9]+$",message="报错！缴款单位必须是数字和字母")
    private String companyTaxNo;

    @ApiModelProperty("缴款单位名称")
    @NotBlank(message = "报错！缴款单位名称是必填项")
    private String companyName;
    //yyyyMMdd
    @ApiModelProperty("填发日期")
    @NotBlank(message = "报错！填发日期是必填项")
    @Pattern(regexp = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))$",message = "报错！填发日期必须是日期，如：20230423")
    private String paperDrewDate;

    @ApiModelProperty("税款金额")
    @Pattern(regexp="^\\d+(\\.\\d+)?$",message="报错！税款金额必须超过0")
    @NotBlank(message = "报错！税款金额是必填项")
    private String effectiveTaxAmount;

}
