package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

public final class InvoiceAuthDailyReportExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceAuthDailyReportExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<DailyReportEntity> list = (List<DailyReportEntity>)this.map.get(excelName);
        //设置开始行
        int beginLine = 3;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        //填充纳税人名称
        setSheetValue(sheet, 1, 0, this.map.get("taxName").toString(), style);
        //填充纳税人识别号
        setSheetValue(sheet, 1, 2, this.map.get("taxNo").toString(), style);
        //数据填入excel
        for (DailyReportEntity entity : list) {
            //认证日期
            setSheetValue(sheet, beginLine, 0, entity.getRzhDate(), style);
            //发票数量
            setSheetValue(sheet, beginLine, 1, entity.getCount(), style);
            //合计金额
            setSheetValue(sheet, beginLine, 2, CommonUtil.formatMoney(entity.getAmount()), style);
            //合计税额
            setSheetValue(sheet, beginLine, 3, CommonUtil.formatMoney(entity.getTax()), style);

            beginLine++;
        }
        setSheetValue(sheet, beginLine, 0,"合计", style);
        setSheetValue(sheet, beginLine, 1,this.map.get("totalCount").toString(), style);
        setSheetValue(sheet, beginLine, 2,this.map.get("totalAmount").toString(), style);
        setSheetValue(sheet, beginLine, 3,this.map.get("totalTax").toString(), style);
    }
}
