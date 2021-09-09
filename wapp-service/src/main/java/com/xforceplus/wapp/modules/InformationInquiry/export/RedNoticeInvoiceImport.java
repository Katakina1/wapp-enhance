package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedNoticeBathEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入红字通知信息excel
 * @author Colin.hu
 * @date 4/19/2018
 */
public class RedNoticeInvoiceImport extends AbstractImportExcel {

    private MultipartFile file;

    public RedNoticeInvoiceImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 红字通知单实体
     * @throws ExcelException 读取异常
     */
    public List<RedNoticeBathEntity> analysisExcel() throws ExcelException {
        List<RedNoticeBathEntity> enjoySubsidedList = newArrayList();
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
                final RedNoticeBathEntity entity = createImportCertificationEntity(row, index);
                enjoySubsidedList.add(entity);
            }
        }

        return enjoySubsidedList;
    }

    /**
     * 构建导入红字通知单实体
     * @param row 行
     * @return 红字通知单实体
     */
    private RedNoticeBathEntity createImportCertificationEntity(Row row, int index) {
        final RedNoticeBathEntity redInvoice = new RedNoticeBathEntity();
        //序号
        redInvoice.setIndexNo(index);



        //供应商名称
        final String xfName= getCellData(row, 5);
        redInvoice.setXfName(xfName);

        //供应商税号(纳税识别号)
        final String xfTaxno = getCellData(row, 6);
        redInvoice.setXfTaxno(xfTaxno);

        //序列号
        final String redTicketDataSerialNumber1 = getCellData(row, 7);
//        String redTicketDataSerialNumber2 = StringUtils.substringBeforeLast(redTicketDataSerialNumber1,")");
        String redTicketDataSerialNumber = StringUtils.substringAfter(redTicketDataSerialNumber1,"_");
        redInvoice.setRedTicketDataSerialNumber(redTicketDataSerialNumber);

//        //购方名称
//        final String gfName = getCellData(row, 7);
//        redInvoice.setGfName(gfName);
//
//        //购方纳税识别号
//        final String gfTaxno = getCellData(row, 8);
//        redInvoice.setGfTaxno(gfTaxno);

        //金额
        final String amount = getCellData(row, 10);
        redInvoice.setAmount(amount);

        //税率
        final String taxRate = getCellData(row, 11);
        redInvoice.setTaxRate(taxRate);

        //税额
        final String taxAmount = getCellData(row, 12);
        redInvoice.setTaxAmount(taxAmount);



        //填开日期
        final String tkDate = getCellData(row, 2);
        redInvoice.setTkDate(tkDate);

        //红字发票信息表编号
        final String redNoticeNumber = getCellData(row, 15);
        redInvoice.setRedNoticeNumber(redNoticeNumber);

        return redInvoice;
    }
}
