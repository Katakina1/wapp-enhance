package com.xforceplus.wapp.modules.enterprise.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 企业黑名单-模板下载
 * Created by vito.xing on 2018/4/27
 */
public class EnterpriseBlackTemplate extends AbstractExportExcel {
    @Override
    protected String getExcelUri() {
        return "export/enterprise/enterpriseblack.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
