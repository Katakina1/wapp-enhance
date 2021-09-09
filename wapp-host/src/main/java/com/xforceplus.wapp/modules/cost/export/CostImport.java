package com.xforceplus.wapp.modules.cost.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.cost.dao.CostApplicationDao;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CostImport extends AbstractImportExcel {

    private MultipartFile file;

    private CostApplicationDao dao;

    public CostImport(MultipartFile file, CostApplicationDao dao) {
        this.file = file;
        this.dao = dao;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<List<String>> analysisExcel() throws Exception {
        List<List<String>> list = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum()+1;

        //获取数据,从第四行开始取
        for (int i = 3; i < rowCount; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                final List<String> entity = createCostImportEntity(row);
                int count = dao.checkInvoice(entity.get(0), entity.get(7).substring(0,8));
                if(count>0){
                    throw new RuntimeException("发票代码:"+entity.get(0)+",发票号码:"+entity.get(7).substring(0,8)+"已存在!");
                }
                list.add(entity);
            }
        }

        return list;
    }

    /**
     * 构建导入费用实体
     * @param row 行
     * @return 导入费用实体
     */
    private List<String> createCostImportEntity(Row row) {
        final List<String> entity = newArrayList();
        //发票信息
        entity.add(getCellData(row, 0));//发票代码
        entity.add(getCellData(row, 2));//校验码
        entity.add(getCellData(row, 3));//发票金额
        entity.add(getCellData(row, 4));//税额
        entity.add(getCellData(row, 5));//价税合计
        //税率信息
        entity.add(getCellData(row, 6));//InvoiceType
        entity.add(getCellData(row, 7));//TaxRate
        entity.add(getCellData(row, 8));//InvoiceNumber
        entity.add(getCellData(row, 9));//InvoiceAmount
        entity.add(getCellData(row, 10));//TaxAmount
        entity.add(getCellData(row, 11));//WithoutTaxAmount
        entity.add(getCellData(row, 12));//InvoiceDate
        //费用信息
        entity.add(getCellData(row, 13));//FeesType
        entity.add(getCellData(row, 14));//FeesDepartment
        entity.add(getCellData(row, 15));//FeesTime
        entity.add(getCellData(row, 16));//FeesTimeTo
        entity.add(getCellData(row, 17));//FeesDescription
        entity.add(getCellData(row, 18));//FeesCount
        entity.add(getCellData(row, 19));//ProjectID

        //发票信息补充
        entity.add(getCellData(row, 1));//jvcode

        return entity;
    }
}
