package com.xforceplus.wapp.modules.exchangeTicket.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketExportDto;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketImportDto;
import com.xforceplus.wapp.repository.entity.TXfExchangeTicketEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface ExchangeTicketConverter {
    List<TXfExchangeTicketEntity> map(List<ExchangeTicketImportDto> list);

    List<ExchangeTicketExportDto> exportMap(List<TXfExchangeTicketEntity> list);

    default String mapBigdecimal(BigDecimal decimal) {
        return Objects.isNull(decimal) ? StringUtils.EMPTY : decimal.toPlainString();
    }
}
