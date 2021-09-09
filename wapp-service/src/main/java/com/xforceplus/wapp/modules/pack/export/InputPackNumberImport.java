package com.xforceplus.wapp.modules.pack.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.InputPackNumberEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

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
public class InputPackNumberImport extends AbstractImportExcel {

    private MultipartFile file;

    public InputPackNumberImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */

    public Map<String, Object> analysisExcel() throws ExcelException {
        List<InputPackNumberEntity> enjoySubsidedList = newArrayList();
        HashSet<String> set = new HashSet<>();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();
        Map<String, Object> map = newHashMap();
        if(rowCount>5001){
            map.put("status","false");
            return map;
        }
        int index = 0;
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 1; i < rowCount + 1; i++) {

            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
                final InputPackNumberEntity entity = createImportCertificationEntity(row, index);
                enjoySubsidedList.add(entity);

            }
        }
        map.put("enjoySubsidedList",enjoySubsidedList);
        map.put("status","true");
        return map;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private InputPackNumberEntity createImportCertificationEntity(Row row, int index) {
        final InputPackNumberEntity redInvoice = new InputPackNumberEntity();

        final String bindNumber = getCellData(row, 1);
        redInvoice.setBbindingNo(bindNumber);
        final String packNumber = getCellData(row, 5);
        redInvoice.setPackingNo(packNumber);
        final String packAddress = getCellData(row, 6);
        redInvoice.setPackingAddress(packAddress);
        return redInvoice;
    }
}
