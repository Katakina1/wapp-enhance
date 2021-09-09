package com.xforceplus.wapp.modules.posuopei.export;

import com.aisinopdf.text.pdf.S;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
import com.xforceplus.wapp.modules.posuopei.entity.ImportEntity;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.posuopei.service.impl.MatchServiceImpl;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
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
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<ImportEntity> analysisExcel() throws Exception {
        List<ImportEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();
        if(rowCount>500){
            throw  new Exception("超过500条不可导入");
        }
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
//                    enjoySubsidedList.get(0).setTrue(false);
                }else {
                    int n=i+1;
                    throw  new Exception("第"+n+"行数据有误！");

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
        //发票代码
        final String invoiceCode = getCellData(row, 1);
        //发票号码
        final String invoiceNo = getCellData(row, 2);
        //开票日期
        final String invoiceDate = getCellData(row, 3);
        //金额
        final String amount = getCellData(row, 4);

        //价税合计
        final String totalAmount = getCellData(row, 5);

        //税率
        final String taxRate = getCellData(row, 6);

        //税额
        final String taxAmount = getCellData(row, 7);

        //验证码
        final String checkNo = getCellData(row, 8);
        final Boolean flag = this.checkInvoiceMessage(invoiceCode, invoiceNo, invoiceDate, amount, totalAmount, taxRate, taxAmount,checkNo);
        invoiceEntity.setTrue(flag);
        if (flag) {
            invoiceEntity.setCheckNo(checkNo);
            invoiceEntity.setInvoiceCode(invoiceCode);
            invoiceEntity.setInvoiceNo(invoiceNo);
            if (StringUtils.isNotEmpty(invoiceDate)) {
                invoiceEntity.setInvoiceDate(invoiceDate);
            } else {
                invoiceEntity.setTrue(false);
            }
            invoiceEntity.setAmount(amount);
            invoiceEntity.setTotalAmount(totalAmount);
            invoiceEntity.setTaxAmount(taxAmount);
            invoiceEntity.setTaxRate(taxRate);
        }
        return invoiceEntity;

    }

    private Boolean checkInvoiceMessage(String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount,String checkNo) {
        Boolean flag=true;
        if("04".equals(CommonUtil.getFplx(invoiceCode))){
            if(checkNo.length()!=6){
                return false;
            }
        }
        if(StringUtils.isEmpty(taxRate)){
            return false;
        }
        if(!CommonUtil.isValidNum(invoiceCode,"^(\\d{10}|\\d{12})$")) {
            flag=false;
        }else if(!CommonUtil.isValidNum(invoiceNo,"^[\\d]{8}$")){
            flag=false;
        }else if(!CommonUtil.isValidNum(taxRate,"^[0-9]*$")){
            if(!"1.5".equals(taxRate)){
                flag=false;
            }
        }else if((!CommonUtil.isValidNum(invoiceAmount,"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum(totalAmount,"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum(taxAmount,"^[0-9]+(.[0-9]{2})?$"))){
            flag=false;
        }else if((!CommonUtil.isValidNum(invoiceDate,"^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$"))){
            flag=false;
        }else if(!("04".equals(CommonUtil.getFplx(invoiceCode))||"01".equals(CommonUtil.getFplx(invoiceCode)))){
            flag=false;
        }
        if(flag){
            BigDecimal amount=new BigDecimal(invoiceAmount);
            BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));;
            BigDecimal taxAmount1=new BigDecimal(taxAmount);
            BigDecimal rest=taxAmount1.subtract(amount.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
            if("04".equals(CommonUtil.getFplx(invoiceCode))){
                if(amount.add(taxAmount1).compareTo(new BigDecimal(totalAmount))!=0){
                    flag=false;
                }
            }else {
                if (rest.compareTo(BigDecimal.ZERO) > 0) {
                    flag = !(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO) > 0);

                } else {
                    flag = rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO) > 0;
                }
                if(amount.add(taxAmount1).compareTo(new BigDecimal(totalAmount))!=0){
                    flag=false;
                }
            }
        }
        return flag;

    }
}
