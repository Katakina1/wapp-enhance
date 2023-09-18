package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xforceplus.wapp.enums.DeductBillMakeInvoiceStatusEnum;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Describe: 业务单开票状态同步
 * PS: 冗余状态用于业务单查询
 *
 * @Author xiezhongyong
 * @Date 2022/9/23
 */
@Slf4j
@Service
public class BillMakeInvoiceStatusService {

    @Autowired
    private DeductBatchService deductBatchService;

    /**
     * 业务单开票状态同步
     *
     * @param deductId          业务单ID
     * @param invoiceStatusEnum 开具状态
     */
    public void syncMakeInvoiceStatus(@NonNull Long deductId, @NonNull DeductBillMakeInvoiceStatusEnum invoiceStatusEnum) {

        try {
            List<Integer> currentStatusCode = DeductBillMakeInvoiceStatusEnum.otherCode(invoiceStatusEnum).stream().map(DeductBillMakeInvoiceStatusEnum::code).collect(Collectors.toList());
            log.info("业务单开票状态同步, deductId: {}, makeInvoiceStatus: {}", deductId, invoiceStatusEnum.code());
            final LambdaUpdateWrapper<TXfBillDeductEntity> update = Wrappers.lambdaUpdate(TXfBillDeductEntity.class)
                    .set(TXfBillDeductEntity::getMakeInvoiceStatus, invoiceStatusEnum.code())
                    .set(TXfBillDeductEntity::getUpdateTime, new Date())
                    .eq(TXfBillDeductEntity::getId, deductId)
                    //增加当前状态判断，视同乐观锁，防止被误改
                    .in(TXfBillDeductEntity::getMakeInvoiceStatus,currentStatusCode)
                    ;
            deductBatchService.update(update);
        } catch (Exception e) {
            log.error("业务单: {}开票状态同步异常：{}", deductId, e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
