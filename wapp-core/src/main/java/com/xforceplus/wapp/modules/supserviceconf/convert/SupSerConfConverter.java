package com.xforceplus.wapp.modules.supserviceconf.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketExportDto;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketImportDto;
import com.xforceplus.wapp.modules.supserviceconf.dto.SuperServiceConfExportDto;
import com.xforceplus.wapp.modules.supserviceconf.dto.SuperServiceConfImportDto;
import com.xforceplus.wapp.repository.entity.TAcUserEntity;
import com.xforceplus.wapp.repository.entity.TXfExchangeTicketEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface SupSerConfConverter {
    List<TAcUserEntity> map(List<SuperServiceConfImportDto> list);

    List<SuperServiceConfExportDto> exportMap(List<TAcUserEntity> list);

    default String mapBigdecimal(BigDecimal decimal) {
        return Objects.isNull(decimal) ? StringUtils.EMPTY : decimal.toPlainString();
    }
}
