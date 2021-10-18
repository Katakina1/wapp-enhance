package com.xforceplus.wapp.modules.overdue.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface OverdueConverter {
    List<Overdue> map(List<OverdueEntity> entity);

    @IterableMapping(qualifiedByName = "OverdueEntityReverse")
    List<OverdueEntity> reverse(List<OverdueDto> entity, @Context Long user);

    @Named("OverdueEntityReverse")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createUser", expression = "java(user)")
    @Mapping(target = "updateUser", expression = "java(user)")
    OverdueEntity map(OverdueDto overdue, @Context Long user);

    @Mapping(target = "sellerName", ignore = true)
    @Mapping(target = "sellerTaxNo", ignore = true)
    @Mapping(target = "sellerNo", ignore = true)
    @Mapping(target = "type", ignore = true)
    OverdueEntity map(OverdueDto overdue);
}
