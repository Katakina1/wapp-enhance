package com.xforceplus.wapp.modules.base.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.base.entity.UserImportEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入excel
 * @author Adil.Xu
 * @date 11/21/2018
 */
public class UserImport extends AbstractImportExcel {
    private MultipartFile file;

    public UserImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */

    public List<UserImportEntity> analysisExcel() throws ExcelException {
        List<UserImportEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();

        int index = 0;
        //获取数据
        for (int i = 1; i <= rowCount; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
                final UserImportEntity userEntity = createImportCertificationEntity(row, index);

                enjoySubsidedList.add(userEntity);
            }
        }

        return enjoySubsidedList;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private UserImportEntity createImportCertificationEntity(Row row, int index) {
        final UserImportEntity userEntity = new UserImportEntity();

        //供应商号
        final String usercode = getCellData(row, 0);
        //供应商名称
        final String username = getCellData(row, 1);

        //序号
        userEntity.setIndexNo(index);
        userEntity.setUsercode(usercode);
        userEntity.setUsername(username);
        return userEntity;
    }
}
