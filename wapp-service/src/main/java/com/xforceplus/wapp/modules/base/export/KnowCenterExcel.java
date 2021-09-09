package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
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
 * 发票借阅发票导出
 */
public final class KnowCenterExcel extends AbstractExportExcel {

    private final Map<String, List<KnowledgeFileEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public KnowCenterExcel(Map<String, List<KnowledgeFileEntity>> map, String excelTempPath, String excelName) {
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
        final List<KnowledgeFileEntity> list = this.map.get(excelName);
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
        for (KnowledgeFileEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商类型
            setSheetValue(sheet, beginLine, 1, formateVenderType(entity.getVenderType()), style);
            //文件类型
            setSheetValue(sheet, beginLine, 2, entity.getFileExtension(), style);
            //文件名称
            setSheetValue(sheet, beginLine, 3, entity.getFileName(), style);
            //文件大小
            setSheetValue(sheet, beginLine, 4,entity.getFileSize()+"k", style);
            //上传时间
            setSheetValue(sheet, beginLine, 5, formatDate(entity.getUploadDate()), style);
            beginLine++;
        }
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateVenderType(String venderType){
        String value="";
        if("0".equals(venderType)){
            value="商品";
        }else if("1".equals(venderType)){
            value="费用";
        }
        return value;
    }
}
