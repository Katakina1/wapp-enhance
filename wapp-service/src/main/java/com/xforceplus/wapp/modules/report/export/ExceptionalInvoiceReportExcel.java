package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class ExceptionalInvoiceReportExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public ExceptionalInvoiceReportExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>)this.map.get(excelName);
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
        for (ComprehensiveInvoiceQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++ , style);
            //异常状态
            setSheetValue(sheet, beginLine, 1, formatInvoiceStatus(entity.getInvoiceStatus()), style);
            //异常时间
            setSheetValue(sheet, beginLine, 2, formatDate(entity.getStatusUpdateDate()), style);
            //发票代码
            setSheetValue(sheet, beginLine, 3, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 5, formatDateString(entity.getInvoiceDate()), style);
            //购方税号
            setSheetValue(sheet, beginLine, 6, entity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 7, entity.getGfName(), style);
            //销方税号
            setSheetValue(sheet, beginLine, 8, entity.getXfTaxNo(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 9, entity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 10, CommonUtil.formatMoney(entity.getInvoiceAmount()), style);
            //税额
            setSheetValue(sheet, beginLine, 11, CommonUtil.formatMoney(entity.getTaxAmount()), style);
            //签收状态
            setSheetValue(sheet, beginLine, 12, formatQsStatus(entity.getQsStatus()), style);
            //认证状态
            setSheetValue(sheet, beginLine, 13, formatRzhStatus(entity.getRzhYesorno()), style);
            //税款所属期
            setSheetValue(sheet, beginLine, 14, entity.getRzhBelongDate(), style);

            beginLine++;
        }
        setSheetValue(sheet, beginLine, 1,"合计", style);
        setSheetValue(sheet, beginLine, 10,this.map.get("totalAmount").toString(), style);
        setSheetValue(sheet, beginLine, 11,this.map.get("totalTax").toString(), style);
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

    private String formatInvoiceStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "正常" :
                        "1".equals(status) ? "失控" :
                                "2".equals(status) ? "作废" :
                                        "3".equals(status) ? "红冲" :
                                                "4".equals(status) ? "异常" : "";
    }

    private String formatQsStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未签收" :
                        "1".equals(status) ? "已签收" : "";
    }

    private String formatRzhStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未认证" :
                        "1".equals(status) ? "已认证" : "";
    }
}
