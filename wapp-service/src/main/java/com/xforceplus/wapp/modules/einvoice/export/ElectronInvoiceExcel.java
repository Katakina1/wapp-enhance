package com.xforceplus.wapp.modules.einvoice.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.util.DateTimeHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.util.List;

import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_EXPORT_TEMPLET;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.ELECTRON_INVOICE_QS_TYPE_FIVE;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.ELECTRON_INVOICE_QS_TYPE_FOUR;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.ELECTRON_INVOICE_QS_TYPE_ONE;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.ELECTRON_INVOICE_QS_TYPE_THREE;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.ELECTRON_INVOICE_QS_TYPE_TWO;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.ELECTRON_INVOICE_QS_TYPE_ZERO;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_QS_STATUS_FAIL_ZERO;

/**
 * Date 4/23/2018.
 *
 * @author marvin.zhong
 */
public class ElectronInvoiceExcel extends AbstractExportExcel {

    private List<ElectronInvoiceEntity> list;

    public ElectronInvoiceExcel(List<ElectronInvoiceEntity> list) {
        this.list = list;
    }

    @Override
    protected String getExcelUri() {
        return ELECTRON_INVOICE_EXPORT_TEMPLET;
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        final XSSFSheet sheet = workBook.getSheetAt(0);
        final int beginLine = 2;

        final List<ElectronInvoiceEntity> entitiese = list;
        int lineNum = beginLine;

        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 1, 1);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);

        for (ElectronInvoiceEntity invoiceEntity : entitiese) {
            setSheetValue(sheet, lineNum, 0, lineNum-1, style);
            setSheetValue(sheet, lineNum, 1, INVOICE_QS_STATUS_FAIL_ZERO.equals(invoiceEntity.getQsStatus()) ? "签收失败" : "签收成功", style);
            setSheetValue(sheet, lineNum, 2, this.getQsType(invoiceEntity.getQsType()), style);
            setSheetValue(sheet, lineNum, 3, invoiceEntity.getInvoiceCode(), style);
            setSheetValue(sheet, lineNum, 4, invoiceEntity.getInvoiceNo(), style);
            setSheetValue(sheet, lineNum, 5, DateTimeHelper.formatDate(invoiceEntity.getInvoiceDate()), style);
            setSheetValue(sheet, lineNum, 6, null != invoiceEntity.getInvoiceAmount() ? invoiceEntity.getInvoiceAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() : "0.00", style);
            setSheetValue(sheet, lineNum, 7, null != invoiceEntity.getTaxAmount() ? invoiceEntity.getTaxAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() : "0.00", style);
            lineNum++;
        }
    }

    private String getQsType(String type) {
        String qsType = "";
        if (ELECTRON_INVOICE_QS_TYPE_ZERO.equals(type)) {
            qsType = "扫码签收";
        } else if (ELECTRON_INVOICE_QS_TYPE_ONE.equals(type)) {
            qsType = "扫描仪签收";
        } else if (ELECTRON_INVOICE_QS_TYPE_TWO.equals(type)) {
            qsType = "app签收";
        } else if (ELECTRON_INVOICE_QS_TYPE_THREE.equals(type)) {
            qsType = "导入签收";
        } else if (ELECTRON_INVOICE_QS_TYPE_FOUR.equals(type)) {
            qsType = "手工签收";
        } else if (ELECTRON_INVOICE_QS_TYPE_FIVE.equals(type)) {
            qsType = "pdf上传";
        }

        return qsType;
    }
}
