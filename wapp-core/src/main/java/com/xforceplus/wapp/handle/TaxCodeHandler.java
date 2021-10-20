package com.xforceplus.wapp.handle;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.handle.vo.TaxCodeVO;
import com.xforceplus.wapp.modules.taxcode.converters.TaxCodeConverter;
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
    private final TaxCodeConverter taxCodeConverter;

    public TaxCodeHandler(TaxCodeServiceImpl taxCodeService, TaxCodeConverter taxCodeConverter) {
        this.taxCodeService = taxCodeService;
        this.taxCodeConverter = taxCodeConverter;
    }

    @Override
    public String requestName() {
        return "taxcodeCooperation";
    }


    @Override
    public boolean handle(SealedMessage sealedMessage) {
        log.info("request name:{},payload obj:{},header:{}", this.requestName(), sealedMessage.getPayload().getObj(), sealedMessage.getHeader());
        TaxCodeVO vo = JsonUtil.fromJson(sealedMessage.getPayload().getObj().toString(), TaxCodeVO.class);
        TaxCodeEntity taxCode = taxCodeConverter.map(vo);
        return new LambdaQueryChainWrapper<>(taxCodeService.getBaseMapper())
                .eq(TaxCodeEntity::getItemNo, taxCode.getItemNo()).oneOpt()
                .map(it -> {
                    taxCode.setId(it.getId());
                    return taxCodeService.updateById(taxCode);
                }).orElseGet(() -> taxCodeService.save(taxCode));
    }
}
