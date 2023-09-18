package com.xforceplus.wapp.modules.backfill.mapstruct;


import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.backfill.model.HostInvoiceModel;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, imports = {DateUtils.class})
public interface RecordInvoiceMapper {

    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "invoiceDate", expression = "java(DateUtils.format(entity.getInvoiceDate(), \"yyyy-MM-dd\"))")
    HostInvoiceModel map(TDxRecordInvoiceEntity entity, String taxRate);

    default List<HostInvoiceModel> map(List<TDxRecordInvoiceEntity> list, Map<String, String> taxRateMap) {
        List<HostInvoiceModel> result = new ArrayList<>();
        for (TDxRecordInvoiceEntity entity : list) {
            result.add(map(entity, taxRateMap.get(entity.getInvoiceNo())));
        }
        return result;
    }
}
