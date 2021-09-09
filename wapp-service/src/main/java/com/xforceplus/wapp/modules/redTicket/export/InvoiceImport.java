package com.xforceplus.wapp.modules.redTicket.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.redTicket.entity.ImportEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.EntryRedTicketService;
import com.xforceplus.wapp.modules.redTicket.service.impl.EntryRedTicketServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入excel
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class InvoiceImport extends AbstractImportExcel {

    private MultipartFile file;

    public InvoiceImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入红票实体集
     * @throws ExcelException 读取异常
     */
    public List<ImportEntity> analysisExcel() throws ExcelException {
        List<ImportEntity> enjoySubsidedList = newArrayList();
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
                final ImportEntity invoiceEntity = createImportCertificationEntity(row, index);
                if(invoiceEntity.getTrue()){
                    enjoySubsidedList.add(invoiceEntity);
                }
            }
        }

        return enjoySubsidedList;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private ImportEntity createImportCertificationEntity(Row row, int index) {
        final ImportEntity invoiceEntity = new ImportEntity();


        //序号
        invoiceEntity.setIndexNo(index);
        //数据校验状态
        invoiceEntity.setTrue(true);
        //红字通知单号
        final String redNoticeNumber = getCellData(row, 1);
        //发票代码
        final String invoiceCode = getCellData(row, 2);
        //发票号码
        final String invoiceNo = getCellData(row, 3);
        //开票日期
        final String invoiceDate = getCellData(row, 4);
        //金额
        final String amount = getCellData(row, 5);
        //税额
        final String taxAmount = getCellData(row, 6);
        //税率
        final String taxRate = getCellData(row, 7);
        //价税合计
        final String totalAmount = getCellData(row, 8);

       // final Boolean flag = this.checkInvoiceMessage(redNoticeNumber,invoiceCode, invoiceNo, invoiceDate, amount, totalAmount, taxRate, taxAmount);
        //invoiceEntity.setTrue(flag);
        //if (flag) {
            invoiceEntity.setRedNoticeNumber(redNoticeNumber);
            invoiceEntity.setInvoiceCode(invoiceCode);
            invoiceEntity.setInvoiceNo(invoiceNo);
            invoiceEntity.setInvoiceDate(invoiceDate);
            invoiceEntity.setAmount(amount);
            invoiceEntity.setTotalAmount(totalAmount);
            invoiceEntity.setTaxAmount(taxAmount);
            invoiceEntity.setTaxRate(taxRate);
       // }
        return invoiceEntity;

    }




}
