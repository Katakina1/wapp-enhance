package com.xforceplus.wapp.modules.preinvoice.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface PreInvoiceConverter {
    List<PreInvoice> map(List<TXfPreInvoiceEntity> entity);

    PreInvoice map(TXfPreInvoiceEntity entity, List<TXfPreInvoiceItemEntity> items);
}
