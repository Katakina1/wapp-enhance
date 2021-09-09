package com.xforceplus.wapp.modules.cost.importTemplate;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class CostTypeTemplate extends AbstractExportExcel {
    @Override
    protected String getExcelUri() {
        return "export/cost/costtype.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
