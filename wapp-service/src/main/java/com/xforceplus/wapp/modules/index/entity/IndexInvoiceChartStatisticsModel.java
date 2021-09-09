package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bobby
 * @date 2018/4/17
 * 首页-图表
 */
@Getter
@Setter
@ToString
public final class IndexInvoiceChartStatisticsModel {
    public Integer getXzInvoiceCount() {
        return xzInvoiceCount;
    }

    public void setXzInvoiceCount(Integer xzInvoiceCount) {
        this.xzInvoiceCount = xzInvoiceCount;
    }

    public Integer getRzcgInvoiceCount() {
        return rzcgInvoiceCount;
    }

    public void setRzcgInvoiceCount(Integer rzcgInvoiceCount) {
        this.rzcgInvoiceCount = rzcgInvoiceCount;
    }

    public Integer getRzsbInvoiceCount() {
        return rzsbInvoiceCount;
    }

    public void setRzsbInvoiceCount(Integer rzsbInvoiceCount) {
        this.rzsbInvoiceCount = rzsbInvoiceCount;
    }

    public String getDayOfMonthInYear() {
        return dayOfMonthInYear;
    }

    public void setDayOfMonthInYear(String dayOfMonthInYear) {
        this.dayOfMonthInYear = dayOfMonthInYear;
    }

    public String getRzcgDayOfMonthInYear() {
        return rzcgDayOfMonthInYear;
    }

    public void setRzcgDayOfMonthInYear(String rzcgDayOfMonthInYear) {
        this.rzcgDayOfMonthInYear = rzcgDayOfMonthInYear;
    }

    public String getRzsbDayOfMonthInYear() {
        return rzsbDayOfMonthInYear;
    }

    public void setRzsbDayOfMonthInYear(String rzsbDayOfMonthInYear) {
        this.rzsbDayOfMonthInYear = rzsbDayOfMonthInYear;
    }

    /**
     * 发票-新增
     */
    private Integer xzInvoiceCount;

    /**
     * 发票-认证成功
     */
    private Integer rzcgInvoiceCount;

    /**
     * 发票-认证失败
     */
    private Integer rzsbInvoiceCount;

    /**
     * 时间
     */
    private String dayOfMonthInYear;

    /**
     * 认证成功时间
     */
    private String rzcgDayOfMonthInYear;

    /**
     * 认证失败时间
     */
    private String rzsbDayOfMonthInYear;
}
