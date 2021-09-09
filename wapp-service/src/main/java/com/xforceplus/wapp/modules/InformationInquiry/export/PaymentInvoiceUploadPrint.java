package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PaymentInvoiceUploadPrint extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/InformationInquiry/paymentInvoiceUploadExportList.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
