package com.xforceplus.wapp.modules.deduct.mapstruct;

import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendResponse;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.mapstruct.Mapper;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-02 10:38
 **/
@Mapper(componentModel = "spring")
public interface InvoiceRecommendMapper {
    InvoiceRecommendResponse toDto(TDxRecordInvoiceEntity entity);
}
