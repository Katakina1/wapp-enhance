package com.xforceplus.wapp.modules.scanRefund.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryEntity;
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
 * 订单信息导出
 */
public final class RebatenoForQueryInquiryExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public RebatenoForQueryInquiryExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<RebatenoForQueryEntity> list = (List<RebatenoForQueryEntity>)this.map.get(excelName);
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

        for (RebatenoForQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //签收状态
            setSheetValue(sheet, beginLine, 1, formateVenderType(entity.getQsStatus()), style);
            //签收日期
            setSheetValue(sheet, beginLine, 2, formatDate(entity.getQsDate()), style);
            //eps_no
            setSheetValue(sheet, beginLine, 3, entity.getEpsNo(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 5, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 6, formatDate(entity.getCreateDate()), style);
            //供应商号
            setSheetValue(sheet, beginLine, 7, entity.getVenderid(), style);
            //购方名
            setSheetValue(sheet, beginLine, 8, entity.getGfName(), style);
            //销方名
            setSheetValue(sheet, beginLine, 9, entity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 10, entity.getInvoiceAmount().toString(), style);
            //税额
            setSheetValue(sheet, beginLine, 11, entity.getTaxAmount().toString(), style);
            //JvCode
            setSheetValue(sheet, beginLine, 12, entity.getJvCode(), style);

            setSheetValue(sheet, beginLine, 13, entity.getCompanyCode(), style);

            setSheetValue(sheet, beginLine, 14, entity.getNotes(), style);

            setSheetValue(sheet, beginLine, 15, entity.getRebateNo(), style);

            setSheetValue(sheet, beginLine, 16, entity.getRebateExpressno(), style);

            setSheetValue(sheet, beginLine, 17, entity.getMailCompany(), style);

            setSheetValue(sheet, beginLine, 18, entity.getMailDate(), style);

            setSheetValue(sheet, beginLine, 19, formateFlowType(entity.getFlowType()), style);

            setSheetValue(sheet, beginLine, 20, formatDate(entity.getRebateDate()), style);

            beginLine++;
        }
    }


    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateVenderType(String qsStatus){
        String value="";
        if("0".equals(qsStatus)){
            value="签收失败";
        }else if("1".equals(qsStatus)){
            value="签收成功";
        }
        return value;
    }

    private String formateFlowType(String qsStatus){
        String value="";
        if("1".equals(qsStatus)){
            value="商品";
        }else if("2".equals(qsStatus)){
            value="费用";
        }else if("3".equals(qsStatus)){
            value="外部红票";
        }else if("4".equals(qsStatus)){
            value="内部红票";
        }else if("5".equals(qsStatus)){
            value="供应商红票";
        }else if("6".equals(qsStatus)){
            value="租赁";
        }else if("7".equals(qsStatus)){
            value="直接认证";
        }else if("8".equals(qsStatus)){
            value="Ariba";
        }
        return value;
    }
}
