package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.deduct.dto.DeductExportRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import lombok.Data;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class DeductBillExportDto {

    private XFDeductionBusinessTypeEnum type;
    private DeductExportRequest request;
    private Long logId;
    private Long userId;


    /**
     *
     */
    private String loginName;
}
