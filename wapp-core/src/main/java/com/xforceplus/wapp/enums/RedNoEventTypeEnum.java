package com.xforceplus.wapp.enums;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 红字申请/撤销触发事件
 * @date : 2022/09/09 16:55
 **/
public enum RedNoEventTypeEnum {

    /**
     * 红字申请/撤销触发事件
     */
    DESTROY_SETTLEMENT("DESTROY_SETTLEMENT", "结算单作废"),
    SPLIT_AGAIN("SPLIT_AGAIN", "结算单重新拆票");

    private String code;

    private String desc;

    RedNoEventTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String code() {
        return this.code;
    }

    public String desc() {
        return this.desc;
    }
}
