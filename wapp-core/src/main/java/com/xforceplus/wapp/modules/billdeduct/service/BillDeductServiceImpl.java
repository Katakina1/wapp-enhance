package com.xforceplus.wapp.modules.billdeduct.service;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class BillDeductServiceImpl extends ServiceImpl<TXfBillDeductDao, TXfBillDeductEntity> {

    public List<TXfBillDeductEntity> listBusinessNoByIds(Collection<Long> ids) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .in(TXfBillDeductEntity::getId, ids)
                .select(TXfBillDeductEntity::getId, TXfBillDeductEntity::getBusinessNo)
                .list();
    }
}
