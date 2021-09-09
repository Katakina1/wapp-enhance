package com.xforceplus.wapp.modules.certification.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.certification.entity.EnterpriseTaxInformationEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class EnterpriseTaxInformationExcel extends AbstractExportExcel {
    private final Map<String, List<EnterpriseTaxInformationEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public EnterpriseTaxInformationExcel(Map<String, List<EnterpriseTaxInformationEntity>> map, String excelTempPath, String excelName) {
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
        final List<EnterpriseTaxInformationEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        BigDecimal sumCollectCount = new BigDecimal(0);
        BigDecimal sumTotalAmount = new BigDecimal(0);
        BigDecimal sumTaxAmount = new BigDecimal(0);
        //数据填入excel
        for (EnterpriseTaxInformationEntity entity : list) {
            //当前税号	旧税号	企业名称	当前税款所属期	勾选开始日期	勾选结束日期	所属期结束日期	信用等级	申报周期

            //当前税号
            setSheetValue(sheet, beginLine, 0, entity.getTaxNo(), style);
            //旧税号
            setSheetValue(sheet, beginLine, 1, entity.getOldTaxNo(), style);
            //企业名称
            setSheetValue(sheet, beginLine, 2, entity.getTaxName(), style);
            //当前税款所属期
            setSheetValue(sheet, beginLine, 3, formatStrDate(entity.getCurrentTaxPeriod()), style);
            //勾选开始日期
            setSheetValue(sheet, beginLine, 4, formatDate(entity.getSelectStartDate()),style);
            //勾选结束日期
            setSheetValue(sheet, beginLine, 5, formatDate(entity.getSelectEndDate()), style);
            //所属期结束日期
            setSheetValue(sheet, beginLine, 6, formatDate(entity.getOperationEndDate()), style);
            //信用等级
            setSheetValue(sheet, beginLine, 7, entity.getCreditRating(),style);
            //申报周期
            setSheetValue(sheet, beginLine, 8, formatDeclarePeriod(entity.getDeclarePeriod()), style);
            beginLine++;
        }
    }

    private String formatDate(String source) {
        return source == null ? "" : source.substring(0, 4)+"-"+source.substring(4,6)+"-"+source.substring(6);
    }

    private String formatStrDate(String source) {
        return source == null ? "" : source.substring(0, 4)+"年"+source.substring(4)+"月";
    }
    private String formatDeclarePeriod(String source){
        if(null==source){
            return "";
        }
        if(source.equals("1")){
            return "月报";
        }else if (source.equals("3")){
            return "季报";
        }else{
            return "";
        }
    }
}
