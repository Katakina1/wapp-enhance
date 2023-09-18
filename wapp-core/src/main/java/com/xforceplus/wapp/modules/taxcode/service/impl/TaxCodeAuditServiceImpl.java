package com.xforceplus.wapp.modules.taxcode.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TaxCodeAuditDao;
import com.xforceplus.wapp.repository.entity.TaxCodeAuditEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
public class TaxCodeAuditServiceImpl extends ServiceImpl<TaxCodeAuditDao, TaxCodeAuditEntity> {
    public Page<TaxCodeAuditEntity> query(String itemNo, String itemName, String sellerName, String sellerNo, Integer auditStatus, Integer sendStatus, String begin, String end, String auditBegin, String auditEnd, Integer current, Integer size) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(StringUtils.isNoneBlank(itemNo), TaxCodeAuditEntity::getItemNo, itemNo)
                .eq(StringUtils.isNoneBlank(sellerName), TaxCodeAuditEntity::getSellerName, sellerName)
                .eq(StringUtils.isNoneBlank(sellerNo), TaxCodeAuditEntity::getSellerNo, sellerNo)
                .like(StringUtils.isNoneBlank(itemName), TaxCodeAuditEntity::getItemName, itemName)
                .eq(auditStatus != null, TaxCodeAuditEntity::getAuditStatus, auditStatus)
                .eq(sendStatus != null, TaxCodeAuditEntity::getSendStatus, sendStatus)
                .ge(StringUtils.isNoneBlank(begin), TaxCodeAuditEntity::getCreateTime, begin)
                .le(StringUtils.isNoneBlank(end), TaxCodeAuditEntity::getCreateTime, end + " 23:59:59")
                .ge(StringUtils.isNoneBlank(auditBegin), TaxCodeAuditEntity::getAuditTime, auditBegin)
                .le(StringUtils.isNoneBlank(auditEnd), TaxCodeAuditEntity::getAuditTime, auditEnd + " 23:59:59")
                .orderByDesc(TaxCodeAuditEntity::getCreateTime)
                .page(new Page<>(current, size));
    }
}
