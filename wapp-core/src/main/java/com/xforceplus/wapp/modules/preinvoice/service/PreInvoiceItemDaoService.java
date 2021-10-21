package com.xforceplus.wapp.modules.preinvoice.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceItemDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreInvoiceItemDaoService extends ServiceImpl<TXfPreInvoiceItemDao, TXfPreInvoiceItemEntity> {

    public List<TXfPreInvoiceItemEntity> getByInvoiceId(@NonNull Long invoiceId) {
        return new LambdaQueryChainWrapper<>(getBaseMapper()).eq(TXfPreInvoiceItemEntity::getPreInvoiceId, invoiceId)
                .list();
    }

}
