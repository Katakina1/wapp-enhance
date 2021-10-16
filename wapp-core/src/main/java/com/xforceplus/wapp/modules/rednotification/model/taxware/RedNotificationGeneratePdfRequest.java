package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedNotificationGeneratePdfRequest {

    private RequestHead head;

    private String serialNo;

    private RedGeneratePdfInfo redInfo;

}
