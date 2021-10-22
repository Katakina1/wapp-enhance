package com.xforceplus.wapp.modules.billdeduct.service;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemRefDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class BillDeductItemServiceImpl extends ServiceImpl<TXfBillDeductItemDao, TXfBillDeductItemEntity> {
    private final TXfBillDeductItemRefDao billDeductItemRefDao;

    public BillDeductItemServiceImpl(TXfBillDeductItemRefDao billDeductItemRefDao) {
        this.billDeductItemRefDao = billDeductItemRefDao;
    }

    public List<TXfBillDeductItemRefEntity> listByRefItemIds(Collection<Long> refItemIds) {
        return new LambdaQueryChainWrapper<>(billDeductItemRefDao)
                .in(TXfBillDeductItemRefEntity::getDeductItemId, refItemIds)
                .select(TXfBillDeductItemRefEntity::getDeductItemId, TXfBillDeductItemRefEntity::getDeductId)
                .list();
    }

    public List<TXfBillDeductItemEntity> listByDeductId(Long billDeductId) {
        Set<Long> itemIds = new LambdaQueryChainWrapper<>(billDeductItemRefDao)
                .eq(TXfBillDeductItemRefEntity::getDeductId, billDeductId)
                .list().stream().map(TXfBillDeductItemRefEntity::getDeductItemId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(itemIds)) {
            return Lists.newArrayList();
        }
        return listByIds(itemIds);
    }

    public Map<Long, List<TXfBillDeductItemEntity>> listByDeductIds(List<Long> billDeductId) {
        if (CollectionUtils.isEmpty(billDeductId)) {
            return Maps.newHashMap();
        }
        val itemRef = new LambdaQueryChainWrapper<>(billDeductItemRefDao)
                .in(TXfBillDeductItemRefEntity::getDeductId, billDeductId)
                .select(TXfBillDeductItemRefEntity::getDeductId, TXfBillDeductItemRefEntity::getDeductItemId)
                .list();
        val map = Maps.<Long, List<TXfBillDeductItemEntity>>newHashMap();
        if (itemRef.isEmpty()) {
            return map;
        }
        val idAndItemIdMap = itemRef.stream()
                .collect(Collectors.groupingBy(TXfBillDeductItemRefEntity::getDeductId,
                        Collectors.mapping(TXfBillDeductItemRefEntity::getDeductItemId, Collectors.toList())));
        val itemIds = itemRef.stream().map(TXfBillDeductItemRefEntity::getDeductItemId).collect(Collectors.toSet());
        val item = listByIds(itemIds).stream()
                .collect(Collectors.groupingBy(TXfBillDeductItemEntity::getId));
        billDeductId.forEach(it -> {
            List<Long> itemIdList = idAndItemIdMap.get(it);
            if (CollectionUtils.isNotEmpty(itemIdList)) {
                map.put(it, itemIdList.stream().map(item::get).flatMap(List::stream).collect(Collectors.toList()));
            }
        });
        return map;
    }
}
