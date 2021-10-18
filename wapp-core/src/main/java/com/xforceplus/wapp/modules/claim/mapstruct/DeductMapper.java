package com.xforceplus.wapp.modules.claim.mapstruct;

import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeductMapper {

    List<DeductListResponse> toResponse(List<TXfBillDeductEntity> entities);

    DeductListResponse toResponse(TXfBillDeductEntity entity);
}
