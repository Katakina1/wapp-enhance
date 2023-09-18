package com.xforceplus.wapp.modules.deduct.dto;

import lombok.Data;

/**
 * @author aiwentao@xforceplus.com
 */
@Data
public class ImportResponse {
    private int importCount;
    private int passCount;
    private int failCount;
    private String errorMsg;
}
