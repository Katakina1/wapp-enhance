package com.xforceplus.wapp.modules.claim.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 14:52
 **/
@Setter
@Getter
public class ApplyVerdictRequest implements Serializable {
    @ApiModelProperty(value = "索赔单id")
    @NotBlank(message = "索赔单id不能为空")
    private List<Long> billDeductIdList;
    @ApiModelProperty(value = "供应商编号",hidden = true)
    private String sellerNo;
}
