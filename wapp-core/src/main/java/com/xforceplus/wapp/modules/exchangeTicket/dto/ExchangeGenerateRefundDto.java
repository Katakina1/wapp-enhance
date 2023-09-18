package com.xforceplus.wapp.modules.exchangeTicket.dto;

import io.swagger.annotations.ApiModelProperty;
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
public class ExchangeGenerateRefundDto implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("是否取消匹配关系 0 不取消 1取消")
    private String isCancelMatch;
    @ApiModelProperty("包含项 选中的id")
    private List<Long> idList;
}
