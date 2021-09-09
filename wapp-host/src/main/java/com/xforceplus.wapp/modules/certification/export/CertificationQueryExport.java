package com.xforceplus.wapp.modules.certification.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 认证查询导出
 * @author Colin.hu
 * @date 4/14/2018
 */
public class CertificationQueryExport extends AbstractExportExcel {

    private final Map<String, List<InvoiceCollectionInfo>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public CertificationQueryExport(Map<String, List<InvoiceCollectionInfo>> map, String excelTempPath, String excelName) {
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
        final List<InvoiceCollectionInfo> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 16);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        //数据填入excel
        for (InvoiceCollectionInfo entity : list) {
            //认证结果
            final String authStatus = formatAuthStatus(entity.getAuthStatus());
           /* setSheetValue(sheet, beginLine, 0, authMap().get(entity.getAuthStatus()), style);*/
            setSheetValue(sheet, beginLine, 0, authStatus, style);
            //认证状态（是否认证）
            setSheetValue(sheet, beginLine, 1, entity.getRzhYesornoName(), style);
            //认证时间
            setSheetValue(sheet, beginLine, 2, formatDate(entity.getRzhDate()), style);
            //认证归属期
            setSheetValue(sheet, beginLine, 3,entity.getRzhBelongDate(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 4,entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 5,entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 6, formatDate(entity.getInvoiceDate()), style);
            //购方税号
            setSheetValue(sheet, beginLine, 7,entity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 8,entity.getGfName(), style);
            //销方税号
            setSheetValue(sheet, beginLine, 9,entity.getXfTaxNo(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 10,entity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 11,entity.getInvoiceAmount(), style);
            //税额
            setSheetValue(sheet, beginLine, 12,entity.getTaxAmount(), style);
            //发票状态
            setSheetValue(sheet, beginLine, 13,entity.getInvoiceStatusName(), style);
            //签收状态
            setSheetValue(sheet, beginLine, 14,entity.getQsStatusName(), style);
            //签收日期
            setSheetValue(sheet, beginLine, 15, formatDate(entity.getQsDate()), style);
            //签收方式
            setSheetValue(sheet, beginLine, 16,entity.getQsTypeName(), style);
            beginLine++;
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT).format(source);
    }

    private String formatAuthStatus(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "已勾选";
        } else  if("2".equals(authStatus)) {
            authStatusName = "已确认";
        } else  if("3".equals(authStatus)) {
            authStatusName = "已发送认证";
        } else  if("4".equals(authStatus)) {
            authStatusName = "认证成功";
        } else if("5".equals(authStatus)) {
            authStatusName = "认证失败";
        }
     return authStatusName;
    }
}
