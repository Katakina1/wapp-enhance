package com.xforceplus.wapp.modules.report.export;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class ComprehensiveInvoiceQueryExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public ComprehensiveInvoiceQueryExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>)this.map.get(excelName);
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
        for (ComprehensiveInvoiceQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //发票代码
            setSheetValue(sheet, beginLine, 1, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 2, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 3, formatDateString(entity.getInvoiceDate()), style);
            //购方税号
            setSheetValue(sheet, beginLine, 4, entity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 5, entity.getGfName(), style);
            //销方税号
            setSheetValue(sheet, beginLine, 6, entity.getXfTaxNo(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 7, entity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 8, CommonUtil.formatMoney(entity.getInvoiceAmount()), style);
            //税额
            setSheetValue(sheet, beginLine, 9, CommonUtil.formatMoney(entity.getTaxAmount()), style);
            //发票状态
            setSheetValue(sheet, beginLine, 10, formatInvoiceStatus(entity.getInvoiceStatus()), style);
            if("invoiceExport".equals(this.excelName)){
                //大象慧云匹配状态
                setSheetValue(sheet, beginLine, 11, formatdxhyMatchStatus(entity.getDxhyMatchStatus()), style);
                //匹配日期
                setSheetValue(sheet, beginLine, 12, formatDate(entity.getMatchDate()), style);
                //host状态
                setSheetValue(sheet, beginLine, 13, formatHostStatus(entity.getHostStatus(),entity.getTpStatus()), style);
                //扫描流水号
                setSheetValue(sheet, beginLine, 14, entity.getScanningSeriano(), style);
                //装订册号
                setSheetValue(sheet, beginLine, 15,
                        entity.getBbindingno(), style);
                //税款所属期
                setSheetValue(sheet, beginLine, 16, entity.getRzhBelongDate(), style);
                //装箱号
                setSheetValue(sheet, beginLine, 17, entity.getPackingno(), style);

            }else {
                //供应商号
                setSheetValue(sheet, beginLine, 11, entity.getVenderId(), style);
                //凭证号
                setSheetValue(sheet, beginLine, 12, entity.getCertificateNo(), style);
                //JV
                setSheetValue(sheet, beginLine, 13, entity.getCertificateNo(), style);
                //公司代码
                setSheetValue(sheet, beginLine, 14, entity.getCertificateNo(), style);
                //价税合计
                setSheetValue(sheet, beginLine, 15, CommonUtil.formatMoney(entity.getTotalAmount()), style);
                //大象慧云匹配状态
                setSheetValue(sheet, beginLine, 16, formatdxhyMatchStatus(entity.getDxhyMatchStatus()), style);
                //匹配日期
                setSheetValue(sheet, beginLine, 17, formatDate(entity.getMatchDate()), style);
                //签收状态
                setSheetValue(sheet, beginLine, 18, formatQsStatus(entity.getQsStatus()), style);
                //签收方式
                setSheetValue(sheet, beginLine, 19, formatQsType(entity.getQsType()), style);
                //签收日期
                setSheetValue(sheet, beginLine, 20, formatDate(entity.getQsDate()), style);
                //认证状态
                setSheetValue(sheet, beginLine, 21, formatRzhStatus(entity.getRzhYesorno()), style);
                //认证日期
                setSheetValue(sheet, beginLine, 22, formatDate(entity.getRzhDate()), style);
                //host状态
                setSheetValue(sheet, beginLine, 23, formatHostStatus(entity.getHostStatus(),entity.getTpStatus()), style);
                //扫描流水号
                setSheetValue(sheet, beginLine, 24, entity.getScanningSeriano(), style);
                //装订册号
                setSheetValue(sheet, beginLine, 25,
                        entity.getBbindingno(), style);
                //装箱号
                setSheetValue(sheet, beginLine, 26, entity.getPackingno(), style);
                //税款所属期
                setSheetValue(sheet, beginLine, 27, entity.getRzhBelongDate(), style);
                //认证结果
                setSheetValue(sheet, beginLine, 28, formatAuthStatus(entity.getAuthStatus()), style);
                //发票流程类型
                setSheetValue(sheet, beginLine, 29, formatFlowType(entity.getFlowType()), style);
            }
            beginLine++;
        }
        //合计
        setSheetValue(sheet, beginLine, 1,"合计", style);
        setSheetValue(sheet, beginLine, 8,this.map.get("totalAmount").toString(), style);
        setSheetValue(sheet, beginLine, 9,this.map.get("totalTax").toString(), style);
        if(this.map.get("taxAmount")!=null){
            setSheetValue(sheet, beginLine, 15,this.map.get("taxAmount").toString(), style);
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

    private String formatInvoiceStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "正常" :
                        "1".equals(status) ? "失控" :
                                "2".equals(status) ? "作废" :
                                        "3".equals(status) ? "红冲" :
                                                "4".equals(status) ? "异常" : "";
    }

    private String formatQsStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未签收" :
                        "1".equals(status) ? "已签收" : "";
    }
    private String formatdxhyMatchStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未匹配" :
                        "1".equals(status) ? "预匹配" :
                                "2".equals(status) ? "部分匹配":
                                        "3".equals(status) ? "完全匹配":
                                                "4".equals(status) ? "差异匹配":
                                                        "5".equals(status) ? "匹配失败":
                                                                "6".equals(status) ? "取消匹配":"";
    }

    private String formatHostStatus(String status,String tpStatus){
        if(tpStatus!=null&tpStatus!=""){
            if(tpStatus.equals("2")){
                return "已退票";
            }
        }
        return null==status ? "" :
                "0".equals(status) ? "未处理" :
                        "1".equals(status) ? "未处理" :
                                "10".equals(status) ? "未处理":
                                        "13".equals(status) ? "完全匹配":
                                                "14".equals(status) ? "待付款":
                                                        "11".equals(status) ? "已匹配":
                                                                "15".equals(status) ? "已付款":
                                                                        "99".equals(status) ? "已付款":
                                                                                "999".equals(status) ? "已付款":
                                                                                        "9".equals(status) ? "待付款":"";
    }

    private String formatQsType(String type){
        return null==type ? "" :
                "0".equals(type) ? "扫码签收" :
                        "1".equals(type) ? "扫描仪签收" :
                                "2".equals(type) ? "app签收" :
                                        "3".equals(type) ? "导入签收" :
                                                "4".equals(type) ? "手工签收" :
                                                        "5".equals(type) ? "pdf上传签收" : "";
    }

    private String formatRzhStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未认证" :
                        "1".equals(status) ? "已认证" : "";
    }

    private String formatAuthStatus(String type){
        return null==type ? "" :
                "0".equals(type) ? "未认证" :
                        "1".equals(type) ? "已勾选未确认" :
                                "2".equals(type) ? "已确认" :
                                        "3".equals(type) ? "已发送认证" :
                                                "4".equals(type) ? "认证成功" :
                                                        "5".equals(type) ? " 认证失败" : "";
    }

    private String formatFlowType(String type){
        return null==type ? "" :
                        "1".equals(type) ? "商品" :
                                "2".equals(type) ? "费用" :
                                        "3".equals(type) ? "外部红票" :
                                                "4".equals(type) ? "内部红票" :
                                                        "5".equals(type) ? " 供应商红票" :
                                                                "6".equals(type) ? " 租赁" :"7".equals(type) ? "直接认证": "8".equals(type) ? "Ariba":"";
    }
}
