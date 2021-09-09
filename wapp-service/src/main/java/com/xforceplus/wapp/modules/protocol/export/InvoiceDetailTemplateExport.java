package com.xforceplus.wapp.modules.protocol.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class InvoiceDetailTemplateExport extends AbstractExportExcel {

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    public InvoiceDetailTemplateExport(String excelTempPath) {
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
