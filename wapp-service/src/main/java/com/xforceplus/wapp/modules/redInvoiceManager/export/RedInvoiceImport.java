package com.xforceplus.wapp.modules.redInvoiceManager.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入认证excel
 * @author Colin.hu
 * @date 4/19/2018
 */
public class RedInvoiceImport extends AbstractImportExcel {

    private MultipartFile file;

    public RedInvoiceImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<RedInvoiceData> analysisExcel() throws ExcelException {
        List<RedInvoiceData> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();

        int index = 0;
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 1; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
                final RedInvoiceData entity = createImportCertificationEntity(row, index);
                enjoySubsidedList.add(entity);
            }
        }

        return enjoySubsidedList;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private RedInvoiceData createImportCertificationEntity(Row row, int index) {
        final RedInvoiceData redInvoice = new RedInvoiceData();
        //序号
        redInvoice.setIndexNo(index);

        //jvcode
        final String jvcode = getCellData(row, 1);
        redInvoice.setJvCode(jvcode);

        //承担店号
        final String store = getCellData(row, 2);
        redInvoice.setStore(store);
        //税率
        final String taxRate = getCellData(row, 3);
        redInvoice.setTaxRate(taxRate);

        //税额
        final String taxAmount = getCellData(row, 4);
        redInvoice.setTaxAmount(taxAmount);

        //金额
        final String amount = getCellData(row, 5);
        redInvoice.setInvoiceAmount(amount);


        //开票月份
        final String invoiceDate = getCellData(row, 6);
        redInvoice.setInvoiceDate(invoiceDate);

        //开票类型
        final String invoiceType = getCellData(row, 7);
        redInvoice.setInvoiceType(invoiceType);

        //开票方名称
        final String kpfName = getCellData(row, 8);
        redInvoice.setKpfName(kpfName);

        //收票方名称
        final String spfName = getCellData(row,9);
        redInvoice.setSpfName(spfName);

        //申请单号
        final String sqNo = getCellData(row,9);
        redInvoice.setSqNo(sqNo);

        return redInvoice;
    }
}
