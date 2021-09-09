package com.xforceplus.wapp.modules.InformationInquiry.importTemplate;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ScanConfirmImportExcel extends AbstractImportExcel {

    private MultipartFile file;

    public ScanConfirmImportExcel(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<ConfirmInvoiceEntity> analysisExcel() throws ExcelException {
        List<ConfirmInvoiceEntity> list = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum()+1;

        //获取数据,从第3行开始取
        for (int i = 2; i < rowCount; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                final ConfirmInvoiceEntity entity = createImportEntity(row);
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * 构建导入实体
     * @param row 行
     * @return 导入实体
     */
    private ConfirmInvoiceEntity createImportEntity(Row row) {
        final ConfirmInvoiceEntity entity = new ConfirmInvoiceEntity();
        //发票信息
        entity.setId(Long.valueOf(getCellData(row, 0)));//序号
        entity.setInvoiceCode(getCellData(row, 1));//发票代码
        entity.setInvoiceNo(getCellData(row, 2));//发票号码
        entity.setGfTaxNo(getCellData(row, 4));//购方税号
        entity.setJvcode(getCellData(row, 12));//jvcode
        entity.setVenderid(getCellData(row, 13));//供应商号
        entity.setConfirmReason(getCellData(row, 11));//确认原因
        if(getCellData(row, 14)!=null) {
            entity.setDeductibleTaxRate(new BigDecimal(getCellData(row, 14)));
        }else {
            entity.setDeductibleTaxRate(new BigDecimal(0));
        }
        if(getCellData(row, 15)!=null) {
            entity.setDeductibleTax(new BigDecimal(getCellData(row, 15)));
        }else {
            entity.setDeductibleTax(new BigDecimal(0));
        }
        return entity;
    }
}

