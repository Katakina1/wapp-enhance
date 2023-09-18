package com.xforceplus.wapp.modules.supserviceconf.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xforceplus.wapp.enums.SuperServiceTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @Description 供应商服务导入，服务类型枚举转换
 * @Author pengtao
**/
public class SuperServiceTypeConverter implements Converter<Integer> {


    @Override
    public Class supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if(StringUtils.isNotBlank(cellData.getStringValue())){
            if(StringUtils.equals(SuperServiceTypeEnum.VIP.getDesc(),cellData.getStringValue())){
                return SuperServiceTypeEnum.VIP.getCode();
            }else if(StringUtils.equals(SuperServiceTypeEnum.NORMAL.getDesc(),cellData.getStringValue())){
                return SuperServiceTypeEnum.NORMAL.getCode();
            }else {
                return -1;
            }
        }else {
            return -1;
        }
    }

    @Override
    public CellData convertToExcelData(Integer value, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if(Objects.nonNull(value)){
            if(value.equals(SuperServiceTypeEnum.VIP.getCode())){
                return new CellData(SuperServiceTypeEnum.VIP.getDesc());
            }else if(value.equals(SuperServiceTypeEnum.NORMAL.getCode())){
                return new CellData(SuperServiceTypeEnum.NORMAL.getDesc());
            }
        }
        return new CellData(value);
    }
}
