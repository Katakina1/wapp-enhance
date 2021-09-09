package com.xforceplus.wapp.modules.enterprise.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 商品黑名单-模板下载
 * Created by vito.xing on 2018/4/27
 */
public class GoodsBlackTemplate extends AbstractExportExcel {
    @Override
    protected String getExcelUri() {
        return "export/enterprise/goodsblack.xlsx";
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
    }
}
