package com.xforceplus.wapp.modules.discountRateLog.dto;

import lombok.Data;

@Data
public class SupplierInvoiceQuotaLogDto {
    /**
     * 机构ID
     */
    private Long orgid;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 修改前限额
     */
    private Long updateBefore;
    /**
     * 修改后限额
     */
    private Long updateAfter;
    /**
     * 更改时间
     */
    private String updateTime;

    private Integer pageSize=20;


    private Integer pageNo=1;
}
