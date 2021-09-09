package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
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
 * 公告导出
 */
public final class AnnouncementExcel extends AbstractExportExcel {

    private final Map<String, List<AnnouncementEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public AnnouncementExcel(Map<String, List<AnnouncementEntity>> map, String excelTempPath, String excelName) {
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
        final List<AnnouncementEntity> list = this.map.get(excelName);
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
        for (AnnouncementEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //公告标题
            setSheetValue(sheet, beginLine, 1, entity.getAnnouncementTitle(), style);
            //公告类型
            setSheetValue(sheet, beginLine, 2, formateAnnounceType(entity.getAnnouncementType()), style);
            //发布日期
            setSheetValue(sheet, beginLine, 3, formatDate(entity.getReleasetime()), style);
            //内容
            setSheetValue(sheet, beginLine, 4,entity.getAnnouncementInfo(), style);
            //供应商已读数量
            setSheetValue(sheet, beginLine, 5, entity.getSupplierReadNum()==null?0:entity.getSupplierReadNum(), style);
            //供应商未读数量
            setSheetValue(sheet, beginLine, 6, entity.getSupplierUnreadNum()==null?0:entity.getSupplierUnreadNum(), style);
            //供应商同意数量
            setSheetValue(sheet, beginLine, 7, entity.getSupplierAgreeNum()==null?0:entity.getSupplierAgreeNum(), style);
            //供应商不同意数量
            setSheetValue(sheet, beginLine, 8, entity.getSupplierDisagreeNum()==null?0:entity.getSupplierDisagreeNum(), style);
            beginLine++;
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateAnnounceType(String venderType){
        String value="";
        if("0".equals(venderType)){
            value="普通";
        } else if("1".equals(venderType)){
            value="培训公告";
        } else if("1".equals(venderType)){
            value="自定义公告";
        }
        return value;
    }
}
