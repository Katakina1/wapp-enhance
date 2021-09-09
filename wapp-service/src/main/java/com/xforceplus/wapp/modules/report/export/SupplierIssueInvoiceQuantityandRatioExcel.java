package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.entity.QuestionInvoiceQuantityAndRatioEntity;
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
 * 供应商问题发票数量及比率导出
 */
public final class SupplierIssueInvoiceQuantityandRatioExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public SupplierIssueInvoiceQuantityandRatioExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<QuestionInvoiceQuantityAndRatioEntity> list = (List<QuestionInvoiceQuantityAndRatioEntity>)this.map.get(excelName);
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
        for (QuestionInvoiceQuantityAndRatioEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商号
            setSheetValue(sheet, beginLine, 1, entity.getVenderId(), style);
            //供应商名称
            setSheetValue(sheet, beginLine, 2, entity.getVendername(), style);
            //正常发票数量
            setSheetValue(sheet, beginLine, 3, entity.getNormalInvoice(), style);
            //问题发票数量
            setSheetValue(sheet, beginLine, 4, entity.getProblemInvoice(), style);
            //问题发票比率
            setSheetValue(sheet, beginLine, 5, entity.getProblemInvoiceRatio(), style);
            beginLine++;
        }
    }

}
