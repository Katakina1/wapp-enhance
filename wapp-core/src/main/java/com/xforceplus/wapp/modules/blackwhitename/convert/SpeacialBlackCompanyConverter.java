package com.xforceplus.wapp.modules.blackwhitename.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyBlackImportDto;
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
public interface SpeacialBlackCompanyConverter {


    @IterableMapping(qualifiedByName = "SpecialBlackCompanyEntityReverse")
    List<TXfBlackWhiteCompanyEntity> reverse(List<SpecialCompanyBlackImportDto> entity, @Context Long user);

    @Named("SpecialBlackCompanyEntityReverse")
    TXfBlackWhiteCompanyEntity map(SpecialCompanyBlackImportDto dto, @Context Long user);
}
