package com.xforceplus.wapp.enums.syscode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : hujintao
 * @version : 1.0
 * @description :
 * @date : 2022/10/25 14:04
 **/
@Getter
@AllArgsConstructor
public enum SysIdEnum {

    /**
     * 配置小代码sysId
     */
    DESTROY_SETTLEMENT_SELLER("DESTROY_SELLER", "供应商侧撤销结算单按钮配置");

    private String code;

    private String desc;
}
