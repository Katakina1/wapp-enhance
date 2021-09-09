package com.xforceplus.wapp.modules.cost.importTemplate;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireOrLeadEntity;
import com.xforceplus.wapp.modules.cost.entity.ApplicantEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入excel
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class ApplicantImport extends AbstractImportExcel {

    private MultipartFile file;

    public ApplicantImport(MultipartFile file) {
        this.file = file;
    }
    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<ApplicantEntity> analysisExcel() throws ExcelException {
        List<ApplicantEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();


        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 2; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                final ApplicantEntity invoiceEntity = createImportCertificationEntity(row);
                enjoySubsidedList.add(invoiceEntity);
            }
        }
        return enjoySubsidedList;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private ApplicantEntity createImportCertificationEntity(Row row) {
        final ApplicantEntity invoiceEntity = new ApplicantEntity();
        final String epsNo = getCellData(row,0);
        final String shopNo = getCellData(row, 1);
        final String applicantDepartment = getCellData(row, 2);
        final String applicantNo = getCellData(row, 3);
        final String applicantName = getCellData(row, 4);
        final String applicantCall = getCellData(row, 5);
        final String applicantSubarea = getCellData(row, 6);


            invoiceEntity.setEpsNo(epsNo);
            invoiceEntity.setShopNo(shopNo);
            invoiceEntity.setApplicantDepartment(applicantDepartment);
            invoiceEntity.setApplicantNo(applicantNo);
            invoiceEntity.setApplicantName(applicantName);
            invoiceEntity.setApplicantCall(applicantCall);
            invoiceEntity.setApplicantSubarea(applicantSubarea);



        return invoiceEntity;

    }

}
