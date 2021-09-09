package com.xforceplus.wapp.modules.scanRefund.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EnterPackageNumberPrint extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/scanRefund/EnterPackageNumberList.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
