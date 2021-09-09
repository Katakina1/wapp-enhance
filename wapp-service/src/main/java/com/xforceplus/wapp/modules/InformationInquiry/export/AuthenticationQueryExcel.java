package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.math.BigDecimal;
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
public class AuthenticationQueryExcel extends AbstractExportExcel {

    private final Map<String, List<InvoiceCollectionInfo>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public AuthenticationQueryExcel(Map<String, List<InvoiceCollectionInfo>> map, String excelTempPath, String excelName) {
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
        final XSSFCellStyle style = getCellStyle(sheet, 2, 0);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index =1;
        //数据填入excel
        for (InvoiceCollectionInfo entity : list) {
            //认证结果
            final String authStatus = formatAuthStatus(entity.getAuthStatus());
            //认证方式
            final String rzhType = formatRzhType(entity.getRzhType());
           /* setSheetValue(sheet, beginLine, 0, authMap().get(entity.getAuthStatus()), style);*/
            //COMPANYCODE
            setSheetValue(sheet, beginLine, 0, index++, style);
            setSheetValue(sheet, beginLine, 1, entity.getCompanyCode(), style);
            //JVCODE
            setSheetValue(sheet, beginLine, 2,entity.getJvCode(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 3,entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 4,entity.getInvoiceNo(), style);
            //扫描日期（签收日期）
            setSheetValue(sheet, beginLine, 5,formatDate(entity.getQsDate()), style);
            //供应商号
            setSheetValue(sheet, beginLine, 6, entity.getVenderid(), style);
            //供应商名称
            setSheetValue(sheet, beginLine, 7, entity.getVendername(), style);
            ////store#空值  等确认
            setSheetValue(sheet, beginLine, 8,entity.getStore(), style);
            //税额
            setSheetValue(sheet, beginLine, 9,entity.getTaxAmount(), style);
          //TaxCode
            setSheetValue(sheet, beginLine, 10,entity.getTaxCode(), style);
            //税率
            setSheetValue(sheet, beginLine, 11,formatTaxRate(entity.getTaxRate()), style);

            //价税合计
            setSheetValue(sheet, beginLine, 12,formatTotalAmount(entity.getTotalAmount()), style);
            ////Voucher# 凭证号
            setSheetValue(sheet, beginLine, 13,entity.getCertificateNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 14, formatDate(entity.getInvoiceDate()), style);
            //备注
            setSheetValue(sheet, beginLine, 15,entity.getRemark(), style);
            ////业务类型
            setSheetValue(sheet, beginLine, 16,entity.getServiceType(), style);
            //扫描人
            setSheetValue(sheet, beginLine, 17,entity.getScanName(), style);
            ////购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%- 暂时为空
            setSheetValue(sheet, beginLine, 18,"否", style);
            //大类（指商品类，资产类，费用类）
            setSheetValue(sheet, beginLine, 19, formatDaLeiType(entity.getFlowType()), style);
            //成本金额
            setSheetValue(sheet, beginLine, 20,entity.getInvoiceAmount(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 21,entity.getGfName(), style);
            //税务所属期
            setSheetValue(sheet, beginLine, 22,entity.getRzhBelongDate(), style);
            //成本中心
            setSheetValue(sheet, beginLine, 23,entity.getCostDeptId(), style);
            //Eps
            setSheetValue(sheet, beginLine, 24,entity.getEpsNo(), style);
            beginLine++;
        }
    }

    private String formatTotalAmount(String totalAmount) {
        if("".equals(totalAmount)){
            return "--";
        }else {
            return  totalAmount.substring(0, totalAmount.indexOf('.')+3);
        }
    }

    private String formatTaxRate(String taxRate) {

        if ("".equals(taxRate)|| taxRate==null) {
           return "--";
        }else {
            return  taxRate.substring(0, taxRate.indexOf('.')) + "%";
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatRzhType(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "线上认证";
        } else  if("2".equals(authStatus)) {
            authStatusName = "线下认证";
        }
        return authStatusName;
    }

    private String formatDaLeiType(String authStatus) {
        String authStatusName = "";
        //指商品类，资产类，费用类
        if("1".equals(authStatus)) {
            authStatusName = "商品类";
        } else  if("2".equals(authStatus)) {
            authStatusName = "费用类";
        }else {
            authStatusName = "";
        }
        return authStatusName;
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
