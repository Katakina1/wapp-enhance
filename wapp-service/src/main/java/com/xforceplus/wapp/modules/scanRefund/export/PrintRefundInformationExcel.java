package com.xforceplus.wapp.modules.scanRefund.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class PrintRefundInformationExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public PrintRefundInformationExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<EnterPackageNumberEntity> list = (List<EnterPackageNumberEntity>)this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (EnterPackageNumberEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //退单时间
            setSheetValue(sheet, beginLine, 1, entity.getRebateDate(), style);
            //退单号
            setSheetValue(sheet, beginLine, 2, entity.getRebateNo(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 3, entity.getVenderId(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 5, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 6, entity.getInvoiceDate().substring(0,10), style);
            //金额
            if(entity.getInvoiceAmount()!=null) {
            setSheetValue(sheet, beginLine, 7, entity.getInvoiceAmount().toString().substring(0,entity.getInvoiceAmount().length()-2), style);
            }else {
            	 setSheetValue(sheet, beginLine, 7, "", style);
            }
            //税额
            setSheetValue(sheet, beginLine, 8, entity.getTaxAmount().toString().substring(0,entity.getTaxAmount().length()-2), style);
            //eps单号
            setSheetValue(sheet, beginLine, 9, entity.getEpsNo(), style);
            beginLine++;
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }



}
