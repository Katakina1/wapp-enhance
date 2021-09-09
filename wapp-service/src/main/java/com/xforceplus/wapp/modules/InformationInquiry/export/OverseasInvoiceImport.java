package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.OverseasInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.DateFormat;
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
public class OverseasInvoiceImport extends AbstractImportExcel {

    private MultipartFile file;

    public OverseasInvoiceImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */

    public List<OverseasInvoiceEntity> analysisExcel() throws ExcelException {
        List<OverseasInvoiceEntity> enjoySubsidedList = newArrayList();
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

                final OverseasInvoiceEntity entity = createImportCertificationEntity(row, index);
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
    private OverseasInvoiceEntity createImportCertificationEntity(Row row, int index) {
        final OverseasInvoiceEntity redInvoice = new OverseasInvoiceEntity();
        //序号
//        redInvoice.setIndexNo(index);

        //公司代码
        final String  companyCode= getCellData(row, 0);
        redInvoice.setCompanyCode(companyCode);

        //orgcode
        final String jvcode = getCellData(row, 1);
        redInvoice.setJvcode(jvcode);

        //发票号码
        final String invoiceNo = getCellData(row, 2);
        redInvoice.setInvoiceNo(invoiceNo);

        //发票代码
        final String invoiceCode = getCellData(row, 3);
        redInvoice.setInvoiceCode(invoiceCode);

        //开票日期
        final String invoiceDate = getCellData(row, 4);
        redInvoice.setInvoiceDate(invoiceDate);

        //供应商号
        final String venderid = getCellData(row, 5);
        String venderid1 = venderid;
        if(venderid.length()<6){
            venderid1 = addZeroForNum(venderid,6);
        }
        redInvoice.setVenderid(venderid1);

        //购方名称
        final String gfName = getCellData(row, 6);
        redInvoice.setGfname(gfName);

        //门店
        final String store = getCellData(row, 7);
        redInvoice.setStore(store);

        //税额
        final String taxAmount = getCellData(row, 8);
        redInvoice.setTaxAmount(new BigDecimal(taxAmount).setScale(2, BigDecimal.ROUND_HALF_UP));

        //税码
        final String taxCode = getCellData(row, 9);
        redInvoice.setTaxCode(taxCode);

        //税率
        final String taxRate1 = getCellData(row, 10);
        if(taxRate1.indexOf("%")!=-1){
            String[] split = taxRate1.split("%");
            String taxRate = split[0];
            redInvoice.setTaxRate(new BigDecimal(taxRate));
        }else if(taxRate1.indexOf(".")!=-1){
            BigDecimal taxRate2 = new BigDecimal(taxRate1).multiply(new BigDecimal(100));
            redInvoice.setTaxRate(taxRate2);
        }else {
            redInvoice.setTaxRate(new BigDecimal(taxRate1));
        }

        //成本金额
        final String costAmount = getCellData(row, 11);
        redInvoice.setCostAmount(new BigDecimal(costAmount));

        //价税合计
        final String totalAmount = getCellData(row, 12);
        redInvoice.setTotalAmount(new BigDecimal(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP));

        //凭证号
        final String certificateNo = getCellData(row, 13);
        redInvoice.setCertificateNo(certificateNo);


        //备注
        final String remarks = getCellData(row, 14);
        redInvoice.setRemarks(remarks);

        //业务类型
        final String flowType = getCellData(row, 15);
        redInvoice.setFlowType(flowType);

        return redInvoice;
    }

    private  String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }
}
