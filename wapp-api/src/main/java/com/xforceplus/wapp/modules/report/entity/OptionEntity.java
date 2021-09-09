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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
