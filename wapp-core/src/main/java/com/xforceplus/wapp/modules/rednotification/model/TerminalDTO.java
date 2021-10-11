package com.xforceplus.wapp.modules.rednotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ApiModel("终端信息")
public class TerminalDTO {
    @ApiModelProperty("终端类型 1单盘，2服务器,3底账，4区块链，5 Ukey")
    @JsonProperty("terminalType")
    private String terminalType;
    @ApiModelProperty("设备编码")
    @JsonProperty("deviceUn")
    private String deviceUn;
    @ApiModelProperty("终端唯一码")
    @JsonProperty("terminalUn")
    private String terminalUn;
    @ApiModelProperty("终端名称")
    @JsonProperty("terminalName")
    private String terminalName;
    @ApiModelProperty("在线标识 1在线 0 不在线")
    @JsonProperty("directOnlineFlag")
    private Integer directOnlineFlag;
}
