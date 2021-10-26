package com.xforceplus.wapp.modules.noneBusiness.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-24 13:57
 **/
@Setter
@Getter
public class SummerySubmitResponse implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("提交总数")
    private Integer submitCount;
    @ApiModelProperty("可提交数量")
    private Integer waitSubmit;

    @ApiModelProperty("已经提交数量")
    private Integer submitedCount;
}
