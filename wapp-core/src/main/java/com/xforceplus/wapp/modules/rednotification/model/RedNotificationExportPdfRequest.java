package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("导出pdf")
@Data
public class RedNotificationExportPdfRequest {
    @ApiModelProperty(value = "合并类型",notes = "0 全部合并 1 按照销方公司拆分 2 按购方公司拆分")
    private Integer generateModel;

    @ApiModelProperty(value = "红字信息筛选条件")
    private QueryModel queryModel;

    //申请完红字信息自动尝试获取pdf
    private Boolean autoFlag;
}
