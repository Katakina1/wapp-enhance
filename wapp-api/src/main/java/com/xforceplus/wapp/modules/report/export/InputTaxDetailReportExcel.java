package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map;

public final class InputTaxDetailReportExcel extends AbstractExportExcel {
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InputTaxDetailReportExcel(Map<String, Object> map, String excelTempPath, String excelName) {
        this.map = map;
        this.excelTempPath = excelTempPath;
        this.excelName = excelName;
    }

    @Override
    protected String getExcelUri() {
        return excelTempPath;
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);

        final String taxName = this.map.get("taxName").toString();
        final String rzhBelongDate = this.map.get("rzhBelongDate").toString();
        //数据填入excel
        setSheetValue(sheet, 1, 0, "税款所属期: "+rzhBelongDate.substring(0,4)+"年"+rzhBelongDate.substring(4,6)+"月");
        setSheetValue(sheet, 2, 0, "纳税人名称: "+taxName+"（公章）");
        setSheetValue(sheet, 4, 2, map.get("totalAmount").toString());
        setSheetValue(sheet, 4, 3, map.get("totalTax").toString());
        setSheetValue(sheet, 6, 2, map.get("amount17").toString());
        setSheetValue(sheet, 6, 3, map.get("tax17").toString());
        setSheetValue(sheet, 8, 2, map.get("amount13").toString());
        setSheetValue(sheet, 8, 3, map.get("tax13").toString());
        setSheetValue(sheet, 9, 2, map.get("amount11").toString());
        setSheetValue(sheet, 9, 3, map.get("tax11").toString());
        setSheetValue(sheet, 15, 2, map.get("amount6").toString());
        setSheetValue(sheet, 15, 3, map.get("tax6").toString());
        setSheetValue(sheet, 20, 2, map.get("amount5").toString());
        setSheetValue(sheet, 20, 3, map.get("tax5").toString());
        setSheetValue(sheet, 22, 2, map.get("amount3").toString());
        setSheetValue(sheet, 22, 3, map.get("tax3").toString());
    }
}
