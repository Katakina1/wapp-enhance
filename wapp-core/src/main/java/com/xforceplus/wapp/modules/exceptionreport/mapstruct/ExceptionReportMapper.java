package com.xforceplus.wapp.modules.exceptionreport.mapstruct;

import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.mapstruct.Mapper;

/**
 * 例外报告类转换
 */
@Mapper(config = GlobalConfig.class)
public interface ExceptionReportMapper {

    TXfExceptionReportEntity toEntity(ExceptionReportRequest request);
}