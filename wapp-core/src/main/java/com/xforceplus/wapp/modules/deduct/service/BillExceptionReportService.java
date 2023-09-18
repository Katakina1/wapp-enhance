package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Describe: 同步列外报告
 * PS: 业务单冗余业务报告方便查询
 * @Author xiezhongyong
 * @Date 2022-09-14
 */
@Slf4j
@Service
public class BillExceptionReportService {

    @Autowired
    private DeductBatchService deductBatchService;

    /**
     * 同步列外报告信息
     * @param exceptionReport
     */
    public void syncExceptionReport(TXfExceptionReportEntity exceptionReport){
        try {
            log.info("新增列外报告同步到业务单: {}", JsonUtil.toJsonStr(exceptionReport));
            final LambdaUpdateWrapper<TXfBillDeductEntity> update = Wrappers.lambdaUpdate(TXfBillDeductEntity.class)
                    .set(TXfBillDeductEntity::getExceptionStatus, 1)
                    .set(TXfBillDeductEntity::getExceptionCode, exceptionReport.getCode())
                    .set(TXfBillDeductEntity::getExceptionDescription, exceptionReport.getDescription())
                    .set(TXfBillDeductEntity::getUpdateTime,new Date())
                    .eq(TXfBillDeductEntity::getId,exceptionReport.getBillId());

            deductBatchService.update(update);
        }catch (Exception e) {
            log.error("同步列外报表信息异常：{}", e);
        }
    }

    /**
     * 同步列外报告信息处理状态
     * @param billId
     * @param status
     */
    public void syncExceptionStatus(@NonNull Long billId, @NonNull Integer status){
        try {
            log.info("列外报告处理同步到业务单: {}, status: {}", billId, status);
            final LambdaUpdateWrapper<TXfBillDeductEntity> update = Wrappers.lambdaUpdate(TXfBillDeductEntity.class)
                    .set(TXfBillDeductEntity::getExceptionStatus, status)
                    .set(TXfBillDeductEntity::getUpdateTime,new Date())
                    .eq(TXfBillDeductEntity::getId, billId);

            deductBatchService.update(update);
        }catch (Exception e) {
            log.error("同步列外报表处理状态异常：{}", e);
        }
    }
}
