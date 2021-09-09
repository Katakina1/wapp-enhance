package com.xforceplus.wapp.modules.businessData.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * Created by 1 on 2018/12/20 21:29
 */
public class PoQueryExcel extends AbstractExportExcel {
    private final PagedQueryResult<PoEntity> poEntityPagedQueryResult;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public PoQueryExcel(PagedQueryResult<PoEntity> poEntityPagedQueryResult,String excelTempPath, String excelName) {
        this.poEntityPagedQueryResult = poEntityPagedQueryResult;
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
        final List<PoEntity> list = this.poEntityPagedQueryResult.getResults();
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 1, 0);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (PoEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //JV
            setSheetValue(sheet, beginLine, 1, entity.getJvcode(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 2, entity.getVenderid(), style);
            //订单号
            setSheetValue(sheet, beginLine, 3, entity.getPocode(), style);
            //订单类型
            setSheetValue(sheet, beginLine, 4, entity.getPoType(), style);
            //收货日期
            setSheetValue(sheet, beginLine, 5, formatDate(entity.getReceiptdate()), style);
            //收货号
            setSheetValue(sheet, beginLine, 6, entity.getReceiptid(), style);
            //交易号
            setSheetValue(sheet, beginLine, 7, entity.getTractionNbr(), style);
            //收货金额
            setSheetValue(sheet, beginLine, 8, formatAmount(entity.getReceiptAmount().toString()), style);
            //未结金额
           // setSheetValue(sheet, beginLine, 9, entity.getAmountunpaid().toString(), style);
            //已结金额
           // setSheetValue(sheet, beginLine, 10, entity.getAmountpaid().toString(), style);
            //发票号
            setSheetValue(sheet, beginLine, 9, entity.getInvoiceno(), style);
            //匹配状态
            setSheetValue(sheet, beginLine, 10,formatDxhyMatchStatus(entity.getDxhyMatchStatus()), style);
            //订单状态
            setSheetValue(sheet, beginLine, 11,formatHostStatus(entity.getHoststatus()), style);
            //付款期限
           // setSheetValue(sheet, beginLine, 14,formatDate(entity.getDueDate()), style);

            beginLine++;
        }
    }

    private String formatHostStatus(String dxhyMatchStatus) {
        String str = "";
        if(StringUtils.isEmpty(dxhyMatchStatus)){
            return "未处理";
        }
        if(dxhyMatchStatus.equals("0")){
            str="未处理";
        }
        if(dxhyMatchStatus.equals("1")){
            str="未处理";
        }
        if(dxhyMatchStatus.equals("5")){
            str="已处理";
        }
        if(dxhyMatchStatus.equals("10")){
            str="未处理";
        }
        if(dxhyMatchStatus.equals("13")){
            str="已删除";
        }
        if(dxhyMatchStatus.equals("14")){
            str="待付款";
        }
        if(dxhyMatchStatus.equals("11")){
            str="已匹配";
        }
        if(dxhyMatchStatus.equals("12")){
            str="已匹配";
        }
        if(dxhyMatchStatus.equals("15")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("19")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("9")){
            str="待付款";
        }
        if(dxhyMatchStatus.equals("99")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("999")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("8")){
            str="HOLD";
        }
        return str;
    }

    private String formatDxhyMatchStatus(String dxhyMatchStatus) {
        String str = "";
        if(StringUtils.isEmpty(dxhyMatchStatus)){
            return "";
        }
        if(dxhyMatchStatus.equals("0")){
            str="未匹配";
        }
        if(dxhyMatchStatus.equals("1")){
            str="预匹配";
        }
        if(dxhyMatchStatus.equals("2")){
            str="部分匹配";
        }
        if(dxhyMatchStatus.equals("3")){
            str="完全匹配";
        }
        if(dxhyMatchStatus.equals("4")){
            str="差异匹配";
        }
        if(dxhyMatchStatus.equals("5")){
            str="匹配失败";
        }
        if(dxhyMatchStatus.equals("6")){
            str="取消匹配";
        }
        return str;
    }



    /*private String formatPoType(String poType) {

        if(StringUtils.isEmpty(poType)){
            return "";
        }
        if(poType.equals()){
            return "";
        }
    }*/

    public String formatDate(Date date){
        if(date ==null){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);

    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }

}
