package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@ApiModel("业务单导出请求")
@Data
public class DeductExportRequest extends QueryDeductListNewRequest {

    @ApiModelProperty("导出数据类型;1:主信息,默认;2:主信息及明细")
    private Integer exportDataType = 1;

}
