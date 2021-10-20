package com.xforceplus.wapp.modules.blackwhitename.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class SpeclialCompanyImportListener extends AnalysisEventListener<SpecialCompanyImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private StringBuilder builder;
    private final List<SpecialCompanyImportDto> validInvoices = Lists.newArrayList();
    private final List<SpecialCompanyImportDto> invalidInvoices = Lists.newArrayList();

    public SpeclialCompanyImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }


    @Override
    public void invoke(SpecialCompanyImportDto SpecialCompanyImportDto, AnalysisContext analysisContext) {
        validInvoices.add(SpecialCompanyImportDto);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

}
