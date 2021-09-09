package com.xforceplus.wapp.common.utils;


import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * Created by Bourne.Lv on 2018/04/12.
 * <p>
 * 数据字典明细-枚举类
 */
@Getter
public enum DictDetaEnum {

    /**
     * 性别--男
     */
    SEX_MALE("0","男"),

    /**
     * 性别--女
     */
    SEX_FEMALE("1","女"),

    /**
     * 认证状态-认证成功
     */
    AUTH_STATUS_SUCCESS("4","认证成功"),

    /**
     * 认证状态-认证失败
     */
    AUTH_STATUS_ERROR("5","认证失败"),

    /**
     * 默认
     */
    DEFAULT("");

    /**
     * 数据字典名称
     */
    private String dictName;

    /**
     * 数据字典编码
     */
    private String dictCode;

    public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getDictCode() {
		return dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	/**
     * 构造器
     *
     * @param code 数据字典编码
     */
    DictDetaEnum(String code) {
        dictCode = code;
    }

    /**
     * 构造器
     *
     * @param code  数据字典编码
     * @param value 数据字典名称
     */
    DictDetaEnum(String code, String value) {
        dictCode = code;
        dictName = value;
    }

    public static Map<String, String> authMap() {
        final Map<String, String> map = Maps.newHashMapWithExpectedSize(2);
        map.put(AUTH_STATUS_SUCCESS.getDictCode(),AUTH_STATUS_SUCCESS.dictName);
        map.put(AUTH_STATUS_ERROR.getDictCode(),AUTH_STATUS_ERROR.dictName);
        return map;
    }
}
