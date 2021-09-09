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
public class InqueryInvoiceCostExcel extends AbstractExportExcel {

    private Map<String, List<RecordInvoiceEntity>> map;

    public InqueryInvoiceCostExcel(Map<String, List<RecordInvoiceEntity>> map) {
        this.map = map;
    }


    @Override
    protected String getExcelUri() {
        return "export/signin/signCostQuery.xlsx";
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
            //扫描日期
            setSheetValue(sheet, beginLine, 1, dateFormat2.format(recordInvoiceEntity.getCreateDate()), style);
            //扫描流水号
            setSheetValue(sheet, beginLine, 2, recordInvoiceEntity.getScanId(), style);
            //文件类型
            setSheetValue(sheet, beginLine, 3, fileTypeResult(recordInvoiceEntity.getFileType()), style);
            //JV
            setSheetValue(sheet, beginLine, 4, recordInvoiceEntity.getJvCode(), style);
            //公司代码
            setSheetValue(sheet, beginLine, 5, recordInvoiceEntity.getCompanyCode(), style);
            //扫描描述
            setSheetValue(sheet, beginLine, 6, recordInvoiceEntity.getNotes(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 7, recordInvoiceEntity.getVenderid(), style);
            //费用号
            setSheetValue(sheet, beginLine, 8, recordInvoiceEntity.getCostNo(), style);

            //发票代码
            setSheetValue(sheet, beginLine, 9, recordInvoiceEntity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 10, recordInvoiceEntity.getInvoiceNo(), style);
            //开票日期
            try {
                setSheetValue(sheet, beginLine, 11, dateFormat2.format(recordInvoiceEntity.getInvoiceDate()), style);
            }catch (Exception e){
                setSheetValue(sheet, beginLine, 11, "", style);
            }
            //金额
            try {
            	setSheetValue(sheet, beginLine, 12, formatBigDecimal(recordInvoiceEntity.getInvoiceAmount()), style);
            }catch(Exception e) {
            	setSheetValue(sheet, beginLine, 12, "", style);
            }
            //税额
            try {
            	setSheetValue(sheet, beginLine, 13, formatBigDecimal(recordInvoiceEntity.getTaxAmount()), style);
            }catch(Exception e) {
            	setSheetValue(sheet, beginLine, 13, "", style);
            	
            }
            //认证结果
            setSheetValue(sheet, beginLine, 14, rzhResult(recordInvoiceEntity.getRzhYesorno()), style);
            //认证日期
            setSheetValue(sheet, beginLine, 15, recordInvoiceEntity.getRzhDate()!=null?dateFormat2.format(recordInvoiceEntity.getRzhDate()):"", style);
            //业务类型
            setSheetValue(sheet, beginLine, 16, flowTypeResult(recordInvoiceEntity.getFlowType()), style);
            //发票匹配状态
            setSheetValue(sheet, beginLine, 17, ScanMatchStatusResult(recordInvoiceEntity), style);
            //匹配失败原因
            setSheetValue(sheet, beginLine, 18, recordInvoiceEntity.getScanFailReason(), style);
            beginLine++;
        }
    }

    private String ScanMatchStatusResult(RecordInvoiceEntity scanMatchStatus) {
        if("1".equals(scanMatchStatus.getFileType())){
            if("1".equals(scanMatchStatus.getScanMatchStatus())){
                return "匹配成功";
            }else if("2".equals(scanMatchStatus.getScanMatchStatus())){
                return "匹配失败";
            }else  {
                return "未匹配";
            }
        }else{
            return "—— ——";
        }
    }

    private String flowTypeResult(String flowType) {
        if("1".equals(flowType)){
            return "商品";
        }else
        if("2".equals(flowType)){
            return "费用";
        }if("3".equals(flowType)){
            return "外红";
        }if("4".equals(flowType)){
            return "内红";
        }if("5".equals(flowType)){
            return "供应商红票";
        }if("6".equals(flowType)){
            return "租赁";
        }
        if("7".equals(flowType)) {
        	return "直接认证";
        }
        if("8".equals(flowType)) {
            return "Ariba";
        }
		return "";
    }

    private String formatBigDecimal(BigDecimal val){
        if(val!=null){
            return String.valueOf(new DecimalFormat("#,##0.00").format(val));
        }
        return null;
    }
    private String rzhResult(String code){
        String value=null;
        if("1".equals(code)){
            value="已认证";
        }else{
            value="未认证";
        }
        return value;
    }


    private String fileTypeResult(String code){
        String value=null;
        if("1".equals(code)){
            value="发票";
        }else if("2".equals(code)){
            value="附件";
        }else if("3".equals(code)){
            value="封面";
        }else{
            value=null;
        }
        return value;
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
