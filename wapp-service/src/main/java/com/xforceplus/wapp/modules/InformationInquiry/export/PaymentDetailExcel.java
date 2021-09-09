package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailEntity;
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
 * 供应商付款信息导出
 */
public final class PaymentDetailExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public PaymentDetailExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<PaymentDetailEntity> list = (List<PaymentDetailEntity>)this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 1);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        //数据填入excel
        for (PaymentDetailEntity entity : list) {
            //供应商号
            setSheetValue(sheet, beginLine, 0, entity.getVenderid(), style);
            //供应商名称
            setSheetValue(sheet, beginLine, 1, entity.getOrgName(), style);
            //发票号
            setSheetValue(sheet, beginLine, 2, entity.getReferTo(), style);
            //付款金额
            setSheetValue(sheet, beginLine, 3, entity.getShowCurrencyAmount().doubleValue(), style);
            //付款日期
            setSheetValue(sheet, beginLine, 4, entity.getPaymentDate(), style);
            //JV号
            setSheetValue(sheet, beginLine, 5, entity.getJvcode(), style);
            //备注
            setSheetValue(sheet, beginLine, 6, entity.getInvoiceText(), style);
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

}
