package com.xforceplus.wapp.handle;

import com.google.common.collect.Lists;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.client.TaxWareInvoice;
import com.xforceplus.wapp.client.TaxWareInvoiceDetail;
import com.xforceplus.wapp.client.TaxWareInvoiceVO;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.converters.InvoiceConverter;
import com.xforceplus.wapp.converters.InvoiceDetailsConverter;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import com.xforceplus.wapp.service.RecordInvoiceDetailsServiceImpl;
import com.xforceplus.wapp.service.RecordInvoiceServiceImpl;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author mashaopeng@xforceplus.com
 */
@Component
@Slf4j
public class TaxWareInvoiceHandler implements IntegrationResultHandler {
    private final TaxWareService taxWareService;
    private final RecordInvoiceServiceImpl invoiceService;
    private final RecordInvoiceDetailsServiceImpl invoiceDetailsService;
    private final InvoiceConverter invoiceConverter;
    private final InvoiceDetailsConverter invoiceDetailsConverter;

    public TaxWareInvoiceHandler(TaxWareService taxWareService, RecordInvoiceServiceImpl invoiceService, RecordInvoiceDetailsServiceImpl invoiceDetailsService, InvoiceConverter invoiceConverter, InvoiceDetailsConverter invoiceDetailsConverter) {
        this.taxWareService = taxWareService;
        this.invoiceService = invoiceService;
        this.invoiceDetailsService = invoiceDetailsService;
        this.invoiceConverter = invoiceConverter;
        this.invoiceDetailsConverter = invoiceDetailsConverter;
    }

    @Override
    public String requestName() {
        return "purchaserInvoiceSync";
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean handle(SealedMessage sealedMessage) {
        log.info("payload obj:{},header:{}", sealedMessage.getPayload().getObj(), sealedMessage.getHeader());
        List<TaxWareInvoiceVO> vo = JsonUtil.fromJsonList(sealedMessage.getPayload().getObj().toString(), TaxWareInvoiceVO.class);
//        for (List<TaxWareInvoiceVO> it : Lists.partition(vo, 10)) {
//            val collect = it.parallelStream()
//                    .map(taxWareService::getInvoiceAllElements).filter(Optional::isPresent)
//                    .map(Optional::get).collect(Collectors.toList());
//            List<TaxWareInvoice> invoiceList = collect.parallelStream().map(Tuple2::_1).collect(Collectors.toList());
//            List<TaxWareInvoiceDetail> detailList = collect.parallelStream().map(Tuple2::_2).flatMap(List::stream).collect(Collectors.toList());
//        }
//        invoiceService.saveBatch(invoiceConverter.map(invoiceList));
//        invoiceDetailsService.saveBatch(invoiceDetailsConverter.map(detailList), 2000);
        return true;
    }
}
