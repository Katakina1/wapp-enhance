package com.xforceplus.wapp.modules.supserviceconf.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class SuperServiceConfDto {

    @ApiModelProperty("用户Id")
    private Integer userId;

    @ApiModelProperty("组织Id")
    private Integer orgId;

    @ApiModelProperty("供应商号")
    private String userCode;

    @ApiModelProperty("供应商名称")
    private String userName;

    @ApiModelProperty("税号")
    private String taxNo;

    @ApiModelProperty("服务类型")
    private Integer serviceType;

    @ApiModelProperty("协议折让率")
    private String discountRate;

    @ApiModelProperty("生效日期yyyy-mm-dd")
    private String assertDate;

    @ApiModelProperty("失效日期yyyy-mm-dd")
    private String expireDate;

    @ApiModelProperty("更新日期yyyy-mm-dd")
    private String updateDate;
}
