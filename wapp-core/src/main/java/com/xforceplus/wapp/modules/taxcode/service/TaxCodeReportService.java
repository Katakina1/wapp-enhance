package com.xforceplus.wapp.modules.taxcode.service;

import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.export.dto.TaxCodeReportExportDto;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeReportDto;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeReportRequest;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeReportEntity;

import java.util.List;

public interface TaxCodeReportService {

    PageResult<TXfTaxCodeReportEntity> getPage(TaxCodeReportRequest request);

    boolean update(String status, TaxCodeReportRequest request);

    void export(TaxCodeReportRequest request, ExceptionReportTypeEnum typeEnum);

    void doExport(TaxCodeReportExportDto exportDto);

    List<TaxCodeReportDto> toExportDto(List<TXfTaxCodeReportEntity> entity);

}
