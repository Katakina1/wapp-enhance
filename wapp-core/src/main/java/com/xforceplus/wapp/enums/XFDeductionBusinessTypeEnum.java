package com.xforceplus.wapp.enums;

/**
 * 类描述：
 *
 * @ClassName XFDeductionEnum
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 11:47
 */
public enum XFDeductionBusinessTypeEnum {
    CLAIM_BILL(1,"索赔单") ,AGREEMENT_BILL(2,"协议单"),EPD_BILL(3,"EPD单");
    private Integer type;
    private String des;

    XFDeductionBusinessTypeEnum(Integer type, String des) {
        this.type = type;
        this.des = des;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
