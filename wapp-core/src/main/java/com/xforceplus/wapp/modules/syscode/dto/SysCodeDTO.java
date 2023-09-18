package com.xforceplus.wapp.modules.syscode.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : model
 * @date : 2022/10/25 10:48
 **/
@ApiModel("小代码模型")
@Data
@NoArgsConstructor
public class SysCodeDTO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("代码id")
    @NotBlank(message = "代码id不能为空")
    private String sysId;

    @ApiModelProperty("代码code")
    private String sysCode;

    @ApiModelProperty("代码名称")
    private String sysName;

    @ApiModelProperty("序号")
    private Integer seqNum;

    @ApiModelProperty("备注")
    @Size(max = 200, message = "备注长度不能超过200字符")
    private String remark;
}
