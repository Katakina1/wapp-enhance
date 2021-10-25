package com.xforceplus.wapp.modules.log.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by SunShiyong on 2021/10/25.
 */
@ApiModel("日志查询响应")
@Data
public class QueryOperationLogResponse{


    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 操作类型 0 settlement, 1 deduct ,2 invoice
     */
    @ApiModelProperty("操作类型 0 settlement, 1 deduct ,2 invoice")
    private String operateType;

    /**
     * 操作描述
     */
    @ApiModelProperty("操作描述")
    private String operateDesc;

    /**
     * 操作用户id
     */
    @ApiModelProperty("操作用户id")
    private Long userId;

    /**
     * 操作用户名称
     */
    @ApiModelProperty("操作用户名称")
    private String userName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 业务状态
     */
    @ApiModelProperty("业务状态")
    private String businessStatus;

    /**
     * 业务id
     */
    @ApiModelProperty("业务id")
    private Long businessId;
}
