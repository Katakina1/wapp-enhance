package com.xforceplus.wapp.common.safesoft;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2010-11-22
 * Time: 10:05:30
 */

public abstract class AbstractBaseDomain implements Serializable {

    public AbstractBaseDomain() {
        id = null;
    }

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String schemaLabel;

    public abstract Boolean isNullObject();
}

