package com.xforceplus.wapp.modules.signin.toexcel;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导入认证excel
 *
 * @author Colin.hu
 * @date 4/19/2018
 */
public class SignImport extends AbstractImportExcel {

    private final static Logger LOGGER = getLogger(SignImport.class);

    private MultipartFile file;

    public SignImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     *
     * @return 导入签收实体集
     * @throws ExcelException 读取异常
     */
    public List<RecordInvoiceEntity> analysisExcel() throws ExcelException {
        List<RecordInvoiceEntity> recordInvoiceEntityList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();

        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 2; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if (!isRowEmpty(row)) {
                if(StringUtils.isNotEmpty(getCellData(row, 1)) && StringUtils.isNotEmpty(getCellData(row, 2))) {
                    final RecordInvoiceEntity recordInvoiceEntity = createRecordInvoiceEntity(row);
                    recordInvoiceEntityList.add(recordInvoiceEntity);
                }
            }
        }

        return recordInvoiceEntityList;
    }

    /**
     * 构建导入认证实体
     *
     * @param row 行
     * @return 导入认证实体
     */
    private RecordInvoiceEntity createRecordInvoiceEntity(Row row) {
        final RecordInvoiceEntity recordInvoiceEntity = new RecordInvoiceEntity();

        //发票代码
        final String invoiceCode = getCellData(row, 1);
        recordInvoiceEntity.setInvoiceCode(invoiceCode);

        //发票号码
        final String invoiceNo = getCellData(row, 2);
        recordInvoiceEntity.setInvoiceNo(invoiceNo);

        //金额
        final String amount = getCellData(row, 3);
        if(StringUtils.isNotEmpty(amount) && CommonUtil.isNumber(amount)) {
            final String strAmount = amount.replace(",", "");
            recordInvoiceEntity.setInvoiceAmount(new BigDecimal(strAmount));
        } else {
            recordInvoiceEntity.setInvoiceAmount(null);
        }

        //开票日期
        final String invoiceDate = getCellData(row, 4);
        try {
            recordInvoiceEntity.setInvoiceDate(new DateTime(invoiceDate).toDate());
        } catch (Exception e) {
            LOGGER.error("开票日期格式错误：{}", e);
            recordInvoiceEntity.setInvoiceDate(null);
        }

        //校验码
        final String checkCode = getCellData(row, 5);
        recordInvoiceEntity.setCheckCode(checkCode);

        return recordInvoiceEntity;
    }
}
