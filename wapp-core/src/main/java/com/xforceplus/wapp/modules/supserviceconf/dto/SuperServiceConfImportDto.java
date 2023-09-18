package com.xforceplus.wapp.modules.supserviceconf.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.xforceplus.wapp.modules.supserviceconf.convert.SuperServiceTypeConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("供应商服务导入配置")
public class SuperServiceConfImportDto {

    @ExcelProperty(value = "供应商号", index = 0)
    @ColumnWidth(20)
    private String userCode;

    @ExcelProperty(value = "服务类型", index = 1/*,converter = SuperServiceTypeConverter.class*/)
    private String serviceType;

    @ExcelProperty(value = "生效日期yyyy-mm-dd", index = 2)
    @ColumnWidth(20)
    private String assertDate;

    @ExcelProperty(value = "失效日期yyyy-mm-dd", index = 3)
    @ColumnWidth(20)
    private String expireDate;

    @ExcelProperty(value = "错误信息", index = 4)
    @ColumnWidth(50)
    private String errorMsg;
}
