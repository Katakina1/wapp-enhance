package com.xforceplus.wapp.modules.taxcode.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("名称")
    private String categoryName;

    @ApiModelProperty("编码")
    private String categoryCode;

    @ApiModelProperty("税编子树")
    private List<TaxCodeTree> children;
}