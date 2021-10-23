package com.xforceplus.wapp.modules.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemTaxNoUpdatedRequest;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettlementItemServiceImpl extends ServiceImpl<TXfSettlementItemDao, TXfSettlementItemEntity> {

    public Page<TXfSettlementItemEntity> getItemsBySettlementNo(String settlementNo, int page, int size) {
        final LambdaQueryWrapper<TXfSettlementItemEntity> eq = Wrappers.lambdaQuery(TXfSettlementItemEntity.class).eq(TXfSettlementItemEntity::getSettlementNo, settlementNo);
        return page(new Page<>(page, size), eq);
    }

    public void batchUpdateItemTaxNo(SettlementItemTaxNoUpdatedRequest request){
        if (CollectionUtils.isEmpty(request.getItem())){
            return;
        }

        List<TXfSettlementItemEntity> updated=new ArrayList<>();
    }
}
