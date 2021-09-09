package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bobby
 * @date 2018/4/19
 * 首页-任务提醒
 */
@Setter
@Getter
@ToString
public final class IndexInvoiceTaskRemindingModel {

    /**
     * 逾期发票预警
     */
    private Integer countNum;


    /**
     * 预警类型
     */
    private String redType;
}
