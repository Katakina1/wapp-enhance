package com.xforceplus.wapp.modules.overdue.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("超期配置信息")
public class Overdue {
    @ApiModelProperty("唯一id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("超期配置类型 1.索赔、2.协议、3.EPD")
    private Integer type;

    @ApiModelProperty("供应商名称")
    private String sellerName;

    @ApiModelProperty("供应商编号")
    private String sellerNo;

    @ApiModelProperty("供应商税号")
    private String sellerTaxNo;

    @ApiModelProperty("超期时间（天）")
    private Integer overdueDay;
    
    @ApiModelProperty("更新时间")
    private Long updateTime;
}
