package com.xforceplus.wapp.modules.redInvoiceManager.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.service.UploadScarletLetterService;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

public class InvoiceListExcel extends AbstractExportExcel {
    private final Map<String, Object> map;
    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public InvoiceListExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<UploadScarletLetterEntity> list = (List<UploadScarletLetterEntity>)this.map.get(excelName);
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
        for (UploadScarletLetterEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //jv
            setSheetValue(sheet, beginLine, 1, entity.getJvCode(), style);
            //税务承担店号
            setSheetValue(sheet, beginLine, 2, entity.getStore(), style);
            //收票方名称
            setSheetValue(sheet, beginLine, 3, entity.getBuyerName(), style);
            //开票类型
            setSheetValue(sheet, beginLine, 4, entity.getInvoiceType(), style);
            //开红票金额
            setSheetValue(sheet, beginLine, 5,(entity.getInvoiceAmount().setScale(2, BigDecimal.ROUND_UP)).toString(), style);
            //开红票税率
            setSheetValue(sheet, beginLine, 6,entity.getTaxRate().toString()+"%", style);
            //开红票税额
            setSheetValue(sheet, beginLine, 7, (entity.getTaxAmount().setScale(2, BigDecimal.ROUND_UP)).toString(), style);
            //开票月份
            setSheetValue(sheet, beginLine, 8, entity.getMakeoutDate(), style);
            //商品名称
            setSheetValue(sheet, beginLine, 9, "日用商品一批", style);
            //红字通知单号
            setSheetValue(sheet, beginLine, 10, entity.getRedLetterNotice(), style);

            beginLine++;
        }
    }
}
