package com.xforceplus.wapp.modules.invoicetaxmapping.dto;

import lombok.Data;

@Data
public class InvoiceTaxMappingQuery {
    /**
     * 税收分类编码
     */
    private String goodsTaxNo;
    /**
     * 发票类型
     */
    private String invoiceType;
    /**
     * 修改人
     */
    private String lastUpdateBy;
    /**
     * id
     */
    private Integer id;
    /**
     * 每页条数
     */
    private Integer pageSize=20;
    /**
     * 页码
     */
    private Integer pageNo=1;
}
