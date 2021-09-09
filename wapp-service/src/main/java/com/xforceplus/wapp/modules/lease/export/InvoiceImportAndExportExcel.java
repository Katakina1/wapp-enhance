package com.xforceplus.wapp.modules.lease.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 订单信息导出
 */
public final class InvoiceImportAndExportExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceImportAndExportExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
            setSheetValue(sheet, beginLine, 0, entity.getId(), style);
            //序号
            setSheetValue(sheet, beginLine, 1, index++, style);
            setSheetValue(sheet, beginLine, 2, entity.getInvoiceType(), style);
            setSheetValue(sheet, beginLine, 3, entity.getCompanyCode(), style);
            setSheetValue(sheet, beginLine, 4, entity.getVenderId(), style);
            setSheetValue(sheet, beginLine, 5, entity.getVenderName(), style);
            setSheetValue(sheet, beginLine, 6, entity.getInvoiceCode(), style);
            setSheetValue(sheet, beginLine, 7, entity.getInvoiceNo(), style);
            setSheetValue(sheet, beginLine, 8, formatDate(entity.getInvoiceDate()), style);
            setSheetValue(sheet, beginLine, 9, fromAmount(entity.getInvoiceAmount()), style);
            setSheetValue(sheet, beginLine, 10, fromAmount(entity.getTaxAmount()), style);
            setSheetValue(sheet, beginLine, 11, fromAmounts(entity.getTaxRate()), style);
            setSheetValue(sheet, beginLine, 13, entity.getReMark(), style);
            setSheetValue(sheet, beginLine, 12, fromAmount(entity.getTotalAmount()), style);


            setSheetValue(sheet, beginLine, 14, entity.getShopNo(), style);
            setSheetValue(sheet, beginLine, 15, entity.getJvCode(), style);
            setSheetValue(sheet, beginLine, 16, entity.getPeRiod(), style);
            setSheetValue(sheet, beginLine, 17, formatedxhyMatchStatusType(entity.getMatChing()), style);
            setSheetValue(sheet, beginLine, 18, formatDate(entity.getMatChingDate()), style);



            beginLine++;
        }
    }


    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatedxhyMatchStatusType(String dxhyMatchStatus){
        String value="";
        if(dxhyMatchStatus==null || dxhyMatchStatus == ""){
            value= "未成功";
        }else if("0".equals(dxhyMatchStatus)){
            value="未成功";
        }else if("1".equals(dxhyMatchStatus)) {
            value = "成功";
        }
        return value;
    }

    private String fromAmount(Double d){
       BigDecimal b=new BigDecimal(d);
        DecimalFormat df=new DecimalFormat("######0.00");
        return df.format(d);
   }
    private String fromAmounts(Double d){
        BigDecimal b=new BigDecimal(d);
        DecimalFormat df=new DecimalFormat("######0");
        return df.format(d);
    }
}
