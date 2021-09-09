package com.xforceplus.wapp.modules.transferOut.export;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/23
 * Time:9:00
*/

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class transferOutExport extends AbstractExportExcel {


    private final Map<String, List<InvoiceEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public transferOutExport(Map<String, List<InvoiceEntity>> map, String excelTempPath, String excelName) {
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
        final List<InvoiceEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        //数据填入excel
        for (InvoiceEntity entity : list) {
            //转出日期
            setSheetValue(sheet, beginLine, 0, formatDate(entity.getOutDate()), style);
            //发票代码
            setSheetValue(sheet, beginLine, 1, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 2, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 3, formatDate(entity.getInvoiceDate()), style);
             //发票状态
            setSheetValue(sheet, beginLine, 4, formatInvoiceStatus(entity.getInvoiceStatus()), style);
            //购方税号
            setSheetValue(sheet, beginLine, 5, entity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 6, entity.getGfName(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 7, entity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 8, CommonUtil.formatMoney(entity.getInvoiceAmount()), style);
            //税额
            setSheetValue(sheet, beginLine, 9, CommonUtil.formatMoney(entity.getTaxAmount()), style);
            //转出金额
            setSheetValue(sheet, beginLine, 10, CommonUtil.formatMoney(entity.getOutInvoiceAmout()), style);
            //转出税额
            setSheetValue(sheet, beginLine, 11, CommonUtil.formatMoney(entity.getOutTaxAmount()), style);
            //转出原因
            setSheetValue(sheet, beginLine, 12, formatOutReason(entity.getOutReason()), style);
            beginLine++;
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatInvoiceStatus(String status){
        return null==status ? "—— ——" :
                "0".equals(status) ? "正常" :
                        "1".equals(status) ? "失控" :
                                "2".equals(status) ? "作废" :
                                        "3".equals(status) ? "红冲" :
                                                "4".equals(status) ? "异常" : "—— ——";
    }

    private String formatOutReason(String outReason){
        return null==outReason ? "—— ——" :
                        "1".equals(outReason) ? "免税项目用" :
                                "2".equals(outReason) ? "集体福利,个人消费" :
                                        "3".equals(outReason) ? "非正常损失" :
                                                "4".equals(outReason) ? "简易计税方法征税项目用" :
                                                        "5".equals(outReason) ? "免抵退税办法不得抵扣的进项税额" :
                                                                "6".equals(outReason) ? "纳税检查调减进项税额" :
                                                                        "7".equals(outReason) ? "红字专用发票通知单注明的进项税额" :
                                                                             "8".equals(outReason) ? "上期留抵税额抵减欠税" : "—— ——";
    }
}
