package com.xforceplus.wapp.modules.blackwhitename.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportDto;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface SpeacialCompanyConverter {


    @IterableMapping(qualifiedByName = "SpecialCompanyEntityReverse")
    List<TXfBlackWhiteCompanyEntity> reverse(List<SpecialCompanyImportDto> entity, @Context Long user);

    @Named("SpecialCompanyEntityReverse")
    TXfBlackWhiteCompanyEntity map(SpecialCompanyImportDto dto, @Context Long user);
}
