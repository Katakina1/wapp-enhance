package com.xforceplus.wapp.modules.noneBusiness.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;

public class TaxRateConverter implements Converter<String> {
    /**
     * Back to object types in Java
     *
     * @return Support for Java class
     */
    @Override
    public Class supportJavaTypeKey() {
        return String.class;
    }

    /**
     * Back to object enum in excel
     *
     * @return Support for {@link CellDataTypeEnum}
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * Convert excel objects to Java objects
     *
     * @param cellData            Excel cell data.NotNull.
     * @param contentProperty     Content property.Nullable.
     * @param globalConfiguration Global configuration.NotNull.
     * @return Data to put into a Java object
     * @throws Exception Exception.
     */
    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return cellData.getStringValue();
    }

    /**
     * Convert Java objects to excel objects
     *
     * @param value               Java Data.NotNull.
     * @param contentProperty     Content property.Nullable.
     * @param globalConfiguration Global configuration.NotNull.
     * @return Data to put into a Excel
     * @throws Exception Exception.
     */
    @Override
    public CellData convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData(RecordInvoiceService.mapTaxRate(value));
    }
}
