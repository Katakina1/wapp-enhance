package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/18
 * 发票签收失败统计导出
 */
public class ReceiptInvoiceFailStatisticsExportExcel extends AbstractExportExcel {

    private String FONT_NAME = "宋体";
    private String DATE_FORMAT = "yyyy-MM-dd";
    private Map<String, Object> map;

    public ReceiptInvoiceFailStatisticsExportExcel(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    protected String getExcelUri() {
        return "export/report/receiptInvoiceFailStatistics.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>) this.map.get("receiptInvoiceFailStatistics");
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 3);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName(FONT_NAME);
        style.setFont(font);
        //数据填入excel
        for (ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, beginLine - 1, style);
            //签收日期
            setSheetValue(sheet, beginLine, 1, formatDate(comprehensiveInvoiceQueryEntity.getQsDate()), style);
            //发票代码
            setSheetValue(sheet, beginLine, 2, comprehensiveInvoiceQueryEntity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 3, comprehensiveInvoiceQueryEntity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 4, comprehensiveInvoiceQueryEntity.getInvoiceDate(), style);
            //购方税号
            setSheetValue(sheet, beginLine, 5, comprehensiveInvoiceQueryEntity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 6, comprehensiveInvoiceQueryEntity.getGfName(), style);
            //销方税号
            setSheetValue(sheet, beginLine, 7, comprehensiveInvoiceQueryEntity.getXfTaxNo(), style);
            //金额
            if (comprehensiveInvoiceQueryEntity.getInvoiceAmount() == null) {
                setSheetValue(sheet, beginLine, 8, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 8, comprehensiveInvoiceQueryEntity.getInvoiceAmount(), style);
            }
            //税额
            if (comprehensiveInvoiceQueryEntity.getTaxAmount() == null) {
                setSheetValue(sheet, beginLine, 9, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 9, comprehensiveInvoiceQueryEntity.getTaxAmount(), style);
            }

            beginLine++;
        }
        setSheetValue(sheet, beginLine, 1,"合计", style);
        setSheetValue(sheet, beginLine, 8,this.map.get("totalAmount").toString(), style);
        setSheetValue(sheet, beginLine, 9,this.map.get("totalTax").toString(), style);
    }

    private String formatDate(Date source) {
        return source == null ? "" : new SimpleDateFormat(DATE_FORMAT).format(source);
    }
}

