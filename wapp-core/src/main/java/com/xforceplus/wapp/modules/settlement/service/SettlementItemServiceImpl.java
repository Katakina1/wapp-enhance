package com.xforceplus.wapp.modules.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemTaxNoUpdatedRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SettlementItemServiceImpl extends ServiceImpl<TXfSettlementItemDao, TXfSettlementItemEntity> {

    @Autowired
    private TXfSettlementDao tXfSettlementDao;

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

        final TXfSettlementItemEntity tXfSettlementItemEntity = request.getItems().get(0);
        final LambdaQueryWrapper<TXfSettlementItemEntity> wrapper = Wrappers.lambdaQuery(TXfSettlementItemEntity.class).eq(TXfSettlementItemEntity::getSettlementNo, tXfSettlementItemEntity.getSettlementNo()).isNull(TXfSettlementItemEntity::getGoodsTaxNo);
        final int count = this.count(wrapper);
        if (count == 0) {
            final LambdaUpdateWrapper<TXfSettlementEntity> updateWrapper = Wrappers.lambdaUpdate(TXfSettlementEntity.class).eq(TXfSettlementEntity::getSettlementNo, tXfSettlementItemEntity.getSettlementNo());
            TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CONFIRM.getCode());
            tXfSettlementDao.update(tXfSettlementEntity,updateWrapper);
        }
    }
}
