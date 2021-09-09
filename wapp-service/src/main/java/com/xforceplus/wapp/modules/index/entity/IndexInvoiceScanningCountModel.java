package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-发票扫描
 */
@Getter
@Setter
@ToString
public final class IndexInvoiceScanningCountModel {

    /**
     * 统计值
     */
    private Integer countNum;

    /**
     * 签收类型
     */
    private String qsType;
}
