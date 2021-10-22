package com.xforceplus.wapp.modules.company.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.blackwhitename.util.RegUtils;
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
        if(StringUtils.isEmpty(checkData(companyImportDto))){
            validInvoices.add(companyImportDto);
        }else{
            invalidInvoices.add(companyImportDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public String checkData(CompanyImportDto companyImportDto){
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isEmpty(companyImportDto.getSupplierCode())){
            builder.append("供应商6D编码/沃尔玛公司代码不能为空 ,");
        }
        if(StringUtils.isEmpty(companyImportDto.getSupplierName())){
            builder.append("公司名称不能为空 ,");
        }

        if(StringUtils.isEmpty(companyImportDto.getSupplierTaxNo())){
            builder.append("供应商6D编码/沃尔玛公司代码不能为空 ,");
        }
        if(StringUtils.isEmpty(companyImportDto.getTelePhoneNo())){
            builder.append("电话不能为空 ,");
            if(RegUtils.isMoblie(companyImportDto.getTelePhoneNo())){
                builder.append("电话格式错误 ,");
            }
        }
        if(StringUtils.isEmpty(companyImportDto.getAddress())){
            builder.append("地址不能为空 ,");
        }
        if(StringUtils.isEmpty(companyImportDto.getEmaiAdress())){
            if(RegUtils.isEmail(companyImportDto.getEmaiAdress())){
                builder.append("邮箱格式错误 ,");
            }
        }
        if(StringUtils.isEmpty(companyImportDto.getBank())){
            builder.append("开户银行不能为空 ,");
        }
        if(null==companyImportDto.getQuota()){
            builder.append("开票限额不能为空 ,");
        }
        if(StringUtils.isEmpty(companyImportDto.getOrgType())){
            builder.append("类型不能为空 ,");
            if(!companyImportDto.getOrgType().equals(Constants.COMPANY_TYPE_SUPPLIER)&&!companyImportDto.getOrgType().equals(Constants.COMPANY_TYPE_SUPPLIER)){
                builder.append("类型填写错误，只能为5/8 ,");
            }
        }
        return builder.toString();
    }
}
