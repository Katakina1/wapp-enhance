package com.xforceplus.wapp.common.safesoft;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;

/**
 * excel导入抽象类
 *
 * @author Colin.hu
 * @date 04/19/2018
 */
public abstract class AbstractImportExcel {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractImportExcel.class);

    //根据文件名获取模板类
    protected Workbook getWorkBook(MultipartFile file) throws ExcelException {
        final Workbook workbook;
        try {
            if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }else if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLSM)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            else {
                throw new ExcelException(ExcelException.READ_ERROR, "读取Excel文件失败");
            }
            return workbook;
        } catch (IOException e) {
            LOGGER.error("读取Excel文件失败:{}", e);
            throw new ExcelException(ExcelException.READ_ERROR, "读取Excel文件失败");
        }
    }


    //通过excel一格数据，获取对象单个属性
    protected String getCellData(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            return EMPTY;
        }
        int type = cell.getCellType();
        String returnValue = null;
        switch (type) {
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    returnValue = new DateTime(date).toString( "yyyy-MM-dd");
                } else {
                    Double value = cell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("0.###");
                    returnValue = df.format(value);
                }
                break;
            case Cell.CELL_TYPE_STRING:
                returnValue = trimAllWhitespace(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                Double val = cell.getNumericCellValue();
                DecimalFormat dfm = new DecimalFormat("0.00");
                returnValue = dfm.format(val);
                break;
            case Cell.CELL_TYPE_BLANK:
                returnValue = EMPTY;
                break;
            default:
                LOGGER.error("read excel is error!");
                break;
        }
        return returnValue;
    }

    protected Boolean isRowEmpty(Row row) {
        if (row != null) {
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                Cell cell = row.getCell(c);
                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

}