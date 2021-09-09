package com.xforceplus.wapp.modules.signin.toexcel;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.apache.http.client.utils.DateUtils.formatDate;

/**
 * CreateBy leal.liang on 2018/4/19.
 **/
public class SignatureProcessingExcel extends AbstractExportExcel {


    private Map<String, List<RecordInvoiceEntity>> map;

    public SignatureProcessingExcel(Map<String, List<RecordInvoiceEntity>> map) {
        this.map = map;
    }


    @Override
    protected String getExcelUri() {
        return "export/signin/SignatureProcessing.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<RecordInvoiceEntity> list = this.map.get("InvoiceEntityList");
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 1, 1);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
        //数据填入excel
        for (RecordInvoiceEntity recordInvoiceEntity : list) {
                //序号
                setSheetValue(sheet, beginLine, 0, String.valueOf(beginLine-1), style);
                //签收状态
                setSheetValue(sheet, beginLine, 1, qsResult(recordInvoiceEntity.getQsStatus()), style);
                //签收类型
                setSheetValue(sheet, beginLine, 2, qsType(recordInvoiceEntity.getQsType()), style);
                //签收日期
                setSheetValue(sheet, beginLine, 3, dateFormat2.format(recordInvoiceEntity.getSignInDate()), style);
                //发票代码
                setSheetValue(sheet, beginLine, 4, recordInvoiceEntity.getInvoiceCode(), style);
                //发票号码
                setSheetValue(sheet, beginLine, 5, recordInvoiceEntity.getInvoiceNo(), style);
                //开票日期
                if(recordInvoiceEntity.getInvoiceDate()!=null){
                    setSheetValue(sheet, beginLine, 6, dateFormat2.format(recordInvoiceEntity.getInvoiceDate()), style);
                }else{
                     setSheetValue(sheet, beginLine, 6,"--", style);
                }
                //金额
                setSheetValue(sheet, beginLine, 7, formatBigDecimal(recordInvoiceEntity.getInvoiceAmount()), style);
                //税额
                setSheetValue(sheet, beginLine, 8, formatBigDecimal(recordInvoiceEntity.getTaxAmount()), style);

                beginLine++;
        }
    }


    private String formatBigDecimal(BigDecimal val){
        if(val!=null){
            return String.valueOf(new DecimalFormat("#,##0.00").format(val));
        }
        return null;
    }

    private String qsResult(String code){
        String value=null;
        if("0".equals(code)){
            value="签收失败";
        }else if("1".equals(code)){
            value="签收成功";
        }else{
            value=null;
        }
        return value;
    }

    //0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收，5-pdf上传
    private String qsType(String code){
        String val=null;
        if(code!=null){
            switch (code){
                case"4" : val="手工签收"; break;
                case"2": val="app签收"; break;
                case"0": val="扫码签收"; break;
                case"1": val="扫描仪签收"; break;
                case"3": val="导入签收"; break;
                case"5": val="pdf上传"; break;
                default: val=null; break;
            }
            return val;
        }
        return null;
    }
}
