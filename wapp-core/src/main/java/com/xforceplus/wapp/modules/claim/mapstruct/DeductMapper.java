package com.xforceplus.wapp.modules.claim.mapstruct;

import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.vo.BillDeductLeftSettlementVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeductMapper {

    List<DeductListResponse> toResponse(List<TXfBillDeductEntity> entities);

    @Mapping(target = "billNo",source = "businessNo")
    @Mapping(target = "lock",source = "lockFlag")
    DeductListResponse toResponse(BillDeductLeftSettlementVo entity);
}
