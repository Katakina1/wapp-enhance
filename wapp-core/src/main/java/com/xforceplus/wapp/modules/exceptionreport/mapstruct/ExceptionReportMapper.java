package com.xforceplus.wapp.modules.exceptionreport.mapstruct;

import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportDto;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ReportExportDto;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 例外报告类转换
 */
@Mapper(config = GlobalConfig.class)
public interface ExceptionReportMapper {

    TXfExceptionReportEntity toEntity(ExceptionReportRequest request);

    ExceptionReportDto toDto(TXfExceptionReportEntity entity);

    List<ExceptionReportDto> toDto(List<TXfExceptionReportEntity> entity);

    TXfExceptionReportEntity deductToReport(TXfBillDeductEntity entity);


    ReportExportDto toExport(TXfExceptionReportEntity entity);

    List<ReportExportDto> toExport(List<TXfExceptionReportEntity> entity);
}
