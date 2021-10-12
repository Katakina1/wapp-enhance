package com.xforceplus.wapp.modules.overdue.converters;

import org.mapstruct.Mapper;

import java.util.Date;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper
public interface BaseConverter {
    default Long mapDateToLong(Date date) {
        return date.getTime();
    }

    default String mapDateToString(Date date) {
        return String.valueOf(date.getTime());
    }
}
