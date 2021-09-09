package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.List;
import java.util.Map;

public final class MakeInvoiceSearchExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public MakeInvoiceSearchExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<SupplierInformationSearchEntity> list = (List<SupplierInformationSearchEntity>) this.map.get(excelName);
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
        for (SupplierInformationSearchEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //JV码
            setSheetValue(sheet, beginLine, 1, entity.getOrgcode(), style);
            //合资公司名称
            setSheetValue(sheet, beginLine, 2, entity.getOrgName(), style);            
            //纳税人识别号
            setSheetValue(sheet, beginLine, 3, entity.getTaxno(), style);
            //公司地址
            setSheetValue(sheet, beginLine, 4, entity.getAddress(), style);
            //联系电话
            setSheetValue(sheet, beginLine, 5, entity.getPhone(), style);
            //开户行
            setSheetValue(sheet, beginLine, 6, entity.getBank(), style);
            //账号
            setSheetValue(sheet, beginLine, 7, entity.getAccount(), style);
            //备注
            setSheetValue(sheet, beginLine, 8, entity.getRemark(), style);
            //成本中心
            setSheetValue(sheet, beginLine, 9, entity.getStoreNumber(),style);
            beginLine++;
          
        }
       
    }

 
}
