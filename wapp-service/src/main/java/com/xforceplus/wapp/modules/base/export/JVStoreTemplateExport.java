package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JVStoreTemplateExport extends AbstractExportExcel {

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    public JVStoreTemplateExport(String excelTempPath) {
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
