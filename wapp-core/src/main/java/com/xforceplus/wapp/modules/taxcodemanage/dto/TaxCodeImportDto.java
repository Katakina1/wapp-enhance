package com.xforceplus.wapp.modules.taxcodemanage.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ExcelIgnoreUnannotated
public class TaxCodeImportDto {
    private int id;

    @ExcelProperty(value = "税号", index = 0)
    private String taxNo;

    @ExcelProperty(value = "公司", index = 1)
    private String taxName;

    @ExcelProperty(value = "省份", index = 2)
    private String province;

    @ExcelProperty(value = "备注", index = 3)
    private String taxRemark;

    private Date createTime;

}
