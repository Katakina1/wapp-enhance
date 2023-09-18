package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeReportRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaxCodeReportExportDto {
    private ExceptionReportTypeEnum type;
    private TaxCodeReportRequest request;

    private Long logId;

    private Long userId;


    /**
     *
     */
    private String loginName;
}
