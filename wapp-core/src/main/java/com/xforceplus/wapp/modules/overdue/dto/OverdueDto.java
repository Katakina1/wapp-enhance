package com.xforceplus.wapp.modules.overdue.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.overdue.valid.OverdueCreateValidGroup;
import com.xforceplus.wapp.modules.overdue.valid.OverdueUpdateValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@ApiModel("超期配置")
public class OverdueDto {
    @ApiModelProperty("唯一id")
    @ExcelIgnore
    @NotNull(message = "ID不能为空", groups = OverdueUpdateValidGroup.class)
    private Long id;

    @ExcelIgnore
    @ApiModelProperty("超期配置类型 1.索赔、2.协议、3.EPD")
    @NotNull(message = "超期配置类型不能为空", groups = {OverdueUpdateValidGroup.class, OverdueCreateValidGroup.class})
    @Range.List(value = {@Range(min = 1, max = 3, message = "超期配置类型不正确", groups = {OverdueUpdateValidGroup.class, OverdueCreateValidGroup.class})})
    private Integer type;

    @ApiModelProperty("供应商名称")
    @ExcelProperty(value = "供应商名称")
    @NotEmpty(message = "供应商名称不能为空", groups = OverdueCreateValidGroup.class)
    private String sellerName;

    @ApiModelProperty("供应商税号")
    @ExcelProperty(value = "供应商税号")
    @NotEmpty(message = "供应商税号不能为空", groups = OverdueCreateValidGroup.class)
    private String sellerTaxNo;

    @ApiModelProperty("供应商编号")
    @ExcelProperty(value = "供应商编号")
    @NotEmpty(message = "供应商编号不能为空", groups = OverdueCreateValidGroup.class)
    private String sellerNo;

    @ApiModelProperty("超期时间（天）")
    @ExcelProperty(value = "超期时间（天）")
    @NotNull(message = "超期时间（天）不能为空", groups = {OverdueUpdateValidGroup.class, OverdueCreateValidGroup.class})
    @Min(message = "超期时间不能小于0", value = 0, groups = {OverdueUpdateValidGroup.class, OverdueCreateValidGroup.class})
    private Integer overdueDay;
}
