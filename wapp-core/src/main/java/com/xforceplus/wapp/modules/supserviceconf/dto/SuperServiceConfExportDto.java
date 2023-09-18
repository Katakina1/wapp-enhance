package com.xforceplus.wapp.modules.supserviceconf.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class SuperServiceConfExportDto {

    @ExcelProperty("供应商号")
    @ColumnWidth(20)
    private String userCode;

    @ExcelProperty("供应商名称")
    @ColumnWidth(20)
    private String userName;

    @ExcelProperty("服务类型")
    private String serviceType;

    @ExcelProperty("生效日期")
    @ColumnWidth(20)
    private String assertDate;

    @ExcelProperty("失效日期")
    @ColumnWidth(20)
    private String expireDate;

    @ExcelProperty("更新日期")
    @ColumnWidth(20)
    private String updateDate;
}
