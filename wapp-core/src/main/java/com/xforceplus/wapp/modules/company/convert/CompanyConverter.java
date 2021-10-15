package com.xforceplus.wapp.modules.company.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface CompanyConverter {
    List<TAcOrgEntity> map(List<TAcOrgEntity> entity);

    TAcOrgEntity map(TAcOrgEntity entity);
}
