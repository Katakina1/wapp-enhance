package com.xforceplus.wapp.modules.fixed.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportImportEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入excel
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class InvoiceImportExportImport extends AbstractImportExcel {

    private MultipartFile file;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    // new Date()为获取当前系统时间

    public InvoiceImportExportImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     *
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public  Map<String , Object> analysisExcel() throws ExcelException {
        Map<String , Object>  map= new HashMap<>();
        List<InvoiceImportAndExportImportEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();
        //获取模版信息，取第一行第一格（发票导入导出） 第一行头信息（序号	JV	公司代码	供应商号	发票代码	发票号码	开票日期	金额	税额	SAP处理结果	sap处理时间）
        final Row row0 = sheet.getRow(0);
        if (!isRowEmpty(row0)) {
            map.put( "row1cellN",getCellData(row0, 13));
        }else{
            map.put( "row1cellN","");
        }


        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 2; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if (!isRowEmpty(row)) {
                final InvoiceImportAndExportImportEntity invoiceEntity = createImportCertificationEntity(row);
                enjoySubsidedList.add(invoiceEntity);
            }
        }
        map.put( "enjoySubsidedList",enjoySubsidedList);
        return map;
    }

    /**
     * 构建导入认证实体
     *
     * @param row 行
     * @return 导入认证实体
     */
    private InvoiceImportAndExportImportEntity createImportCertificationEntity(Row row) {
        final InvoiceImportAndExportImportEntity invoiceEntity = new InvoiceImportAndExportImportEntity();

        final String invoiceCode = getCellData(row, 4);
        final String invoiceNo = getCellData(row, 5);
        final String sapResult = getCellData(row, 9);
        invoiceEntity.setJvCode(getCellData(row, 1));
        invoiceEntity.setCompanyCode(getCellData(row, 2));
        invoiceEntity.setVenderId(getCellData(row, 3));
        invoiceEntity.setInvoiceDate(getCellData(row, 6));
        invoiceEntity.setInvoiceAmount(getCellData(row, 7));
        invoiceEntity.setTaxAmount(getCellData(row, 8));
//        invoiceEntity.setsAp(formatSap(sapResult));
        invoiceEntity.setsAp(sapResult);
        invoiceEntity.setsApDate(getCellData(row, 10));
        invoiceEntity.setInvoiceCode(invoiceCode);
        invoiceEntity.setUuid(invoiceCode.trim() + invoiceNo.trim());
        return invoiceEntity;
    }

    private String formatSap(String sap) {
        if (sap == null) {
            return "";
        } else if (sap.trim().equals("待确认")) {
            return "0";
        } else if (sap.trim().equals("成功")) {
            return "1";
        } else if (sap.trim().equals("未处理")) {
            return "2";
        } else if (sap.trim().equals("已退票")) {
            return "3";
        } else {
            return "";
        }
    }

    private static void change(InvoiceImportAndExportImportEntity entity,Integer a,String s){
        entity.setUuid("1");
        a=1;
        s="1";
    }

    public static void main(String[] args){
        InvoiceImportAndExportImportEntity entity = new InvoiceImportAndExportImportEntity();
        entity.setUuid("0");
        Integer a = new Integer(124124);
        String s ="0";
        change(entity,a,s);
        System.out.println(entity.getUuid());
        System.out.println(a);
        System.out.println(s);
    }



}
