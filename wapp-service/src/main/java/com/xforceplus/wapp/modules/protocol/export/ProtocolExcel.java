package com.xforceplus.wapp.modules.protocol.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 发票借阅发票导出
 */
public final class ProtocolExcel extends AbstractExportExcel {

    private final Map<String, List<ProtocolEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public ProtocolExcel(Map<String, List<ProtocolEntity>> map, String excelTempPath, String excelName) {
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
        final List<ProtocolEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 8);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (ProtocolEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //供应商号
            setSheetValue(sheet, beginLine, 1, entity.getVenderId(), style);
            //供应商名称
            setSheetValue(sheet, beginLine, 2, entity.getVenderName()==null?"":entity.getVenderName(), style);
            //部门号
            setSheetValue(sheet, beginLine, 3, entity.getDeptNo()==null?"":entity.getDeptNo(), style);
            //顺序号
            setSheetValue(sheet, beginLine, 4, entity.getSeq()==null?"":entity.getSeq(), style);
            //协议号
            setSheetValue(sheet, beginLine, 5,entity.getProtocolNo(), style);
            //扣款项目
            setSheetValue(sheet, beginLine, 6,entity.getPayItem()==null?"":entity.getPayItem(), style);
            //扣款公司名称
            setSheetValue(sheet, beginLine, 7,entity.getPayCompany(), style);
            //扣款金额
            if(entity.getAmount()!=null) {
                setSheetValue(sheet, beginLine, 8, entity.getAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString(), style);
            } else {
                setSheetValue(sheet, beginLine, 8, "", style);
            }
            //协议状态
            setSheetValue(sheet, beginLine, 9,formatProtocolStatus(entity.getProtocolStatus()), style);
            //协议定案日期
            setSheetValue(sheet, beginLine, 10, formatDate(entity.getCaseDate()), style);
            //付款日期
            setSheetValue(sheet, beginLine, 11, formatDate(entity.getPayDate()), style);
            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatProtocolStatus(String protocolStatus) {
        if ("0".equals(protocolStatus)) {
            return "协议更改-审批完成";
        } else if ("1".equals(protocolStatus)) {
            return "协议审批完成";
        }
        return "";
    }

}
