package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.base.entity.DebtEntity;
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

/**
 * 债务数据导出
 */
public final class DebtExcel extends AbstractExportExcel {

    private final Map<String, List<DebtEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public DebtExcel(Map<String, List<DebtEntity>> map, String excelTempPath, String excelName) {
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
        //获取PC工作表
        final XSSFSheet PCsheet = workBook.getSheetAt(1);
        //获取要导出的数据
        final List<DebtEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 1;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(MDsheet, 0, 12);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        List<DebtEntity> pcList = new ArrayList<>();
        List<DebtEntity> mdList = new ArrayList<>();
        for(DebtEntity entity:list){
            if("2".equals(entity.getDebtType())){
                pcList.add(entity);
            }else if("3".equals(entity.getDebtType())){
                mdList.add(entity);
            }
        }
        //数据填入MD sheet中
        for (DebtEntity entity : mdList) {
            //供应商号
            setSheetValue(MDsheet, beginLine, 0, entity.getVenderId()==null?"":entity.getVenderId(), style);
            //部门
            setSheetValue(MDsheet, beginLine, 1, entity.getDeptNo()==null?"":entity.getDeptNo(), style);
            //商品号
            setSheetValue(MDsheet, beginLine, 2, entity.getGoodsNo()==null?"":entity.getGoodsNo(), style);
            //商品名称
            setSheetValue(MDsheet, beginLine, 3, entity.getGoodsName()==null?"":entity.getGoodsName(), style);
            //商品价格下调日期
            setSheetValue(MDsheet, beginLine, 4,  formatDate(entity.getGoodsReduceDate()), style);
            //下调前价格
            if(entity.getPriceReduceBefore()!=null) {
                setSheetValue(MDsheet, beginLine, 5, entity.getPriceReduceBefore().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(MDsheet, beginLine, 5, "", style);
            }
            //下调后价格
            if(entity.getPriceReduceAfter()!=null) {
                setSheetValue(MDsheet, beginLine, 6, entity.getPriceReduceAfter().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(MDsheet, beginLine, 6, "", style);
            }
            //商品价格下调日库存数量
            setSheetValue(MDsheet, beginLine, 7,entity.getReduceStockNum()==null?0:entity.getReduceStockNum(), style);
            //税率
            if(entity.getTaxRate()!=null) {
                setSheetValue(MDsheet, beginLine, 8, entity.getTaxRate().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(MDsheet, beginLine, 8, "", style);
            }
            //协议号
            setSheetValue(MDsheet, beginLine, 9,entity.getProtocolNo()==null?"":entity.getProtocolNo(), style);
            //协议号金额
            if(entity.getProtocolAmount()!=null) {
                setSheetValue(MDsheet, beginLine, 10, entity.getProtocolAmount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(MDsheet, beginLine, 10, "", style);
            }
            //库存商品补偿金
            if(entity.getCompensationAmount()!=null) {
                setSheetValue(MDsheet, beginLine, 11, entity.getCompensationAmount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(MDsheet, beginLine, 11, "", style);
            }
            beginLine++;
        }
        beginLine=1;
        //数据填入PC sheet中
        for (DebtEntity entity : pcList) {
            //供应商号
            setSheetValue(PCsheet, beginLine, 0, entity.getVenderId()==null?"":entity.getVenderId(), style);
            //部门
            setSheetValue(PCsheet, beginLine, 1, entity.getDeptNo()==null?"":entity.getDeptNo(), style);
            //订单号
            setSheetValue(PCsheet, beginLine, 2, entity.getOrderNo()==null?"":entity.getOrderNo(), style);
            //店号
            setSheetValue(PCsheet, beginLine, 3, entity.getStore()==null?"":entity.getStore(), style);
            //收货日期
            setSheetValue(PCsheet, beginLine, 4,  formatDate(entity.getReceiveDate()), style);
            //商品价格下调日期
            setSheetValue(PCsheet, beginLine, 5,  formatDate(entity.getGoodsReduceDate()), style);
            //商品号
            setSheetValue(PCsheet, beginLine, 6, entity.getGoodsNo()==null?"":entity.getGoodsNo(), style);
            //商品名称
            setSheetValue(PCsheet, beginLine, 7, entity.getGoodsName()==null?"":entity.getGoodsName(), style);
            //收货数量
            setSheetValue(PCsheet, beginLine, 8, entity.getReceiveNum()==null?0:entity.getReceiveNum(), style);
            //包装数量
            setSheetValue(PCsheet, beginLine, 9, entity.getPackageNum()==null?0:entity.getPackageNum(), style);
            //商品实际结算价格（单件商品）
            if(entity.getGoodsActualPrice()!=null) {
                setSheetValue(PCsheet, beginLine, 10, entity.getGoodsActualPrice().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(PCsheet, beginLine, 10, "", style);
            }
            //商品下调后的价格（单件商品）
            if(entity.getPriceReduceAfter()!=null) {
                setSheetValue(PCsheet, beginLine, 11, entity.getPriceReduceAfter().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(PCsheet, beginLine, 11, "", style);
            }
            //订单折扣
            if(entity.getOrderDiscount()!=null) {
                setSheetValue(PCsheet, beginLine, 12, entity.getOrderDiscount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(PCsheet, beginLine, 12, "", style);
            }
            //税率
            if(entity.getTaxRate()!=null) {
                setSheetValue(PCsheet, beginLine, 13, entity.getTaxRate().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(PCsheet, beginLine, 13, "", style);
            }
            //价格调整商品应付差额
            if(entity.getPriceDifference()!=null) {
                setSheetValue(PCsheet, beginLine, 14, entity.getPriceDifference().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(PCsheet, beginLine, 14, "", style);
            }
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy/MM/dd")).format(source);
    }

}
