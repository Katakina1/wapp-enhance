package com.xforceplus.wapp.modules.exchangeTicket.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketImportDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class ExchangeTicketImportListener extends AnalysisEventListener<ExchangeTicketImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<ExchangeTicketImportDto> validInvoices = Lists.newArrayList();
    private final List<ExchangeTicketImportDto> invalidInvoices = Lists.newArrayList();

    public ExchangeTicketImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }


    @Override
    public void invoke(ExchangeTicketImportDto exchangeTicketImportDto, AnalysisContext analysisContext) {
        rows++;
        String message = checkData(exchangeTicketImportDto);
        if (StringUtils.isEmpty(message)) {
            validInvoices.add(exchangeTicketImportDto);
        } else {
            exchangeTicketImportDto.setErrorMsg(message);
            invalidInvoices.add(exchangeTicketImportDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public String checkData(ExchangeTicketImportDto exchangeTicketImportDto) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(exchangeTicketImportDto.getJvCode())) {
            builder.append("JVCODE不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getAmountWithoutTax())) {
            builder.append("不含税金额不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getExchangeReason())) {
            builder.append("换票原因不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getFlowType())) {
            builder.append("换票类型不能为空; ");
        }
        if (StringUtils.isNotEmpty(exchangeTicketImportDto.getInvoiceNo()) &&
                20 != exchangeTicketImportDto.getInvoiceNo().length() &&
                StringUtils.isEmpty(exchangeTicketImportDto.getInvoiceCode())) {
            builder.append("发票代码不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getInvoiceNo())) {
            builder.append("发票号码不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getPaperDate())) {
            builder.append("开票日期不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getTaxRate())) {
            builder.append("税率不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getVenderId())) {
            builder.append("供应商6D不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getVenderName())) {
            builder.append("供应商名称不能为空; ");
        }

        return builder.toString();
    }

}
