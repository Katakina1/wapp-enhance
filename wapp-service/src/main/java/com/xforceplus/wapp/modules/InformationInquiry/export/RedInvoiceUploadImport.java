package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadEntity;
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
public class RedInvoiceUploadImport extends AbstractImportExcel {

    private MultipartFile file;

    public RedInvoiceUploadImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */

    public List<RedInvoiceUploadEntity> analysisExcel() throws ExcelException {
        List<RedInvoiceUploadEntity> enjoySubsidedList = newArrayList();
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

                final RedInvoiceUploadEntity entity = createImportCertificationEntity(row, index);
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
    private RedInvoiceUploadEntity createImportCertificationEntity(Row row, int index) {
        final RedInvoiceUploadEntity redInvoice = new RedInvoiceUploadEntity();
        DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
        //类型
        final String redType = getCellData(row, 0);
        redInvoice.setRedType(redType);
        //红票生成日期
        final String redInvoiceDate = getCellData(row, 1);
        if(redInvoiceDate!=""){
            if(redInvoiceDate.indexOf("/")==-1){
                redInvoice.setRedInvoiceDate(redInvoiceDate);

            }else{
                if(redInvoiceDate.substring(0,3).indexOf("/")!=-1) {
                    try {
                        Date date1 = format1.parse(redInvoiceDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setRedInvoiceDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Date date1 = format2.parse(redInvoiceDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setRedInvoiceDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            redInvoice.setRedInvoiceDate(redInvoiceDate);
        }
        //供应商号
        final String venderId = getCellData(row, 2);
        redInvoice.setVenderid(venderId);
        //供应商号
        final String venderName = getCellData(row, 3);
        redInvoice.setVenderName(venderName);
        //金额
        final String redAmount = getCellData(row, 4);
        redInvoice.setRedAmount(redAmount);
        /*//税率
        final String taxRate = getCellData(row, 5);
        redInvoice.setTaxRate(taxRate);
        //税额
        final String taxAmount = getCellData(row, 6);
        redInvoice.setTaxAmount(taxAmount);*/
        //发票或协议号
        final String invoiceOrAgreementNo = getCellData(row, 5);
        redInvoice.setInvoiceOrAgreementNo(invoiceOrAgreementNo);

        //红票信息编号
        final String redInvoiceNo = getCellData(row, 6);
        redInvoice.setRedInvoiceNo(redInvoiceNo);


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
