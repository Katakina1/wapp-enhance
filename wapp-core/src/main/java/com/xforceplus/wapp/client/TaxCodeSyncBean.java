package com.xforceplus.wapp.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mashaopeng@xforceplus.com
 */
@NoArgsConstructor
@Data
public class TaxCodeSyncBean {
    @JsonProperty("quantityUnit")
    private String quantityUnit = "";
    @JsonProperty("ext5")
    private String ext5 = "";
    @JsonProperty("itemSpec")
    private String itemSpec = "";
    @JsonProperty("itemName")
    private String itemName = "";
    @JsonProperty("taxConvertCode")
    private String taxConvertCode;
    @JsonProperty("goodsTaxNo")
    private String goodsTaxNo;
    @JsonProperty("taxPre")
    private String taxPre = "";
    @JsonProperty("taxPreCon")
    private String taxPreCon = "";
    @JsonProperty("taxRate")
    private String taxRate = "";
    @JsonProperty("zeroTax")
    private String zeroTax = "";
    @JsonProperty("tenantCode")
    private String tenantCode;
    @JsonProperty("itemCode")
    private String itemCode;
    @JsonProperty("standardItemName")
    private String standardItemName;
    @JsonProperty("tenantName")
    private String tenantName;
}
