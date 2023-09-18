package com.xforceplus.wapp.modules.supserviceconf.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class SuperServiceConfQueryDto {

    @ApiModelProperty("userId")
    private String userId;

    @ApiModelProperty("供应商Id")
    private Integer orgId;

    @ApiModelProperty("供应商号")
    private String userCode;

    @ApiModelProperty("供应商名称")
    private String userName;

    @ApiModelProperty("税号")
    private String taxNo;

    @ApiModelProperty("服务类型")
    private Integer serviceType;

    @ApiModelProperty("生效日期日期yyyy-mm-dd")
    private String assertDate;

    @ApiModelProperty("失效日期日期yyyy-mm-dd")
    private String expireDate;

    @ApiModelProperty("生效日期起始日期yyyy-mm-dd")
    private String assertDateStart;

    @ApiModelProperty("生效日期结束日期yyyy-mm-dd")
    private String assertDateEnd;

    @ApiModelProperty("失效日期起始日期yyyy-mm-dd")
    private String expireDateStart;

    @ApiModelProperty("失效日期结束日期yyyy-mm-dd")
    private String expireDateEnd;

    @ApiModelProperty("更新日期起始日期yyyy-mm-dd")
    private String updateDateStart;

    @ApiModelProperty("更新日期结束日期yyyy-mm-dd")
    private String updateDateEnd;

    @ApiModelProperty("页数")
    private Integer pageSize=20;

    @ApiModelProperty("页码")
    private Integer pageNo=1;
}
