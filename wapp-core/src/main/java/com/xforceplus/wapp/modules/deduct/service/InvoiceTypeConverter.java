package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by SunShiyong on 2021/11/10.
 */
public class InvoiceTypeConverter implements Converter<String> {


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
        return null;
    }

    @Override
    public CellData convertToExcelData(String s, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        InvoiceTypeEnum invoiceTypeEnum = ValueEnum.getEnumByValue(InvoiceTypeEnum.class, s).get();
        return new CellData(invoiceTypeEnum.getResultTip());
    }
}
