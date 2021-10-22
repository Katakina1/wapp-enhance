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
public class DeductExportRequest extends QueryDeductListRequest{

    @ApiModelProperty("业务单id列表")
    private List<Long> idList;

}
