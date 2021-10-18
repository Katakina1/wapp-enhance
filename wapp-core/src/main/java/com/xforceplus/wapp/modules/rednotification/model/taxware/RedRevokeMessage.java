package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RedRevokeMessage implements Serializable {

    private String redNotificationNo;

    private String serialNo;

}
