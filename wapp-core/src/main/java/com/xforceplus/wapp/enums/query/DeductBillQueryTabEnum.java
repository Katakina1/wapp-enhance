package com.xforceplus.wapp.enums.query;


import com.xforceplus.wapp.common.dto.param.BillQueryParam;
import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.enums.DeductBillMakeInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;

import java.util.Arrays;
import java.util.List;

/**
 * Describe: 索赔-业务单查询 tab 及对应查询状态
 * 业务单状态（索赔）
 * 待匹配(101，102，103，104，105)、
 * 待确认(106+1)、
 * 待开票(106+2)、
 * 部分开票(106+3)、 --> 106+业务单开票状态：1 部分开票
 * 已开票(106+4)、--> 106+业务单开票状态：2 已开票
 * 待审核(107)、
 * 已取消(108)
 *
 * @Author xiezhongyong
 * @Date 2022/9/7
 */
public enum DeductBillQueryTabEnum implements IQueryTab<BillQueryParam> {


    /**
     * 待匹配---------------------------------
     * 业务单状态：101，102，103，104，105 + 待匹配（Code=S002、S004、S006）
     * 结算单状态：无
     * exceptionCodes 供应商侧使用
     */
    WAIT_MATCH("01", "待匹配",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_DIFF.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode())

            ).exceptionCodes(
                    Arrays.asList(
                            ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE.getCode(),
                            ExceptionReportCodeEnum.NOT_MATCH_CLAIM_DETAIL.getCode(),
                            ExceptionReportCodeEnum.PART_MATCH_CLAIM_DETAIL.getCode())
            ).build()
    ),


    /**
     * 待确认---------------------------------
     * 业务单状态：106
     * 结算单状态：1/8/9/10
     */
    WAIT_CONFIRM("02", "待确认",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).settlementStatus(
                    Arrays.asList(
                            TXfSettlementStatusEnum.WAIT_CONFIRM.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode())
            ).build()

    ),
    /**
     * 待开票---------------------------------
     * 业务单状态：106
     * 结算单状态：2
     */
    WAIT_MAKE_INVOICE("03", "待开票",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.WAIT_MAKE_INVOICE.code()
                    )
            ).build()
    ),
    /**
     * 部分开票---------------------------------
     * 业务单状态：106
     * 业务单开票状态：1
     */
    PART_MAKE_INVOICE("04", "部分开票",

            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.PART_MAKE_INVOICE.code()
                    )
            ).build()
    ),

    /**
     * 已开票(全部开票)---------------------------------
     * 业务单状态：106
     * 业务单开票状态：2
     */
    COMPLETE_MAKE_INVOICE("05", "已开票",

            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.COMPLETE_MAKE_INVOICE.code()
                    )
            ).build()
    ),


//    /**
//     * 待审核---------------------------------
//     * 业务单状态：107
//     * 结算单状态：无
//     */
//    WAIT_AUDIT("06", "待审核",
//
//            BillQueryParam.builder().billStatus(
//                    Arrays.asList(
//                            TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode())
//            ).build()
//    ),

    /**
     * 已取消---------------------------------
     * 业务单状态：108
     * 结算单状态：无
     */
    CANCELED("07", "已取消",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_DESTROY.getCode())
            ).build()
    ),


    /****
     * 全部---------------------------------
     * */
    ALL("00", "全部",
            // 待匹配
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_DIFF.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode(),
                            TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode())

            ).exceptionCodes(
                    Arrays.asList(
                            ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE.getCode(),
                            ExceptionReportCodeEnum.NOT_MATCH_CLAIM_DETAIL.getCode(),
                            ExceptionReportCodeEnum.PART_MATCH_CLAIM_DETAIL.getCode())
            ).build(),

            // 待确认
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).settlementStatus(
                    Arrays.asList(
                            TXfSettlementStatusEnum.WAIT_CONFIRM.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode())
            ).build(),

            // 待开票
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.WAIT_MAKE_INVOICE.code()
                    )
            ).build(),

            // 部分开票
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.PART_MAKE_INVOICE.code()
                    )
            ).build(),

            // 已开票
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.COMPLETE_MAKE_INVOICE.code()
                    )
            ).build(),

//            // 待审核
//            BillQueryParam.builder().billStatus(
//                    Arrays.asList(
//                            TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode())
//            ).build(),

            // 已取消
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.CLAIM_DESTROY.getCode())
            ).build()


    ),


    ;

    DeductBillQueryTabEnum(String code, String message, BillQueryParam... queryParams) {
        this.code = code;
        this.message = message;
        this.queryParams = null == queryParams ? null : Arrays.asList(queryParams);
    }

    private String code;
    private String message;
    private List<BillQueryParam> queryParams;

    @Override
    public Integer businessType() {
        return 1;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public List<BillQueryParam> queryParams() {
        return queryParams;
    }

    public static DeductBillQueryTabEnum fromCode(String code) {
        return Arrays.stream(values()).filter(s -> s.code().equals(code))
                .findAny().orElse(null);
    }

}
