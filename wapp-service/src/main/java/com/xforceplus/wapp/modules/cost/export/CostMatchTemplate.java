package com.xforceplus.wapp.modules.cost.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class CostMatchTemplate extends AbstractExportExcel {

    private final List<CostEntity> list;

    /**
     * excel模板名
     */
    private final String excelName;

    public CostMatchTemplate(List<CostEntity> list, String excelName){
        this.list = list;
        this.excelName = excelName;
    }

    @Override
    protected String getExcelUri() {
        return "export/cost/costMatchApplication.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //设置开始行
        int beginLine = 3;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        //数据填入excel
        for (CostEntity entity : list) {
            //主键
            setSheetValue(sheet, beginLine, 0, entity.getId(), style);
            //费用类型名称
            setSheetValue(sheet, beginLine, 1, entity.getCostTypeName(), style);
            //费用发生时间
            setSheetValue(sheet, beginLine, 2, entity.getCostTime(), style);
            //费用金额
            setSheetValue(sheet, beginLine, 3, CommonUtil.formatMoney(entity.getCostAmount().doubleValue()), style);
            //费用承担部门
            setSheetValue(sheet, beginLine, 4, entity.getCostDept(), style);
            //用途
            setSheetValue(sheet, beginLine, 5, entity.getCostUse(), style);
            //未冲销金额
            setSheetValue(sheet, beginLine, 6, CommonUtil.formatMoney(entity.getUncoveredAmount().doubleValue()), style);

            beginLine++;
        }
    }
}
