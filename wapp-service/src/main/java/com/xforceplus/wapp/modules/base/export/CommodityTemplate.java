package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CommodityTemplate extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/base/CommodityTemplate.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
