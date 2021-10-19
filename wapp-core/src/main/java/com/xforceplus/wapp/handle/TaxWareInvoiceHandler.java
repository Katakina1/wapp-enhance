package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.client.TaxWareInvoiceVO;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.converters.InvoiceConverter;
import com.xforceplus.wapp.converters.InvoiceItemConverter;
import com.xforceplus.wapp.modules.invoice.service.InvoiceItemServiceImpl;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author mashaopeng@xforceplus.com
 */
@Component
@Slf4j
public class TaxWareInvoiceHandler implements IntegrationResultHandler {
    private final InvoiceServiceImpl invoiceService;
    private final InvoiceItemServiceImpl invoiceDetailsService;
    private final InvoiceConverter invoiceConverter;
    private final InvoiceItemConverter invoiceItemConverter;

    public TaxWareInvoiceHandler(InvoiceServiceImpl invoiceService, InvoiceItemServiceImpl invoiceDetailsService, InvoiceConverter invoiceConverter, InvoiceItemConverter invoiceItemConverter) {
        this.invoiceService = invoiceService;
        this.invoiceDetailsService = invoiceDetailsService;
        this.invoiceConverter = invoiceConverter;
        this.invoiceItemConverter = invoiceItemConverter;
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
