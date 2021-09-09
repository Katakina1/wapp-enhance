package com.xforceplus.wapp.modules.redTicket.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 红票审核清单
 */
public class OpenRedTicketExcel extends AbstractExportExcel {

    private final Map<String, List<RedTicketMatch>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public OpenRedTicketExcel(Map<String, List<RedTicketMatch>> map, String excelTempPath, String excelName) {
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
        final List<RedTicketMatch> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 2, 0);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (RedTicketMatch entity : list) {
           //序列号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商号
            setSheetValue(sheet, beginLine, 1, entity.getVenderid(), style);
            //生成红票序列号
            setSheetValue(sheet, beginLine, 2, entity.getRedTicketDataSerialNumber(), style);
            //业务类型
            setSheetValue(sheet, beginLine, 3,formatBusinessType(entity.getBusinessType()), style);
            //红冲总金额
            setSheetValue(sheet, beginLine, 4,entity.getRedTotalAmount().toString(), style);
            //红票通知单号
            setSheetValue(sheet, beginLine, 5, entity.getRedNoticeNumber(), style);

            //是否上传资料
            setSheetValue(sheet, beginLine, 6,formatDataStatus(entity.getDataStatus()), style);
            //是否上传红字通知单
            setSheetValue(sheet, beginLine, 7,formatDataStatus(entity.getNoticeStatus()), style);
            //审核结果
            setSheetValue(sheet, beginLine, 8, formatExamineResult(entity.getExamineResult()), style);
            //审核备注
            setSheetValue(sheet, beginLine, 9, entity.getExamineRemarks(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 10, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 11, entity.getInvoiceNo(), style);
            //发票金额
            String inAmount="";
            String taxAmount="";
            String taxRate="";
            String totalAmount="";
            if(entity.getInvoiceAmount()!=null){
                inAmount=entity.getInvoiceAmount().stripTrailingZeros().toPlainString()+".00";
            }
            if(entity.getTaxAmount()!=null){
                taxAmount= entity.getTaxAmount().stripTrailingZeros().toPlainString()+".00";
            }
            if(entity.getTaxRate()!=null){
                taxRate=entity.getTaxRate().stripTrailingZeros().toPlainString()+".00%";
            }
            if(entity.getTotalAmount()!=null){
                totalAmount=entity.getTotalAmount().stripTrailingZeros().toPlainString()+".00";
            }
            setSheetValue(sheet, beginLine, 12,inAmount , style);
            //发票税额
            setSheetValue(sheet, beginLine, 13,taxAmount, style);
            //发票税率
            setSheetValue(sheet, beginLine, 14,taxRate , style);
            //价税合计
            setSheetValue(sheet, beginLine, 15,totalAmount , style);
            //扫描匹配状态
            setSheetValue(sheet, beginLine, 16, formatScanMatchStatus(entity.getScanMatchStatus()), style);

            beginLine++;
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatBusinessType(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "索赔类型";
        } else  if("2".equals(authStatus)) {
            authStatusName = "协议类型";
        }
        else  if("3".equals(authStatus)) {
            authStatusName = "折让类型";
        }
        return authStatusName;
    }

    private String formatScanMatchStatus(String authStatus) {
        String authStatusName = "";
        if("0".equals(authStatus)) {
            authStatusName = "未扫描匹配";
        } else  if("1".equals(authStatus)) {
            authStatusName = "扫描匹配成功";
        }
        else  if("2".equals(authStatus)) {
            authStatusName = "扫描匹配失败";
        }
        return authStatusName;
    }
    private String formatDataStatus(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "是";
        } else  if("2".equals(authStatus)) {
            authStatusName = "否";
        }
        return authStatusName;
    }
    private String formatExamineResult(String authStatus) {
        //1-未审核 2-同意 3-不同意
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "未审核";
        } else  if("2".equals(authStatus)) {
            authStatusName = "同意";
        }else  if("3".equals(authStatus)) {
            authStatusName = "不同意";
        }
        return authStatusName;
    }
}
