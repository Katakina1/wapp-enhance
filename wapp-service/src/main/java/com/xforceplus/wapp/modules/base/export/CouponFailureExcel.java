package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.base.entity.CouponEntity;
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
 * 债务数据导出
 */
public final class CouponFailureExcel extends AbstractExportExcel {

    private final Map<String, List<CouponEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public CouponFailureExcel(Map<String, List<CouponEntity>> map, String excelTempPath, String excelName) {
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
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<CouponEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 1;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 1);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);

        //数据填入sheet中
        for (CouponEntity entity : list) {
            setSheetValue(sheet, beginLine, 0, entity.getSixD()==null?"":entity.getSixD(), style);
            setSheetValue(sheet, beginLine, 1, entity.getEightD()==null?"":entity.getEightD(), style);
            setSheetValue(sheet, beginLine, 2, entity.getVenderName()==null?"":entity.getVenderName(), style);
            setSheetValue(sheet, beginLine, 3, formatDate(entity.getCaseDate()), style);
            setSheetValue(sheet, beginLine, 4,  entity.getStore()==null?"":entity.getStore(), style);
            setSheetValue(sheet, beginLine, 5,  entity.getCouponNo()==null?"":entity.getCouponNo(), style);
            setSheetValue(sheet, beginLine, 6,  entity.getNineD()==null?"":entity.getNineD(), style);
            setSheetValue(sheet, beginLine, 7,  entity.getCouponDesc()==null?"":entity.getCouponDesc(), style);
            setSheetValue(sheet, beginLine, 8,  entity.getTicketDesc()==null?"":entity.getTicketDesc(), style);
            setSheetValue(sheet, beginLine, 9,  formatDate(entity.getStartDate()), style);
            setSheetValue(sheet, beginLine, 10,  formatDate(entity.getEndDate()), style);
            setSheetValue(sheet, beginLine, 11,  entity.getCouponCount()==null?0:entity.getCouponCount(), style);
            if(entity.getCaseAmount()!=null) {
                setSheetValue(sheet, beginLine, 12, entity.getCaseAmount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(sheet, beginLine, 12, "", style);
            }
            setSheetValue(sheet, beginLine, 13,  entity.getAssumeScale()==null?"":entity.getAssumeScale(), style);

            if(entity.getReceivableAmount()!=null) {
                setSheetValue(sheet, beginLine, 14, entity.getReceivableAmount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(sheet, beginLine, 14, "", style);
            }
            //失败原因
            setSheetValue(sheet, beginLine, 15,  entity.getFailureReason()==null?"":entity.getFailureReason(), style);
            beginLine++;
        }

    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy/MM/dd")).format(source);
    }

}
