package com.xforceplus.wapp.modules.company.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author aiwentao@xforceplus.com
 */
@Data
@ApiModel("抬头导入配置")
public class CompanyImportDto {

    @ExcelProperty(value = "供应商6D编码", index = 0)
    private String supplierCode;

    @ExcelProperty(value = "供应商名称", index = 1)
    private String supplierName;

    @ExcelProperty(value = "供应商税号", index = 2)
    private String supplierTaxNo;


    @ExcelProperty(value = "联系人", index = 3)
    private String linkMan;


    @ExcelProperty(value = "电话号码", index = 4)
    private String telePhoneNo;

    @ExcelProperty(value = "地址", index = 5)
    private String address;

    @ExcelProperty(value = "邮箱地址", index = 6)
    private String emaiAdress;

    @ExcelProperty(value = "开户行名称", index = 7)
    private String bank;

    @ExcelProperty(value = "开户行账号", index = 8)
    private String account;
    @ExcelProperty(value = "开票限额", index = 9)
    private Double quota;
    @ExcelProperty(value = "类型 5:沃尔玛 8：供应商", index = 10)
    private String orgType;
}
