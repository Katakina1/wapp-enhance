package com.xforceplus.wapp.modules.invoiceBorrow.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.invoiceBorrow.entity.BorrowEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 借阅记录导出
 */
public final class BorrowRecordExcel extends AbstractExportExcel {

    private final Map<String, List<BorrowEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public BorrowRecordExcel(Map<String, List<BorrowEntity>> map, String excelTempPath, String excelName) {
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
        final List<BorrowEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 8);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (BorrowEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //jvcode
            setSheetValue(sheet, beginLine, 1,entity.getJvCode()== null ? "" :entity.getJvCode(), style);
            //companycode
            setSheetValue(sheet, beginLine, 2,entity.getCompanyCode()== null ? "" :entity.getCompanyCode(), style);
            //凭证号
            setSheetValue(sheet, beginLine, 3,entity.getCertificateNo()== null ? "" :entity.getCertificateNo(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceNo(), style);
            //装箱号
            setSheetValue(sheet, beginLine, 5,entity.getPackingno()== null ? "" :entity.getPackingno(), style);
            //装订册号
            setSheetValue(sheet, beginLine, 6,entity.getBbindingno()== null ? "" :entity.getBbindingno(), style);
            //借阅日期
            if("0".equals(entity.getOperateType())) {
                setSheetValue(sheet, beginLine, 7, formatDateString(entity.getBorrowDate()), style);
            } else{
                setSheetValue(sheet, beginLine, 7, "", style);
            }
            //借阅人
            if("0".equals(entity.getOperateType())) {
                setSheetValue(sheet, beginLine, 8, entity.getBorrowUser(), style);
            } else{
                setSheetValue(sheet, beginLine, 8, "", style);
            }
            //借阅原因
            if("0".equals(entity.getOperateType())) {
                setSheetValue(sheet, beginLine, 9, entity.getBorrowReason(), style);
            } else {
                setSheetValue(sheet, beginLine, 9, "", style);
            }
            //借阅部门
            if("0".equals(entity.getOperateType())) {
                setSheetValue(sheet, beginLine, 10, entity.getBorrowDept(), style);
            } else{
                setSheetValue(sheet, beginLine, 10, "", style);
            }
            //归还日期
            if("1".equals(entity.getOperateType())) {
                setSheetValue(sheet, beginLine, 11, formatDateString(entity.getBorrowDate()), style);
            } else{
                setSheetValue(sheet, beginLine, 11, "", style);
            }
            //归还人
            if("1".equals(entity.getOperateType())) {
                setSheetValue(sheet, beginLine, 12,entity.getBorrowUser(), style);
            } else{
                setSheetValue(sheet, beginLine, 12,"", style);
            }
            //供应商号
            setSheetValue(sheet, beginLine, 13,entity.getVenderId()==null?"":entity.getVenderId(), style);

            //eps号
            setSheetValue(sheet, beginLine, 14,entity.getEpsNo()==null?"":entity.getEpsNo(), style);
            beginLine++;
        }
    }

    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

}
