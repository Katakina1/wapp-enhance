package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.base.entity.CustomPFREntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

public class PFRListExcel extends AbstractExportExcel {

    private final Map<String, List<CustomPFREntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public PFRListExcel(Map<String, List<CustomPFREntity>> map, String excelTempPath, String excelName) {
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
        //获取MD工作表
        final XSSFSheet MDsheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<CustomPFREntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 1;
        //获取单元格样式
        final XSSFCellStyle style = workBook.createCellStyle();
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        //数据填入MD sheet中
        for (CustomPFREntity entity : list) {
            //供应商号
            setSheetValue(MDsheet, beginLine, 0, entity.getVenderId()==null?"":entity.getVenderId(), style);
            //供应商名称
            setSheetValue(MDsheet, beginLine, 1, entity.getVenderName()==null?"":entity.getVenderName(), style);
            //部门号
            setSheetValue(MDsheet, beginLine, 2, entity.getDeptNo()==null?"":entity.getDeptNo(), style);
            //订单号
            setSheetValue(MDsheet, beginLine, 3, entity.getOrderNo()==null?"":entity.getOrderNo(), style);
            //商品号
            setSheetValue(MDsheet, beginLine, 4, entity.getGoodsNo()==null?"":entity.getGoodsNo(), style);
            //商品描述
            setSheetValue(MDsheet, beginLine, 5, entity.getGoodsName()==null?"":entity.getGoodsName(), style);
            //订单取消日期
            setSheetValue(MDsheet, beginLine, 6,  formatDate(entity.getOrderCancelDate()), style);
            //未送齐货金额（含税）
            setSheetValue(MDsheet, beginLine, 7, entity.getNotFullGoodsAmount()==null?"":entity.getNotFullGoodsAmount(), style);
            //合同违约金比率
            setSheetValue(MDsheet, beginLine, 8, entity.getContractBreakRate()==null?"":entity.getContractBreakRate(), style);
            //合同生效时间
            setSheetValue(MDsheet, beginLine, 9,  formatDate(entity.getContarctEffectDate()), style);
            //订单折扣
            setSheetValue(MDsheet, beginLine, 10, entity.getOrderDiscount()==null?"":entity.getOrderDiscount().toString(), style);
            //应收违约金（含税）
            setSheetValue(MDsheet, beginLine, 11, entity.getBreakAmount()==null?"":entity.getBreakAmount().toString(), style);
            if(StringUtils.isNotBlank(entity.getFailureReason())){
                //失败原因
                setSheetValue(MDsheet, beginLine, 12, entity.getFailureReason(), style);
            }
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy/MM/dd")).format(source);
    }
}
