package com.xforceplus.wapp.modules.scanRefund.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 订单信息导出
 */
public final class CostGenerateRefundNumberAllExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public CostGenerateRefundNumberAllExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<GenerateRefundNumberEntity> list = (List<GenerateRefundNumberEntity>)this.map.get(excelName);
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

        for (GenerateRefundNumberEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //扫描日期
            setSheetValue(sheet, beginLine, 1, formatDate(entity.getCreateDate()), style);
            //审扫描流水号
            setSheetValue(sheet, beginLine, 2, entity.getInvoiceSerialNo(), style);
            //申请类型
            setSheetValue(sheet, beginLine, 3, entity.getBelongsTo(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 4, entity.getVenderId(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 5, entity.getInvoiceCode(), style);
            //EPS_NO
            setSheetValue(sheet, beginLine, 6, entity.getEpsNo(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 7, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 8, formatDate(entity.getInvoiceDate()), style);
            //金额
            setSheetValue(sheet, beginLine, 9, formatAmount(entity.getInvoiceAmount().toString()), style);
            //税额
            setSheetValue(sheet, beginLine, 10, formatAmount(entity.getTaxAmount().toString()), style);
            //退票原因
            setSheetValue(sheet, beginLine, 11, entity.getRefundReason(), style);
            //退票编号
            setSheetValue(sheet, beginLine, 12, entity.getRefundCode(), style);
            beginLine++;
        }
    }


    private String formatDate(String source) {
        return source == null ? "" : source.substring(0, 10);
    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
    private String errCode(String getisdel,String code){
        String value="";
        if("2".equals(getisdel)){
            value="";
        }else{
            value=code;
        }
        return value;
    }
}
