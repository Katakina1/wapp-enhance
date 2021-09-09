package com.xforceplus.wapp.modules.cost.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CostTemplate extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/cost/costApplication.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
