package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author mashaopeng@xforceplus.com
 */
@Component
@Slf4j
public class TaxCodeHandler implements IntegrationResultHandler {
    private final TaxCodeServiceImpl taxCodeService;

    public TaxCodeHandler(TaxCodeServiceImpl taxCodeService) {
        this.taxCodeService = taxCodeService;
    }

    @Override
    public String requestName() {
        //TODO 队列名
        return "tax_code";
    }


    @Override
    public boolean handle(SealedMessage sealedMessage) {
        log.info("payload obj:{},header:{}",sealedMessage.getPayload().getObj(),sealedMessage.getHeader());
        TaxCodeEntity taxCode = JsonUtil.fromJson(sealedMessage.getPayload().getObj().toString(), TaxCodeEntity.class);
        return taxCodeService.save(taxCode);
    }
}
