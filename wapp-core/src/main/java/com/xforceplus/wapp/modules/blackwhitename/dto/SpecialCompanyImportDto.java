package com.xforceplus.wapp.modules.blackwhitename.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author aiwentao@xforceplus.com
 */
@Data
@ApiModel("黑白名单导入配置")
public class SpecialCompanyImportDto {


    @ExcelProperty(value = "供应商编号", index = 0)
    private String sapNo;


    @ExcelProperty(value = "供应商名称", index = 1)
    private String companyName;


    @ExcelProperty(value = "供应商税号", index = 2)
    private String supplierTaxNo;

    @ExcelProperty(value = "供应商6D编号", index = 3)
    private String supplier6d;

    @ExcelProperty(index = 4)
    private String errorMessage;


}
