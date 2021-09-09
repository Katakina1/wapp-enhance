package com.xforceplus.wapp.modules.check.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CheckTemplate extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/check/checkTemplate.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
