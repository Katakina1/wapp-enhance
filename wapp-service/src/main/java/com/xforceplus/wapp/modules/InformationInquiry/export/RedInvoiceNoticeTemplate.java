package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by 1 on 2018/12/4 10:06
 */
public class RedInvoiceNoticeTemplate   extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/redTicket/noticeImport.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}