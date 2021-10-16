package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RevokeRequest {

    private String serialNo;

    private String applyTaxCode;

    private String terminalUn;

    private String deviceUn;


    List<RevokeRedNotificationInfo> redNotificationList;

}
