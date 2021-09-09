package com.xforceplus.wapp.modules.check.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

/**
 * @author Bobby
 * @date 2018/4/21
 */
public final class InvoiceCheckHistoryExport extends AbstractExportExcel {


    private final Map<String, List<InvoiceCheckModel>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceCheckHistoryExport(Map<String, List<InvoiceCheckModel>> map, String excelTempPath, String excelName) {
        this.map = map;
        this.excelTempPath = excelTempPath;
        this.excelName = excelName;
    }

    @Override
    protected String getExcelUri() {
        return this.excelTempPath;
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {

        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<InvoiceCheckModel> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        //数据填入excel
        for (InvoiceCheckModel checkModel : list) {
            //发票代码
            setSheetValue(sheet, beginLine, 0, assignDefaultValue(checkModel.getInvoiceCode()), style);
            //发票号码
            setSheetValue(sheet, beginLine, 1, assignDefaultValue(checkModel.getInvoiceNo()), style);
            //开票日期
            if(StringUtils.isNotEmpty(checkModel.getInvoiceDate())) {
                setSheetValue(sheet, beginLine, 2, assignDefaultValue(checkModel.getInvoiceDate().substring(0, 10)), style);
            } else {
                setSheetValue(sheet, beginLine, 2, assignDefaultValue(checkModel.getInvoiceDate()), style);
            }
            //购方名称
            setSheetValue(sheet, beginLine, 3, assignDefaultValue(checkModel.getBuyerName()), style);
            //金额
            setSheetValue(sheet, beginLine, 4, checkModel.getInvoiceAmount() == null ? 0.00 : checkModel.getInvoiceAmount(), style);
            //税额
            setSheetValue(sheet, beginLine, 5, checkModel.getTaxAmount() == null ? 0.00 : checkModel.getTaxAmount(), style);
            //价税合计
            setSheetValue(sheet, beginLine, 6, checkModel.getTotalAmount() == null ? 0.00 : checkModel.getTotalAmount(), style);
            //查验结果
            setSheetValue(sheet, beginLine, 7, assignDefaultValue(checkModel.getCheckMassege()), style);
            beginLine++;
        }
        //采集时间

    }

    private String assignDefaultValue(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        } else {
            return str;
        }
    }

}



