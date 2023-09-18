package com.xforceplus.wapp.modules.exchangeTicket.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xforceplus.wapp.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class ExchangeDateConver implements Converter<String> {
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
        if (StringUtils.isNotEmpty(o)) {
            Date date=DateUtils.strToDate(o);
            return new CellData(DateUtils.format(date,DateUtils.DATE_PATTERN));

        }
        return new CellData(o);

    }
}
