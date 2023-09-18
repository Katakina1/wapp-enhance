package com.xforceplus.wapp.modules.exchangeTicket.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.repository.entity.TXfExchangeTicketEntity;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ExchangeTicketResultDto {

    Page<TXfExchangeTicketEntity> result;

    BigDecimal taxAmount;

    BigDecimal totalAmount;

    BigDecimal totalTax;


}
