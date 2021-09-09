package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map;

public final class InputTaxReportExcel extends AbstractExportExcel {
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InputTaxReportExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        //获取需要的数据
        final String totalAmount = this.map.get("totalAmount")==null ? "" : this.map.get("totalAmount").toString();
        final String totalTax = this.map.get("totalTax")==null ? "" : this.map.get("totalTax").toString();
        final String totalOutTax = this.map.get("totalOutTax")==null ? "" : this.map.get("totalOutTax").toString();
        final String outTax1 = this.map.get("outTax1")==null ? "" : this.map.get("outTax1").toString();
        final String outTax2 = this.map.get("outTax2")==null ? "" : this.map.get("outTax2").toString();
        final String outTax3 = this.map.get("outTax3")==null ? "" : this.map.get("outTax3").toString();
        final String outTax4 = this.map.get("outTax4")==null ? "" : this.map.get("outTax4").toString();
        final String outTax5 = this.map.get("outTax5")==null ? "" : this.map.get("outTax5").toString();
        final String outTax6 = this.map.get("outTax6")==null ? "" : this.map.get("outTax6").toString();
        final String outTax7 = this.map.get("outTax7")==null ? "" : this.map.get("outTax7").toString();
        final String outTax8 = this.map.get("outTax8")==null ? "" : this.map.get("outTax8").toString();
        final String outTax0 = this.map.get("outTax0")==null ? "" : this.map.get("outTax0").toString();
        final String taxName = this.map.get("taxName").toString();
        final String rzhBelongDate = this.map.get("rzhBelongDate").toString();
        //数据填入excel
        setSheetValue(sheet, 2, 0, "税款所属期: "+rzhBelongDate.substring(0,4)+"年"+rzhBelongDate.substring(4,6)+"月");
        setSheetValue(sheet, 3, 0, "纳税人名称: "+taxName+"（公章）");
        setSheetValue(sheet, 6, 3, totalAmount);
        setSheetValue(sheet, 6, 4, totalTax);
        setSheetValue(sheet, 20, 2, totalOutTax);
        setSheetValue(sheet, 21, 2, outTax1);
        setSheetValue(sheet, 22, 2, outTax2);
        setSheetValue(sheet, 23, 2, outTax3);
        setSheetValue(sheet, 24, 2, outTax4);
        setSheetValue(sheet, 25, 2, outTax5);
        setSheetValue(sheet, 26, 2, outTax6);
        setSheetValue(sheet, 27, 2, outTax7);
        setSheetValue(sheet, 28, 2, outTax8);
        setSheetValue(sheet, 30, 2, outTax0);
    }
}
