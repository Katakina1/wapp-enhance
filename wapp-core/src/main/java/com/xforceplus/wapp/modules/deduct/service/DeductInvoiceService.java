package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-23 13:55
 **/
@Service
public class DeductInvoiceService extends ServiceImpl<TXfBillDeductInvoiceDao, TXfBillDeductInvoiceEntity> {

    public List<TXfBillDeductInvoiceEntity> getBySettlementId(Long settlementId, XFDeductionBusinessTypeEnum typeEnum){
        final LambdaQueryWrapper<TXfBillDeductInvoiceEntity> wrapper = Wrappers.lambdaQuery(TXfBillDeductInvoiceEntity.class)
                .eq(TXfBillDeductInvoiceEntity::getThridId, settlementId).eq(TXfBillDeductInvoiceEntity::getBusinessType, typeEnum.getValue());
        return this.list(wrapper);
    }
}
