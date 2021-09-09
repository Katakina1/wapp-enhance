package com.xforceplus.wapp.common.safesoft;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.io.Resources;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;

import static com.google.common.io.Files.asByteSource;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;


/**
 * Created by vito.xing on 2018/4/14
 */
public abstract class AbstractImportExcelController extends AbstractController{
    private static final Logger LOGGER = getLogger(AbstractImportExcelController.class);

    /**
     * 获取excel单元格数据
     * @param row 行
     * @param cellNum 列号
     * @return 单元格数据
     */
    protected static String getCellData(Row row, int cellNum) {
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
                returnValue = NumberFormat.getInstance().format(cell.getNumericCellValue()).replace(",","");
                break;
            case Cell.CELL_TYPE_FORMULA:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                if (!cell.getStringCellValue().equals("")) {
                    returnValue = cell.getStringCellValue();
                } else {
                    returnValue = cell.getNumericCellValue() + "";
                }
                if("#N/A".equals(returnValue)){
                    returnValue = EMPTY;
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                returnValue = EMPTY;
                break;
            default:
                LOGGER.error("Excel读取错误!");
                break;
        }
        return returnValue;
    }

    /**
     * 获取工作簿对象
     * @param file 导入的文件
     * @return 工作簿对象
     * @throws ExcelException 异常
     */
    protected static Workbook getWorkBook(MultipartFile file) throws ExcelException {
        final Workbook workbook;
        try {
            if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                throw new ExcelException(ExcelException.READ_ERROR, "Excel读取错误，请导入.xls或.xlsx文件!");
            }
            return workbook;
        } catch (IOException e) {
            LOGGER.error("Excel读取错误:{}", e);
            throw new ExcelException(ExcelException.READ_ERROR, "Excel读取错误!");
        }
    }

    /**
     * 下载模板
     * @param response 响应体
     * @param url 模板地址
     */
    protected static void exportTemplate(HttpServletResponse response,String url) {
        final URL u = Resources.getResource(url);
        try{
            final File file = new File(u.getPath());

            final byte[] fileByte = asByteSource(file).read();
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setContentLength((int)file.length());
            response.getOutputStream().write(fileByte);

        } catch (IOException e) {
            LOGGER.error("业务处理异常:",e);
        }
    }


}
