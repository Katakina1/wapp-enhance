package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 问题单信息导出
 */
public final class CostListExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public CostListExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<ScanningEntity> list = (List<ScanningEntity>)this.map.get(excelName);
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
        for (ScanningEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //JV
            setSheetValue(sheet, beginLine, 1, entity.getJvCode(), style);
            //公司编码
            setSheetValue(sheet, beginLine, 2, entity.getCompanyCode(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 3, entity.getVenderid(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 4, entity.getGfName(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 5, entity.getXfName(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 6, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 7, formatDate(entity.getInvoiceDate()), style);
            //金额
            setSheetValue(sheet, beginLine, 8, entity.getInvoiceAmount().toString(), style);
            //退票原因
            setSheetValue(sheet, beginLine, 9, entity.getRefundNotes(), style);
            //退单号
            setSheetValue(sheet, beginLine, 10, entity.getRebateno(), style);
            //邮包号
            setSheetValue(sheet, beginLine, 11, entity.getRebateExpressno(), style);
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

}
