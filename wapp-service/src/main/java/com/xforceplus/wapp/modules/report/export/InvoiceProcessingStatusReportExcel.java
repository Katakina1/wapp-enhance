package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
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
 * 发票处理状态报告导出
 */
public final class InvoiceProcessingStatusReportExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceProcessingStatusReportExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<MatchEntity> list = (List<MatchEntity>)this.map.get(excelName);
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
        for (MatchEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //购方名称
            setSheetValue(sheet, beginLine, 1, entity.getGfName(), style);
            //公司代码
            setSheetValue(sheet, beginLine, 2, entity.getCompanyCode(), style);
            //供应商名称
            setSheetValue(sheet, beginLine, 3, entity.getVendername(), style);
            //供应商编码
            setSheetValue(sheet, beginLine, 4, entity.getVenderId(), style);
            //发票金额
            setSheetValue(sheet, beginLine, 5, entity.getInvoiceAmount().toString(), style);
            //发票数量
            setSheetValue(sheet, beginLine, 6, entity.getInvoiceNum(), style);
            //PO金额
            setSheetValue(sheet, beginLine, 7, entity.getPoAmount().toString(), style);
            //PO数量
            setSheetValue(sheet, beginLine, 8, entity.getPoNum(), style);
            //索赔金额
            setSheetValue(sheet, beginLine, 9, entity.getClaimAmount().toString(), style);
            //索赔单数量
            setSheetValue(sheet, beginLine, 10, entity.getClaimNum(), style);
            //匹配日期
            setSheetValue(sheet, beginLine, 11, formatDate(entity.getMatchDate()), style);
            //结算金额
            setSheetValue(sheet, beginLine, 12, entity.getSettlementAmount().toString(), style);
            //HOST状态
            setSheetValue(sheet, beginLine, 13, formateHostStatusType(entity.getHostStatus()), style);
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateHostStatusType(String hostStatus){
        String value="";
        if("0".equals(hostStatus)){
            value="loaded";
        }else if("1".equals(hostStatus)){
            value="loaded";
        }else if("13".equals(hostStatus)){
            value="invoice delete";
        }else if("14".equals(hostStatus)){
            value="invoice reactived";
        }else if("10".equals(hostStatus)){
            value="unmatched";
        }else if("12".equals(hostStatus)){
            value="matched out";
        }else if("11".equals(hostStatus)){
            value="matched eireconciled(manual)";
        }else if("19".equals(hostStatus)){
            value="reconciled(auto)";
        }else if("9".equals(hostStatus)){
            value="extracted for payment";
        }else if("99".equals(hostStatus)){
            value="paid";
        }else if("999".equals(hostStatus)){
            value="purged";
        }
        return value;
    }
}
