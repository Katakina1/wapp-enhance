package com.xforceplus.wapp.modules.blackwhitename.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    private final List<SpecialCompanyImportDto> validInvoices = Lists.newArrayList();
    private final List<SpecialCompanyImportDto> invalidInvoices = Lists.newArrayList();
    private String type;

    public SpeclialCompanyImportListener(String type) {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
        this.type=type;
    }


    @Override
    public void invoke(SpecialCompanyImportDto specialCompanyImportDto, AnalysisContext analysisContext) {
        if(StringUtils.isEmpty(checkData(specialCompanyImportDto))){
            validInvoices.add(specialCompanyImportDto);
        }else{
            specialCompanyImportDto.setErrorMessage(checkData(specialCompanyImportDto));
            invalidInvoices.add(specialCompanyImportDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
    public String checkData(SpecialCompanyImportDto SpecialCompanyImportDto){
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isEmpty(SpecialCompanyImportDto.getSapNo())){
            builder.append("供应商编号不能为空; ");
        }
        if(StringUtils.isEmpty(SpecialCompanyImportDto.getCompanyName())){
            builder.append("供应商名称不能为空; ");
        }
        if(StringUtils.isEmpty(SpecialCompanyImportDto.getSupplierTaxNo())){
            builder.append("供应商税号不能为空; ");
        }
        if(Constants.COMPANY_TYPE_WHITE.equals(type)&&StringUtils.isEmpty(SpecialCompanyImportDto.getSupplier6d())){
            builder.append("供应商6D编号不能为空; ");
        }
        return builder.toString();
    }

}
