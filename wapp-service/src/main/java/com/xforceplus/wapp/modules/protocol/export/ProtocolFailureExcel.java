package com.xforceplus.wapp.modules.protocol.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolEntity;
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
 * 导入失败的协议导出
 */
public final class ProtocolFailureExcel extends AbstractExportExcel {

    private final Map<String, List<ProtocolEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public ProtocolFailureExcel(Map<String, List<ProtocolEntity>> map, String excelTempPath, String excelName) {
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
        int beginLine = 1;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 14);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (ProtocolEntity entity : list) {
            //协议号码
            setSheetValue(sheet, beginLine, 0, entity.getProtocolNo(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 1, entity.getVenderId(), style);
            //部门号
            setSheetValue(sheet, beginLine, 2, entity.getDeptNo()==null?"":entity.getDeptNo(), style);
            //顺序号
            setSheetValue(sheet, beginLine, 3, entity.getSeq()==null?"":entity.getSeq(), style);
            //扣款项目
            setSheetValue(sheet, beginLine, 4,entity.getPayItem()==null?"":entity.getPayItem(), style);
            //扣款公司供码
            setSheetValue(sheet, beginLine, 5,entity.getPayCompanyCode()==null?"":entity.getPayCompanyCode(), style);
            //扣款金额
            if(entity.getAmount()!=null) {
                setSheetValue(sheet, beginLine, 6, entity.getAmount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(sheet, beginLine, 6, "", style);
            }
            //协议状态
            setSheetValue(sheet, beginLine, 7,formatProtocolStatus(entity.getProtocolStatus()), style);
            //协议定案日期
            setSheetValue(sheet, beginLine, 8, formatDate(entity.getCaseDate()), style);
            //扣款原因
            setSheetValue(sheet, beginLine, 9,  entity.getReason()==null?"": entity.getReason(), style);
            //号码
            setSheetValue(sheet, beginLine, 10,  entity.getNumber()==null?"": entity.getNumber(), style);
            //号码解释
            setSheetValue(sheet, beginLine, 11,  entity.getNumberDesc()==null?"": entity.getNumberDesc(), style);
            //金额
            if(entity.getDetailAmount()!=null) {
                setSheetValue(sheet, beginLine, 12, entity.getDetailAmount().stripTrailingZeros().toPlainString(), style);
            } else {
                setSheetValue(sheet, beginLine, 12, "", style);
            }
            //店号
            setSheetValue(sheet, beginLine, 13,  entity.getStore()==null?"": entity.getStore(), style);
            //失败原因
            setSheetValue(sheet, beginLine, 14,  entity.getFailureReason()==null?"":entity.getFailureReason(), style);

            beginLine++;
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy/MM/dd")).format(source);
    }

    private String formatProtocolStatus(String protocolStatus) {
        if ("0".equals(protocolStatus)) {
            return "协议审批未完成";
        } else if ("1".equals(protocolStatus)) {
            return "协议审批完成";
        }
        return "";
    }

}
