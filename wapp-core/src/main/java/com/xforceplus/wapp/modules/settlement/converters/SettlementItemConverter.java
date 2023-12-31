package com.xforceplus.wapp.modules.settlement.converters;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.deduct.dto.SettmentRedListResponse;
import com.xforceplus.wapp.modules.statement.models.AgreementItem;
import com.xforceplus.wapp.modules.statement.models.ConfirmItem;
import com.xforceplus.wapp.repository.vo.SettlementRedVo;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface SettlementItemConverter {
    List<AgreementItem> map(List<TXfSettlementItemEntity> entities);

    List<ConfirmItem> mapItem(List<TXfSettlementItemEntity> items);

    List<SettmentRedListResponse> mapList(List<SettlementRedVo> items);
}
