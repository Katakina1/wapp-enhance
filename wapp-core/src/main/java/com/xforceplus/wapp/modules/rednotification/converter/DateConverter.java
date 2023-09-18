package com.xforceplus.wapp.modules.rednotification.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-01-18 17:32
 **/
public class DateConverter implements Converter {
    /**
     * Back to object types in Java
     *
     * @return Support for Java class
     */
    @Override
    public Class supportJavaTypeKey() {
        return Date.class;
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
    public Object convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
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
    public CellData convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Date date= (Date) value;
        return new CellData(Optional.ofNullable(date).map(x->
            LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
             ).orElse(StringUtils.EMPTY)
        );
    }
}
