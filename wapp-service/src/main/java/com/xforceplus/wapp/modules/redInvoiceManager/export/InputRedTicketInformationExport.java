package com.xforceplus.wapp.modules.redInvoiceManager.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel认证模板导出
 * @author Colin.hu
 * @date 4/14/2018
 */
public class InputRedTicketInformationExport extends AbstractExportExcel {

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    public InputRedTicketInformationExport(String excelTempPath) {
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
