package com.xforceplus.wapp.modules.redInvoiceManager.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.redTicket.entity.ImportEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

/**
 * 导入excel
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class InputRedInvoiceImport extends AbstractImportExcel {

    private MultipartFile file;

    public InputRedInvoiceImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
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
        for (int i = 1; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
                final ImportEntity invoiceEntity = createImportCertificationEntity(row, index);

                if(invoiceEntity.getTrue()){
                    enjoySubsidedList.add(invoiceEntity);
//                    enjoySubsidedList.get(0).setTrue(false);
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
        final String redNoticeNumber1 = getCellData(row, 24);
        String[] split = redNoticeNumber1.split("-");
        String redNoticeNumber2 = split[0];
        String redNoticeNumber = redNoticeNumber2.substring(redNoticeNumber2.length()-16,redNoticeNumber2.length());


        //发票代码
        final String invoiceCode = getCellData(row, 2);
        //发票号码
        final String invoiceNo = getCellData(row, 3);
        //金额
        final String amount = getCellData(row, 11);
        //税额
        final String taxAmount = getCellData(row, 13);
        //税率
        final String taxRate = getCellData(row, 12);
//        String[] split = taxRate1.split("%");
//        String taxRate = split[0];
        //价税合计
        final String totalAmount = getCellData(row, 14);
        //开票日期
        final String invoiceDate1 = getCellData(row, 15);
        String str = invoiceDate1.substring(0,10);
       // String str1 = invoiceDate1.substring(10,18);
       // String str2 = str + " " + str1;

//        Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                /*Date invoiceDate2 = sdf.parse(str);
                String invoiceDate = sdf.format(invoiceDate2);*/
                invoiceEntity.setInvoiceDate(invoiceDate1);

            }catch (Exception e){

            }

            invoiceEntity.setRedNoticeNumber(redNoticeNumber);
            invoiceEntity.setInvoiceCode(invoiceCode);
            invoiceEntity.setInvoiceNo(invoiceNo);

            invoiceEntity.setAmount(amount);
            invoiceEntity.setTotalAmount(totalAmount);
            invoiceEntity.setTaxAmount(taxAmount);
            invoiceEntity.setTaxRate(taxRate);

        return invoiceEntity;

    }

//    private Boolean checkInvoiceMessage(String redNoticeNumber, String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount) {
//        Boolean flag=true;
//
//        if(!CommonUtil.isValidNum(invoiceCode,"^(\\d{10}|\\d{12})$")) {
//            flag=false;
//        }else if(!CommonUtil.isValidNum(invoiceNo,"^[\\d]{8}$")){
//            flag=false;
//        }
//        else if(!CommonUtil.isValidNum(taxRate,"^[0-9]*$")) {
//            flag = false;
//        }
////        }else if((!CommonUtil.isValidNum(invoiceDate,"^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$"))){
////            flag=false;
////        }
//        else if(!("01".equals(CommonUtil.getFplx(invoiceCode)))){
//            flag=false;
//        }
//
//        if(flag){
//            BigDecimal amount=new BigDecimal(invoiceAmount);
//            BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));;
//            BigDecimal taxAmount1=new BigDecimal(taxAmount);
//
//        }
//        return flag;
//
//    }




}
