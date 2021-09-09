package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-今日采集
 */

public final class IndexInvoiceCollectionCountModel  {


    /**
     * 统计值
     */
    private Integer countNum;

//    /**
//     * 类型
//     */
//    private String invoiceType;
    /**
     * 类型
     */
    private String sourceSystem;

    public Integer getCountNum() {
        return countNum;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
}
