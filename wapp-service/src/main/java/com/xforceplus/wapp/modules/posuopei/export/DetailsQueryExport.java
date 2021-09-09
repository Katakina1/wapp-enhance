package com.xforceplus.wapp.modules.posuopei.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
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
public final class DetailsQueryExport extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public DetailsQueryExport(Map<String,Object> map, String excelTempPath, String excelName) {
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
            setSheetValue(sheet, beginLine, 0, index++, style);
            setSheetValue(sheet, beginLine, 1, entity.getGfTaxNo(), style);
            setSheetValue(sheet, beginLine, 2, entity.getVenderid(), style);
            setSheetValue(sheet, beginLine, 3, entity.getVenderName(), style);
            setSheetValue(sheet, beginLine, 4, formatAmount(entity.getInvoiceAmount().toString()), style);
            setSheetValue(sheet, beginLine, 5, entity.getInvoiceNum(), style);
            setSheetValue(sheet, beginLine, 6, formatAmount(entity.getPoAmount().toString()), style);
            setSheetValue(sheet, beginLine, 7, entity.getPoNum(), style);
            setSheetValue(sheet, beginLine, 8, formatAmount(entity.getClaimAmount().toString()), style);
            setSheetValue(sheet, beginLine, 9, entity.getClaimNum(), style);
            setSheetValue(sheet, beginLine, 10, formatDate(entity.getMatchDate()), style);
            setSheetValue(sheet, beginLine, 11, formatAmount(entity.getSettlementamount().toString()), style);

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
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
}
