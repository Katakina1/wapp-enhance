package com.xforceplus.wapp.enums.exceptionreport;

import lombok.Getter;

/**
 * @author Xforce
 */
public enum BillJobOriginDataTypeEnum {
    CLAIM_INFO(1, "索赔单主信息","claim"),
    CLAIM_HYPER(2, "索赔单hyper明细","hyper"),
    CLAIM_SAMS(3, "索赔单sams明细","sams"),
    AGREEMENT_ZARR(4, "协议单zarr","zarr"),
    AGREEMENT_FBL5N(5, "协议单fbl5n","fbl5n"),
    EPD_INFO(6, "epd单主信息","epd"),
    EPD_LOG(7, "epd单log","epdlog"),
    ;

    @Getter
    private int type;
    @Getter
    private String desc;
    @Getter
    private String sheet;

    BillJobOriginDataTypeEnum(int type, String desc,String sheet) {
        this.type = type;
        this.desc = desc;
        this.sheet = sheet;
    }
}
