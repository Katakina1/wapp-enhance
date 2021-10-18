package com.xforceplus.wapp.modules.exceptionreport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 19:42
 **/
@ApiModel
@Setter
@Getter
public class ReMatchRequest {
    /**
     * 例外报告ID集合
     */
    @ApiModelProperty("例外报告ID集合")
    private List<Long> ids;
}
