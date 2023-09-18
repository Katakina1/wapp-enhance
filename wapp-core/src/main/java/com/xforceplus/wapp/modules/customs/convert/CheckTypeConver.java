package com.xforceplus.wapp.modules.customs.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * @Description 勾选状态
 * @Author pengtao
 * @return
**/
public class CheckTypeConver implements Converter<String> {
    @Override
    public Class supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return cellData.getStringValue();
    }

    @Override
    public CellData convertToExcelData(String o, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {

        return new CellData(CheckTypeExportEnum.getValue(o));

    }
}
