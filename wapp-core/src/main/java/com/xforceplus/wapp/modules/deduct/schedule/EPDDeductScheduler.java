package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class EPDDeductScheduler {
    @Autowired
    private DeductService deductService;

    @Autowired
    private TXfBillDeductExtDao tXfBillDeductExtDao;


    @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){
       // deductService.receiveDone(XFDeductionBusinessTypeEnum.EPD_BILL);
    }
    /**
     *
     * @param deductionEnum
     * @param tXfBillDeductStatusEnum
     * @param targetStatus
     * @param manualChoice 页面手动选择的 单子明细
     * @return
     */
    public boolean mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum deductionEnum, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, TXfBillDeductStatusEnum targetStatus, List<TXfBillDeductEntity> manualChoice) {

        int expireScale = -5;
        /**
         * 获取超期时间 判断超过此日期的正数单据
         */
        Date referenceDate = DateUtils.addDate(DateUtils.getNow(), expireScale);
        //查询大于expireDate， 同一购销对，同一税率 总不含税金额为正的单据
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitablePositiveBill(referenceDate, deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode());
        if (CollectionUtils.isNotEmpty(manualChoice)) {
            tXfBillDeductEntities.addAll(manualChoice);
        }
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
            /**
             * 查询 同一购销对，同一税率 下所有的负数单据
             */
            TXfBillDeductEntity negativeBill = tXfBillDeductExtDao.querySpecialNegativeBill(tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(), deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode());
            if (negativeBill.getAmountWithoutTax().add(tmp.getAmountWithoutTax()).compareTo(BigDecimal.ZERO) > 0) {
                try {
                    batchUpdateMergeBill(deductionEnum,tmp, negativeBill, tXfBillDeductStatusEnum, referenceDate, targetStatus);
                } catch (Exception e) {
                    log.error("{}单合并异常 购方:{}，购方:{}，税率:{}", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate());
                }
            } else {
                log.warn("{}单合并失败：合并收金额不为负数 购方:{}，购方:{}，税率:{}，手动勾选 {} ", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(),manualChoice);
            }
        }
        return false;
    }


    /**
     * 批量进行更新操作 保证单进程操作
     * @param tmp
     * @param tXfBillDeductStatusEnum
     * @param referenceDate
     * @param targetSatus
     * @return
     */
    @Transactional
    public boolean batchUpdateMergeBill(XFDeductionBusinessTypeEnum deductionEnum,TXfBillDeductEntity tmp,TXfBillDeductEntity negativeBill, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfBillDeductStatusEnum targetSatus) {
        String purchaserNo = tmp.getPurchaserNo();
        String sellerNo = tmp.getSellerNo();
        BigDecimal taxRate = tmp.getTaxRate();
        Integer type = deductionEnum.getValue();
        Integer status = tXfBillDeductStatusEnum.getCode();
        Integer targetStatus = targetSatus.getCode();
        List<TXfBillDeductEntity> tXfBillDeductEntities = new ArrayList<>();
        tXfBillDeductEntities.add(tmp);
        tXfBillDeductEntities.add(negativeBill);
        TXfSettlementEntity tXfSettlementEntity = deductService.trans2Settlement(tXfBillDeductEntities,deductionEnum);
        tXfBillDeductExtDao.updateMergeNegativeBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, type, status, targetStatus);
        tXfBillDeductExtDao.updateMergePositiveBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, referenceDate, type, status, targetStatus);
        /**
         * 更新完成后，进行此结算下的数据校验，校验通过，提交，失败，回滚：表示有的新的单子进来，不满足条件了,回滚操作
         */
        tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo());
        if (tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0) {
            /**
             * 说明在更新过程钟，新的单据被更新到,而且更新到的负数大于正数，合并失败
             */
            throw new RuntimeException("");
        }
        return true;
    }





}
