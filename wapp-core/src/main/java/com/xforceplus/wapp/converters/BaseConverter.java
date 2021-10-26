package com.xforceplus.wapp.converters;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class)
public interface BaseConverter {
    default Long mapDateToLong(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.getTime();
    }

    default BigDecimal mapBigDecimal(String decimal) {
        return StringUtils.isBlank(decimal) ? null : new BigDecimal(decimal);
    }

    @SneakyThrows
    @Named("formatYMD")
    default Date formatYMD(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.parse(date);
    }

    @SneakyThrows
    @Named("formatYMDHMS")
    default Date formatYMDHMS(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(date);
    }
}
