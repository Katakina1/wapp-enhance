package com.xforceplus.wapp.modules.blackwhitename.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author aiwentao@xforceplus.com
 */
@Data
public class SpecialCompanyImportSizeDto {


    private int importCount;

    private int validCDount;

    private int unValidCount;

    private String errorMsg;


}
