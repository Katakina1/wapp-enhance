//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.excel.util;

import org.apache.poi.ss.usermodel.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class StyleUtil {
    public StyleUtil() {
    }

    public static CellStyle buildDefaultCellStyle(Workbook workbook) {
        CellStyle newCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)14);
//        font.setBold(true);
        newCellStyle.setFont(font);
        newCellStyle.setWrapText(true);
//        newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        newCellStyle.setAlignment(HorizontalAlignment.CENTER);
        newCellStyle.setLocked(true);
//        newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        newCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//        newCellStyle.setBorderBottom(BorderStyle.THIN);
//        newCellStyle.setBorderLeft(BorderStyle.THIN);
        return newCellStyle;
    }

    public static CellStyle buildCellStyle(Workbook workbook, com.alibaba.excel.metadata.Font f, IndexedColors indexedColors) {
        CellStyle cellStyle = buildDefaultCellStyle(workbook);
        if (f != null) {
            Font font = workbook.createFont();
            font.setFontName(f.getFontName());
            font.setFontHeightInPoints(f.getFontHeightInPoints());
//            font.setBold(f.isBold());
            cellStyle.setFont(font);
        }

        if (indexedColors != null) {
            cellStyle.setFillForegroundColor(indexedColors.getIndex());
        }

        return cellStyle;
    }

    public static Sheet buildSheetStyle(Sheet currentSheet, Map<Integer, Integer> sheetWidthMap) {
        currentSheet.setDefaultColumnWidth(20);
        Iterator var2 = sheetWidthMap.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<Integer, Integer> entry = (Entry)var2.next();
            currentSheet.setColumnWidth((Integer)entry.getKey(), (Integer)entry.getValue());
        }

        return currentSheet;
    }
}
