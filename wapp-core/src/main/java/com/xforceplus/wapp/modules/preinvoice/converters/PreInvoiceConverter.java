package com.xforceplus.wapp.modules.preinvoice.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Objects;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class, imports = {Objects.class})
public interface PreInvoiceConverter {

    List<PreInvoice> map(List<TXfPreInvoiceEntity> entity);

    @Mapping(target = "isOil", expression = "java(!Objects.isNull(entity.getIsOil()) && entity.getIsOil() == 1)")
    PreInvoice map(TXfPreInvoiceEntity entity);

    @Mapping(target = "isOil", expression = "java(!Objects.isNull(entity.getIsOil()) && entity.getIsOil() == 1)")
    PreInvoice map(TXfPreInvoiceEntity entity, List<TXfPreInvoiceItemEntity> items);

    @AfterMapping
    default void update(@MappingTarget PreInvoice preInvoice) {
        if (preInvoice.getIsOil() && CollectionUtils.isNotEmpty(preInvoice.getItems())) {
            preInvoice.getItems().forEach(it->{
                it.setQuantity(null);
                it.setUnitPrice(null);
            });
        }
    }
}
