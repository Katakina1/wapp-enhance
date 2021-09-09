package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import org.xhtmlrenderer.css.style.derived.StringValue;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * 导入认证excel
 * @author Colin.hu
 * @date 4/19/2018
 */
public class PaymentInvoiceUploadImport extends AbstractImportExcel {

    private MultipartFile file;

    public PaymentInvoiceUploadImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */

    public List<PaymentInvoiceUploadEntity> analysisExcel() throws ExcelException {
        List<PaymentInvoiceUploadEntity> enjoySubsidedList = newArrayList();
        HashSet<String> set = new HashSet<>();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();
        Map<String, Object> map = newHashMap();

        int index = 0;
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 1; i < rowCount + 1; i++) {

            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;

                final PaymentInvoiceUploadEntity entity = createImportCertificationEntity(row, index);
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
    private PaymentInvoiceUploadEntity createImportCertificationEntity(Row row, int index) {
        final PaymentInvoiceUploadEntity redInvoice = new PaymentInvoiceUploadEntity();
        DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
        //序号
//        redInvoice.setIndexNo(index);

        //扣款公司
        final String jvcode = getCellData(row, 0);
//        redInvoice.setGfName(gfName);
        redInvoice.setJvcode(jvcode);

        //供应商号
        final String supplierAssociation = getCellData(row, 1);
        String supplierAssociation1 = supplierAssociation;
        if(supplierAssociation.length()<6 && supplierAssociation.length()>=1){
            supplierAssociation1 = addZeroForNum(supplierAssociation,6);

        }
        redInvoice.setSupplierAssociation(supplierAssociation1);


        //类型
        final String caseType = getCellData(row, 2);
        redInvoice.setCaseType(caseType);

        //备注
        final String remark = getCellData(row, 3);
        redInvoice.setRemark(remark);

        //换货号
        final String exchangeNo = getCellData(row, 4);
        redInvoice.setExchangeNo(exchangeNo);

        //索赔号
        final String returnGoodsCode = getCellData(row, 5);
        redInvoice.setReturnGoodsCode(returnGoodsCode);

        //定案日期
        final String returnGoodsDate = getCellData(row, 6);
           if(returnGoodsDate!=""){
              if(returnGoodsDate.indexOf("/")==-1){
                  redInvoice.setReturnGoodsDate(returnGoodsDate);

            }else{
                  if(returnGoodsDate.substring(0,3).indexOf("/")!=-1) {
                      try {
                          Date date1 = format1.parse(returnGoodsDate);
                          SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                          String date2 = sim1.format(date1);
                          redInvoice.setReturnGoodsDate(date2);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }else {
                      try {
                          Date date1 = format2.parse(returnGoodsDate);
                          SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                          String date2 = sim1.format(date1);
                          redInvoice.setReturnGoodsDate(date2);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }
              }
          }else {
               redInvoice.setReturnGoodsDate(returnGoodsDate);
           }


        //成本金额
        final String returnCostAmount = getCellData(row, 7);
        redInvoice.setReturnCostAmount(returnCostAmount);
//        if(StringUtils.isEmpty(returnCostAmount)){
//            BigDecimal a = new BigDecimal(-1);
//            redInvoice.setReturnCostAmount(a);
//        }else {
//            BigDecimal returnCostAmount1 = new BigDecimal(returnCostAmount);
//            redInvoice.setReturnCostAmount(returnCostAmount1);
//        }


        //供应商结款发票号
        final String paymentInvoiceNo = getCellData(row, 8);
        redInvoice.setPaymentInvoiceNo(paymentInvoiceNo);

        //扣款日期
        final String deductionDate = getCellData(row, 9);
        if(deductionDate!=""){
            if(deductionDate.indexOf("/")==-1){
                redInvoice.setDeductionDate(deductionDate);

            }else{
                if(deductionDate.substring(0,3).indexOf("/")!=-1) {
                    try {
                        Date date1 = format1.parse(deductionDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setDeductionDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Date date1 = format2.parse(deductionDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setDeductionDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }else {
            redInvoice.setDeductionDate(deductionDate);
        }


        //沃尔玛扣款发票号
        final String purchaseInvoiceNo = getCellData(row, 10);
        redInvoice.setPurchaseInvoiceNo(purchaseInvoiceNo);

        //税率
        final String taxRate1 = getCellData(row, 11);
//        if(!StringUtils.isEmpty(taxRate1)){
//            if(taxRate1.indexOf("%")!=-1){
//                 taxRate1= String.valueOf(Integer.parseInt(taxRate1.substring(0,taxRate1.length()-1))/100);
//            }
//        }
//        String[] split = taxRate1.split("%");
//        String taxRate = split[0];

            if(taxRate1.indexOf("%")!=-1){
                String[] split = taxRate1.split("%");
                String taxRate = split[0];
                BigDecimal taxRate2 = new BigDecimal(taxRate).divide(new BigDecimal(100));
                redInvoice.setTaxRate(taxRate2.toString());
            }else {
                redInvoice.setTaxRate(taxRate1);
            }



        //含税金额
        final String taxAmount = getCellData(row, 12);
        redInvoice.setTaxAmount(taxAmount);
//        if(StringUtils.isEmpty(taxAmount)){
//            BigDecimal a = new BigDecimal(-1);
//            redInvoice.setTaxAmount(a);
//        }else {
//            BigDecimal taxAmount1 = new BigDecimal(taxAmount);
//            redInvoice.setTaxAmount(taxAmount1);
//        }



        //上传日期
        final String sendDate = getCellData(row, 13);
        if(sendDate!=""){
            if(sendDate.indexOf("/")==-1){
                redInvoice.setSendDate(sendDate);

            }else{
                if(sendDate.substring(0,3).indexOf("/")!=-1) {
                    try {
                        Date date1 = format1.parse(sendDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setSendDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Date date1 = format2.parse(sendDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setSendDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            redInvoice.setSendDate(sendDate);
        }


        //邮寄时间
        final String mailData = getCellData(row, 14);
        if(mailData!=""){
            if(mailData.indexOf("/")==-1){
                redInvoice.setMailData(mailData);

            }else{
                if(mailData.substring(0,3).indexOf("/")!=-1) {
                    try {
                        Date date1 = format1.parse(mailData);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setMailData(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Date date1 = format2.parse(mailData);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setMailData(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            redInvoice.setMailData(null);
        }



        //快递单号
        final String expressNo = getCellData(row, 15);
        redInvoice.setExpressNo(expressNo);

        //快递名称
        final String expressName = getCellData(row, 16);
        redInvoice.setExpressName(expressName);

//        //红票序列号
//        final String redticketDataSerialNumber = getCellData(row, 17);
//        redInvoice.setRedticketDataSerialNumber(redticketDataSerialNumber);
//
//        //JVCODE
//        final String JVCODE = getCellData(row, 18);
//        redInvoice.setJVCODE(JVCODE);


        return redInvoice;
    }

    private  String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
        sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
         // sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
    return str;
    }

}
