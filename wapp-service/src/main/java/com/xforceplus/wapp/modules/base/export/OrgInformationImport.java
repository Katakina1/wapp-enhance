package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入认证excel
 * @author Colin.hu
 * @date 4/19/2018
 */
public class OrgInformationImport extends AbstractImportExcel {

    private MultipartFile file;

    public OrgInformationImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<OrganizationEntity> analysisExcel() throws ExcelException {
        List<OrganizationEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();

        int index = 0;
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 1; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
                final OrganizationEntity entity = createImportCertificationEntity(row, index);
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
    private OrganizationEntity createImportCertificationEntity(Row row, int index) {
        final OrganizationEntity redInvoice = new OrganizationEntity();
        //jvcode
        final String jvcode = getCellData(row, 0);
        redInvoice.setOrgcode(jvcode);

        //机构名称
        final String orgname = getCellData(row, 1);
        redInvoice.setOrgname(orgname);

        //税号
        final String taxno = getCellData(row, 2);
        redInvoice.setTaxno(taxno);

        //地址
        final String address = getCellData(row, 3);
        redInvoice.setAddress(address);

        //电话
        final String phone = getCellData(row, 4);
        redInvoice.setPhone(phone);

        //开户行
        final String bank = getCellData(row, 5);
        redInvoice.setBank(bank);

        //账号
        final String account = getCellData(row, 6);
        redInvoice.setAccount(account);

        //店号
        final String storeNumber = getCellData(row, 8);
        redInvoice.setStoreNumber(storeNumber);
        return redInvoice;
    }
}
