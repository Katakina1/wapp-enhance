package com.xforceplus.wapp.modules.report.export;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;

public final class ComprehensiveInvoiceQueryExcelSL extends AbstractExportExcel {
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;
    private static final Logger LOGGER = LoggerFactory.getLogger(ComprehensiveInvoiceQueryExcelSL.class);
    private static final String ERROR_MESSAGE = "the java IO error:";

    public ComprehensiveInvoiceQueryExcelSL(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>) this.map.get(excelName);
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
            if (entity.getInvoiceAmount() == null && entity.getTaxAmount() == null &&
                    (entity.getTaxRate() == null || BigDecimal.ZERO.compareTo(entity.getTaxRate()) == 0)) {
                continue;
            }
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //发票代码
            setSheetValue(sheet, beginLine, 1, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 2, entity.getInvoiceNo(), style);
            //金额
            setSheetValue(sheet, beginLine, 3, CommonUtil.formatMoney(entity.getInvoiceAmount()), style);
            //税额
            setSheetValue(sheet, beginLine, 4, CommonUtil.formatMoney(entity.getTaxAmount()), style);
            //税率
            setSheetValue(sheet, beginLine, 5, entity.getTaxRate() == null ? "" : entity.getTaxRate().stripTrailingZeros().toPlainString(), style);

            beginLine++;
        }

    }

}
