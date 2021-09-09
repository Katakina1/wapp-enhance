package com.xforceplus.wapp.modules.analysis.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class MaterialInvoiceSubmitDetailExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public MaterialInvoiceSubmitDetailExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>) this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 2);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        int index = 1;
      
        //数据填入excel
        for (ComprehensiveInvoiceQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商号码
            setSheetValue(sheet, beginLine, 1, entity.getVenderId(), style);
            //供应商名字
            setSheetValue(sheet, beginLine, 2, entity.getVenderName(), style);            
            //发票号
            setSheetValue(sheet, beginLine, 3, entity.getInvoiceNo(), style);
            //发票日期
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceDate(), style);
            //税率
            setSheetValue(sheet, beginLine, 5, entity.getTaxRate()==null?"":entity.getTaxRate().toString(), style);
            //税额
            setSheetValue(sheet, beginLine, 6, CommonUtil.formatMoney(entity.getTaxAmount()), style);
            //价税合计
            setSheetValue(sheet, beginLine, 7, CommonUtil.formatMoney(entity.getTotalAmount()), style);
            //发票类型
            setSheetValue(sheet, beginLine, 8, formatInvoiceType(entity.getInvoiceType()), style);
            //购货单位名称
            setSheetValue(sheet, beginLine, 9, entity.getGfName(), style);
            //数据提交日期
            setSheetValue(sheet, beginLine, 10, formatDate(entity.getMatchDate()), style);
            //实物发票处理日期
            setSheetValue(sheet, beginLine, 11, formatDate(entity.getScanMatchDate()), style);
            beginLine++;
          
        }
       
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    

    private String formatInvoiceType(String type){
        return null==type? "" :
                "01".equals(type) ? "增值税专用发票" :
                        "02".equals(type) ? "机动车销售统一发票" :
                                "04".equals(type) ? "增值税普通发票" :
                                        "10".equals(type) ? "增值税电子普通发票" :
                                        	"11".equals(type) ? "增值税普通发票（卷票）" :
                                                "14".equals(type) ? "增值税电子普通发票（通行费）" : "";
        
        
    }

 
}
