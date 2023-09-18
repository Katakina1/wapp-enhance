package com.xforceplus.wapp.enums.query;


import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;

import java.util.Arrays;
import java.util.List;

/**
 * Describe: 结算单查询状态
 * 待确认(1,8,9,10)、
 * 待开票(2)、
 * 部分开票(3)、
 * 已开票(4)、
 * 已完成(5)、
 * 待审核(6)、
 * 已撤销(7)
 *
 * @Author xiezhongyong
 * @Date 2022/9/12
 */
public enum SettlementQueryTabEnum {
    /**
     * 待确认---------------------------------
     */
    WAIT_CONFIRM("01", "待确认", Arrays.asList(
            TXfSettlementStatusEnum.WAIT_CONFIRM.getValue(),
            TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getValue(),
            TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getValue(),
            TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getValue()
    )),

    /**
     * 待开票---------------------------------
     */
    WAIT_MAKE_INVOICE("02", "待开票",
            Arrays.asList(
                    TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getValue()
            )
    ),
    /**
     * 部分开票---------------------------------
     */
    PART_MAKE_INVOICE("03", "部分开票",
            Arrays.asList(
                    TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getValue()
            )
    ),

    /**
     * 已开票(全部开票)---------------------------------
     */
    COMPLETE_MAKE_INVOICE("04", "已开票",
            Arrays.asList(
                    TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getValue()
            )
    ),


    /**
     * 已完成---------------------------------
     */
    FINISH("05", "已完成",
            Arrays.asList(
                    TXfSettlementStatusEnum.FINISH.getValue()
            )
    ),

    /**
     * 待审核---------------------------------
     */
    WAIT_AUDIT("06", "待审核",
            Arrays.asList(
                    TXfSettlementStatusEnum.WAIT_CHECK.getValue()
            )
    ),

    /**
     * 已撤销---------------------------------
     */
    CANCELED("07", "已撤销",
            Arrays.asList(
                    TXfSettlementStatusEnum.DESTROY.getValue()
            )
    ),


    /****
     * 全部---------------------------------
     * */
    ALL("00", "全部",
            Arrays.asList(
                    TXfSettlementStatusEnum.WAIT_CONFIRM.getValue(),
                    TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getValue(),
                    TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getValue(),
                    TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getValue(),
                    TXfSettlementStatusEnum.FINISH.getValue(),
                    TXfSettlementStatusEnum.WAIT_CHECK.getValue(),
                    TXfSettlementStatusEnum.DESTROY.getValue(),
                    TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getValue(),
                    TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getValue(),
                    TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getValue()
            )
    ),
    ;

    SettlementQueryTabEnum(String code, String message, List<Integer> queryParams) {
        this.code = code;
        this.message = message;
        this.queryParams = queryParams;
    }

    private String code;
    private String message;
    private List<Integer> queryParams;

    public Integer businessType() {
        return 1;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public List<Integer> queryParams() {
        return queryParams;
    }

    public static SettlementQueryTabEnum fromCode(String code) {
        return Arrays.stream(values()).filter(s -> s.code().equals(code))
                .findAny().orElse(null);
    }

    /**
     * 通过结算单状态获取枚举对象
     * @param settlementStatus
     * @return
     */
    public static SettlementQueryTabEnum fromSettlementStatus(Integer settlementStatus) {

        for (SettlementQueryTabEnum tabEnum : Arrays.asList(values())) {
            if(tabEnum.queryParams.contains(settlementStatus)) {
                return tabEnum;
            }
        }

        return null;
    }


}
