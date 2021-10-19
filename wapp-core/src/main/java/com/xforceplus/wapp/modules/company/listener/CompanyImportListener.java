package com.xforceplus.wapp.modules.company.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.company.dto.CompanyImportDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class CompanyImportListener extends AnalysisEventListener<CompanyImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private StringBuilder builder;
    private final List<CompanyImportDto> validInvoices = Lists.newArrayList();
    private final List<CompanyImportDto> invalidInvoices = Lists.newArrayList();

    public CompanyImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }


    @Override
    public void invoke(CompanyImportDto companyImportDto, AnalysisContext analysisContext) {
        validInvoices.add(companyImportDto);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public boolean checkData(CompanyImportDto companyImportDto){
        if(StringUtils.isEmpty(companyImportDto.getSupplierTaxNo())){
            return false;
        }

        if(StringUtils.isEmpty(companyImportDto.getSupplierCode())){
            return false;
        }

        if(StringUtils.isEmpty(companyImportDto.getSupplierTaxNo())){
            return false;
        }

        if(StringUtils.isEmpty(companyImportDto.getSupplierName())){
            return false;
        }

        if(StringUtils.isEmpty(companyImportDto.getAccount())){
            return false;
        }

        if(StringUtils.isEmpty(companyImportDto.getBank())){
            return false;
        }

        if(StringUtils.isEmpty(companyImportDto.getOrgType())){
            return false;
        }
        return true;
    }
}
