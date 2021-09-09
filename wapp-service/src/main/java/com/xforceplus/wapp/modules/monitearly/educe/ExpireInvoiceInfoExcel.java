package com.xforceplus.wapp.modules.monitearly.educe;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 异常发票预警导出excle工具类
 * Created by vito.xing on 2018/07/27.
 */
public class ExpireInvoiceInfoExcel extends AbstractExportExcel {

    //单元格的行索引
    private static final Integer ROW_INDEX=0;

    //单元格数
    private static final Integer CELL_INDEX=8;

    private static final Integer INDEX=0;

    private static final short  FONT_FONT_HEIGHT_INPOINTS=12;

    private Map<String, List<RecordInvoiceEntity>> map;

    public ExpireInvoiceInfoExcel(Map<String, List<RecordInvoiceEntity>> map) {
        this.map = map;
    }

    @Override
    protected String getExcelUri() {
        return "export/monitearly/ExpireInovice.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {

        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(INDEX);

        //获取要导出的数据
        final List<RecordInvoiceEntity> list = this.map.get("expireInvoiceEntityList");

        //设置开始行
        int beginLine = 2;

        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, ROW_INDEX, CELL_INDEX);

        //为金额单独设置的样式
        final XSSFCellStyle moneystyle = getCellStyle(sheet, 1, 8);

        //设置值靠右对齐
        moneystyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

        final Font font = workBook.createFont();
        font.setFontHeightInPoints(FONT_FONT_HEIGHT_INPOINTS);
        font.setFontName("宋体");
        style.setFont(font);

        //数据填入excel
        for (RecordInvoiceEntity recordInvoiceEntity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, String.valueOf(beginLine-1),style);
            //认证截止日
            setSheetValue(sheet, beginLine, 1, formatDateTime(recordInvoiceEntity.getCutApproveDate()), style);
            //发票代码
            setSheetValue(sheet, beginLine, 2, recordInvoiceEntity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 3, recordInvoiceEntity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 4, formatDateTime(recordInvoiceEntity.getInvoiceDate()), style);
            //购方名称
            setSheetValue(sheet, beginLine, 5, recordInvoiceEntity.getGfName(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 6, recordInvoiceEntity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 7, String.valueOf(recordInvoiceEntity.getInvoiceAmount()), style);
            //税额
            setSheetValue(sheet, beginLine, 8, String.valueOf(recordInvoiceEntity.getTaxAmount()), style);

            beginLine++;
        }
    }


    private String formatDateTime(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
}
