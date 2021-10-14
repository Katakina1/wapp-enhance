package com.xforceplus.wapp.converters;

import org.mapstruct.Mapper;

import java.util.Date;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class)
public interface BaseConverter {
    default Long mapDateToLong(Date date) {
        return date.getTime();
    }

    default String mapDateToString(Date date) {
        return String.valueOf(date.getTime());
    }
}
