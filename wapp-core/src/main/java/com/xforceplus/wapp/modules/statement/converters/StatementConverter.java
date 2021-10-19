package com.xforceplus.wapp.modules.statement.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.statement.models.Statement;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface StatementConverter {
    List<Statement> map(List<TXfSettlementEntity> records);
}
