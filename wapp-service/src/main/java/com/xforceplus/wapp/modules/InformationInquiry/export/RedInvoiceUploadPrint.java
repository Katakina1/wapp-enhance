package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RedInvoiceUploadPrint extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/InformationInquiry/redInvoiceUploadExportList.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
