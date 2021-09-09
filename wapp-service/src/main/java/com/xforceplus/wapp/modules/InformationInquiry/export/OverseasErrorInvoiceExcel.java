package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.OverseasInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
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
 * 海外问题发票信息导出
 */
public final class OverseasErrorInvoiceExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public OverseasErrorInvoiceExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<OverseasInvoiceEntity> list = (List<OverseasInvoiceEntity>)this.map.get(excelName);
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
        for (OverseasInvoiceEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //公司代码
            setSheetValue(sheet, beginLine, 1, entity.getCompanyCode(), style);
            //orgcode
            setSheetValue(sheet, beginLine, 2, entity.getJvcode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 3, entity.getInvoiceNo(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceCode(), style);
           //开票日期
            setSheetValue(sheet, beginLine, 5, formatDateString(entity.getInvoiceDate()), style);
            //供应商号
            setSheetValue(sheet, beginLine, 6, entity.getVenderid(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 7, entity.getGfname(), style);
            //店号
            setSheetValue(sheet, beginLine, 8,entity.getStore(), style);
            //税额
            setSheetValue(sheet, beginLine, 9, entity.getTaxAmount().toString(), style);
            //税率
            setSheetValue(sheet, beginLine, 11, entity.getTaxRate().toString(), style);
            //税码
            setSheetValue(sheet, beginLine, 10, entity.getTaxCode(), style);
            //成本金额
            setSheetValue(sheet, beginLine, 12,entity.getCostAmount().toString(), style);
            //价税合计
            setSheetValue(sheet, beginLine, 13,entity.getTotalAmount().toString(), style);
            //凭证号
            setSheetValue(sheet, beginLine, 14,entity.getCertificateNo(), style);
            //备注
            setSheetValue(sheet, beginLine, 15,entity.getRemarks(), style);
            //类别
            setSheetValue(sheet, beginLine, 16,entity.getFlowType(), style);
            //问题描述
            setSheetValue(sheet, beginLine, 17,entity.getErrorDescription(), style);




            beginLine++;
        }
    }


    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

    private String formatedxhyMatchStatusType(String dxhyMatchStatus){
        String value="";
        if("0".equals(dxhyMatchStatus)){
            value="未匹配";
        }else if("1".equals(dxhyMatchStatus)){
            value="预匹配";
        }else if("2".equals(dxhyMatchStatus)){
            value="部分匹配";
        }else if("3".equals(dxhyMatchStatus)){
            value="完全匹配";
        }else if("4".equals(dxhyMatchStatus)){
            value="差异匹配";
        }else if("5".equals(dxhyMatchStatus)){
            value="匹配失败";
        }else if("6".equals(dxhyMatchStatus)){
            value="取消匹配";
        }
        return value;
    }

    private String formateVenderType(String hostStatus){
        String value="";
        if("".equals(hostStatus)){
            value="未处理";
        }else if("0".equals(hostStatus)){
            value="未处理";
        }else if("1".equals(hostStatus)){
            value="未处理";
        }else if("5".equals(hostStatus)){
            value="已处理";
        }else if("10".equals(hostStatus)){
            value="未处理";
        }else if("13".equals(hostStatus)){
            value="已删除";
        }else if("14".equals(hostStatus)){
            value="待付款";
        }else if("11".equals(hostStatus)){
            value="已匹配";
        }else if("12".equals(hostStatus)){
            value="已匹配";
        }else if("15".equals(hostStatus)){
            value="已匹配";
        } else if("19".equals(hostStatus)){
            value="已付款";
        }else if("9".equals(hostStatus)){
            value="待付款";
        }else if("99".equals(hostStatus)){
            value="当月付款";
        }else if("999".equals(hostStatus)){
            value="已付款";
        }else if("8".equals(hostStatus)){
            value="HOLD";
        }

        return value;
    }
}
