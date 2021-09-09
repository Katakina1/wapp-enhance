package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.base.entity.Staff;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
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
public class StaffImport extends AbstractImportExcel {

    private MultipartFile file;

    public StaffImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<Staff> analysisExcel() throws ExcelException {
        List<Staff> enjoySubsidedList = newArrayList();
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
                final Staff entity = createImportCertificationEntity(row, index);
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
    private Staff createImportCertificationEntity(Row row, int index) {
        final Staff staff = new Staff();
        //序号
//        redInvoice.setIndexNo(index);

        //winid
        final String winid = getCellData(row, 0);
        staff.setWinID(winid);

        //工号
        final String staffno = getCellData(row, 1);
        staff.setStaffNo(staffno);

        //员工名称
        final String staffName = getCellData(row, 2);
        staff.setStaffName(staffName);

        //邮箱
        final String email = getCellData(row, 3);
        staff.setEmail(email);


//        //成本中心编号
//        final String cbzx = getCellData(row, 4);
//        staff.setCostCenter(cbzx);
//
//        //成本中心名称
//        final String cbzxName = getCellData(row, 5);
//        staff.setCostCenterName(cbzxName);

        //供应商号
        final String vendors = getCellData(row, 4);
        staff.setVendors(vendors);

        //成本中心对应的购方税号
//        final String gfTaxno = getCellData(row, 7);
//        staff.setGfTaxNo(gfTaxno);

        //jvcode数据
        final String jvs  = getCellData(row,5);
        staff.setJvs(jvs);

        return staff;
    }
}
