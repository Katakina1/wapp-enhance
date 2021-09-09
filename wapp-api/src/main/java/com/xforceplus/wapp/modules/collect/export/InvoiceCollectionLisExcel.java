package com.xforceplus.wapp.modules.collect.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发票采集列表导出
 *
 * @author Colin.hu
 * @date 4/13/2018
 */
public final class InvoiceCollectionLisExcel extends AbstractExportExcel {

    private final Map<String, List<CollectListStatistic>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceCollectionLisExcel(Map<String, List<CollectListStatistic>> map, String excelTempPath, String excelName) {
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
        final List<CollectListStatistic> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(Constant.FONT);
        style.setFont(font);
        BigDecimal sumCollectCount = new BigDecimal(0);
        BigDecimal sumTotalAmount = new BigDecimal(0);
        BigDecimal sumTaxAmount = new BigDecimal(0);
        //数据填入excel
        for (CollectListStatistic collectListStatistic : list) {
            //采集时间
            setSheetValue(sheet, beginLine, 0, formatDate(collectListStatistic.getCreateDate()), style);
            //购方税号
            setSheetValue(sheet, beginLine, 1, collectListStatistic.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 2, collectListStatistic.getGfName(), style);
            //采集数量合计
            setSheetValue(sheet, beginLine, 3, collectListStatistic.getCollectCount(), style);
            //金额合计
            setSheetValue(sheet, beginLine, 4, collectListStatistic.getSumTotalAmount(), style);
            //税额合计
            setSheetValue(sheet, beginLine, 5, collectListStatistic.getSumTaxAmount(), style);

            sumCollectCount = sumCollectCount.add(new BigDecimal(collectListStatistic.getCollectCount()));
            sumTotalAmount = sumTotalAmount.add(new BigDecimal(collectListStatistic.getSumTotalAmount()));
            sumTaxAmount = sumTaxAmount.add(new BigDecimal(collectListStatistic.getSumTaxAmount()));
            beginLine++;
        }
        if (list.size() > 0) {
            //采集时间
            setSheetValue(sheet, beginLine, 0, "合计", style);
            //购方税号
            setSheetValue(sheet, beginLine, 1, StringUtils.EMPTY, style);
            //购方名称
            setSheetValue(sheet, beginLine, 2, StringUtils.EMPTY, style);
            //采集数量合计
            setSheetValue(sheet, beginLine, 3, sumCollectCount.toString(), style);
            //金额合计
            setSheetValue(sheet, beginLine, 4, sumTotalAmount.toString(), style);
            //税额合计
            setSheetValue(sheet, beginLine, 5, sumTaxAmount.toString(), style);
        }
    }

    private String formatDate(Date source) {
        //return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);

        return source == null ? "" : new SimpleDateFormat(Constant.DEFAULT_SHORT_DATE_FORMAT).format(source);
    }
}
