package com.xforceplus.wapp.modules.discountRateLog.dto;

import lombok.Data;

@Data
public class OrgDto {
    /**
     * 机构ID
     */
    private Long orgId;
    private String orgCode;
    private String orgName;
    private Integer pageSize=20;
    private Integer pageNo=1;
    private String isAllSelected;
}
