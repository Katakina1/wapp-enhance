package com.xforceplus.wapp.modules.weekdays.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @author aiwentao@xforceplus.com
 */
@Data
@ApiModel("工作日导入配置")
public class WeekDaysImportDto {

    @ExcelProperty(value = "工作日信息", index = 0)
    private Date weekdays;

    @ExcelProperty(value = "备注", index = 1)
    private String weekdaysRemark;

    @ExcelProperty( index = 2)
    private String errorMessage;


}
