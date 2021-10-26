package com.xforceplus.wapp.modules.noneBusiness.dto;

import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-24 13:57
 **/
@Setter
@Getter
public class ValidSubmitResponse implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("提交总数")
    private Integer submitCount;
    @ApiModelProperty("可以提交数量")
    private Integer inSubmit;

    @ApiModelProperty("不可以提交数量")
    private Integer exSubmit;
}
