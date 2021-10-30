package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.converters.TDxRecordInvoiceDetailEntityConvertor;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceExtService;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.util.CollectionUtils;
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
     * 大象底账业务表
     */
    @Autowired
    private RecordInvoiceService invoiceService;
    @Autowired
    private RecordInvoiceExtService extInvoiceService;

    // /**
    //  * 大象底账明细表
    //  */
    // @Autowired
    // private RecordInvoiceDetailService invoiceItemService;
    /**
     * 蓝冲用途的发票服务
     */
    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;

    public List<MatchRes> matchInvoiceInfo(BigDecimal amount, XFDeductionBusinessTypeEnum deductionEnum, String settlementNo, String sellerTaxNo, String purchserTaxNo,BigDecimal taxRate) {
        switch (deductionEnum) {
            case AGREEMENT_BILL:
                return obtainAgreementInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo);
            case CLAIM_BILL:
                return obtainClaimInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo);
            case EPD_BILL:
                return obtainEpdInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo);
            default:
                log.error("未识别的单据类型{}", deductionEnum);
                return Collections.emptyList();
        }
    }

    private List<MatchRes> obtainAgreementInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchserTaxNo) {
        return obtainInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, true);
    }

    private List<MatchRes> obtainClaimInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchserTaxNo) {
        return obtainInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, false);
    }

    private List<MatchRes> obtainEpdInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchserTaxNo) {
        return obtainInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, true);
    }

    /**
     * @param amount
     * @param settlementNo
     * @param sellerTaxNo
     * @param purchserTaxNo
     * @param withItems
     * @return
     */
    private List<MatchRes> obtainInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchserTaxNo, boolean withItems) {
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new NoSuchInvoiceException("非法的负数待匹配金额" + amount);
        }
        log.info("收到匹配蓝票任务 待匹配金额amount={} settlementNo={} sellerTaxNo={} purchserTaxNo={} withItems={}", amount, settlementNo, sellerTaxNo, purchserTaxNo, withItems);
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity;
        do {
            tDxRecordInvoiceEntity = invoiceService.getOne(
                    new QueryWrapper<TDxRecordInvoiceEntity>()
                            // 只返回第一行数据，否则getOne可能会报错
                            .select("top 1 *")
                            .lambda()
                            .eq(TDxRecordInvoiceEntity::getXfTaxNo, sellerTaxNo)
                            .eq(TDxRecordInvoiceEntity::getGfTaxNo, purchserTaxNo)
                            // 排除状态异常的发票（只要正常的发票）
                            .eq(TDxRecordInvoiceEntity::getInvoiceStatus, "0")
                            // 排除非专票（只要增值税专票 和 电子专票）
                            .in(TDxRecordInvoiceEntity::getInvoiceType, InvoiceTypeEnum.SPECIAL_INVOICE.getValue(), InvoiceTypeEnum.E_SPECIAL_INVOICE.getValue())
                            // 排除可用金额=0的发票
                            .gt(TDxRecordInvoiceEntity::getRemainingAmount, BigDecimal.ZERO)
                            // 排除未完成付款的蓝票(已认证)
                            .eq(TDxRecordInvoiceEntity::getRzhYesorno, "1")
                            // 按照发票先进先出
                            .orderByAsc(TDxRecordInvoiceEntity::getInvoiceDate)
            );
            if (Objects.nonNull(tDxRecordInvoiceEntity)) {
                // 排除蓝冲用途的发票（正常的发票）
                if (!blueInvoiceRelationService.existsByBlueInvoice(tDxRecordInvoiceEntity.getInvoiceNo(), tDxRecordInvoiceEntity.getInvoiceCode())) {
                    BigDecimal lastRemainingAmount = tDxRecordInvoiceEntity.getRemainingAmount();
                    if (leftAmount.get().compareTo(lastRemainingAmount) >= 0) {
                        // 如果蓝票的剩余可用金额不够抵扣的
                        TDxRecordInvoiceEntity newTDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
                        newTDxRecordInvoiceEntity.setRemainingAmount(BigDecimal.ZERO);
                        if (invoiceService.update(newTDxRecordInvoiceEntity,
                                new QueryWrapper<TDxRecordInvoiceEntity>()
                                        .lambda()
                                        .eq(TDxRecordInvoiceEntity::getId, tDxRecordInvoiceEntity.getId())
                                        .eq(TDxRecordInvoiceEntity::getRemainingAmount, lastRemainingAmount))) {
                            leftAmount.updateAndGet(v1 -> v1.subtract(lastRemainingAmount));
                            if (withItems) {
                                List<TDxRecordInvoiceDetailEntity> items = obtainAvailableItems(
                                        tDxRecordInvoiceEntity.getInvoiceNo() + tDxRecordInvoiceEntity.getInvoiceCode(),
                                        tDxRecordInvoiceEntity.getInvoiceAmount(), lastRemainingAmount, lastRemainingAmount);
                                list.add(MatchRes.builder()
                                        .invoiceId(tDxRecordInvoiceEntity.getId())
                                        .invoiceNo(tDxRecordInvoiceEntity.getInvoiceNo())
                                        .invoiceCode(tDxRecordInvoiceEntity.getInvoiceCode())
                                        .deductedAmount(lastRemainingAmount)
                                        .invoiceItems(items
                                                .stream()
                                                .map(TDxRecordInvoiceDetailEntityConvertor.INSTANCE::toSettlementItem)
                                                .collect(Collectors.toList())
                                        )
                                        .build());
                            } else {
                                list.add(MatchRes.builder()
                                        .invoiceId(tDxRecordInvoiceEntity.getId())
                                        .invoiceNo(tDxRecordInvoiceEntity.getInvoiceNo())
                                        .invoiceCode(tDxRecordInvoiceEntity.getInvoiceCode())
                                        .deductedAmount(lastRemainingAmount)
                                        .build());
                            }
                        } else {
                            log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 更新后剩余可用金额={}",
                                    tDxRecordInvoiceEntity.getId(), lastRemainingAmount, newTDxRecordInvoiceEntity.getRemainingAmount());
                        }
                    } else {
                        // 如果蓝票的剩余可用金额抵扣后仍有剩余
                        BigDecimal deductedAmount = leftAmount.get();
                        TDxRecordInvoiceEntity newTDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
                        newTDxRecordInvoiceEntity.setRemainingAmount(lastRemainingAmount.subtract(deductedAmount));
                        if (invoiceService.update(newTDxRecordInvoiceEntity,
                                new QueryWrapper<TDxRecordInvoiceEntity>()
                                        .lambda()
                                        .eq(TDxRecordInvoiceEntity::getId, tDxRecordInvoiceEntity.getId())
                                        .eq(TDxRecordInvoiceEntity::getRemainingAmount, lastRemainingAmount))) {
                            leftAmount.set(BigDecimal.ZERO);
                            if (withItems) {
                                List<TDxRecordInvoiceDetailEntity> items = obtainAvailableItems(
                                        tDxRecordInvoiceEntity.getInvoiceNo() + tDxRecordInvoiceEntity.getInvoiceCode(),
                                        tDxRecordInvoiceEntity.getInvoiceAmount(), lastRemainingAmount, deductedAmount);
                                list.add(MatchRes.builder()
                                        .invoiceId(tDxRecordInvoiceEntity.getId())
                                        .invoiceNo(tDxRecordInvoiceEntity.getInvoiceNo())
                                        .invoiceCode(tDxRecordInvoiceEntity.getInvoiceCode())
                                        .deductedAmount(deductedAmount)
                                        .invoiceItems(
                                                items
                                                        .stream()
                                                        .map(TDxRecordInvoiceDetailEntityConvertor.INSTANCE::toSettlementItem)
                                                        .collect(Collectors.toList())
                                        )
                                        .build());
                            } else {
                                list.add(MatchRes.builder()
                                        .invoiceId(tDxRecordInvoiceEntity.getId())
                                        .invoiceNo(tDxRecordInvoiceEntity.getInvoiceNo())
                                        .invoiceCode(tDxRecordInvoiceEntity.getInvoiceCode())
                                        .deductedAmount(deductedAmount)
                                        .build());
                            }
                        } else {
                            log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 更新后剩余可用金额={}",
                                    tDxRecordInvoiceEntity.getId(), lastRemainingAmount, newTDxRecordInvoiceEntity.getRemainingAmount());
                        }
                    }
                }
            }
        } while (Objects.nonNull(tDxRecordInvoiceEntity) && BigDecimal.ZERO.compareTo(leftAmount.get()) < 0);
        if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
            log.info("没有足够的待匹配的蓝票，回撤变更的发票");
            withdrawInvoices(list);
            throw new NoSuchInvoiceException();
        }
        log.info("已匹配的发票列表={}", CollectionUtils.flattenToString(list));
        return list;
    }

    /**
     * 根据之前的剩余金额，以及本次需要抵扣的可用金额，按照明细顺序找到合适的明细行返回
     *
     * @param uuid                  发票号码+发票代码 拼接
     * @param totalAmountWithoutTax
     * @param lastRemainingAmount
     * @param deductedAmount
     * @return
     */
    public List<TDxRecordInvoiceDetailEntity> obtainAvailableItems(String uuid, BigDecimal totalAmountWithoutTax, BigDecimal lastRemainingAmount, BigDecimal deductedAmount) {
        log.info("收到匹配蓝票明细任务 发票uuid={} totalAmountWithoutTax={} lastRemainingAmount={} deductedAmount={}", uuid, totalAmountWithoutTax, lastRemainingAmount, deductedAmount);
        List<TDxRecordInvoiceDetailEntity> list = new ArrayList<>();
        // 之前抵扣的金额
        BigDecimal lastDeductedAmount = totalAmountWithoutTax.subtract(lastRemainingAmount);
        // 总共抵扣金额
        BigDecimal totalDeductedAmount = lastDeductedAmount.add(deductedAmount);
        List<TDxRecordInvoiceDetailEntity> items = invoiceService.getInvoiceDetailByUuid(uuid);
        BigDecimal accumulatedAmount = BigDecimal.ZERO;
        for (TDxRecordInvoiceDetailEntity item : items) {
            // 获取明细不含税金额
            BigDecimal amountWithoutTax;
            try {
                amountWithoutTax = new BigDecimal(item.getDetailAmount());
            } catch (NumberFormatException e) {
                log.warn("明细不含税金额转换成数字失败，跳过此明细，uuid={} detailAmount={}", uuid, item.getDetailAmount());
                continue;
            }
            // 顺序不能颠倒
            if (accumulatedAmount.compareTo(lastDeductedAmount) < 0
                    && accumulatedAmount.add(amountWithoutTax).compareTo(lastDeductedAmount) > 0) {
                item.setDetailAmount(lastDeductedAmount.subtract(accumulatedAmount).toPlainString());
            }
            if (accumulatedAmount.compareTo(totalDeductedAmount) < 0
                    && accumulatedAmount.add(amountWithoutTax).compareTo(totalDeductedAmount) > 0) {
                item.setDetailAmount(totalAmountWithoutTax.subtract(accumulatedAmount).toPlainString());
            }
            accumulatedAmount = accumulatedAmount.add(amountWithoutTax);
            if (accumulatedAmount.compareTo(lastDeductedAmount) > 0) {
                list.add(item);
            }
            if (accumulatedAmount.compareTo(totalDeductedAmount) >= 0) {
                return list;
            }
            // 顺序不能颠倒
        }
        log.info("已匹配的发票明细列表={}", CollectionUtils.flattenToString(list));
        return list;
    }

    /**
     * 撤回抵扣的发票金额，将抵扣金额返还到原有发票上
     *
     * @param list
     * @return
     */
    public boolean withdrawInvoices(List<MatchRes> list) {
        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            log.info("开始撤回抵扣的发票金额，将抵扣金额返还到原有发票上, 发票列表为空，跳过此步骤");
        } else {
            log.info("开始撤回抵扣的发票金额，将抵扣金额返还到原有发票上, 发票列表={}", CollectionUtils.flattenToString(list));
            List<TDxRecordInvoiceEntity> invoices = list
                    .stream()
                    .map(
                            v -> {
                                TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
                                tDxRecordInvoiceEntity.setId(v.getInvoiceId());
                                tDxRecordInvoiceEntity.setRemainingAmount(v.getDeductedAmount());
                                return tDxRecordInvoiceEntity;
                            }
                    )
                    .collect(Collectors.toList());
            return extInvoiceService.withdrawRemainingAmountById(invoices);
        }
        return true;
    }

    @Data
    @Builder
    public static class MatchRes {
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
