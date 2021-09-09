package com.xforceplus.wapp.modules.signin.toexcel;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.apache.http.client.utils.DateUtils.formatDate;

/**
 * CreateBy leal.liang on 2018/4/14.
 **/
public class ReceiptInvoiceExcel extends AbstractExportExcel {

    private Map<String, List<RecordInvoiceEntity>> map;
    private String listName;

    public ReceiptInvoiceExcel(Map<String, List<RecordInvoiceEntity>> map, String listName) {
        this.map = map;
        this.listName = listName;
    }


    @Override
    protected String getExcelUri() {
        return "export/signin/RecordInvoice.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<RecordInvoiceEntity> list = this.map.get("InvoiceEntityList");

        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 1, 1);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");

        setSheetValue(sheet, 0, 0, listName);
        //数据填入excel
        for (RecordInvoiceEntity recordInvoiceEntity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, String.valueOf(beginLine-1), style);
            //发票代码
            setSheetValue(sheet, beginLine, 1, recordInvoiceEntity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 2, recordInvoiceEntity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 3, dateFormat2.format(recordInvoiceEntity.getInvoiceDate()), style);
            //购方名称
            setSheetValue(sheet, beginLine, 4, recordInvoiceEntity.getGfName(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 5,recordInvoiceEntity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 6, formatBigDecimal(recordInvoiceEntity.getInvoiceAmount()), style);
            //税额
            setSheetValue(sheet, beginLine, 7, formatBigDecimal(recordInvoiceEntity.getTaxAmount()), style);
            beginLine++;
        }
    }

    private String formatBigDecimal(BigDecimal val){
        if(val!=null){
            return String.valueOf(new DecimalFormat("#,##0.00").format(val));
        }
        return null;
    }
}
