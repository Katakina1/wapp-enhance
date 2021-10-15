package com.xforceplus.wapp.common.enums;

import lombok.NonNull;
import org.apache.commons.lang3.EnumUtils;

import java.util.Optional;

public interface ValueEnum<T> {
    T getValue();

    static <E extends Enum<E> & ValueEnum<T>, T> boolean isValid(Class<E> clazz, T value) {
        return getEnumByValue(clazz, value).isPresent();
    }

    static <E extends Enum<E> & ValueEnum<T>, T> Optional<E> getEnumByValue(@NonNull Class<E> clazz, T value) {
        return value == null ? Optional.empty() : EnumUtils.getEnumList(clazz).stream()
                .filter(c -> value.equals(c.getValue())).findFirst();
    }

    static <E extends Enum<E> & ValueEnum<T>, T> Optional<E> getEnumByOrdinal(@NonNull Class<E> clazz, Integer ordinal) {
        return ordinal == null ? Optional.empty() : EnumUtils.getEnumList(clazz).stream()
                .filter(p -> p.ordinal() == ordinal).findFirst();
    }

    static <E extends Enum<E> & ValueEnum<T>, T> Integer getOrdinalByValue(@NonNull Class<E> clazz, T value) {
        return ValueEnum.getEnumByValue(clazz, value).map(Enum::ordinal).orElse(null);
    }
}
