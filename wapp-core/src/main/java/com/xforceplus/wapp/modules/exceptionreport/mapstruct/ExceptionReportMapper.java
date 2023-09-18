package com.xforceplus.wapp.modules.exceptionreport.mapstruct;

import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportStatusEnum;
import com.xforceplus.wapp.modules.exceptionreport.dto.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Objects;

/**
 * 例外报告类转换
 */
@Mapper(config = GlobalConfig.class)
public interface ExceptionReportMapper {

    TXfExceptionReportEntity toEntity(ExceptionReportRequest request);
    
    TXfExceptionReportEntity toEntity(ExceptionReportDto dto);

    @Mapping(target = "status", source = "entity", qualifiedByName = "claimStatusNumber")
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
    @Mapping(target = "createTime",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.dateToStrLong(entity.getCreateTime()))")
    @Mapping(target = "verdictDate",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.format(entity.getVerdictDate()))")
    @Mapping(target = "status", source = "entity", qualifiedByName = "claimStatus")
    @Mapping(target = "taxBalance",expression = "java(entity.getTaxBalance()!=null?entity.getTaxBalance().toPlainString():\"\")")
    ClaimReportExportDto toClaimExport(TXfExceptionReportEntity entity);

    List<ClaimReportExportDto> toClaimExport(List<TXfExceptionReportEntity> entity);

    @Mapping(target = "text",source = "description")
    ReportCodeResponse toReportCode(ExceptionReportCodeEnum codeEnums);

    List<ReportCodeResponse> toReportCode(List<ExceptionReportCodeEnum> codeEnums);

    @Named("claimStatus")
    default String status(TXfExceptionReportEntity entity){

        // 如果 S001/S005 状态=无需状态
        if (ExceptionReportCodeEnum.WITH_DIFF_TAX.getCode().equals(entity.getCode()) || ExceptionReportCodeEnum.CLAIM_DETAIL_ZERO_TAX_RATE.getCode().equals(entity.getCode())) {
            return "无需处理";
        }
        if (Objects.equals(entity.getStatus(),2)){
            return "已处理";
        }
        return "未处理";
    }
    @Named("claimStatusNumber")
    default Integer statusNumber(TXfExceptionReportEntity entity){

        // 如果 S001/S005 状态=无需状态
        if (ExceptionReportCodeEnum.WITH_DIFF_TAX.getCode().equals(entity.getCode()) || ExceptionReportCodeEnum.CLAIM_DETAIL_ZERO_TAX_RATE.getCode().equals(entity.getCode())) {
            return ExceptionReportStatusEnum.IGNORE.getType();
        }
        return entity.getStatus();
    }
}
