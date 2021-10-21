package com.xforceplus.wapp.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 默认分页参数
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:57
 **/
@Getter
@Setter
public class PageViewRequest {

    /**
     * 页码默认1
     */
    @ApiModelProperty("页码，默认1，不适用于导出")
    private int page = 1;

    /**
     * 每页默认显示50条
     */
    @ApiModelProperty("每页数量，默认50，不适用于导出")
    private int size = 50;
}
