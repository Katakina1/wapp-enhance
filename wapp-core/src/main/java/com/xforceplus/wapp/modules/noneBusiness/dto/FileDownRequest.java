package com.xforceplus.wapp.modules.noneBusiness.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-24 13:57
 **/
@Setter
@Getter
public class FileDownRequest implements Serializable {
    private final static long serialVersionUID = 1L;

    private Integer pdf;
    private Integer ofd;
    private Integer xml;
    private List<Long> ids;

    @Deprecated
    private String single;
}
