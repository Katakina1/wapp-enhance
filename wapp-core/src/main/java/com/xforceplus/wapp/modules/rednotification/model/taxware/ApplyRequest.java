package com.xforceplus.wapp.modules.rednotification.model.taxware;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel("红字信息申请请求类")
@Getter
@Setter
public class ApplyRequest {

    private String serialNo;

    private String terminalUn;

    private String deviceUn;

    private List<RedInfo> redInfoList;


}
