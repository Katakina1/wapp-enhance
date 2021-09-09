package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.base.entity.UniversalTaxRateEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CommodityImport extends AbstractImportExcel {

    private MultipartFile file;

    public CommodityImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<UniversalTaxRateEntity> analysisExcel() throws ExcelException {
        List<UniversalTaxRateEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();

        final String names = getCellData(sheet.getRow(0), 0);
        int index = 0;
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        if (names.equals("供应商普票零税率")) {
        for (int i = 2; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行

                if (!isRowEmpty(row)) {
                    ++index;
                    final UniversalTaxRateEntity entity = createImportCertificationEntity(row, index);
                    enjoySubsidedList.add(entity);
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
    private UniversalTaxRateEntity createImportCertificationEntity(Row row, int index) {
        final UniversalTaxRateEntity universalTaxRate = new UniversalTaxRateEntity();
        //序号
        universalTaxRate.setIndex(index);

        //供应商编号
        final String vendorNbr = getCellData(row, 1);
        universalTaxRate.setVendorNbr(vendorNbr);

        //部门编号
        final String deptId = getCellData(row, 2);
        universalTaxRate.setDeptId(deptId);

        //商品编号
        final String itemNbr = getCellData(row, 3);
        universalTaxRate.setItemNbr(itemNbr);

        //供应商名称
        final String vendorName = getCellData(row, 4);
        universalTaxRate.setVendorName(vendorName);

        //商品说明
        final String notes = getCellData(row, 5);
        universalTaxRate.setNotes(notes);

        //税率
        final String inputTaxe = getCellData(row, 6);
        universalTaxRate.setInputTaxe(inputTaxe);

        return universalTaxRate;
    }
}
