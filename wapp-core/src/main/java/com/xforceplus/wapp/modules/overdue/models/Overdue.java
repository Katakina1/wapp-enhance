package com.xforceplus.wapp.modules.overdue.models;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
@ApiModel("超期配置实体类")
public class Overdue {
    @ApiModelProperty("唯一id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("供应商名称")
    private String sellerName;

    @ApiModelProperty("供应商税号")
    private String sellerTaxNo;

    @ApiModelProperty("超期时间（天）")
    private Integer overdueDay;
    
    @ApiModelProperty("更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long updateTime;
}
