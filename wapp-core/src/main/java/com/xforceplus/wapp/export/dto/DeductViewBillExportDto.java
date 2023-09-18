package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;

import lombok.Data;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class DeductViewBillExportDto {

    private TXfDeductionBusinessTypeEnum type;
    private DeductListRequest request;
    private Long logId;
    private Long userId;


    /**
     *
     */
    private String loginName;
}
