package com.xforceplus.wapp.modules.scanRefund.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RefundTemplateExcel extends AbstractExportExcel {

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    public RefundTemplateExcel(String excelTempPath) {
        this.excelTempPath = excelTempPath;
    }

    @Override
    protected String getExcelUri() {
        return excelTempPath;
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {

    }
}
