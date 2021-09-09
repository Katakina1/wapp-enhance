package com.xforceplus.wapp.modules.redTicket.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
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
 * 红票审核清单
 */
public class RedTicketMatchExamineQueryExcel extends AbstractExportExcel {

    private final Map<String, List<Object>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public RedTicketMatchExamineQueryExcel(Map<String, List<Object>> map, String excelTempPath, String excelName) {
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
        final List<Object> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
//        final XSSFCellStyle style = getCellStyle(sheet, 2, 0);
        XSSFCellStyle style = workBook.createCellStyle();
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        int index = 1;
        //数据填入excel
        for (Object item : list) {
            if (item instanceof RedTicketMatchDetail) {
                RedTicketMatchDetail redTicketMatchDetail = (RedTicketMatchDetail) item;
                //序列号
                setSheetValue(sheet, beginLine, 0, index++, style);
                //货物(劳务服务)名称
                setSheetValue(sheet, beginLine, 4, "商品一批(详见清单)_" + redTicketMatchDetail.getRedTicketDataSerialNumber(), style);
                //单位
                setSheetValue(sheet, beginLine, 5, redTicketMatchDetail.getGoodsUnit(), style);
                //数量
                setSheetValue(sheet, beginLine, 6, redTicketMatchDetail.getRedRushNumber(), style);
                //单价
                setSheetValue(sheet, beginLine, 7, redTicketMatchDetail.getRedRushPrice().toString(), style);
                //金额
                setSheetValue(sheet, beginLine, 8, redTicketMatchDetail.getRedRushAmount().setScale(2,BigDecimal.ROUND_UP).toString(), style);
                //税率
                setSheetValue(sheet, beginLine, 9, new BigDecimal(redTicketMatchDetail.getTaxRate()).setScale(2,BigDecimal.ROUND_UP).toString(), style);
                //税额
                setSheetValue(sheet, beginLine, 10, redTicketMatchDetail.getRedRushAmount().multiply(new BigDecimal(redTicketMatchDetail.getTaxRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_UP).toString(), style);
                //开红票通知单理由
                setSheetValue(sheet, beginLine, 11, formatBusiness(redTicketMatchDetail.getBusinessType()), style);
                //办理类型
                setSheetValue(sheet, beginLine, 13, "购买方办理", style);
                //蓝票是否已抵扣
                setSheetValue(sheet, beginLine, 14, "是", style);
            } else if (item instanceof InvoiceEntity) {
                InvoiceEntity invoiceEntity = (InvoiceEntity) item;
                //供应商号
                setSheetValue(sheet, beginLine, 1, invoiceEntity.getVenderid(), style);
                //纳税识别号
                setSheetValue(sheet, beginLine, 2, invoiceEntity.getXfTaxNo(), style);
                //供应商名称
                setSheetValue(sheet, beginLine, 3, invoiceEntity.getXfName(),style);

//                //购方名称
//                setSheetValue(sheet, beginLine, 7, invoiceEntity.getGfName(), style);
//                //购方识别号
//                setSheetValue(sheet, beginLine, 8, invoiceEntity.getGfTaxNo(), style);
//                //开票日期
//                setSheetValue(sheet, beginLine, 22, invoiceEntity.getInvoiceDate(), style);
            } else if (item instanceof String) {
                String code = (String) item;
                //税收分类编码
                setSheetValue(sheet, beginLine, 12, code, style);
                beginLine++;
            }
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatBusinessType(String authStatus) {
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "索赔类型";
        } else if ("2".equals(authStatus)) {
            authStatusName = "协议类型";
        } else if ("3".equals(authStatus)) {
            authStatusName = "折让类型";
        }
        return authStatusName;
    }
    private String formatBusiness(String authStatus) {
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "退货";
        } else if ("2".equals(authStatus)) {
            authStatusName = "协议";
        } else if ("3".equals(authStatus)) {
            authStatusName = "折让";
        }
        return authStatusName;
    }
    private String formatDataStatus(String authStatus) {
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "是";
        } else if ("2".equals(authStatus)) {
            authStatusName = "否";
        }
        return authStatusName;
    }

    private String formatExamineResult(String authStatus) {
        //1-未审核 2-同意 3-不同意
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "未审核";
        } else if ("2".equals(authStatus)) {
            authStatusName = "同意";
        } else if ("3".equals(authStatus)) {
            authStatusName = "不同意";
        }
        return authStatusName;
    }
}
