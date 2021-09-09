package com.xforceplus.wapp.modules.collect.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 未补明细发票导出
 * @author Colin.hu
 * @date 4/13/2018
 */
public final class NoDetailedInvoiceExcel extends AbstractExportExcel {

    private final Map<String, List<InvoiceCollectionInfo>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public NoDetailedInvoiceExcel(Map<String, List<InvoiceCollectionInfo>> map, String excelTempPath, String excelName) {
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
        final List<InvoiceCollectionInfo> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 8);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        BigDecimal sumTotalAmount = new BigDecimal(0);
        BigDecimal sumTaxAmount = new BigDecimal(0);
        //数据填入excel
        for (InvoiceCollectionInfo invoiceCollectionInfo : list) {
            //发票代码
            setSheetValue(sheet, beginLine, 0, invoiceCollectionInfo.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 1, invoiceCollectionInfo.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 2, formatDate(invoiceCollectionInfo.getInvoiceDate()), style);
            //购方名称
            setSheetValue(sheet, beginLine, 3, invoiceCollectionInfo.getGfName(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 4,invoiceCollectionInfo.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 5,invoiceCollectionInfo.getInvoiceAmount(), style);
            //税额
            setSheetValue(sheet, beginLine, 6,invoiceCollectionInfo.getTaxAmount(), style);
            //校验码
            setSheetValue(sheet, beginLine, 7,invoiceCollectionInfo.getCheckCode(), style);
            //采集时间
            setSheetValue(sheet, beginLine, 8, formatDate(invoiceCollectionInfo.getCreateDate()), style);

            sumTotalAmount = sumTotalAmount.add(new BigDecimal(invoiceCollectionInfo.getInvoiceAmount()));
            sumTaxAmount = sumTaxAmount.add(new BigDecimal(invoiceCollectionInfo.getTaxAmount()));
            beginLine++;
        }
        if (list.size() > 0) {
            //采集时间
            setSheetValue(sheet, beginLine, 0, "合计", style);
            //购方税号
            setSheetValue(sheet, beginLine, 1, StringUtils.EMPTY, style);
            //购方名称
            setSheetValue(sheet, beginLine, 2, StringUtils.EMPTY, style);
            setSheetValue(sheet, beginLine, 3, StringUtils.EMPTY, style);
            setSheetValue(sheet, beginLine, 4, StringUtils.EMPTY, style);
            //金额合计
            setSheetValue(sheet, beginLine, 5, sumTotalAmount.toString(), style);
            //税额合计
            setSheetValue(sheet, beginLine, 6, sumTaxAmount.toString(), style);
            setSheetValue(sheet, beginLine, 7, StringUtils.EMPTY, style);
            setSheetValue(sheet, beginLine, 8, StringUtils.EMPTY, style);
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }
}
