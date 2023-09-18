package com.xforceplus.wapp.enums.customs;


import java.util.Arrays;
import java.util.List;


public enum CustomsQueryAccountTabEnum {
    /**
     * 00, "未入账",01, "入账中",02,"入账企业所得税税前扣除",03,"入账企业所得税不扣除",04,"入账失败",05,"入账撤销中",06,"入账撤销失败"
     */
    QUERY_ACCOUNT_00("00", "未入账", Arrays.asList(
            AccountStatusEnum.ACCOUNT_00.getValue()

    )),
    QUERY_ACCOUNT_01("01", "入账中", Arrays.asList(
            AccountStatusEnum.ACCOUNT_01.getValue()

    )),
    QUERY_ACCOUNT_02("02", "入账企业所得税税前扣除", Arrays.asList(
            AccountStatusEnum.ACCOUNT_02.getValue()

    )),

    QUERY_ACCOUNT_03("03", "入账企业所得税不扣除", Arrays.asList(
            AccountStatusEnum.ACCOUNT_03.getValue()

    )),
    QUERY_ACCOUNT_04("04", "入账失败", Arrays.asList(
            AccountStatusEnum.ACCOUNT_04.getValue()

    )),
    QUERY_ACCOUNT_05("05", "入账撤销中", Arrays.asList(
            AccountStatusEnum.ACCOUNT_05.getValue()

    )),


    QUERY_ACCOUNT_06("06", "入账撤销", Arrays.asList(
            AccountStatusEnum.ACCOUNT_06.getValue()

    )),
    QUERY_ACCOUNT_07("07", "入账撤销失败", Arrays.asList(
            AccountStatusEnum.ACCOUNT_07.getValue()

    )),
    /****
     * 全部---------------------------------
     * */
    ALL("00", "全部",
            Arrays.asList(
                    AccountStatusEnum.ACCOUNT_00.getValue(),
                    AccountStatusEnum.ACCOUNT_01.getValue(),
                    AccountStatusEnum.ACCOUNT_02.getValue(),
                    AccountStatusEnum.ACCOUNT_03.getValue(),
                    AccountStatusEnum.ACCOUNT_04.getValue(),
                    AccountStatusEnum.ACCOUNT_05.getValue(),
                    AccountStatusEnum.ACCOUNT_06.getValue(),
                    AccountStatusEnum.ACCOUNT_07.getValue()
            )
    ),
    ;

    CustomsQueryAccountTabEnum(String code, String message, List<String> queryParams) {
        this.code = code;
        this.message = message;
        this.queryParams = queryParams;
    }

    private String code;
    private String message;
    private List<String> queryParams;

    public Integer businessType() {
        return 1;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public List<String> queryParams() {
        return queryParams;
    }

    public static CustomsQueryAccountTabEnum fromCode(String code) {
        return Arrays.stream(values()).filter(s -> s.code().equals(code))
                .findAny().orElse(null);
    }
}
