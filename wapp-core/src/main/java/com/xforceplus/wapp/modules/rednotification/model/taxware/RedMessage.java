package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
public class RedMessage implements Serializable {

    private String serialNo;

    private List<RedMessageInfo> redApplyResultList;

}
