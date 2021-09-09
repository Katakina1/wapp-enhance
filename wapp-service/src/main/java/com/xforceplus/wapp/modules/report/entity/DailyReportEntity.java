package com.xforceplus.wapp.modules.report.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票认证日报或月报实体
 */
@Getter
@Setter
public class DailyReportEntity implements Serializable {

    public DailyReportEntity(){}

    public DailyReportEntity(String rzhDate){
        this.rzhDate = rzhDate;
        this.count = 0;
        this.amount = 0.00;
        this.tax = 0.00;
    }

    //认证日期(或税款所属期),进项税额报表中为转出原因类别
    private String rzhDate;

    //发票数量
    private Integer count;

    //合计金额
    private Double amount;

    //合计税额
    private Double tax;
}
