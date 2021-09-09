package com.xforceplus.wapp.modules.report.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.DataInput;
import java.io.InterruptedIOException;
import java.io.Serializable;

/**
 * @author joe.tang
 * @date 2018/4/13
 * 认证发票汇总报表统计实体类
 */
@Setter
@Getter
@ToString
public class InvoiceAuthenticationStatisticEntity implements Serializable {

    private static final long serialVersionUID = -8512131254699471875L;

    //当前税款所属期
    private String dqskssq;

    //发票数量
    private Integer invoiceCount;

    //合计金额
    private Double totalAmount;

    //合计税额
    private Double taxAmount;
}
