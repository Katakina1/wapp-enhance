package com.xforceplus.wapp.modules.deduct.mapstruct;

import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
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

    @Mapping(target = "matchedAmount",source = "deductedAmount")
    @Mapping(target = "id",source = "invoiceId")
    @Mapping(target = "invoiceDate",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.format(entity.getInvoiceDate()))")
    MatchedInvoiceListResponse toMatchInvoice(BlueInvoiceService.MatchRes entity);


    List<MatchedInvoiceListResponse> toMatchInvoice(List<BlueInvoiceService.MatchRes> matchedInvoices);
}
