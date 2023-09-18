package com.xforceplus.wapp.modules.exchangeTicket.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xforceplus.wapp.enums.ExchangeTickeySourceEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class ExchangeAmountConver implements Converter<String> {
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
            BigDecimal bigDecimal = new BigDecimal(o);
            bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            return new CellData(bigDecimal.stripTrailingZeros().toString());
        }
        return new CellData(o);

    }
}
