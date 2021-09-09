package com.xforceplus.wapp.modules.certification.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel认证模板导出
 * @author Colin.hu
 * @date 4/14/2018
 */
public class CheckTemplateExport extends AbstractExportExcel {

    @Override
    protected String getExcelUri() {
        return "export/certification/checkTemplate.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
