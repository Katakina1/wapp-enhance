package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 培训公告关联的供应商信息导出
 */
public final class TrainVenderExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public TrainVenderExcel(Map<String, Object> map, String excelTempPath, String excelName) {
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
        final List<UserEntity> list = (List<UserEntity>)this.map.get(excelName);
        String announcementTitle = this.map.get("announcementTitle").toString();
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
        for (UserEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //公告标题
            setSheetValue(sheet, beginLine, 1, announcementTitle, style);
            //供应商号
            setSheetValue(sheet, beginLine, 2, entity.getUsercode()==null?"":entity.getUsercode(), style);
            //供应商名称
            setSheetValue(sheet, beginLine, 3, entity.getUsername()==null?"":entity.getUsername(), style);
            //是否同意培训
            setSheetValue(sheet, beginLine, 4,formatIsAgree(entity.getIsAgree()), style);
            beginLine++;
        }
    }

    private String formatIsAgree(String isAgree){
        String value="";
        if("0".equals(isAgree)){
            value="不同意";
        } else if("1".equals(isAgree)){
            value="同意";
        }
        return value;
    }
}
