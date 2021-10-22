package com.xforceplus.wapp.modules.statement.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.statement.models.ClaimConfirm;
import com.xforceplus.wapp.modules.statement.models.Settlement;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface StatementConverter {
    List<Settlement> map(List<TXfSettlementEntity> records);

    ClaimConfirm map(String businessNo, Set<TXfSettlementItemEntity> items);
}
