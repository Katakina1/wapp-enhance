package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.report.entity.BatchSystemMatchQueryEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
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
 * 发票处理状态报告导出
 */
public final class BatchSystemMatchQueryExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public BatchSystemMatchQueryExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<BatchSystemMatchQueryEntity> list = (List<BatchSystemMatchQueryEntity>)this.map.get(excelName);
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
        for (BatchSystemMatchQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //
            setSheetValue(sheet, beginLine, 1, entity.getMatchNo(), style);

            setSheetValue(sheet, beginLine, 2, entity.getJv(), style);
            //
            setSheetValue(sheet, beginLine, 3, entity.getVender(), style);
            //
            setSheetValue(sheet, beginLine, 4, entity.getInvTotal(), style);
            //
            setSheetValue(sheet, beginLine, 5, entity.getTaxAmount(), style);
            setSheetValue(sheet, beginLine, 6, entity.getTaxRate(), style);
            //
            setSheetValue(sheet, beginLine, 7, entity.getInv(), style);
            //
            setSheetValue(sheet, beginLine, 8, (entity.getYy()+"-"+entity.getMm()+"-"+entity.getDd()), style);
            //
            setSheetValue(sheet, beginLine, 9, (entity.getYy1()+"-"+entity.getMm1()+"-"+entity.getDd1()), style);
            //
            setSheetValue(sheet, beginLine, 10, entity.getTaxRate(), style);
            //
            //导入日期
            setSheetValue(sheet, beginLine, 11, formatDate(entity.getCreateDate()), style);
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
    private String HostStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未处理" :
                        "1".equals(status) ? "已处理" : "";
    }
}
