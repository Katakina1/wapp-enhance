package com.xforceplus.wapp.modules.protocol.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
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
 * 导入失败的发票明细导出
 */
public final class InvoiceDetailFailureExcel extends AbstractExportExcel {

    private final Map<String, List<ProtocolInvoiceDetailEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceDetailFailureExcel(Map<String, List<ProtocolInvoiceDetailEntity>> map, String excelTempPath, String excelName) {
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
        final List<ProtocolInvoiceDetailEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 1;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 13);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (ProtocolInvoiceDetailEntity entity : list) {
            //日期
            setSheetValue(sheet, beginLine, 0, formatDate(entity.getDate()), style);
            //顺序号
            setSheetValue(sheet, beginLine, 1, entity.getSeq()==null?"":entity.getSeq(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 2, entity.getVenderId(), style);
            //公司名称
            setSheetValue(sheet, beginLine, 3, entity.getCompanyName(), style);
            //发票号
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceNo()==null?"":entity.getInvoiceNo(), style);
            //金额
            if(entity.getInvoiceAmount()!=null) {
                setSheetValue(sheet, beginLine, 5, entity.getInvoiceAmount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(sheet, beginLine, 5, "", style);
            }
            //Fapi发票
            setSheetValue(sheet, beginLine, 6,entity.getFapiao()==null?"":entity.getFapiao(), style);
            //公司
            setSheetValue(sheet, beginLine, 7,entity.getCompanyCode()==null?"":entity.getCompanyCode(), style);
            //内容
            setSheetValue(sheet, beginLine, 8,entity.getContent()==null?"":entity.getContent(), style);
            //备注
            setSheetValue(sheet, beginLine, 9, entity.getNotes()==null?"":entity.getNotes(), style);
            //邮寄时间
            setSheetValue(sheet, beginLine, 10,  formatDate(entity.getPostDate()), style);
            //快递单号
            setSheetValue(sheet, beginLine, 11,  entity.getPostNo()==null?"": entity.getPostNo(), style);
            //快递公司
            setSheetValue(sheet, beginLine, 12,  entity.getPostCompany()==null?"": entity.getPostCompany(), style);
            //失败原因
            setSheetValue(sheet, beginLine, 13,  entity.getFailureReason()==null?"":entity.getFailureReason(), style);

            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatProtocolStatus(String protocolStatus) {
        if ("0".equals(protocolStatus)) {
            return "协议审批未完成";
        } else if ("1".equals(protocolStatus)) {
            return "协议审批完成";
        }
        return "";
    }

}
