package com.xforceplus.wapp.common.safesoft;

import com.google.common.io.Closer;
import com.google.common.io.Resources;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 导出excel工具类
 * @author Colin.hu
 * @date 4/13/2018
 */
public abstract class AbstractExportExcel {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExportExcel.class);
    private static final String ERROR_MESSAGE = "the java IO error:";

    /*
     * 准备导入模版
     */
    public void write(HttpServletResponse response) {
        final Closer closer = Closer.create();
        try {
            //将模板写入流
            final InputStream fs = closer.register(Resources.asByteSource(Resources.getResource(getExcelUri())).openStream());
            //创建工作簿
            final XSSFWorkbook workBook = new XSSFWorkbook(OPCPackage.open(fs));
            //创建excel
            buildExcel(workBook);
            response.setHeader("Content-Disposition", "attachment;filename=export.xlsx");
            //写出
            final OutputStream out = closer.register(response.getOutputStream());
            workBook.write(out);
        } catch (InvalidFormatException e) {
            LOGGER.info("invalid format:", e);
        } catch (FileNotFoundException e1) {
            LOGGER.info("the model xls can not find! error:", e1);
        } catch (IOException e1) {
            LOGGER.info(ERROR_MESSAGE, e1);
        }finally{
            try {
                closer.close();
            } catch (IOException e) {
                LOGGER.info(ERROR_MESSAGE, e);
            }
        }
    }

    protected abstract String getExcelUri();

    protected abstract void buildExcel(XSSFWorkbook workBook);

    /*
     * 设置样式
     */
    private void setSheetStyle(XSSFSheet sheet, int rowIndex, int cellIndex, XSSFCellStyle style) {
        //获得行
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            //不存在则创建行
            row = sheet.createRow(rowIndex);
        }
        //获得单元格
        XSSFCell cell = row.getCell(cellIndex);
        if (cell == null) {
            //不存在则创建单元格
            cell = row.createCell(cellIndex);
        }
        //设置单元格样式
        cell.setCellStyle(style);
    }

    /*
     * 设置单元格值
     */
    protected void setSheetValue(XSSFSheet sheet, int rowIndex, int cellIndex, String value) {
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        XSSFCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        cell.setCellValue(value);
    }

    /*
     * 设置单元格的样式及值
     */
    protected void setSheetValue(XSSFSheet sheet, int rowIndex, int cellIndex, String value, XSSFCellStyle style) {
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        XSSFCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        //设置值
        cell.setCellValue(value);
        //设置样式
        cell.setCellStyle(style);
    }

    /*
     * 设置单元格的样式及值
     */
    protected void setSheetValue(XSSFSheet sheet, int rowIndex, int cellIndex, int value, XSSFCellStyle style) {
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        XSSFCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /*
     * 设置单元格的样式及值
     */
    protected void setSheetValue(XSSFSheet sheet, int rowIndex, int cellIndex, double value, XSSFCellStyle style) {
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        XSSFCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /*
     * 设置单元格的样式及值
     */
    protected void setSheetValue(XSSFSheet sheet, int rowIndex, int cellIndex, double value) {
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        XSSFCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        cell.setCellValue(value);
    }

    /*
     * 获取值
     */
    protected Double getSheetValue(XSSFSheet sheet, int rowIndex, int cellIndex) {
        if (sheet.getRow(rowIndex) == null) {
            return null;
        } else {
            if (sheet.getRow(rowIndex).getCell(cellIndex) == null) {
                return null;
            } else {
                return sheet.getRow(rowIndex).getCell(cellIndex).getNumericCellValue();
            }
        }
    }

    /**
     * 合并单元格
     */
    protected void mergedRegion(XSSFSheet sheet, int rowFrom, int rowTo, int columnFrom, int columnTo, XSSFCellStyle cellStyle) {
        CellRangeAddress region = new CellRangeAddress(rowFrom, rowTo, columnFrom, columnTo);
        sheet.addMergedRegion(region);
        // 设置合并单元格的样式
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                setSheetStyle(sheet, i, j, cellStyle);
            }
        }
    }

    /*
     * 合并单元格
     */
    protected void mergedRegion(XSSFSheet sheet, int rowFrom, int rowTo, int columnFrom, int columnTo) {
        sheet.addMergedRegion(new CellRangeAddress(rowFrom, rowTo, columnFrom, columnTo));
    }

    /*
     * 获取样式
     */
    protected XSSFCellStyle getCellStyle(XSSFSheet sheet, int rowIndex, int cellIndex) {
        if (sheet.getRow(rowIndex) == null) {
            return null;
        } else {
            if (sheet.getRow(rowIndex).getCell(cellIndex) == null) {
                return null;
            } else {
                return sheet.getRow(rowIndex).getCell(cellIndex).getCellStyle();
            }
        }
    }

    /**
     * 文件导出，可自定义文件名
     * @param response 响应
     * @param excelName 文件名
     */
    public void write(HttpServletResponse response, String excelName) {
        final Closer closer = Closer.create();
        try {
            //获取文件流
            final InputStream fs = closer.register(Resources.asByteSource(Resources.getResource(getExcelUri())).openStream());
            //创建工作簿
            final XSSFWorkbook workBook = new XSSFWorkbook(OPCPackage.open(fs));
            //创建excel
            buildExcel(workBook);
            //设置响应
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(excelName.getBytes("UTF-8"), "ISO8859-1") + ".xlsx");
            //写出
            final OutputStream outputStream = closer.register(response.getOutputStream());
            workBook.write(outputStream);
        } catch (InvalidFormatException e) {
            LOGGER.info(" invalid format :", e);
        } catch (FileNotFoundException e) {
            LOGGER.info(" the model xls can not find! error:", e);
        } catch (IOException e) {
            LOGGER.info(ERROR_MESSAGE, e);
        } finally {
            try {
                closer.close();
            } catch (IOException e1) {
                LOGGER.info(ERROR_MESSAGE, e1);
            }
        }
    }
}