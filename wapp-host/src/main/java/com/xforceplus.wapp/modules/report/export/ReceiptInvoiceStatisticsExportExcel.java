package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static org.apache.commons.configuration.DataConfiguration.DEFAULT_DATE_FORMAT;

/**
 * @author joe.tang
 * @date 2018/4/16
 * 发票签收统计导出导出
 */
public class ReceiptInvoiceStatisticsExportExcel extends AbstractExportExcel {
    private String FONT_NAME = "宋体";
    private String SCAVENGING_SIGN = "扫码签收";
    private String SCANNER_SIGN = "扫描仪签收";
    private String PHONE_APP_SIGN = "手机APP签收";
    private String IMPORT_SIGN = "导入签收";
    private String MANUAL_SIGN = "手工签收";
    private String PDF_UPLOAD = "pdf上传";
    private String SIGN_SUCESS = "签收成功";
    private String SIGN_FAIL = "签收失败";
    private String DATE_FORMAT = "yyyy-MM-dd";
    private Map<String, Object> map;

    public ReceiptInvoiceStatisticsExportExcel(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    protected String getExcelUri() {
        return "export/report/receiptInvoiceStatistics.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>) this.map.get("receiptInvoiceStatistics");
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 3);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName(FONT_NAME);
        style.setFont(font);
        //数据填入excel
        for (ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, beginLine - 1, style);
            //签收日期
            setSheetValue(sheet, beginLine, 1, formatDate(comprehensiveInvoiceQueryEntity.getQsDate()), style);
            //发票代码
            setSheetValue(sheet, beginLine, 2, comprehensiveInvoiceQueryEntity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 3, comprehensiveInvoiceQueryEntity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 4, comprehensiveInvoiceQueryEntity.getInvoiceDate(), style);
            //购方税号
            setSheetValue(sheet, beginLine, 5, comprehensiveInvoiceQueryEntity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 6, comprehensiveInvoiceQueryEntity.getGfName(), style);
            //销方税号
            setSheetValue(sheet, beginLine, 7, comprehensiveInvoiceQueryEntity.getXfTaxNo(), style);
            //金额
            if (comprehensiveInvoiceQueryEntity.getInvoiceAmount() == null) {
                setSheetValue(sheet, beginLine, 8, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 8, comprehensiveInvoiceQueryEntity.getInvoiceAmount(), style);
            }
            //税额
            if (comprehensiveInvoiceQueryEntity.getTaxAmount() == null) {
                setSheetValue(sheet, beginLine, 9, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 9, comprehensiveInvoiceQueryEntity.getTaxAmount(), style);
            }

            //签收状态
            if ("0".equals(comprehensiveInvoiceQueryEntity.getQsStatus())) {
                setSheetValue(sheet, beginLine, 10, SIGN_FAIL, style);
            } else {
                setSheetValue(sheet, beginLine, 10, SIGN_SUCESS, style);
            }
            //签收类型
            if ("0".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 11, SCAVENGING_SIGN, style);
            } else if ("1".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 11, SCANNER_SIGN, style);
            } else if ("2".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 11, PHONE_APP_SIGN, style);
            } else if ("3".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 11, IMPORT_SIGN, style);
            } else if ("4".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 11, MANUAL_SIGN, style);
            } else if ("5".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 11, PDF_UPLOAD, style);
            }
            beginLine++;
        }
        setSheetValue(sheet, beginLine, 1,"合计", style);
        setSheetValue(sheet, beginLine, 8,this.map.get("totalAmount").toString(), style);
        setSheetValue(sheet, beginLine, 9,this.map.get("totalTax").toString(), style);
    }

    private String formatDate(Date source) {
        return source == null ? "" : new SimpleDateFormat(DATE_FORMAT).format(source);
    }
}
