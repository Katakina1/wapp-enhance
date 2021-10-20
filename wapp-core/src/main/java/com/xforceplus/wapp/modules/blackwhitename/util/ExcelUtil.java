package com.xforceplus.wapp.modules.blackwhitename.util;

import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.NumberFormat;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Slf4j
public class ExcelUtil {
    /**
     * 获取工作簿对象
     *
     * @param file 导入的文件
     * @return 工作簿对象
     * @throws RRException 异常
     */
    public static Workbook getWorkBook(MultipartFile file) throws RRException {
        final Workbook workbook;
        try {
            if (endsWithIgnoreCase(file.getOriginalFilename(), Constants.EXCEL_XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (endsWithIgnoreCase(file.getOriginalFilename(), Constants.EXCEL_XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                throw new RRException("Excel读取错误，请导入.xls或.xlsx文件!");
            }
            return workbook;
        } catch (IOException e) {
            log.error("Excel读取错误:{}", e);
            throw new RRException("Excel读取错误!");
        }
    }

    /**
     * 获取excel单元格数据
     *
     * @param row     行
     * @param cellNum 列号
     * @return 单元格数据
     */
    public static String getCellData(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            return EMPTY;
        }
        int type = cell.getCellType();
        String returnValue = null;
        switch (type) {
            case Cell.CELL_TYPE_STRING:
                returnValue = trimAllWhitespace(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                returnValue = NumberFormat.getInstance().format(cell.getNumericCellValue()).replace(",", "");
                break;
            case Cell.CELL_TYPE_FORMULA:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                if (!cell.getStringCellValue().equals("")) {
                    returnValue = cell.getStringCellValue();
                } else {
                    returnValue = cell.getNumericCellValue() + "";
                }
                if ("#N/A".equals(returnValue)) {
                    returnValue = EMPTY;
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                returnValue = EMPTY;
                break;
            default:
                returnValue = EMPTY;
                break;
        }
        return returnValue;
    }
}
