package com.xforceplus.wapp.client;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class TaxCodeBean {
    private String taxCode;
    private String taxName;
    private String taxShortName;
    private String taxCodeVersion;
    private List<String> taxRateList;
    private String specialManagement;
    private String taxRate;
}
