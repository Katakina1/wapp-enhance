package com.xforceplus.wapp.enums.query;

import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.enums.DeductBillMakeInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.common.dto.param.BillQueryParam;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Describe: 协议-业务单查询 tab 及对应查询状态
 * 业务单状态（索赔）
 * 待匹配(201)、
 * 待确认(202+8/9/10，205+8)、
 * 待开票(202+2)、
 * 部分开票(202+3)、--> 202+业务单开票状态：1 部分开票
 * 已开票(202+4)、--> 202+业务单开票状态：2 已开票
 * 待审核(?)、
 * 已取消(206)
 *
 * @Author xiezhongyong
 * @Date 2022/9/7
 */
@AllArgsConstructor
public enum AgreementBillQueryTabEnum implements IQueryTab<BillQueryParam> {


    /**
     * 待匹配---------------------------------
     * 业务单状态：201
     * 结算单状态：无
     */
	WAIT_MATCH("01", "待匹配", BillQueryParam.builder().billStatus(Arrays.asList(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode())).build()),


    /**
     * 待确认---------------------------------
     * 业务单状态：202+结算单状态：1/8/9/10
     * 业务单状态：205+结算单状态：8
     */
    WAIT_CONFIRM("02", "待确认",
            // 第一种组合
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).settlementStatus(
                    Arrays.asList(
                            TXfSettlementStatusEnum.WAIT_CONFIRM.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode())
            ).build(),
            // 第二种组合
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_BLUE_INVOICE.getCode())
            ).settlementStatus(
                    Arrays.asList(TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode())
            ).build()

    ),
    /**
     * 待开票---------------------------------
     * 业务单状态：202
     * 结算单状态：2
     */
    WAIT_MAKE_INVOICE("03", "待开票",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(DeductBillMakeInvoiceStatusEnum.WAIT_MAKE_INVOICE.code())
            ).build()
    ),
    /**
     * 部分开票---------------------------------
     * 业务单状态：202
     * 业务单开票状态：1
     */
    PART_MAKE_INVOICE("04", "部分开票",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(DeductBillMakeInvoiceStatusEnum.PART_MAKE_INVOICE.code())
            ).build(),
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).settlementStatus(
                    Arrays.asList(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode())
            ).build()
    ),

    /**
     * 已开票(全部开票)---------------------------------
     * 业务单状态：202
     * 业务单开票状态：2
     */
    COMPLETE_MAKE_INVOICE("05", "已开票",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(DeductBillMakeInvoiceStatusEnum.COMPLETE_MAKE_INVOICE.code())
            ).build(),
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).settlementStatus(
                    Arrays.asList(TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode())
            ).build()
    ),


//    /**
//     * 待审核---------------------------------
//     * 业务单状态：202
//     * 结算单状态6
//     */
//    WAIT_AUDIT("06", "待审核",
//
//            BillQueryParam.builder().billStatus(
//                    Arrays.asList(
//                            TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
//            ).settlementStatus(
//                    Arrays.asList(
//                            TXfSettlementStatusEnum.WAIT_CHECK.getCode())
//            ).build()
//    ),

    /**
     * 已取消---------------------------------
     * 业务单状态：206
     * 结算单状态：无
     */
    CANCELED("07", "已取消",
            BillQueryParam.builder().billStatus(
                    Arrays.asList(TXfDeductStatusEnum.AGREEMENT_DESTROY.getCode())
            ).build()
    ),

    /****
     * 全部---------------------------------
     * */
    ALL("00", "全部",
            // 待匹配+已取消
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            // 待匹配
                            TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode(),
                            // 已取消
                            TXfDeductStatusEnum.AGREEMENT_DESTROY.getCode())
            ).build(),

            // 待确认----------
            // 第一种组合
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).settlementStatus(
                    Arrays.asList(
                            TXfSettlementStatusEnum.WAIT_CONFIRM.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode(),
                            TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode())
            ).build(),
            // 第二种组合
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.AGREEMENT_NO_MATCH_BLUE_INVOICE.getCode())
            ).settlementStatus(
                    Arrays.asList(
                            TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode())
            ).build(),

            // 待开票
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.WAIT_MAKE_INVOICE.code()
                    )
            ).build(),

            // 部分开票
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.PART_MAKE_INVOICE.code()
                    )
            ).build(),

            // 已开票
            BillQueryParam.builder().billStatus(
                    Arrays.asList(
                            TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
            ).makeInvoiceStatus(
                    Arrays.asList(
                            DeductBillMakeInvoiceStatusEnum.COMPLETE_MAKE_INVOICE.code()
                    )
            ).build()

//            // 待审核
//            BillQueryParam.builder().billStatus(
//                    Arrays.asList(
//                            TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode())
//            ).settlementStatus(
//                    Arrays.asList(
//                            TXfSettlementStatusEnum.WAIT_CHECK.getCode())
//            ).build()

    ),


    ;

    AgreementBillQueryTabEnum(String code, String message, BillQueryParam... queryParams) {
        this.code = code;
        this.message = message;
        this.queryParams = null == queryParams ? null : Arrays.asList(queryParams);
    }

    private String code;
    private String message;
    private List<BillQueryParam> queryParams;


    @Override
    public Integer businessType() {
        return 2;
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

    public static AgreementBillQueryTabEnum fromCode(String code) {
        return Arrays.stream(values()).filter(s -> s.code().equals(code))
                .findAny().orElse(null);
    }


}
