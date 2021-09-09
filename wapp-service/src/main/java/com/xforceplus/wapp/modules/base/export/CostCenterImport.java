package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.base.entity.CostCenterEntity;
import com.xforceplus.wapp.modules.base.entity.Staff;
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
public class CostCenterImport extends AbstractImportExcel {

    private MultipartFile file;

    public CostCenterImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<CostCenterEntity> analysisExcel() throws ExcelException {
        List<CostCenterEntity> enjoySubsidedList = newArrayList();
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
                final CostCenterEntity entity = createImportCertificationEntity(row, index);
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
    private CostCenterEntity createImportCertificationEntity(Row row, int index) {
        final CostCenterEntity costs = new CostCenterEntity();

        //winid
        final String jv = getCellData(row, 0);
        costs.setJv(jv);

        //工号
        final String cost = getCellData(row, 1);
        costs.setCostCenters(cost);

        return costs;
    }
}
