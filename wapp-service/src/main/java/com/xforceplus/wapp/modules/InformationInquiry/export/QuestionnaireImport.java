package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireOrLeadEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ImportEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入excel
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class QuestionnaireImport extends AbstractImportExcel {

    private MultipartFile file;

    public QuestionnaireImport(MultipartFile file) {
        this.file = file;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<QuestionnaireOrLeadEntity> analysisExcel() throws ExcelException {
        List<QuestionnaireOrLeadEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();


        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 2; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {

                final QuestionnaireOrLeadEntity invoiceEntity = createImportCertificationEntity(row);

                if(invoiceEntity.getAtrue()){
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
    private QuestionnaireOrLeadEntity createImportCertificationEntity(Row row) {
        final QuestionnaireOrLeadEntity invoiceEntity = new QuestionnaireOrLeadEntity();

//id,
//        datet                            dateT,
//        input_user                       inputUser,
//        jv                               jV,
//        vendor_no                        vendorNo,
//        inv_no                           invNo,
//        invoice_cost                     InvoiceCost,
//        wm_cost                          wMCost,
//        batch_id                         batchID,
//      pONo,
//    trans,
//  rece,
//   errCode,
//  errDesc,
//       errStatus
        //数据校验状态
        invoiceEntity.setAtrue(true);
        String id = getCellData(row, 0);
        if (id==""){
            id = "0";
        }
        final String isDel = getCellData(row,2);
        final String dateT = getCellData(row, 3);
        final String inputUser = getCellData(row, 4);
        final String jV = getCellData(row, 5);
        final String vendorNo = getCellData(row, 6);
        final String invNo = getCellData(row, 7);
        final String invoiceCost = getCellData(row, 8);
        final String wMCost = getCellData(row, 12);
        final String batchID = getCellData(row, 13);
        final String pONo = getCellData(row, 14);
        final String trans = getCellData(row, 15);
        final String rece = getCellData(row, 16);
        final String errCode = getCellData(row, 17);
        final String errDesc = getCellData(row, 18);
        final String errStatus = getCellData(row, 19);
        final String invoiceDate = getCellData(row, 20);
        try {
            Date invoiceDate2= sdf.parse(invoiceDate);
            invoiceEntity.setInvoiceDate(invoiceDate2);
        }catch (Exception e){
        }

        final Boolean flag = this.checkInvoiceMessage(dateT);
        //,inputUser,jV,vendorNo,invNo,InvoiceCost,wMCost,batchID,pONo,trans,rece,errCode,errDesc,errStatus
        invoiceEntity.setAtrue(flag);
        if (flag) {
            invoiceEntity.setIds(Integer.valueOf(id));
            invoiceEntity.setIsDel(getisDels(isDel));
            invoiceEntity.setInputUser(inputUser);
            invoiceEntity.setjV(jV);
            invoiceEntity.setVendorNo(vendorNo);
            invoiceEntity.setInvNo(invNo);
            invoiceEntity.setDateT(dateT.substring(0,10).replace("_","-"));
            invoiceEntity.setInvoiceCost(invoiceCost);
            invoiceEntity.setwMCost(wMCost);
            invoiceEntity.setBatchID(batchID);
            invoiceEntity.setpONo(pONo);
            invoiceEntity.setRece(rece);
            invoiceEntity.setErrCode(errCode);
            invoiceEntity.setErrDesc(errDesc);
            invoiceEntity.setErrStatus(errStatus);
            invoiceEntity.setTrans(trans);
        }
        return invoiceEntity;

    }

    private String getisDels(String getisdels){
        String value="";
        if("未处理".equals(getisdels)){
            value="0";
        }else if("已退票".equals(getisdels)){
            value="1";
        }else if("已处理".equals(getisdels)){
            value="2";
        }else if("需重匹".equals(getisdels)){
            value="3";
        }else {
            value = "0";
        }
        return value;
    }

    private Boolean checkInvoiceMessage(String invoiceDate) {
        Boolean flag=true;
       /* if("04".equals(CommonUtil.getFplx(invoiceCode))){
            if(checkNo.length()!=6){
                return false;
            }
        }*/
//       if((!CommonUtil.isValidNum(invoiceDate,"^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$"))){
//            flag=false;
//        }

        if(flag){
//            BigDecimal amount=new BigDecimal(invoiceAmount);
//            BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));;
//            BigDecimal taxAmount1=new BigDecimal(taxAmount);

           // BigDecimal rest=taxAmount1.subtract(amount.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
            /*if(rest.compareTo(BigDecimal.ZERO)>0){
                flag=!(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);

            }else{
                flag=rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0;
            }*/
        }
        return flag;

    }




}
