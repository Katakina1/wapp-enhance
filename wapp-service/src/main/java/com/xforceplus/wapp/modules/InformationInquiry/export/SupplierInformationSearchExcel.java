package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.List;
import java.util.Map;

public final class SupplierInformationSearchExcel extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public SupplierInformationSearchExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
            //供应商业务类型
            setSheetValue(sheet, beginLine, 1, fromatUserType(entity.getUsertype()), style);
            //供应商号码
            setSheetValue(sheet, beginLine, 2, entity.getUserCode(), style);
            //供应商名字
            setSheetValue(sheet, beginLine, 3, entity.getUserName(), style);
            //供应商税号
            setSheetValue(sheet, beginLine, 4, entity.getTaxno(), style);
            //联系人
            setSheetValue(sheet, beginLine, 5, entity.getFinusername(), style);
            //联系电话
            setSheetValue(sheet, beginLine, 6, entity.getPhone(), style);
            //传真	
            setSheetValue(sheet, beginLine, 7, entity.getFax(), style);
            //邮寄地址
            setSheetValue(sheet, beginLine, 8, entity.getEmail(), style);
            //邮寄方式
            setSheetValue(sheet, beginLine, 9, entity.getPostType(), style);
             //邮寄地址
            setSheetValue(sheet, beginLine, 10, entity.getPostAddress(), style);
            //供应闪公告类型
            setSheetValue(sheet, beginLine, 11, formatOrgLevel(entity.getOrgLevel()), style);

            beginLine++;
        }
       
    }

    private String  formatOrgLevel(String usertype) {
        if(StringUtils.isEmpty(usertype)){
            return "--";
        }
        if(usertype.equals("0")){
            return "商品-KEY Vendor";
        }else if(usertype.equals("1")){
            return "商品-VIP Vendor";
        }else if(usertype.equals("2")){
            return "商品-其他";
        }else if(usertype.equals("3")){
            return "费用-其他";
        }else {
            return "--";
        }


    }

    private String fromatUserType(String usertype) {
        if(StringUtils.isEmpty(usertype)){
            return "--";
        }
        if(usertype.equals("P")||usertype.equals("PP")){
            return "商品";
        }else if(usertype.equals("E")||usertype.equals("EJ")){
            return "费用";
        }else{
            return "--";
        }
    }


}
