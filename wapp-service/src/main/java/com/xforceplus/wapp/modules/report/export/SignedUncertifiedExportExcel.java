package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
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
 * 已签收未认证发票导出
 */
public class SignedUncertifiedExportExcel extends AbstractExportExcel {

    private String FONT_NAME = "宋体";
    private String SCAVENGING_SIGN = "扫码签收";
    private String SCANNER_SIGN = "扫描仪签收";
    private String PHONE_APP_SIGN = "手机APP签收";
    private String IMPORT_SIGN = "导入签收";
    private String MANUAL_SIGN = "手工签收";
    private String PDF_UPLOAD = "pdf上传";
    private String DATE_FORMAT = "yyyy-MM-dd";
    private Map<String, Object> map;

    public SignedUncertifiedExportExcel(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    protected String getExcelUri() {
        return "export/report/signedUncertified.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>) this.map.get("signedUncertified");
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
            //签收类型
            if ("0".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 2, SCAVENGING_SIGN, style);
            } else if ("1".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 2, SCANNER_SIGN, style);
            } else if ("2".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 2, PHONE_APP_SIGN, style);
            } else if ("3".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 2, IMPORT_SIGN, style);
            } else if ("4".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 2, MANUAL_SIGN, style);
            } else if ("5".equals(comprehensiveInvoiceQueryEntity.getQsType())) {
                setSheetValue(sheet, beginLine, 2, PDF_UPLOAD, style);
            }
            //发票代码
            setSheetValue(sheet, beginLine, 3, comprehensiveInvoiceQueryEntity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 4, comprehensiveInvoiceQueryEntity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 5, comprehensiveInvoiceQueryEntity.getInvoiceDate(), style);
            //购方税号
            setSheetValue(sheet, beginLine, 6, comprehensiveInvoiceQueryEntity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 7, comprehensiveInvoiceQueryEntity.getGfName(), style);
            //销方税号
            setSheetValue(sheet, beginLine, 8, comprehensiveInvoiceQueryEntity.getXfTaxNo(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 9, comprehensiveInvoiceQueryEntity.getXfName(), style);
            //金额
            if (comprehensiveInvoiceQueryEntity.getInvoiceAmount() == null) {
                setSheetValue(sheet, beginLine, 10, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 10, comprehensiveInvoiceQueryEntity.getInvoiceAmount(), style);
            }
            //税额
            if (comprehensiveInvoiceQueryEntity.getTaxAmount() == null) {
                setSheetValue(sheet, beginLine, 11, "0.00", style);
            } else {
                setSheetValue(sheet, beginLine, 11, comprehensiveInvoiceQueryEntity.getTaxAmount(), style);
            }
            beginLine++;
        }
        setSheetValue(sheet, beginLine, 1,"合计", style);
        setSheetValue(sheet, beginLine, 10,this.map.get("totalAmount").toString(), style);
        setSheetValue(sheet, beginLine, 11,this.map.get("totalTax").toString(), style);
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DATE_FORMAT);
    }
}
