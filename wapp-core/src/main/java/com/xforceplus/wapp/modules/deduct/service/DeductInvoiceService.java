package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.TXfInvoiceDeductTypeEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
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

    public List<TXfBillDeductInvoiceEntity> getBySettlementId(Long settlementId, TXfDeductionBusinessTypeEnum typeEnum){
        Integer relationType = typeEnum != TXfDeductionBusinessTypeEnum.CLAIM_BILL? TXfInvoiceDeductTypeEnum.SETTLEMENT.getCode():TXfInvoiceDeductTypeEnum.CLAIM.getCode();
        final LambdaQueryWrapper<TXfBillDeductInvoiceEntity> wrapper = Wrappers.lambdaQuery(TXfBillDeductInvoiceEntity.class)
                .eq(TXfBillDeductInvoiceEntity::getThridId, settlementId).eq(TXfBillDeductInvoiceEntity::getBusinessType, relationType);
        return this.list(wrapper);
    }


//    public List<TXfBillDeductInvoiceEntity> getBySettlementNo(String settlementNo, XFDeductionBusinessTypeEnum typeEnum){
//        Integer relationType = typeEnum != XFDeductionBusinessTypeEnum.CLAIM_BILL? TXfInvoiceDeductTypeEnum.SETTLEMENT.getCode():TXfInvoiceDeductTypeEnum.CLAIM.getCode();
//        final LambdaQueryWrapper<TXfBillDeductInvoiceEntity> wrapper = Wrappers.lambdaQuery(TXfBillDeductInvoiceEntity.class)
//                .eq(TXfBillDeductInvoiceEntity::getBusinessNo, settlementNo).eq(TXfBillDeductInvoiceEntity::getBusinessType, relationType);
//        return this.list(wrapper);
//    }



}
