package com.xforceplus.wapp.modules.deduct.mapstruct;

import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendResponse;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-02 10:38
 **/
@Mapper(componentModel = "spring")
public interface InvoiceRecommendMapper {

    @Mapping(target = "invoiceDate",expression = "java(com.xforceplus.wapp.common.utils.DateUtils.format(entity.getInvoiceDate()))")
    InvoiceRecommendResponse toDto(TDxRecordInvoiceEntity entity);
}
