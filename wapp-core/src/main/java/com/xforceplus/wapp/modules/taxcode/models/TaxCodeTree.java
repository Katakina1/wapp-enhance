package com.xforceplus.wapp.modules.taxcode.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("税编信息树")
public class TaxCodeTree {
    @ApiModelProperty("大类名称")
    private String largeCategoryName;

    @ApiModelProperty("大类编码")
    private String largeCategoryCode;

    @ApiModelProperty("中类名称")
    private String medianCategoryName;

    @ApiModelProperty("中类编码")
    private String medianCategoryCode;

    @ApiModelProperty("税编子树")
    private List<TaxCodeTree> children;
}