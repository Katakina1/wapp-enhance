package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.converters.TXfInvoiceItemEntityConvertor;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.invoice.service.InvoiceItemServiceImpl;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceItemEntity;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 类描述：
 *
 * @ClassName BuleInvoiceService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/16 18:03
 */
@Slf4j
@Service
public class BlueInvoiceService {

    @Autowired
    private InvoiceServiceImpl invoiceService;
    @Autowired
    private InvoiceItemServiceImpl invoiceItemService;
    @Autowired
    private RecordInvoiceService recordInvoiceService;

    public List<MatchRes> matchInvoiceInfo(BigDecimal amount, XFDeductionBusinessTypeEnum deductionEnum, String settlementNo) {
        switch (deductionEnum) {
            case AGREEMENT_BILL:
                return obtainAgreementInvoices(amount, settlementNo);
            case CLAIM_BILL:
                return obtainClaimInvoices(amount, settlementNo);
            case EPD_BILL:
                return obtainEpdInvoices(amount, settlementNo);
            default:
                log.error("未识别的单据类型{}", deductionEnum);
                return Collections.emptyList();
        }
    }

    private List<MatchRes> obtainAgreementInvoices(BigDecimal amount, String settlementNo) {
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        TXfInvoiceEntity tXfInvoiceEntity;
        do {
            tXfInvoiceEntity = invoiceService.getOne(
                    new QueryWrapper<TXfInvoiceEntity>()
                            .lambda()
                            // 排除可用金额=0的发票
                            .gt(TXfInvoiceEntity::getRemainingAmount, BigDecimal.ZERO)
                            // 排除非专票（只要增值税专票）
                            .eq(TXfInvoiceEntity::getTaxCategory, "01")
                            // 排除状态异常的发票（只要正常的发票）
                            .eq(TXfInvoiceEntity::getStatus, "1")
                            .orderByAsc(TXfInvoiceEntity::getPaperDrewDate)
            );
            if (Objects.nonNull(tXfInvoiceEntity)) {
                TDxRecordInvoiceEntity tDxRecordInvoiceEntity = recordInvoiceService.getOne(new QueryWrapper<TDxRecordInvoiceEntity>()
                        .lambda()
                        .eq(TDxRecordInvoiceEntity::getInvoiceNo, tXfInvoiceEntity.getInvoiceNo())
                        .eq(TDxRecordInvoiceEntity::getInvoiceCode, tXfInvoiceEntity.getInvoiceCode())
                );
                if (Objects.nonNull(tDxRecordInvoiceEntity)
                        // 排除蓝冲用途的发票（正常的发票）
                        && "0".equals(tDxRecordInvoiceEntity.getInvoiceStatus())
                        // 排除未完成付款的蓝票(已付款) TODO 待确认是否需要移除此判断，会导致results的获取没有意义
                        && "1".equals(tDxRecordInvoiceEntity.getBpmsPayStatus())) {
                    BigDecimal lastRemainingAmount = tXfInvoiceEntity.getRemainingAmount();
                    if (leftAmount.get().compareTo(lastRemainingAmount) >= 0) {
                        // 如果蓝票的剩余可用金额不够抵扣的
                        TXfInvoiceEntity newTxfInvoiceEntity = new TXfInvoiceEntity();
                        newTxfInvoiceEntity.setRemainingAmount(BigDecimal.ZERO);
                        if (invoiceService.update(newTxfInvoiceEntity,
                                new QueryWrapper<TXfInvoiceEntity>()
                                        .lambda()
                                        .eq(TXfInvoiceEntity::getId, tXfInvoiceEntity.getId())
                                        .eq(TXfInvoiceEntity::getRemainingAmount, lastRemainingAmount))) {
                            leftAmount.updateAndGet(v1 -> v1.subtract(lastRemainingAmount));
                            List<TXfInvoiceItemEntity> items = obtainAvailableItems(tXfInvoiceEntity.getId(), tXfInvoiceEntity.getAmountWithoutTax(), lastRemainingAmount, lastRemainingAmount);
                            list.add(MatchRes.builder()
                                    .invoiceId(tXfInvoiceEntity.getId())
                                    .invoiceNo(tXfInvoiceEntity.getInvoiceNo())
                                    .invoiceCode(tXfInvoiceEntity.getInvoiceCode())
                                    .deductedAmount(lastRemainingAmount)
                                    .invoiceItems(
                                            items
                                                    .stream()
                                                    .map(TXfInvoiceItemEntityConvertor.INSTANCE::toSettlementItem)
                                                    .collect(Collectors.toList()))
                                    .build());
                        } else {
                            log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 更新后剩余可用金额={}",
                                    tXfInvoiceEntity.getId(), lastRemainingAmount, newTxfInvoiceEntity.getRemainingAmount());
                        }
                    } else {
                        // 如果蓝票的剩余可用金额抵扣后仍有剩余
                        BigDecimal deductedAmount = leftAmount.get();
                        TXfInvoiceEntity newTxfInvoiceEntity = new TXfInvoiceEntity();
                        newTxfInvoiceEntity.setRemainingAmount(lastRemainingAmount.subtract(deductedAmount));
                        if (invoiceService.update(newTxfInvoiceEntity,
                                new QueryWrapper<TXfInvoiceEntity>()
                                        .lambda()
                                        .eq(TXfInvoiceEntity::getId, tXfInvoiceEntity.getId())
                                        .eq(TXfInvoiceEntity::getRemainingAmount, lastRemainingAmount))) {
                            leftAmount.set(BigDecimal.ZERO);
                            List<TXfInvoiceItemEntity> items = obtainAvailableItems(tXfInvoiceEntity.getId(), tXfInvoiceEntity.getAmountWithoutTax(), lastRemainingAmount, deductedAmount);
                            list.add(MatchRes.builder()
                                    .invoiceId(tXfInvoiceEntity.getId())
                                    .invoiceNo(tXfInvoiceEntity.getInvoiceNo())
                                    .invoiceCode(tXfInvoiceEntity.getInvoiceCode())
                                    .deductedAmount(deductedAmount)
                                    .invoiceItems(
                                            items
                                                    .stream()
                                                    .map(TXfInvoiceItemEntityConvertor.INSTANCE::toSettlementItem)
                                                    .collect(Collectors.toList()))
                                    .build());
                        } else {
                            log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 更新后剩余可用金额={}",
                                    tXfInvoiceEntity.getId(), lastRemainingAmount, newTxfInvoiceEntity.getRemainingAmount());
                        }
                    }
                }
            }
        } while (Objects.nonNull(tXfInvoiceEntity) && BigDecimal.ZERO.compareTo(leftAmount.get()) < 0);
        if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
            throw new NoSuchInvoiceException();
        }
        return list;
    }

    private List<MatchRes> obtainClaimInvoices(BigDecimal amount, String settlementNo) {
        // TODO by kenny
        return null;
    }

    private List<MatchRes> obtainEpdInvoices(BigDecimal amount, String settlementNo) {
        return obtainAgreementInvoices(amount, settlementNo);
    }

    /**
     * 根据之前的剩余金额，以及本次需要抵扣的可用金额，按照明细顺序找到合适的明细行返回
     *
     * @param invoiceId
     * @param totalAmountWithoutTax
     * @param lastRemainingAmount
     * @param deductedAmount
     * @return
     */
    private List<TXfInvoiceItemEntity> obtainAvailableItems(Long invoiceId, BigDecimal totalAmountWithoutTax, BigDecimal lastRemainingAmount, BigDecimal deductedAmount) {
        List<TXfInvoiceItemEntity> list = new ArrayList<>();
        // 之前抵扣的金额
        BigDecimal lastDeductedAmount = totalAmountWithoutTax.subtract(lastRemainingAmount);
        // 总共抵扣金额
        BigDecimal totalDeductedAmount = lastDeductedAmount.add(deductedAmount);
        List<TXfInvoiceItemEntity> items = invoiceItemService.list(
                new QueryWrapper<TXfInvoiceItemEntity>()
                        .lambda()
                        .eq(TXfInvoiceItemEntity::getInvoiceId, invoiceId.toString())
                        .orderByAsc(TXfInvoiceItemEntity::getId)
        );
        BigDecimal accumulatedAmount = BigDecimal.ZERO;
        for (TXfInvoiceItemEntity item : items) {
            accumulatedAmount = accumulatedAmount.add(item.getAmountWithoutTax());
            if (accumulatedAmount.compareTo(lastDeductedAmount) > 0) {
                list.add(item);
            }
            if (accumulatedAmount.compareTo(totalDeductedAmount) >= 0) {
                return list;
            }
        }
        return list;
    }

    /**
     * 撤回抵扣的发票，将抵扣金额返还到原有发票上
     *
     * @param list
     * @return
     */
    public boolean withdrawInvoices(List<MatchRes> list) {
        List<TXfInvoiceEntity> invoices = list
                .stream()
                .map(
                        v -> {
                            TXfInvoiceEntity tXfInvoiceEntity = new TXfInvoiceEntity();
                            tXfInvoiceEntity.setId(v.getInvoiceId());
                            tXfInvoiceEntity.setRemainingAmount(v.getDeductedAmount());
                            return tXfInvoiceEntity;
                        }
                )
                .collect(Collectors.toList());
        // TODO by kenny
        // return invoiceService.update().updateBatchById(invoices);
        return false;
    }

    @Data
    @Builder
    static class MatchRes {
        String invoiceNo;
        String invoiceCode;
        /**
         * 底账表中的发票主键，用于出现异常时根据id快速恢复数据
         */
        Long invoiceId;
        /**
         * 本次从剩余可用金额抵扣的额度，在最后一张发票时，很可能和remainingAmount不相同，用于出现异常时将抵扣金额返还到原有发票上
         */
        BigDecimal deductedAmount;
        List<InvoiceItem> invoiceItems;
    }

    @Data
    public static class InvoiceItem {
        private String itemNo;
        /**
         * 发票代码
         */
        private String invoiceCode;

        /**
         * 发票号码
         */
        private String invoiceNo;

        /**
         * 明细序号
         */
        private String detailNo;

        /**
         * 货物或应税劳务名称
         */
        private String goodsName;

        /**
         * 规格型号
         */
        private String model;

        /**
         * 单位
         */
        private String unit;

        /**
         * 数量
         */
        private String num;

        /**
         * 单价
         */
        private String unitPrice;

        /**
         * 金额
         */
        private String detailAmount;

        /**
         * 税率
         */
        private String taxRate;

        /**
         * 税额
         */
        private String taxAmount;

        /**
         * 商品编码
         */
        private String goodsNum;
    }
}
