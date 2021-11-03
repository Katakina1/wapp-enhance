package com.xforceplus.wapp.modules.backFill.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.overdue.valid.OverdueCreateValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * Created by SunShiyong on 2021/10/16.
 */

@Data
@ApiModel("回填发票")
public class BackfillInvoice {

    @ExcelProperty(value = "发票代码")
    private String invoiceCode = null;

    @ExcelProperty(value = "发票号码")
    private String invoiceNo = null;

    @ExcelProperty(value = "开票日期")
    private String paperDrewDate = null;

    @ExcelProperty(value = "不含税金额")
    private String amount = null;

    @ExcelProperty(value = "校验码（后6位）")
    private String checkCode = null;
}
