package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.converters.TXfInvoiceItemEntityConvertor;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.invoice.service.InvoiceItemServiceImpl;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
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
 * 类描述： 根据金额匹配 蓝票
 *
 * @ClassName BuleInvoiceService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/16 18:03
 */
@Slf4j
@Service
public class BlueInvoiceService {

    /**
     * 云砺底账数据表
     */
    @Autowired
    private InvoiceServiceImpl invoiceService;
    /**
     * 云砺底账明细表
     */
    @Autowired
    private InvoiceItemServiceImpl invoiceItemService;
    /**
     * 大象底账业务表
     */
    @Autowired
    private RecordInvoiceService recordInvoiceService;
    /**
     * 蓝冲用途的发票服务
     */
    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;

    public List<MatchRes> matchInvoiceInfo(BigDecimal amount, XFDeductionBusinessTypeEnum deductionEnum, String settlementNo, String sellerTaxNo,String purchserTaxNo) {
        switch (deductionEnum) {
            case AGREEMENT_BILL:
                return obtainAgreementInvoices(amount, settlementNo, sellerTaxNo);
            case CLAIM_BILL:
                return obtainClaimInvoices(amount, settlementNo, sellerTaxNo);
            case EPD_BILL:
                return obtainEpdInvoices(amount, settlementNo, sellerTaxNo);
            default:
                log.error("未识别的单据类型{}", deductionEnum);
                return Collections.emptyList();
        }
    }

    private List<MatchRes> obtainAgreementInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo) {
        return obtainInvoices(amount, settlementNo, sellerTaxNo,true);
    }

    private List<MatchRes> obtainClaimInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo) {
        return obtainInvoices(amount, settlementNo, sellerTaxNo, false);
    }

    private List<MatchRes> obtainEpdInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo) {
        return obtainInvoices(amount, settlementNo, sellerTaxNo, true);
    }

    /**
     * @param amount
     * @param settlementNo
     * @param withItems
     * @return
     */
    private List<MatchRes> obtainInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo, boolean withItems) {
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        TXfInvoiceEntity tXfInvoiceEntity;
        do {
            tXfInvoiceEntity = invoiceService.getOne(
                    new QueryWrapper<TXfInvoiceEntity>()
                            .lambda()
                            .eq(TXfInvoiceEntity::getSellerTaxNo, sellerTaxNo)
                            // 排除状态异常的发票（只要正常的发票）
                            .eq(TXfInvoiceEntity::getStatus, "1")
                            // 排除非专票（只要增值税专票）
                            .eq(TXfInvoiceEntity::getTaxCategory, "01")
                            // 排除可用金额=0的发票
                            .gt(TXfInvoiceEntity::getRemainingAmount, BigDecimal.ZERO)
                            // 按照发票先进先出
                            .orderByAsc(TXfInvoiceEntity::getPaperDrewDate)
            );
            if (Objects.nonNull(tXfInvoiceEntity)) {
                // TDxRecordInvoiceEntity tDxRecordInvoiceEntity = recordInvoiceService.getOne(
                //         new QueryWrapper<TDxRecordInvoiceEntity>()
                //         .lambda()
                //         .eq(TDxRecordInvoiceEntity::getInvoiceNo, tXfInvoiceEntity.getInvoiceNo())
                //         .eq(TDxRecordInvoiceEntity::getInvoiceCode, tXfInvoiceEntity.getInvoiceCode())
                // );
                // 排除蓝冲用途的发票（正常的发票）
                if (!blueInvoiceRelationService.existsByBlueInvoice(tXfInvoiceEntity.getInvoiceNo(), tXfInvoiceEntity.getInvoiceCode())
                    // TODO 待确认是否需要添加排除未完成付款的蓝票(已付款)
                    // && "1".equals(tDxRecordInvoiceEntity.getBpmsPayStatus())
                ) {
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
                            if (withItems) {
                                List<TXfInvoiceItemEntity> items = obtainAvailableItems(tXfInvoiceEntity.getId(), tXfInvoiceEntity.getAmountWithoutTax(), lastRemainingAmount, lastRemainingAmount);
                                list.add(MatchRes.builder()
                                        .invoiceId(tXfInvoiceEntity.getId())
                                        .invoiceNo(tXfInvoiceEntity.getInvoiceNo())
                                        .invoiceCode(tXfInvoiceEntity.getInvoiceCode())
                                        .deductedAmount(lastRemainingAmount)
                                        .invoiceItems(items
                                                .stream()
                                                .map(TXfInvoiceItemEntityConvertor.INSTANCE::toSettlementItem)
                                                .collect(Collectors.toList())
                                        )
                                        .build());
                            } else {
                                list.add(MatchRes.builder()
                                        .invoiceId(tXfInvoiceEntity.getId())
                                        .invoiceNo(tXfInvoiceEntity.getInvoiceNo())
                                        .invoiceCode(tXfInvoiceEntity.getInvoiceCode())
                                        .deductedAmount(lastRemainingAmount)
                                        .build());
                            }
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
                            if (withItems) {
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
                                                        .collect(Collectors.toList())
                                        )
                                        .build());
                            } else {
                                list.add(MatchRes.builder()
                                        .invoiceId(tXfInvoiceEntity.getId())
                                        .invoiceNo(tXfInvoiceEntity.getInvoiceNo())
                                        .invoiceCode(tXfInvoiceEntity.getInvoiceCode())
                                        .deductedAmount(deductedAmount)
                                        .build());
                            }
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
            // 顺序不能颠倒
            if (accumulatedAmount.compareTo(lastDeductedAmount) < 0
                    && accumulatedAmount.add(item.getAmountWithoutTax()).compareTo(lastDeductedAmount) > 0) {
                item.setAmountWithoutTax(lastDeductedAmount.subtract(accumulatedAmount));
            }
            if (accumulatedAmount.compareTo(totalDeductedAmount) < 0
                    && accumulatedAmount.add(item.getAmountWithoutTax()).compareTo(totalDeductedAmount) > 0) {
                item.setAmountWithoutTax(totalAmountWithoutTax.subtract(accumulatedAmount));
            }
            accumulatedAmount = accumulatedAmount.add(item.getAmountWithoutTax());
            if (accumulatedAmount.compareTo(lastDeductedAmount) > 0) {
                list.add(item);
            }
            if (accumulatedAmount.compareTo(totalDeductedAmount) >= 0) {
                return list;
            }
            // 顺序不能颠倒
        }
        return list;
    }

    /**
     * 撤回抵扣的发票金额，将抵扣金额返还到原有发票上
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
        return invoiceService.withdrawRemainingAmountById(invoices);
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
        private BigDecimal num;

        /**
         * 单价
         */
        private BigDecimal unitPrice;

        /**
         * 金额
         */
        private BigDecimal detailAmount;

        /**
         * 税率
         */
        private BigDecimal taxRate;

        /**
         * 税额
         */
        private BigDecimal taxAmount;

        /**
         * 商品编码
         */
        private String goodsNum;
        /**
         * 发票明细ID
         */
        private Long itemId;

    }
}
