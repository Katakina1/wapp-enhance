package com.xforceplus.wapp.common.dto.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe: 业务单查询条件
 *
 * @Author xiezhongyong
 * @Date 2022/9/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillQueryParam {

    /** 业务单状态*/
    private List<Integer> billStatus = new ArrayList<>();

    /** 结算单状态*/
    private List<Integer> settlementStatus = new ArrayList<>();

    /** 列外报告状态*/
    private List<String> exceptionCodes = new ArrayList<>();

    /** 业务单开票状态*/
    private List<Integer> makeInvoiceStatus = new ArrayList<>();


}
