package com.xforceplus.wapp.modules.businessData.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimDetailEntity;
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
 * 索赔信息导出
 */
public final class ClaimDetailInquiryExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public ClaimDetailInquiryExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<ClaimDetailEntity> list = (List<ClaimDetailEntity>)this.map.get(excelName);
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
        for (ClaimDetailEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商号
            setSheetValue(sheet, beginLine, 1, entity.getVndrNbr(), style);
            //索赔号
            setSheetValue(sheet, beginLine, 2, entity.getClaimno(), style);
            //门店号
            setSheetValue(sheet, beginLine, 3, entity.getStoreNbr(), style);
            //部门号
            setSheetValue(sheet, beginLine, 4, entity.getDeptNbr(), style);
            //货物名称
            setSheetValue(sheet, beginLine, 5, entity.getGoodsName(), style);
            //规格型号
            setSheetValue(sheet, beginLine, 6,entity.getGoodsModel(), style);
            //单价
            setSheetValue(sheet, beginLine, 7,formatAmount(entity.getGoodsPrice().toString()), style);
            //单位
            setSheetValue(sheet, beginLine, 8, entity.getGoodsUnit(), style);
            //商品数量
            setSheetValue(sheet, beginLine, 9, entity.getGoodsNumber(), style);
            //金额
            setSheetValue(sheet, beginLine, 10, formatAmount(entity.getGoodsAmount().toString()), style);
            //税额
            setSheetValue(sheet, beginLine, 11, formatAmount(entity.getTaxAmount().toString()), style);
            //税率
            setSheetValue(sheet, beginLine, 12, entity.getTaxRate(), style);
            //商品编码
            setSheetValue(sheet, beginLine, 13, entity.getItemNbr(), style);
            //商品条码
            setSheetValue(sheet, beginLine, 14, entity.getUpcNbr(), style);
            //定案日期
            setSheetValue(sheet, beginLine, 15, formatDate(entity.getFinalDate()), style);
            //商品库存id
            setSheetValue(sheet, beginLine, 16, entity.getVendorStockId(), style);
            //数量
            setSheetValue(sheet, beginLine, 17, entity.getVnpkQty(), style);
            //类别码
            setSheetValue(sheet, beginLine, 18, entity.getGategoryNbr(), style);
            //单个成本
            setSheetValue(sheet, beginLine, 19, formatAmount(entity.getVnpkCost().toString()), style);
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }

}
