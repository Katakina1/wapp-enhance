package com.xforceplus.wapp.modules.exceptionreport.mapstruct;

import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.modules.exceptionreport.dto.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 例外报告类转换
 */
@Mapper(config = GlobalConfig.class)
public interface ExceptionReportMapper {

    TXfExceptionReportEntity toEntity(ExceptionReportRequest request);

    ExceptionReportDto toDto(TXfExceptionReportEntity entity);

    List<ExceptionReportDto> toDto(List<TXfExceptionReportEntity> entity);

    @Mapping(source = "agreementReasonCode",target = "agreementTypeCode")
    @Mapping(source = "agreementTaxCode",target = "taxCode")
    TXfExceptionReportEntity deductToReport(TXfBillDeductEntity entity);


    @Mapping(target = "deductDate",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.format(entity.getDeductDate()))")
    ReportExportDto toExport(TXfExceptionReportEntity entity);

    List<ReportExportDto> toExport(List<TXfExceptionReportEntity> entity);

    @Mapping(target = "deductDate",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.format(entity.getDeductDate()))")
    AgreementReportExportDto toAgreementExport(TXfExceptionReportEntity entity);

    List<AgreementReportExportDto> toAgreementExport(List<TXfExceptionReportEntity> entity);

    @Mapping(target = "deductDate",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.format(entity.getDeductDate()))")
    @Mapping(target = "verdictDate",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.format(entity.getVerdictDate()))")
    ClaimReportExportDto toClaimExport(TXfExceptionReportEntity entity);

    List<ClaimReportExportDto> toClaimExport(List<TXfExceptionReportEntity> entity);

    @Mapping(target = "text",source = "description")
    ReportCodeResponse toReportCode(ExceptionReportCodeEnum codeEnums);

    List<ReportCodeResponse> toReportCode(List<ExceptionReportCodeEnum> codeEnums);
}
