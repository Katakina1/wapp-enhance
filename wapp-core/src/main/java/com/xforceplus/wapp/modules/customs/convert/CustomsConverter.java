package com.xforceplus.wapp.modules.customs.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.customs.dto.CustomsExportDto;
import com.xforceplus.wapp.modules.customs.dto.CustomsImportDto;
import com.xforceplus.wapp.repository.entity.TAcUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface CustomsConverter {
    List<TAcUserEntity> map(List<CustomsImportDto> list);

    List<CustomsExportDto> exportMap(List<TAcUserEntity> list);

    default String mapBigdecimal(BigDecimal decimal) {
        return Objects.isNull(decimal) ? StringUtils.EMPTY : decimal.toPlainString();
    }
}
