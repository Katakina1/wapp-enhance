package com.xforceplus.wapp.common.enums;

import java.util.List;

/**
 * Describe: IQueryTab
 *
 * @Author xiezhongyong
 * @Date 2022-09-09
 */
public interface IQueryTab<T> {

    /** 业务单据类型;1:索赔;2:协议;3:EPD */
    Integer businessType();

    String code();

    String message();

    List<T> queryParams();
}
