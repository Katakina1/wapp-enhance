package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
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
 * 索赔信息导出
 */
public final class ClaimInquiryExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public ClaimInquiryExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<ClaimEntity> list = (List<ClaimEntity>)this.map.get(excelName);
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
        for (ClaimEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商号
            setSheetValue(sheet, beginLine, 1, entity.getVenderid(), style);
            //门店号
            setSheetValue(sheet, beginLine, 2, entity.getStoreNbr(), style);
            //部门号
            setSheetValue(sheet, beginLine, 3, entity.getDept(), style);
            //JVCODE
            setSheetValue(sheet, beginLine, 4, entity.getJvcode(), style);
            //索赔号
            setSheetValue(sheet, beginLine, 5, entity.getClaimno(), style);
            //对应货款发票号
            setSheetValue(sheet, beginLine, 6, entity.getInvoiceno(), style);
            //定案日期
            setSheetValue(sheet, beginLine, 7, formatDate(entity.getPostdate()), style);
            //索赔金额
            setSheetValue(sheet, beginLine, 8, formatAmount(entity.getClaimAmount().toString()), style);
            //匹配状态
            setSheetValue(sheet, beginLine, 9,formatedxhyMatchStatusType(entity.getMatchstatus()), style);
            //索赔状态
            setSheetValue(sheet, beginLine, 10,formateVenderType(entity.getHostStatus()), style);
            beginLine++;
        }
        setSheetValue(sheet, beginLine, 0,"合计", style);
        setSheetValue(sheet, beginLine, 8,formatAmount(this.map.get("claimAmount").toString()), style);
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatedxhyMatchStatusType(String hostStatus){
        String value="";
        if(StringUtils.isEmpty(hostStatus)){
            return "";
        }else if("0".equals(hostStatus)){
            value="未匹配";
        }else if("1".equals(hostStatus)){
            value="预匹配";
        }else if("2".equals(hostStatus)){
            value="部分匹配";
        }else if("3".equals(hostStatus)){
            value="完全匹配";
        }else if("4".equals(hostStatus)){
            value="差异匹配";
        }else if("5".equals(hostStatus)){
            value="匹配失败";
        }else if("6".equals(hostStatus)){
            value="取消匹配";
        }
        return value;
    }

    private String formateVenderType(String hostStatus){
        String value="";
        if(StringUtils.isEmpty(hostStatus)){
            return "未处理";
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
            value="已付款";
        }else if("999".equals(hostStatus)){
            value="已付款";
        }else if("8".equals(hostStatus)){
            value="HOLD";
        }

        return value;
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
