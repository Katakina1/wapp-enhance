package com.xforceplus.wapp.enums;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @description: 公司类型类型
 * @create: 2021-10-14 14:20
 **/
@AllArgsConstructor
@Getter
public enum CompanyTypeEnum {

    /**
     * 购方机构
     */
    COMPANY_TYPE_PUR("2","购方机构"),
    /**
     * 协议单任务
     */
    COMPANY_TYPE_SUR("3","销方机构"),
    /**
     * EPD单任务
     */
    COMPANY_TYPE_WALMART("5","公司类型为购方机构"),

    COMPANY_TYPE_SUPPLIER("8","公司类型为销方机构");

    private String resultCode;

    private String resultTip;


}