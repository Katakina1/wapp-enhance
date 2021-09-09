package com.xforceplus.wapp.modules.analysis.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.List;
import java.util.Map;

public final class DataInvoiceSubmitExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public DataInvoiceSubmitExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        //获取要导出的数据
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>) this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 2);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        int index = 1;
        int totalCount = 0;
        //数据填入excel
        for (ComprehensiveInvoiceQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商号码
            setSheetValue(sheet, beginLine, 1, entity.getVenderId(), style);
            //供应商名字
            setSheetValue(sheet, beginLine, 2, entity.getVenderName(), style);            
            //发票提交数量
            setSheetValue(sheet, beginLine, 3, entity.getCountNum(), style);
            
            beginLine++;
            totalCount += entity.getCountNum();
        }
        //合计
        setSheetValue(sheet, beginLine, 0,"合计", style);
        setSheetValue(sheet, beginLine, 1, StringUtils.EMPTY, style);
        setSheetValue(sheet, beginLine, 2, StringUtils.EMPTY, style);
        setSheetValue(sheet, beginLine, 3, totalCount, style);
    }

}
