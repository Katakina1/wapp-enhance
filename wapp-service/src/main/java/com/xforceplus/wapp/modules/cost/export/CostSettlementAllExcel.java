package com.xforceplus.wapp.modules.cost.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 订单信息导出
 */
public final class CostSettlementAllExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public CostSettlementAllExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<SettlementEntity> list = (List<SettlementEntity>)this.map.get(excelName);
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

        for (SettlementEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //费用号
            setSheetValue(sheet, beginLine, 1, entity.getCostNo(), style);
            //审批人邮箱
            setSheetValue(sheet, beginLine, 2, entity.getApproverEmail(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 3, entity.getVenderId(), style);
            //供应商名称
            setSheetValue(sheet, beginLine, 4, entity.getVenderName(), style);
            //开户行名称
            setSheetValue(sheet, beginLine, 5, entity.getBankName(), style);
            //银行账号
            setSheetValue(sheet, beginLine, 6, entity.getBankAccount(), style);
            //费用金额
            setSheetValue(sheet, beginLine, 7, formatAmount(entity.getSettlementAmount()== null ? "" : entity.getSettlementAmount().toString()), style);
            //EPS_NO
            setSheetValue(sheet, beginLine, 8, entity.getEpsNo(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 9, entity.getInvoiceNo(), style);
            //价税合计
            setSheetValue(sheet, beginLine, 10, formatAmount(entity.getTotalAmount()== null ? "" : entity.getTotalAmount().toString()), style);
            //申请日期
            setSheetValue(sheet, beginLine, 11, formatDate(entity.getCreateDate()), style);
            //walmart状态
            setSheetValue(sheet, beginLine, 12, getisDel(entity.getWalmartStatus()), style);
            //审核变更日期
            setSheetValue(sheet, beginLine, 13, formatDate(entity.getWalmartDate()), style);
            //驳回理由
            setSheetValue(sheet, beginLine, 14, entity.getRejectReason(), style);
            //数据来源
            setSheetValue(sheet, beginLine, 15, getisModel(entity.getPayModel()), style);
            beginLine++;
        }
    }


    private String formatDate(String source) {
        return source == null ? "" : source.substring(0, 10);
    }
    private String formatAmount(String amount) {
        if(amount==null||amount==""|| amount.equals("0")){
            return "0";
        }
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
    private String getisDel(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="已提交";
        }else if("1".equals(getisdel)){
            value="待收票";
        }else if("2".equals(getisdel)){
            value="审批不通过";
        }else if("3".equals(getisdel)){
            value="沃尔玛审批中";
        }else if("4".equals(getisdel)){
            value="待付款";
        }else if("5".equals(getisdel)){
            value="已付款";
        }else if("6".equals(getisdel)){
            value="已冲账";
        }else if("7".equals(getisdel)){
            value="已退票";
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

    private String getisModel(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="wapp";
        }else if("1".equals(getisdel)){
            value="预付款";
        }else if("3".equals(getisdel)){
            value="bpms";
        }
        return value;
    }

}
