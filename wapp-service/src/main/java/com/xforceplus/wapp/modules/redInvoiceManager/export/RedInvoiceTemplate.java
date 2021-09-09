package com.xforceplus.wapp.modules.redInvoiceManager.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RedInvoiceTemplate extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/redInvoice/RedInvoiceTemplate.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
