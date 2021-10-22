package com.xforceplus.wapp.modules.taxcode.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.handle.vo.TaxCodeVO;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.models.TaxCodeTree;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface TaxCodeConverter {
    List<TaxCode> map(List<TaxCodeEntity> entity);

    TaxCode map(TaxCodeEntity entity);

    TaxCodeEntity map(TaxCodeVO taxCode);

    @Mapping(target = "medianCategoryName", ignore = true)
    @Mapping(target = "medianCategoryCode", ignore = true)
    TaxCodeTree map(TaxCodeEntity taxCode, List<TaxCodeEntity> children);
}
