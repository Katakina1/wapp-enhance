package com.xforceplus.wapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 默认分页参数
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:57
 **/
@Getter
@Setter
public class PageViewRequest {

    /**
     * 页码默认1
     */
    private int page = 1;

    /**
     * 每页默认显示50条
     */
    private int size = 50;
}
