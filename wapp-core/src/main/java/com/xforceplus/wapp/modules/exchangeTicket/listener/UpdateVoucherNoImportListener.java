package com.xforceplus.wapp.modules.exchangeTicket.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketExportDto;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketImportDto;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class UpdateVoucherNoImportListener extends AnalysisEventListener<ExchangeTicketExportDto> {
    private int rows;
    private final List<ExchangeTicketExportDto> validInvoices = Lists.newArrayList();
    private final List<Tuple3<ExchangeTicketExportDto/*行数据*/, Integer/*行号*/, String/*异常原因*/>> invalidInvoices = Lists.newArrayList();

    public UpdateVoucherNoImportListener() {
        this.rows = 0;
    }


    @Override
    public void invoke(ExchangeTicketExportDto exchangeTicketImportDto, AnalysisContext analysisContext) {
        rows++;
        String message = checkData(exchangeTicketImportDto);
        if (StringUtils.isEmpty(message)) {
            validInvoices.add(exchangeTicketImportDto);
        } else {
            invalidInvoices.add(Tuple.of(exchangeTicketImportDto, rows + 1, message));
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public String checkData(ExchangeTicketExportDto exchangeTicketImportDto) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(exchangeTicketImportDto.getInvoiceNo()) &&
                20 != exchangeTicketImportDto.getInvoiceNo().length() &&
                StringUtils.isEmpty(exchangeTicketImportDto.getInvoiceCode())) {
            builder.append("发票代码不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getInvoiceNo())) {
            builder.append("发票号码不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getVoucherNo())) {
            builder.append("凭证号不能为空; ");
        }
        if (StringUtils.isEmpty(exchangeTicketImportDto.getExchangeInvoiceNo())) {
            builder.append("换票后发票号码不能为空; ");
        }
        if (StringUtils.isNotEmpty(exchangeTicketImportDto.getExchangeInvoiceNo()) &&
                20 != exchangeTicketImportDto.getExchangeInvoiceNo().length() &&
                StringUtils.isEmpty(exchangeTicketImportDto.getExchangeInvoiceCode())) {
            builder.append("换票后发票代码不能为空; ");
        }
        return builder.toString();
    }

}
