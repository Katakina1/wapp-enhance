package com.xforceplus.wapp.modules.company.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.company.dto.CompanyImportDto;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface CompanyConverter {
    List<TAcOrgEntity> map(List<TAcOrgEntity> entity);

    TAcOrgEntity map(TAcOrgEntity entity);

    @IterableMapping(qualifiedByName = "CompanyEntityReverse")
    List<TAcOrgEntity> reverse(List<CompanyImportDto> entity, @Context Long user);

    @Named("CompanyEntityReverse")
    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "orgCode", source = "supplierCode")
    @Mapping(target = "orgName", source = "supplierName")
    @Mapping(target = "taxNo", source = "supplierTaxNo")
    @Mapping(target = "orgType", source = "orgType")
    TAcOrgEntity map(CompanyImportDto dto, @Context Long user);
}
