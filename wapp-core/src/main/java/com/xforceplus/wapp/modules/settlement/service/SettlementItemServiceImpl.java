package com.xforceplus.wapp.modules.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemTaxNoUpdatedRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SettlementItemServiceImpl extends ServiceImpl<TXfSettlementItemDao, TXfSettlementItemEntity> {

    @Autowired
    private PreinvoiceService preinvoiceService;

    public Page<TXfSettlementItemEntity> getItemsBySettlementNo(String settlementNo, int page, int size) {
        final LambdaQueryWrapper<TXfSettlementItemEntity> eq = Wrappers.lambdaQuery(TXfSettlementItemEntity.class).eq(TXfSettlementItemEntity::getSettlementNo, settlementNo);
        return page(new Page<>(page, size), eq);
    }

    public void batchUpdateItemTaxNo(SettlementItemTaxNoUpdatedRequest request) {
        if (CollectionUtils.isEmpty(request.getItems())) {
            return;
        }

        Long userId = UserUtil.getUserId();
        List<TXfSettlementItemEntity> updated = new ArrayList<>();
        request.getItems().forEach(x -> {
            TXfSettlementItemEntity updateEntity = new TXfSettlementItemEntity();
            updateEntity.setId(x.getId());
            updateEntity.setGoodsTaxNo(x.getGoodsTaxNo());
            updateEntity.setUpdateUser(userId);
            updateEntity.setUpdateTime(new Date());
            updateEntity.setItemCode(x.getItemCode());
            updateEntity.setUnitPrice(x.getUnitPrice());
            updateEntity.setQuantity(x.getQuantity());

            updated.add(updateEntity);
        });
        this.updateBatchById(updated);

        final TXfSettlementItemEntity tXfSettlementItemEntity = request.getItems().get(0);

        preinvoiceService.reFixTaxCode(tXfSettlementItemEntity.getSettlementNo());
    }

    public void updateItem(String taxCode,String sellerNo,  String goodsTaxNo) {
        if (StringUtils.isEmpty(taxCode)) {
            return;
        }
        getBaseMapper().updateItem(taxCode, sellerNo, goodsTaxNo);
    }
}
