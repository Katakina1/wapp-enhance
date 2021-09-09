package com.xforceplus.wapp.modules.certification.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
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
public class InvoiceCertificationImport extends AbstractImportExcel {

    private MultipartFile file;

    public InvoiceCertificationImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<ImportCertificationEntity> analysisExcel() throws ExcelException {
        List<ImportCertificationEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();

        int index = 0;
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 2; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
                final ImportCertificationEntity importCertificationEntity = createImportCertificationEntity(row, index);
                enjoySubsidedList.add(importCertificationEntity);
            }
        }

        return enjoySubsidedList;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private ImportCertificationEntity createImportCertificationEntity(Row row, int index) {
        final ImportCertificationEntity importCertificationEntity = new ImportCertificationEntity();
        //序号
        importCertificationEntity.setIndexNo(index);

        //发票代码
        final String invoiceCode = getCellData(row, 1);
        importCertificationEntity.setInvoiceCode(invoiceCode);

        //发票号码
        final String invoiceNo = getCellData(row, 2);
        importCertificationEntity.setInvoiceNo(invoiceNo);

        //开票日期
        final String invoiceDate = getCellData(row, 3);
        importCertificationEntity.setInvoiceDate(invoiceDate);

        //金额
        final String amount = getCellData(row, 4);
        importCertificationEntity.setAmount(amount);
        if(StringUtils.isNotEmpty(amount)) {
            final String strAmount = amount.replace(",", "");
            importCertificationEntity.setAmount(strAmount);
        }

        return importCertificationEntity;
    }
}
