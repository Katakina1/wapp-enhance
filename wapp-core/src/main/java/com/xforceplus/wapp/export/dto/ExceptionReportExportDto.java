package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 11:30
 **/
@Setter
@Getter
public class ExceptionReportExportDto {
    private ExceptionReportTypeEnum type;
    private ExceptionReportRequest request;
}
