package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 订单信息导出
 */
public final class QuestionnaireExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public QuestionnaireExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<QuestionnaireEntity> list = (List<QuestionnaireEntity>)this.map.get(excelName);
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

        for (QuestionnaireEntity entity : list) {
            setSheetValue(sheet, beginLine, 0, entity.getId(), style);
            setSheetValue(sheet, beginLine, 1, index++, style);
            setSheetValue(sheet, beginLine, 2, getisDel(entity.getIsDel()), style);
            setSheetValue(sheet, beginLine, 3, formatDate(entity.getDateT()), style);
            setSheetValue(sheet, beginLine, 4, entity.getInputUser(), style);
            setSheetValue(sheet, beginLine, 5, entity.getjV(), style);
            setSheetValue(sheet, beginLine, 6, entity.getVendorNo(), style);
            setSheetValue(sheet, beginLine, 7, entity.getInvNo(), style);
            setSheetValue(sheet, beginLine, 8, formatAmount(entity.getInvoiceCost()), style);
            setSheetValue(sheet, beginLine, 9, formatAmount(entity.getTaxAmount()), style);
            setSheetValue(sheet, beginLine, 10,formatAmount(entity.getTaxRate()), style);
            setSheetValue(sheet, beginLine, 11, entity.getTaxType(), style);
            setSheetValue(sheet, beginLine, 12, formatAmount(entity.getwMCost()), style);
            setSheetValue(sheet, beginLine, 13, entity.getBatchID(), style);
            setSheetValue(sheet, beginLine, 14, entity.getpONo(), style);
            setSheetValue(sheet, beginLine, 15, entity.getTrans(), style);
            setSheetValue(sheet, beginLine, 16, entity.getRece(), style);
            setSheetValue(sheet, beginLine, 17, errCode(entity.getIsDel(),entity.getErrCode()), style);
            setSheetValue(sheet, beginLine, 18, errCode(entity.getIsDel(),entity.getErrDesc()), style);
            setSheetValue(sheet, beginLine, 19, errCode(entity.getIsDel(),entity.getErrStatus()), style);
            setSheetValue(sheet, beginLine, 20, formatDate(entity.getInvoiceDate()), style);

            beginLine++;
        }
    }


    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String getisDel(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="未处理";
        }else if("1".equals(getisdel)){
            value="已退票";
        }else if("2".equals(getisdel)){
            value="已处理";
        }else{
            value="未处理";
        }
        return value;
    }
    private String errCode(String getisdel,String code){
        String value="";
        if("2".equals(getisdel)){
            value="";
        }else{
            value=code;
        }
        return value;
    }

    private String fromAmount(Double d){
        BigDecimal b=new BigDecimal(d);
        DecimalFormat df=new DecimalFormat("######0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(b);
    }
    private String formatAmount(String d) {
        try {
            if(StringUtils.isEmpty(d)){
                return "";
            }else{
                BigDecimal b=new BigDecimal(Double.parseDouble(d));
                DecimalFormat df=new DecimalFormat("######0.00");
                df.setRoundingMode(RoundingMode.HALF_UP);
                return df.format(b);
            }
        }catch (Exception e){
            return "";
        }
    }

}
