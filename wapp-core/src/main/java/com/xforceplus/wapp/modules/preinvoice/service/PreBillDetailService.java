package com.xforceplus.wapp.modules.preinvoice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.repository.dao.TXfPreBillDetailDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.TXfPreBillDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : hujintao
 * @version : 1.0
 * @description :
 * @date : 2022/09/05 18:14
 **/
@Service
@Slf4j
public class PreBillDetailService extends ServiceImpl<TXfPreBillDetailDao, TXfPreBillDetailEntity> {

    @Autowired
    private TXfPreInvoiceDao txfPreInvoiceDao;
    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;

    /**
     * 根据预制发票id查询关联关系
     * @param preInvoiceId 预制发票id
     */
    public List<TXfPreBillDetailEntity> getDetails(Long preInvoiceId) {
        if (!CommonUtil.isEdit(preInvoiceId)) {
            return Lists.newArrayList();
        }
        QueryWrapper<TXfPreBillDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfPreBillDetailEntity.PRE_INVOICE_ID, preInvoiceId);
        return list(queryWrapper);
    }

    /**
     * 根据预制发票id集合查询关联关系
     * @param preInvoiceIdList 预制发票id集合
     */
    public List<TXfPreBillDetailEntity> getDetails(List<Long> preInvoiceIdList) {
        if (CollectionUtils.isEmpty(preInvoiceIdList)) {
            return Lists.newArrayList();
        }
        QueryWrapper<TXfPreBillDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(TXfPreBillDetailEntity.PRE_INVOICE_ID, preInvoiceIdList);
        return list(queryWrapper);
    }

    /**
     * 根据结算单查询预制发票关联关系
     * @param settlementIdList 结算单id集合
     */
    public List<TXfPreBillDetailEntity> getDetailsBySettlement(List<Long> settlementIdList) {
        if (CollectionUtils.isEmpty(settlementIdList)) {
            return Lists.newArrayList();
        }

        // 查询结算单下预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceQueryWrapper = new QueryWrapper<>();
        preInvoiceQueryWrapper.in(TXfPreInvoiceEntity.SETTLEMENT_ID, settlementIdList);
        preInvoiceQueryWrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
        List<TXfPreInvoiceEntity> preInvoiceEntityList = txfPreInvoiceDao.selectList(preInvoiceQueryWrapper);
        if (CollectionUtils.isEmpty(preInvoiceEntityList)) {
            return Lists.newArrayList();
        }

        // 剔除蓝冲的预制发票  TODO 是否需要剔除，不剔除是否会影响状态判断?
        preInvoiceEntityList.removeIf(item -> StringUtils.isNotBlank(item.getInvoiceNo())
                && StringUtils.isNotBlank(item.getInvoiceCode())
                && blueInvoiceRelationService.existsByRedInvoice(item.getInvoiceNo(), item.getInvoiceCode()));
        if (CollectionUtils.isEmpty(preInvoiceEntityList)) {
            return Lists.newArrayList();
        }

        // 查询关联关系
        return getDetails(preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList()));
    }
}
