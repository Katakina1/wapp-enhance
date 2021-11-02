package com.xforceplus.wapp.modules.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemTaxNoUpdatedRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SettlementItemServiceImpl extends ServiceImpl<TXfSettlementItemDao, TXfSettlementItemEntity> {

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

//            BigDecimal taxRate = updateEntity.getTaxRate().compareTo(BigDecimal.ONE) > 0 ? updateEntity.getTaxRate().movePointLeft(2) : updateEntity.getTaxRate();
//            updateEntity.setTaxAmount(updateEntity.getAmountWithoutTax().multiply(taxRate).setScale(2, RoundingMode.HALF_UP));
//            updateEntity.setAmountWithTax(updateEntity.getAmountWithoutTax().add(updateEntity.getTaxAmount()));
            updated.add(updateEntity);
        });
        this.updateBatchById(updated);
    }
}
