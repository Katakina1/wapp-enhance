package com.xforceplus.wapp.modules.invoiceBorrow.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 发票归还发票导出
 */
public final class BorrowInvoiceExcel extends AbstractExportExcel {

    private final Map<String, List<ComprehensiveInvoiceQueryEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public BorrowInvoiceExcel(Map<String, List<ComprehensiveInvoiceQueryEntity>> map, String excelTempPath, String excelName) {
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
        final List<ComprehensiveInvoiceQueryEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 8);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (ComprehensiveInvoiceQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //jvcode
            setSheetValue(sheet, beginLine, 1,entity.getJvCode()== null ? "" :entity.getJvCode(), style);
            //companycode
            setSheetValue(sheet, beginLine, 2,entity.getCompanyCode()== null ? "" :entity.getCompanyCode(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 3, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 5, formatDateString(entity.getInvoiceDate()), style);
            //购方名称
            setSheetValue(sheet, beginLine, 6, entity.getGfName(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 7,entity.getXfName(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 8,entity.getVenderId()==null?"":entity.getVenderId(), style);
            //金额
            setSheetValue(sheet, beginLine, 9,entity.getInvoiceAmount()==null?0:entity.getInvoiceAmount(), style);
            //税额
            if(entity.getTaxAmount()!=null) {
                setSheetValue(sheet, beginLine, 10, entity.getTaxAmount(), style);
            } else {
                setSheetValue(sheet, beginLine, 10, "", style);
            }
            //税率
            setSheetValue(sheet, beginLine, 11,entity.getTaxRate() == null ? "" : entity.getTaxRate().stripTrailingZeros().toPlainString(), style);
            //价税合计
            if(entity.getTotalAmount()!=null) {
                setSheetValue(sheet, beginLine, 12, entity.getTotalAmount(), style);
            } else{
                setSheetValue(sheet, beginLine, 12, "", style);
            }
            //凭证号
            setSheetValue(sheet, beginLine, 13,entity.getCertificateNo()== null ? "" :entity.getCertificateNo(), style);
            //扫描流水号
            setSheetValue(sheet, beginLine, 14,entity.getScanningSeriano()== null ? "" :entity.getScanningSeriano(), style);
            //装订册号
            setSheetValue(sheet, beginLine, 15,entity.getBbindingno()== null ? "" :entity.getBbindingno(), style);
            //装箱号
            setSheetValue(sheet, beginLine, 16,entity.getPackingno()== null ? "" :entity.getPackingno(), style);
            //eps号
            setSheetValue(sheet, beginLine, 17,entity.getEpsNo()== null ? "" :entity.getEpsNo(), style);
            beginLine++;
        }
    }

    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

}
