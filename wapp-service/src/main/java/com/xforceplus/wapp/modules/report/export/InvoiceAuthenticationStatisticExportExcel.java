package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.InvoiceAuthenticationStatisticEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/18
 * 认证发票统计导出
 */
public class InvoiceAuthenticationStatisticExportExcel extends AbstractExportExcel {


    private String FONT_NAME = "宋体";
    private String DATE_FORMAT = "yyyyMM";
    private String PERIOD_TAX_PAYMENT = "已过税款所属期";
    private String CURRENT_PERIOD = "当前所属期";
    private Map<String, Object> map;

    public InvoiceAuthenticationStatisticExportExcel(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    protected String getExcelUri() {
        return "export/report/invoiceAuthenticationStatistic.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<InvoiceAuthenticationStatisticEntity> list = (List<InvoiceAuthenticationStatisticEntity>) this.map.get("invoiceauthenticationsummarystatistics");
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 3);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName(FONT_NAME);
        style.setFont(font);
        //数据填入excel
        Date date = new Date();
        for (InvoiceAuthenticationStatisticEntity invoiceAuthenticationStatisticEntity : list) {
            //认证年份
            setSheetValue(sheet, beginLine, 0, invoiceAuthenticationStatisticEntity.getDqskssq(), style);
            //发票数量
            setSheetValue(sheet, beginLine, 1, invoiceAuthenticationStatisticEntity.getInvoiceCount(), style);
            //合计金额
            if (invoiceAuthenticationStatisticEntity.getTotalAmount() == null) {
                setSheetValue(sheet, beginLine, 2, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 2, invoiceAuthenticationStatisticEntity.getTotalAmount(), style);
            }
            //合计税额
            if (invoiceAuthenticationStatisticEntity.getTaxAmount() == null) {
                setSheetValue(sheet, beginLine, 3, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 3, invoiceAuthenticationStatisticEntity.getTaxAmount(), style);
            }
            if (Long.valueOf(invoiceAuthenticationStatisticEntity.getDqskssq()) < Long.valueOf(formatDate(date))) {
                setSheetValue(sheet, beginLine, 4, PERIOD_TAX_PAYMENT, style);
            } else {
                setSheetValue(sheet, beginLine, 4, CURRENT_PERIOD, style);
            }
            beginLine++;
        }
        setSheetValue(sheet, beginLine, 0,"合计", style);
        setSheetValue(sheet, beginLine, 1,this.map.get("totalCount").toString(), style);
        setSheetValue(sheet, beginLine, 2,this.map.get("totalAmount").toString(), style);
        setSheetValue(sheet, beginLine, 3,this.map.get("totalTax").toString(), style);
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DATE_FORMAT);
    }
}
