package com.xforceplus.wapp.modules.scanRefund.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * 导入认证excel
 * @author Colin.hu
 * @date 4/19/2018
 */
public class EnterPackageNumberImport extends AbstractImportExcel {

    private MultipartFile file;

    public EnterPackageNumberImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */

    public Map<String, Object> analysisExcel() throws ExcelException {
        List<EnterPackageNumberEntity> enjoySubsidedList = newArrayList();
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

                final EnterPackageNumberEntity entity = createImportCertificationEntity(row, index);
                set.add(entity.getRebateExpressno());
                enjoySubsidedList.add(entity);

            }
        }
        map.put("set",set);
        map.put("enjoySubsidedList",enjoySubsidedList);

        return map;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private EnterPackageNumberEntity createImportCertificationEntity(Row row, int index) {
        final EnterPackageNumberEntity redInvoice = new EnterPackageNumberEntity();
        DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
        //序号
        redInvoice.setIndexNo(index);

        //退单号
        final String rebateNo = getCellData(row, 1);
        redInvoice.setRebateNo(rebateNo);

        //邮包号
        final String rebateExpressno = getCellData(row, 2);
        redInvoice.setRebateExpressno(rebateExpressno);

        //邮寄时间
        final String mailDate = getCellData(row, 3);
        if(mailDate!=""){
            if(mailDate.indexOf("/")==-1){
                redInvoice.setMailDate(mailDate);

            }else{
                if(mailDate.substring(0,3).indexOf("/")!=-1) {
                    try {
                        Date date1 = format1.parse(mailDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setMailDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Date date1 = format2.parse(mailDate);
                        SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date2 = sim1.format(date1);
                        redInvoice.setMailDate(date2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            redInvoice.setMailDate(mailDate);
        }


        //邮寄公司
        final String mailCompany = getCellData(row, 4);
        redInvoice.setMailCompany(mailCompany);


        return redInvoice;
    }
}
