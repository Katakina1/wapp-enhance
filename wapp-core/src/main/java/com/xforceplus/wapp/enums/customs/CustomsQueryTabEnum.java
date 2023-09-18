package com.xforceplus.wapp.enums.customs;


import java.util.Arrays;
import java.util.List;


public enum CustomsQueryTabEnum {
    /**
     * 勾选状态 -1 - 撤销勾选失败 0-撤销勾选中 1-不可勾选  2-未勾选 3-勾选中 4-已勾选 5-勾选失败  6-抵扣异常 8-已确认抵扣 9-撤销勾选成功(属地使用)
     *
     */
    QUERY_CHECK_2("2", "未勾选", Arrays.asList(
            InvoiceCheckEnum.CHECK_2.getValue()
    )),
    QUERY_CHECK_3("3", "勾选中", Arrays.asList(
            InvoiceCheckEnum.CHECK_3.getValue()
    )),
    QUERY_CHECK_4("4", "已勾选", Arrays.asList(
            InvoiceCheckEnum.CHECK_4.getValue()
    )),

    QUERY_CHECK_5("5", "勾选失败", Arrays.asList(
            InvoiceCheckEnum.CHECK_5.getValue()
    )),
    QUERY_CHECK_0("0", "撤销勾选中", Arrays.asList(
            InvoiceCheckEnum.CHECK_0.getValue()
    )),
    QUERY_CHECK_N1("-1", "撤销勾选失败", Arrays.asList(
            InvoiceCheckEnum.CHECK_N1.getValue()
    )),


    QUERY_CHECK_1("1", "不可勾选", Arrays.asList(
            InvoiceCheckEnum.CHECK_1.getValue()
    )),


    QUERY_CHECK_6("6", "抵扣异常", Arrays.asList(
            InvoiceCheckEnum.CHECK_6.getValue()
    )),

    QUERY_CHECK_8("8", "已确认抵扣", Arrays.asList(
            InvoiceCheckEnum.CHECK_8.getValue()
    )),
    QUERY_CHECK_9("9", "撤销勾选成功", Arrays.asList(
            InvoiceCheckEnum.CHECK_9.getValue()
    )),

    /****
     * 全部---------------------------------
     * */
    ALL("00", "全部",
             Arrays.asList(
                     InvoiceCheckEnum.CHECK_N1.getValue(),
                    InvoiceCheckEnum.CHECK_0.getValue(),
                            InvoiceCheckEnum.CHECK_1.getValue(),
                            InvoiceCheckEnum.CHECK_2.getValue(),
                            InvoiceCheckEnum.CHECK_3.getValue(),
                            InvoiceCheckEnum.CHECK_4.getValue(),
                            InvoiceCheckEnum.CHECK_5.getValue(),
                            InvoiceCheckEnum.CHECK_6.getValue(),
                            InvoiceCheckEnum.CHECK_8.getValue(),
                            InvoiceCheckEnum.CHECK_9.getValue()
                            )
                            ),
    ;

    CustomsQueryTabEnum(String code, String message, List<Integer> queryParams) {
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

    public static CustomsQueryTabEnum fromCode(String code) {
        return Arrays.stream(values()).filter(s -> s.code().equals(code))
                .findAny().orElse(null);
    }
}
