package com.xforceplus.wapp.modules.fixed.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

public final class InvoiceImportExportExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceImportExportExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<InvoiceImportAndExportEntity> list = (List<InvoiceImportAndExportEntity>)this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 1);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel

        for (InvoiceImportAndExportEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            setSheetValue(sheet, beginLine, 1, entity.getJvCode(), style);
            setSheetValue(sheet, beginLine, 2, entity.getCompanyCode(), style);
            setSheetValue(sheet, beginLine, 3, entity.getVenderId(), style);
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceCode(), style);
            setSheetValue(sheet, beginLine, 5, entity.getInvoiceNo(), style);
            setSheetValue(sheet, beginLine, 6, formatDate(entity.getInvoiceDate()), style);
            setSheetValue(sheet, beginLine, 7, bigFomtent(entity.getInvoiceAmount()+""), style);
            setSheetValue(sheet, beginLine, 8, bigFomtent(entity.getTaxAmount()+""), style);
            setSheetValue(sheet, beginLine, 11, entity.getTaxRate(), style);
            setSheetValue(sheet, beginLine, 12, entity.getOrders(), style);
            beginLine++;
        }
    }
    private static  String bigFomtent(String stringValue){
        if(StringUtils.isNotBlank(stringValue)){
            try{
                return new BigDecimal(stringValue).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
            }catch (Exception e){
                return "";
            }
        }else{
            return "";
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

}
