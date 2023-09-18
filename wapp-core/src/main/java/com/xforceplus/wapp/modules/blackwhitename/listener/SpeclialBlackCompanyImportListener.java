package com.xforceplus.wapp.modules.blackwhitename.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyBlackImportDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class SpeclialBlackCompanyImportListener extends AnalysisEventListener<SpecialCompanyBlackImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<SpecialCompanyBlackImportDto> validInvoices = Lists.newArrayList();
    private final List<SpecialCompanyBlackImportDto> invalidInvoices = Lists.newArrayList();
    private String type;

    public SpeclialBlackCompanyImportListener(String type) {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
        this.type=type;
    }


    @Override
    public void invoke(SpecialCompanyBlackImportDto SpecialCompanyBlackImportDto, AnalysisContext analysisContext) {
        rows++;
        if(StringUtils.isEmpty(checkData(SpecialCompanyBlackImportDto))){
            validInvoices.add(SpecialCompanyBlackImportDto);
        }else{
            SpecialCompanyBlackImportDto.setErrorMessage(checkData(SpecialCompanyBlackImportDto));
            invalidInvoices.add(SpecialCompanyBlackImportDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
    public String checkData(SpecialCompanyBlackImportDto SpecialCompanyBlackImportDto){
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isEmpty(SpecialCompanyBlackImportDto.getSupplier6d())){
            builder.append("供应商6D不能为空; ");
        }
        if(StringUtils.isEmpty(SpecialCompanyBlackImportDto.getCompanyName())){
            builder.append("供应商名称不能为空; ");
        }
        if(StringUtils.isEmpty(SpecialCompanyBlackImportDto.getSupplierTaxNo())){
            builder.append("供应商税号不能为空; ");
        }
        return builder.toString();
    }

}
