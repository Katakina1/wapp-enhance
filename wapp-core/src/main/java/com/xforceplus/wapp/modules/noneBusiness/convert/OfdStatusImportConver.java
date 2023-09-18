package com.xforceplus.wapp.modules.noneBusiness.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xforceplus.wapp.enums.OfdStatusExportEnum;
import com.xforceplus.wapp.enums.exceptionreport.NoneBusinessInvoiceTypeExportEnum;
import org.apache.commons.lang.StringUtils;

public class OfdStatusImportConver implements Converter<String> {
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
        String str= cellData.getStringValue();
        if(StringUtils.isNotEmpty(str)){
            return OfdStatusExportEnum.getCode(str);
        }
        return null;
    }

    @Override
    public CellData convertToExcelData(String o, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {

        return new CellData(OfdStatusExportEnum.getValue(o));

    }
}
