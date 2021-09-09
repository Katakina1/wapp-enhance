package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class PaymentInvoiceUploadExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public PaymentInvoiceUploadExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<PaymentInvoiceUploadEntity> list = (List<PaymentInvoiceUploadEntity>)this.map.get(excelName);
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
        for (PaymentInvoiceUploadEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //扣款公司
            setSheetValue(sheet, beginLine, 1, entity.getJvcode(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 2, entity.getSupplierAssociation(), style);
            //类型
            setSheetValue(sheet, beginLine, 3, entity.getCaseType(), style);
            //备注
            setSheetValue(sheet, beginLine, 4, entity.getRemark(), style);
            //换货号
            setSheetValue(sheet, beginLine, 5, entity.getExchangeNo(), style);
            //索赔号
            setSheetValue(sheet, beginLine, 6, entity.getReturnGoodsCode(), style);
            //定案日期
            setSheetValue(sheet, beginLine, 7, entity.getReturnGoodsDate(), style);
            //成本金额
            setSheetValue(sheet, beginLine, 8, formatAmount(entity.getReturnCostAmount()), style);
            //供应商结款发票号
            setSheetValue(sheet, beginLine, 9, entity.getPaymentInvoiceNo(), style);
            //扣款日期
            setSheetValue(sheet, beginLine, 10, entity.getDeductionDate(), style);
            //沃尔玛扣款发票号
            setSheetValue(sheet, beginLine, 11, entity.getPurchaseInvoiceNo(), style);
            //税率
            setSheetValue(sheet, beginLine, 12, entity.getTaxRate(), style);
            //含税金额
            setSheetValue(sheet, beginLine, 13, formatAmount(entity.getTaxAmount()), style);
            //发送日期
            setSheetValue(sheet, beginLine, 14,entity.getSendDate(), style);
            //邮寄时间
            setSheetValue(sheet, beginLine, 15, entity.getMailData(), style);
            //快递单号
            setSheetValue(sheet, beginLine, 16, entity.getExpressNo(), style);
            //快递公司
            setSheetValue(sheet, beginLine, 17, entity.getExpressName(), style);

            beginLine++;
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String taxRate(String taxRate) {
        BigDecimal rate = new BigDecimal(taxRate);
        String str;
        if(rate.compareTo(new BigDecimal(1))==-1){
            rate = rate.multiply(new BigDecimal(100));
            str=rate.toString();
        }else {
            str = taxRate;
        }
        return str;

    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }


}
