package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.modules.rednotification.model.taxware.Tax;

import java.util.Arrays;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 税盘类型
 * @date : 2022/09/06 10:09
 **/
public enum TaxDeviceTypeEnum {
    /**
     * 税盘类型：1 百望单盘 2 百望服务器 3 航信单盘 4 航信服务器 5 虚拟UKey设备 8 税务UKey设备 7 税务证书
     */
    BW_DEVICE(1, "百旺单盘"),
    BW_SERVER(2, "百望服务器"),
    HX_DEVICE(3, "航信单盘"),
    HX_SERVER(4, "航信服务器"),
    V_U_KEY(5, "虚拟UKey设备"),
    U_KEY(8, "税务UKey设备"),
    TAX_CERTIFICATE(7, "税务证书");

    private Integer code;

    private String desc;

    TaxDeviceTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer code() {
        return this.code;
    }

    public String desc() {
        return this.desc;
    }

    public static TaxDeviceTypeEnum fromValue(Integer code) {
        return Arrays.stream(TaxDeviceTypeEnum.values()).filter(taxDeviceTypeEnum -> taxDeviceTypeEnum.code.equals(code)).findFirst().orElse(null);
    }
}
