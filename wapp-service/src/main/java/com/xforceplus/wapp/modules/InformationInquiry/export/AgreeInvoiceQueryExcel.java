package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.posuopei.entity.ClaimEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
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
 * 索赔信息导出
 */
public final class AgreeInvoiceQueryExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public AgreeInvoiceQueryExcel(Map<String,Object> map, String excelTempPath, String excelName) {
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
        final List<MatchEntity> list = (List<MatchEntity>)this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 2, 0);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (MatchEntity entity : list) {
            if(entity.getInvoiceEntityList().size()>1){
                    for(InvoiceEntity invoiceEntity :entity.getInvoiceEntityList()){
                        //序号
                        setSheetValue(sheet, beginLine, 0, index++, style);
                        setSheetValue(sheet, beginLine, 1, entity.getPoEntityList().get(0).getPocode(), style);
                        //差异金额
                        setSheetValue(sheet, beginLine, 2, entity.getMatchCover().doubleValue(), style);

                        setSheetValue(sheet, beginLine, 3, invoiceEntity.getInvoiceAmount().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 4, entity.getMatchno(), style);
                        setSheetValue(sheet, beginLine, 5, invoiceEntity.getInvoiceNo(), style);
                        setSheetValue(sheet, beginLine, 6, invoiceEntity.getInvoiceDate().substring(0,10), style);
                        setSheetValue(sheet, beginLine, 7, invoiceEntity.getTaxRate().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 8, invoiceEntity.getTaxAmount().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 9, invoiceEntity.getTotalAmount().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 10, formatDate(entity.getMatchDate()), style);
                        beginLine++;
                    }
                    for(ClaimEntity claimEntity : entity.getClaimEntityList()){
                        //序号
                        setSheetValue(sheet, beginLine, 0, index++, style);
                        setSheetValue(sheet, beginLine, 1, claimEntity.getClaimno(), style);
                        setSheetValue(sheet, beginLine, 2, "", style);

                        setSheetValue(sheet, beginLine, 3, claimEntity.getClaimAmount().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 4, entity.getMatchno(), style);
                        setSheetValue(sheet, beginLine, 5, "", style);
                        setSheetValue(sheet, beginLine, 6, "", style);
                        setSheetValue(sheet, beginLine, 7, "", style);
                        setSheetValue(sheet, beginLine, 8, "", style);
                        setSheetValue(sheet, beginLine, 9, "", style);
                        setSheetValue(sheet, beginLine, 10, "", style);
                        beginLine++;
                    }

            }else if(entity.getInvoiceEntityList().size()==1){
                    for (PoEntity poEntity :entity.getPoEntityList()){
                        setSheetValue(sheet, beginLine, 0, index++, style);
                        setSheetValue(sheet, beginLine, 1, poEntity.getPocode(), style);
                        //差异金额
                        setSheetValue(sheet, beginLine, 2, entity.getMatchCover().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 3, poEntity.getAmountpaid().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 4, entity.getMatchno(), style);
                        setSheetValue(sheet, beginLine, 5, entity.getInvoiceEntityList().get(0).getInvoiceNo(), style);
                        setSheetValue(sheet, beginLine, 6, entity.getInvoiceEntityList().get(0).getInvoiceDate().substring(0,10), style);
                        setSheetValue(sheet, beginLine, 7, entity.getInvoiceEntityList().get(0).getTaxRate().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 8, entity.getInvoiceEntityList().get(0).getTaxAmount().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 9, entity.getInvoiceEntityList().get(0).getTotalAmount().doubleValue(), style);
                        setSheetValue(sheet, beginLine, 10, formatDate(entity.getMatchDate()), style);

                        beginLine++;

                    }
                for(ClaimEntity claimEntity : entity.getClaimEntityList()){
                    //序号
                    setSheetValue(sheet, beginLine, 0, index++, style);
                    setSheetValue(sheet, beginLine, 1, claimEntity.getClaimno(), style);
                    setSheetValue(sheet, beginLine, 2, "", style);
                    setSheetValue(sheet, beginLine, 3, claimEntity.getClaimAmount().doubleValue(), style);
                    setSheetValue(sheet, beginLine, 4, entity.getMatchno(), style);
                    setSheetValue(sheet, beginLine, 5, "", style);
                    setSheetValue(sheet, beginLine, 6, "", style);
                    setSheetValue(sheet, beginLine, 7, "", style);
                    setSheetValue(sheet, beginLine, 8, "", style);
                    setSheetValue(sheet, beginLine, 9, "", style);
                    setSheetValue(sheet, beginLine, 10, "", style);
                    beginLine++;
                }

            }
        }
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }



}
