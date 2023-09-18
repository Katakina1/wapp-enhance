package com.xforceplus.wapp.modules.taxcode.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.handle.vo.TaxCodeVO;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeDto;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.models.TaxCodeLog;
import com.xforceplus.wapp.modules.taxcode.models.TaxCodeTree;
import com.xforceplus.wapp.repository.entity.TaxCodeAuditEntity;
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

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemNo", source = "itemNo")
    @Mapping(target = "goodsTaxNo", source = "goodsTaxNo")
    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "itemSpec", source = "itemSpec")
    @Mapping(target = "quantityUnit", source = "quantityUnit")
    @Mapping(target = "taxPre", source = "taxPre")
    @Mapping(target = "taxPreCon", source = "taxPreCon")
    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "zeroTax", source = "zeroTax")
    @Mapping(target = "standardItemName", source = "standardItemName")
    TaxCodeEntity map(TaxCode entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemNo", source = "itemNo")
    @Mapping(target = "goodsTaxNo", source = "goodsTaxNo")
    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "itemSpec", source = "itemSpec")
    @Mapping(target = "quantityUnit", source = "quantityUnit")
    @Mapping(target = "taxPre", source = "taxPre")
    @Mapping(target = "taxPreCon", source = "taxPreCon")
    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "zeroTax", source = "zeroTax")
    @Mapping(target = "standardItemName", source = "standardItemName")
    TaxCodeEntity cleanValue(TaxCodeEntity entity);

    @Mapping(target = "before", ignore = true)
    @Mapping(target = "after", ignore = true)
    TaxCodeLog map(TaxCodeAuditEntity entity);

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
