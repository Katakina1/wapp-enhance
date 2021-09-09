package com.xforceplus.wapp.modules.report.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 下拉选项实体
 */
@Getter
@Setter
public class OptionEntity implements Serializable {
    private String value;
    private String label;
    private String costType;
    private String costTypeName;
}
