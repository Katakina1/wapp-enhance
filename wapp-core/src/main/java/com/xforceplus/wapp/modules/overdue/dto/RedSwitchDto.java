package com.xforceplus.wapp.modules.overdue.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@ApiModel("红字信息挂起开关")
public class RedSwitchDto {
    private Long id;
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private Date start;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private Date end;
}
