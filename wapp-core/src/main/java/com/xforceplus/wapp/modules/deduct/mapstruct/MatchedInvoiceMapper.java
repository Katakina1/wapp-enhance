package com.xforceplus.wapp.modules.deduct.mapstruct;

import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-23 14:20
 **/
@Mapper(componentModel = "spring")
public interface MatchedInvoiceMapper {

    @Mapping(target = "invoiceDate", source = "makeDate")
    @Mapping(target = "matchedAmount", source = "invoiceAmount")
    MatchedInvoiceListResponse toMatchedInvoice(TDxInvoiceEntity invoiceEntity);

    List<MatchedInvoiceListResponse> toMatchedInvoice(List<TDxInvoiceEntity> invoiceEntity);
}
