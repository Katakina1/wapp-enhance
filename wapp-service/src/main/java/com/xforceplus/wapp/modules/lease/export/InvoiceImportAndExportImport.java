package com.xforceplus.wapp.modules.lease.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireOrLeadEntity;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportImportEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入excel
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class InvoiceImportAndExportImport extends AbstractImportExcel {

    private MultipartFile file;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    // new Date()为获取当前系统时间

    public InvoiceImportAndExportImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public Map<String, Object> analysisExcel() throws ExcelException {
        List<InvoiceImportAndExportImportEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();

        Map<String, Object> map = new HashMap<>();
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        Integer errorCount=0;
        for (int i = 1; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {

                final InvoiceImportAndExportImportEntity invoiceEntity = createImportCertificationEntity(row);

                if(invoiceEntity.getAtrue()){
                    enjoySubsidedList.add(invoiceEntity);
//                    enjoySubsidedList.get(0).setTrue(false);
                }else{
                    errorCount++;
                }
            }
        }
        map.put("errorCount",errorCount);
        map.put("list",enjoySubsidedList);
        return map;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private InvoiceImportAndExportImportEntity createImportCertificationEntity(Row row) {
        final InvoiceImportAndExportImportEntity invoiceEntity = new InvoiceImportAndExportImportEntity();

        //数据校验状态
        invoiceEntity.setAtrue(true);
        String id = getCellData(row, 0);
        if (id==""){
            id = "0";
        }
        final String shopNo = getCellData(row, 15);
        final String peRiod = getCellData(row, 16);
        final String matChing = getCellData(row, 17);
        final String matChingDate = getCellData(row, 18);
        final String taxCode = getCellData(row, 19);
        final Boolean flag = this.checkInvoiceMessage(matChingDate);
        invoiceEntity.setAtrue(flag);
        if (flag) {
            invoiceEntity.setId(Integer.valueOf(id));
            invoiceEntity.setShopNo(shopNo);
            invoiceEntity.setPeRiod(peRiod);
            invoiceEntity.setMatChing("成功".equals(matChing)?"1":"0");
            invoiceEntity.setMatChingDate(df.format(new Date()).substring(0,10));
            invoiceEntity.setTaxCode(taxCode);
        }
        return invoiceEntity;

    }

    private Boolean checkInvoiceMessage(String matChingDate) {
        Boolean flag=true;
       /* if("04".equals(CommonUtil.getFplx(invoiceCode))){
            if(checkNo.length()!=6){
                return false;
            }
        }*/
       /*if((!CommonUtil.isValidNum(matChingDate,"^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$"))){
            flag=false;
        }*/

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
