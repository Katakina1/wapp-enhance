package com.xforceplus.wapp.modules.deduct.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class ManagerSellerVO {
    @ExcelProperty(value = "供应商名称", index = 0)
    private String sellerName;
    @ExcelProperty(value = "供应商编号", index = 1)
    private String sellerNo;
    private String lockFlag;

    @ExcelProperty(index = 2)
    private String errorMessage;
}
