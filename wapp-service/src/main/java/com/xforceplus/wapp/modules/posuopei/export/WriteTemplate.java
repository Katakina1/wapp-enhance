package com.xforceplus.wapp.modules.posuopei.export;

import java.io.*;
import java.util.List;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.modules.posuopei.entity.ErrorDataEntity;
import com.xforceplus.wapp.modules.posuopei.entity.ExcelExceptionEntity;
import com.google.common.collect.Lists;
import com.google.common.io.Closer;
import com.google.common.io.Resources;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.util.StringUtils.endsWithIgnoreCase;

public class WriteTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteTemplate.class);
    //当前文件已经存在
//    private String excelPath ;
    private static final String ERROR_MESSAGE = "the java IO error:";

    //在当前工作薄的那个工作表单中插入这行数据
    private String sheetName;

    public WriteTemplate( String sheetName) {
//        this.excelPath = excelPath;
        this.sheetName = sheetName;
    }

    /**
     * 总的入口方法
     */
    public static void test(MultipartFile file,HttpServletResponse response) {
        List<ExcelExceptionEntity> list=Lists.newArrayList();
        List<ErrorDataEntity> errorDataEntities=Lists.newArrayList();
        List<ErrorDataEntity> errorDataEntities1=Lists.newArrayList();
        ErrorDataEntity errorDataEntity=new ErrorDataEntity("0000","测试插入数据",15);
        ErrorDataEntity errorDataEntity1=new ErrorDataEntity("0000","测试插入数据",15);
        errorDataEntities.add(errorDataEntity);
        errorDataEntities.add(errorDataEntity1);
        ErrorDataEntity errorDataEntity2=new ErrorDataEntity("0000","测试插入数据",15);
        errorDataEntities1.add(errorDataEntity2);
        ExcelExceptionEntity excelExceptionEntity=new ExcelExceptionEntity(2-1,errorDataEntities);
        ExcelExceptionEntity excelExceptionEntity1=new ExcelExceptionEntity(4-1,errorDataEntities1);
        list.add(excelExceptionEntity);
        list.add(excelExceptionEntity1);
        WriteTemplate crt = new WriteTemplate("Sheet1");

        try {
            crt.insertRows(list,file,response);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
    }
    /**
     * 在已有的Excel文件中插入一行新的数据的入口方法
     */
    protected void insertRows(List<ExcelExceptionEntity> list,MultipartFile file,HttpServletResponse response) throws ExcelException {
        XSSFWorkbook wb = returnWorkBookGivenFileHandle(file);
        XSSFSheet sheet1 = wb.getSheet(sheetName);
        // 设置字体
        CellStyle redStyle = wb.createCellStyle();
        XSSFFont redFont = wb.createFont();
        //颜色
        redFont.setColor(Font.COLOR_RED);
        redStyle.setFont(redFont);

        list.forEach(excelExceptionEntity -> {
            //选中要插入的行
            XSSFRow row = sheet1.getRow(excelExceptionEntity.getRow());

            StringBuffer message=new StringBuffer();
            excelExceptionEntity.getErrorDataLists().forEach(errorDataEntity -> {
               message.append("(");
               message.append(errorDataEntity.getMessage());
               message.append(")");
            });
            //创建要插入的行中单元格
            if(row!=null){
                createCell(row,message.toString(),redStyle);
            }

        });

        writeXlsm(response,wb,sheetName);

    }
//    /**
//     * 保存工作薄
//     * @param wb
//     */
//    private void saveExcel(XSSFWorkbook wb) {
//        FileOutputStream fileOut;
//        try {
//            fileOut = new FileOutputStream(excelPath);
//            wb.write(fileOut);
//            fileOut.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
    /**
     * 创建要插入的行中单元格
     * @param row
     * @return
     */
    private XSSFCell createCell(XSSFRow row,String message,CellStyle redStyle) {
        XSSFCell cell = row.createCell(14);
        cell.setCellStyle(redStyle);
        cell.setCellValue(message);


        return cell;
    }
    /**
     * 得到一个已有的工作薄的POI对象
     * @return
     */
    private XSSFWorkbook returnWorkBookGivenFileHandle(MultipartFile file) throws ExcelException {
        XSSFWorkbook wb  = (XSSFWorkbook)getWorkBook(file);;

        return wb;
    }
    /**
     * 找到需要插入的行数，并新建一个POI的row对象
     * @param sheet
     * @param rowIndex
     * @return
     */
    private XSSFRow createRow(XSSFSheet sheet, Integer rowIndex) {
        XSSFRow row = null;
        if (sheet.getRow(rowIndex) != null) {
            int lastRowNo = sheet.getLastRowNum();
            sheet.shiftRows(rowIndex, lastRowNo, 1);
        }
        row = sheet.createRow(rowIndex);
        return row;
    }
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
//            LOGGER.error("读取Excel文件失败:{}", e);
            throw new ExcelException(ExcelException.READ_ERROR, "读取Excel文件失败");
        }
    }

    /**
     * 文件导出，可自定义文件名
     * @param response 响应
     * @param excelName 文件名
     */
    public void writeXlsm(HttpServletResponse response,XSSFWorkbook workBook,String excelName) {
        final Closer closer = Closer.create();
        try {
            //设置响应
            response.setContentType("application/excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(excelName.getBytes("UTF-8"), "ISO8859-1") + ".xlsx");
            response.setHeader("responseType","blob");
            //写出
            final OutputStream outputStream = closer.register(response.getOutputStream());
            workBook.write(outputStream);
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
