package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bobby
 * @date 2018/4/17
 * 首页-发票认证
 */
@Getter
@Setter
@ToString
public final class IndexInvoiceAuthenticationCountModel {

    /**
     * 统计
     */
    private Integer countNum;

    /**
     * 类型
     */
    private String authenticationType;
}
