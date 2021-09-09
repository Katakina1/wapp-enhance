package com.xforceplus.wapp.common.utils;

import com.alibaba.excel.event.WriteHandler;
import org.apache.poi.ss.usermodel.*;

/**
 * @author Eric on 2019/4/5.
 * @version 1.0
 */
public class StyleExcelHandler implements WriteHandler {

    @Override
    public void sheet(int i, Sheet sheet) {
    }

    @Override
    public void row(int i, Row row) {
    }

    @Override
    public void cell(int i, Cell cell) {
        // 从第二行开始设置格式，第一行是表头
        Workbook workbook = cell.getSheet().getWorkbook();
        CellStyle cellStyle = createStyle(workbook);
        if (cell.getRowIndex() < 1) {
            cell.getRow().setHeightInPoints(30);
        }

    }

    /**
      * 实际中如果直接获取原单元格的样式进行修改, 最后发现是改了整行的样式, 因此这里是新建一个样* 式
      */
    private CellStyle createStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
//        // 下边框
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        // 左边框
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        // 上边框
//        cellStyle.setBorderTop(BorderStyle.THIN);
//        // 右边框
//        cellStyle.setBorderRight(BorderStyle.THIN);
//        // 水平对齐方式
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        // 垂直对齐方式
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }
}