package com.xforceplus.wapp.modules.taxcode.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.handle.vo.TaxCodeVO;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeDto;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.models.TaxCodeTree;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface TaxCodeConverter {
    List<TaxCode> map(List<TaxCodeEntity> entity);
    
    TaxCodeDto map(TaxCodeEntity entity, String itemShortName);

    @BeanMapping(qualifiedByName = "updateTaxCodeDeleteFlag")
    TaxCodeEntity map(TaxCodeVO taxCode);

    @AfterMapping
    @Named("updateTaxCodeDeleteFlag")
    default void updateTaxCodeDeleteFlag(@MappingTarget TaxCodeEntity entity) {
        if ("9".equalsIgnoreCase(entity.getStatus())) {
            entity.setDeleteFlag(String.valueOf(System.currentTimeMillis()));
        }
    }

    @Mapping(target = "categoryName", source = "taxCode.largeCategoryName")
    @Mapping(target = "categoryCode", source = "taxCode.largeCategoryCode")
    @Mapping(target = "children", source = "children")
    TaxCodeTree map(TaxCodeEntity taxCode, List<TaxCodeEntity> children);

    @Mapping(target = "categoryName", source = "medianCategoryName")
    @Mapping(target = "categoryCode", source = "medianCategoryCode")
    TaxCodeTree mapTree(TaxCodeEntity children);
}
