package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.dto.DeductExportRequest;
import lombok.Data;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class DeductBillExportDto {

    private TXfDeductionBusinessTypeEnum type;
    private DeductExportRequest request;
    private Long logId;
    private Long userId;
    private String loginName;
}
