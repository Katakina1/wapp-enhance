package com.xforceplus.wapp.modules.discountRateLog.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DiscountRateLogDto {
    /**
     * 机构ID
     */
    private Long orgid;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 修改前折扣率
     */
    private Long updateBefore;
    /**
     * 修改后折扣率
     */
    private Long updateAfter;
    /**
     * 更改时间
     */
    private String updateTime;

    private Integer pageSize=20;


    private Integer pageNo=1;
}
