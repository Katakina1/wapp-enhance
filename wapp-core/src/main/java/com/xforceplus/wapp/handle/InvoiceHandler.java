package com.xforceplus.wapp.handle;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.converters.InvoiceConverter;
import com.xforceplus.wapp.converters.InvoiceItemConverter;
import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.modules.invoice.service.InvoiceItemServiceImpl;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.xforceplus.wapp.converters.InvoiceConverter.INVOICE_TYPE_MAP;


/**
 * @author mashaopeng@xforceplus.com
 */
@Component
@Slf4j
public class InvoiceHandler implements IntegrationResultHandler {
    private final InvoiceServiceImpl invoiceService;
    private final InvoiceItemServiceImpl invoiceDetailsService;
    private final InvoiceConverter invoiceConverter;
    private final InvoiceItemConverter invoiceItemConverter;

    public InvoiceHandler(InvoiceServiceImpl invoiceService, InvoiceItemServiceImpl invoiceDetailsService, InvoiceConverter invoiceConverter, InvoiceItemConverter invoiceItemConverter) {
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
        log.info("request name:{},payload obj:{},header:{}", this.requestName(), sealedMessage.getPayload().getObj(), JSON.toJSONString(sealedMessage.getHeader()));
        InvoiceVo vo = JsonUtil.fromJson(sealedMessage.getPayload().getObj().toString(), InvoiceVo.class);
        InvoiceVo.Invoice invoice = vo.getData();
        if (!INVOICE_TYPE_MAP.containsKey(invoice.getInvoiceType())) {
            log.info("发票类型[{}]无法转换,invoiceCode:{},invoiceNo:{}。", invoice.getInvoiceType(), invoice.getInvoiceCode(), invoice.getInvoiceNo());
            return true;
        }
        List<InvoiceVo.InvoiceItemVO> items = invoice.getItems();
        TDxRecordInvoiceEntity invoiceMap = invoiceConverter.map(invoice, CollectionUtils.isEmpty(items) ? 0 : 1);
        return new LambdaQueryChainWrapper<>(invoiceService.getBaseMapper())
                .eq(TDxRecordInvoiceEntity::getInvoiceCode, invoice.getInvoiceCode())
                .eq(TDxRecordInvoiceEntity::getInvoiceNo, invoice.getInvoiceNo()).oneOpt()
                .map(it -> {
                    invoiceMap.setId(it.getId());
                    log.warn("发票更新,invoiceCode:{},invoiceNo:{}", it.getInvoiceCode(), it.getInvoiceNo());
                    return invoiceService.updateById(invoiceMap);
                })
                .orElseGet(() -> {
                    invoiceService.save(invoiceMap);
                    if (CollectionUtils.isNotEmpty(items)) {
                        invoiceDetailsService.saveBatch(invoiceItemConverter.map(items), 2000);
                    }
                    log.warn("发票插入,invoiceCode:{},invoiceNo:{}", invoiceMap.getInvoiceCode(), invoiceMap.getInvoiceNo());
                    return true;
                });
    }
}
