package com.xforceplus.wapp.modules.certification.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 企业税务信息
 * @author kevin.wang
 * @date 4/12/2018
 */
@Getter
@Setter
public class EnterpriseTaxInformationEntity {

    //税号
    private String taxNo;

    //企业名称
    private String taxName;

    //企业旧税号
    private String oldTaxNo;

    //当前税款所属期
    private String currentTaxPeriod;

    //当前税款所属期可勾选发票的起始开票日期
    private String selectStartDate;

    //当前税款所属期可勾选发票的截止开票日期
    private String selectEndDate;
    
    //当前税款所属期可勾选发票操作截止日期
    private String operationEndDate;

    //信用等级，值为A/B/C/D或者空
    private String creditRating;

    //申报周期，值为quarter-季度/month-月
    private String declarePeriod;
    private String jvcode;
    private String companyCode;

}
