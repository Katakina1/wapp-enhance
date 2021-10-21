package com.xforceplus.wapp.modules.billdeduct.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.statement.models.Claim;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface BillDeductConverter {

    Claim map(TXfBillDeductEntity records, List<TXfBillDeductItemEntity> items);

    List<Claim> map(List<TXfBillDeductEntity> records);
}
