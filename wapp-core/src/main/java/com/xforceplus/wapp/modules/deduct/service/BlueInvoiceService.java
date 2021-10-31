package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.converters.TDxRecordInvoiceDetailEntityConvertor;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
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

    public List<MatchRes> matchInvoiceInfo(BigDecimal amount, XFDeductionBusinessTypeEnum deductionEnum, String settlementNo, String sellerTaxNo, String purchserTaxNo, BigDecimal taxRate) {
        switch (deductionEnum) {
            case AGREEMENT_BILL:
                return obtainAvailableInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, taxRate, true);
            case CLAIM_BILL:
                return obtainAvailableInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, taxRate, false);
            case EPD_BILL:
                return obtainAvailableInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, taxRate, true);
            default:
                log.error("未识别的单据类型{}", deductionEnum);
                return Collections.emptyList();
        }
    }

    /**
     * @param amount
     * @param settlementNo
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @param withItems
     * @return
     */
    public List<MatchRes> obtainAvailableInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate, boolean withItems) {
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new NoSuchInvoiceException("非法的负数待匹配金额" + amount);
        }
        log.info("收到匹配蓝票任务 待匹配金额amount={} settlementNo={} sellerTaxNo={} purchaserTaxNo={} withItems={}", amount, settlementNo, sellerTaxNo, purchaserTaxNo, withItems);
        if (withItems) {
            return obtainAvailableInvoicesWithItems(amount, settlementNo, sellerTaxNo, purchaserTaxNo, taxRate);
        } else {
            return obtainAvailableInvoicesWithoutItems(amount, settlementNo, sellerTaxNo, purchaserTaxNo, taxRate);
        }
    }

    private List<MatchRes> obtainAvailableInvoicesWithItems(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate) {
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity;
        do {
            tDxRecordInvoiceEntity = extInvoiceService.obtainAvailableInvoice(sellerTaxNo, purchaserTaxNo, taxRate);
            if (Objects.nonNull(tDxRecordInvoiceEntity)) {
                // 排除蓝冲用途的发票（正常的发票）
                if (!blueInvoiceRelationService.existsByBlueInvoice(tDxRecordInvoiceEntity.getInvoiceNo(), tDxRecordInvoiceEntity.getInvoiceCode())) {
                    BigDecimal lastRemainingAmount = tDxRecordInvoiceEntity.getRemainingAmount();
                    BigDecimal deductedAmount = leftAmount.get().compareTo(lastRemainingAmount) >= 0 ?
                            // 如果蓝票的剩余可用金额不够抵扣，那么deductedAmount = lastRemainingAmount
                            lastRemainingAmount :
                            // 如果蓝票的剩余可用金额抵扣后仍有剩余，那么deductedAmount = leftAmount.get()
                            leftAmount.get();
                    // 获取该发票的所有正数明细
                    //TODO No+code不正确，大象的逻辑都是 code+no
                    String uuid =  tDxRecordInvoiceEntity.getInvoiceCode() + tDxRecordInvoiceEntity.getInvoiceNo() ;
                    List<TDxRecordInvoiceDetailEntity> items = obtainAvailableItems(uuid, tDxRecordInvoiceEntity.getInvoiceAmount(), lastRemainingAmount, lastRemainingAmount);
                    // 如果该发票没有可用明细，那么跳过
                    if (org.springframework.util.CollectionUtils.isEmpty(items)) {
                        log.info("丢弃没有明细的发票 号码={} 代码={}", tDxRecordInvoiceEntity.getInvoiceNo(), tDxRecordInvoiceEntity.getInvoiceCode());
                        continue;
                    }
                    // 累计明细金额
                    AtomicReference<BigDecimal> accumulatedDetailAmount = new AtomicReference<>(BigDecimal.ZERO);
                    items.forEach(
                            v -> accumulatedDetailAmount
                                    .updateAndGet(
                                            v1 -> {
                                                try {
                                                    return v1.add(new BigDecimal(v.getDetailAmount()));
                                                } catch (Exception e) {
                                                    log.warn("跳过金额异常的明细，detailAmount={}, 明细={}", v.getDetailAmount(), v);
                                                    return v1;
                                                }
                                            }
                                    )
                    );
                    if (BigDecimal.ZERO.compareTo(accumulatedDetailAmount.get()) == 0) {
                        log.info("丢弃明细金额总和为0的发票 号码={} 代码={}", tDxRecordInvoiceEntity.getInvoiceNo(), tDxRecordInvoiceEntity.getInvoiceCode());
                        continue;
                    }
                    // 异常情况，当明细不完整（只导入了一部分），如果累计明细金额小于待扣除的可用金额，那么以明细金额为准
                    if (deductedAmount.compareTo(accumulatedDetailAmount.get()) > 0) {
                        deductedAmount = accumulatedDetailAmount.get();
                    }
                    TDxRecordInvoiceEntity deduction = new TDxRecordInvoiceEntity();
                    deduction.setId(tDxRecordInvoiceEntity.getId());
                    // 设置需要扣除的金额
                    deduction.setRemainingAmount(deductedAmount);
                    if (extInvoiceService.deductRemainingAmount(deduction) > 0) {
                        BigDecimal finalDeductedAmount = deductedAmount;
                        leftAmount.updateAndGet(v1 -> v1.subtract(finalDeductedAmount));
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
                        log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 更新后剩余可用金额={}",
                                tDxRecordInvoiceEntity.getId(), lastRemainingAmount, deduction.getRemainingAmount());
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

    private List<MatchRes> obtainAvailableInvoicesWithoutItems(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate) {
        return obtainAvailableInvoicesWithoutItems(amount, settlementNo, sellerTaxNo, purchaserTaxNo, taxRate,true);
    }

    public List<MatchRes> obtainAvailableInvoicesWithoutItems(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate,boolean deductRemainingAmount) {
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity;
        do {
            tDxRecordInvoiceEntity = extInvoiceService.obtainAvailableInvoice(sellerTaxNo, purchaserTaxNo, taxRate);
            if (Objects.nonNull(tDxRecordInvoiceEntity)) {
                // 排除蓝冲用途的发票（正常的发票）
                if (!blueInvoiceRelationService.existsByBlueInvoice(tDxRecordInvoiceEntity.getInvoiceNo(), tDxRecordInvoiceEntity.getInvoiceCode())) {
                    BigDecimal lastRemainingAmount = tDxRecordInvoiceEntity.getRemainingAmount();
                    BigDecimal deductedAmount = leftAmount.get().compareTo(lastRemainingAmount) >= 0 ?
                            // 如果蓝票的剩余可用金额不够抵扣，那么deductedAmount = lastRemainingAmount
                            lastRemainingAmount :
                            // 如果蓝票的剩余可用金额抵扣后仍有剩余，那么deductedAmount = leftAmount.get()
                            leftAmount.get();
                    TDxRecordInvoiceEntity deduction = new TDxRecordInvoiceEntity();
                    deduction.setId(tDxRecordInvoiceEntity.getId());
                    // 设置需要扣除的金额
                    deduction.setRemainingAmount(deductedAmount);
                    if (deductRemainingAmount) {
                        if (extInvoiceService.deductRemainingAmount(deduction) <= 0) {
                            log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 需要扣除的可用金额={}", tDxRecordInvoiceEntity.getId(), lastRemainingAmount, lastRemainingAmount);
                        }
                    }
                    leftAmount.updateAndGet(v1 -> v1.subtract(deductedAmount));
                    list.add(MatchRes.builder()
                            .invoiceId(tDxRecordInvoiceEntity.getId())
                            .invoiceNo(tDxRecordInvoiceEntity.getInvoiceNo())
                            .invoiceCode(tDxRecordInvoiceEntity.getInvoiceCode())
                            .deductedAmount(deductedAmount)
                            .invoiceDate(tDxRecordInvoiceEntity.getInvoiceDate())
                            .build());
                }
            }
        } while (Objects.nonNull(tDxRecordInvoiceEntity) && BigDecimal.ZERO.compareTo(leftAmount.get()) < 0);
        if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
            log.info("没有足够的待匹配的蓝票，回撤变更的发票");
            withdrawInvoices(list);
            throw new NoSuchInvoiceException();
        }
        log.info("已匹配到的发票列表={}", CollectionUtils.flattenToString(list));
        return list;
    }

    /**
     * 根据之前的剩余金额，以及本次需要抵扣的可用金额，按照明细顺序找到合适的明细行返回，返回的明细金额总和一定<=需要抵扣的可用金额
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
     * @param matchRes
     * @return
     */
    public boolean withdrawInvoice(MatchRes matchRes) {
        log.info("开始撤回抵扣的发票金额，将抵扣金额返还到原有发票上, 发票={}", matchRes);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
        tDxRecordInvoiceEntity.setId(matchRes.getInvoiceId());
        tDxRecordInvoiceEntity.setRemainingAmount(matchRes.getDeductedAmount());
        return extInvoiceService.withdrawRemainingAmount(tDxRecordInvoiceEntity) > 0;
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
        /**
         * 开票日期
         */
        Date invoiceDate;
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


    @Transactional
    public List<MatchRes> obtainInvoiceByIds(BigDecimal amount,List<Long> invoiceIds){
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        for (Long invoiceId : invoiceIds) {
            final TDxRecordInvoiceEntity invoice = extInvoiceService.getById(invoiceId);
            if (invoice == null) {
                throw new EnhanceRuntimeException("参数不合法，发票ID[" + invoiceId + "]不存在");
            }

            if (!Objects.equals(invoice.getInvoiceStatus(),"0")){
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]不是正常状态发票");
            }

            if (invoice.getInvoiceAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]负数金额不能参与匹配");
            }

            if (!Objects.equals(invoice.getRzhYesorno(), "1")) {
                log.info("发票[{}][{}]未认证，不能参与匹配", invoice.getInvoiceNo(), invoice.getInvoiceCode());
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]未认证，不能参与匹配");
            }

            if (Objects.isNull(invoice.getRemainingAmount())) {
                invoice.setRemainingAmount(invoice.getInvoiceAmount());
            }

            if (invoice.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]可用金额["+invoice.getRemainingAmount().toPlainString()+"]不足，不能参与匹配");
            }

            if (blueInvoiceRelationService.existsByBlueInvoice(invoice.getInvoiceNo(), invoice.getInvoiceCode())) {
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]是蓝冲用途的发票，不能参与匹配");
            }


            BigDecimal lastRemainingAmount = invoice.getRemainingAmount();
            BigDecimal deductedAmount = leftAmount.get().compareTo(lastRemainingAmount) >= 0 ?
                    // 如果蓝票的剩余可用金额不够抵扣，那么deductedAmount = lastRemainingAmount
                    lastRemainingAmount :
                    // 如果蓝票的剩余可用金额抵扣后仍有剩余，那么deductedAmount = leftAmount.get()
                    leftAmount.get();

            // 获取该发票的所有正数明细
            String uuid =  invoice.getInvoiceCode() + invoice.getInvoiceNo() ;
            List<TDxRecordInvoiceDetailEntity> items = obtainAvailableItems(uuid, invoice.getInvoiceAmount(), lastRemainingAmount, lastRemainingAmount);
            // 如果该发票没有可用明细，那么跳过
            if (org.springframework.util.CollectionUtils.isEmpty(items)) {
                log.info("丢弃没有明细的发票 号码={} 代码={}", invoice.getInvoiceNo(), invoice.getInvoiceCode());
                continue;
            }
            // 累计明细金额
            AtomicReference<BigDecimal> accumulatedDetailAmount = new AtomicReference<>(BigDecimal.ZERO);
            items.forEach(
                    v -> accumulatedDetailAmount
                            .updateAndGet(
                                    v1 -> {
                                        try {
                                            return v1.add(new BigDecimal(v.getDetailAmount()));
                                        } catch (Exception e) {
                                            log.warn("跳过金额异常的明细，detailAmount={}, 明细={}", v.getDetailAmount(), v);
                                            return v1;
                                        }
                                    }
                            )
            );
            if (BigDecimal.ZERO.compareTo(accumulatedDetailAmount.get()) == 0) {
                log.info("丢弃明细金额总和为0的发票 号码={} 代码={}", invoice.getInvoiceNo(), invoice.getInvoiceCode());
                continue;
            }
            // 异常情况，当明细不完整（只导入了一部分），如果累计明细金额小于待扣除的可用金额，那么以明细金额为准
            if (deductedAmount.compareTo(accumulatedDetailAmount.get()) > 0) {
                deductedAmount = accumulatedDetailAmount.get();
            }
            TDxRecordInvoiceEntity deduction = new TDxRecordInvoiceEntity();
            deduction.setId(invoice.getId());
            // 设置需要扣除的金额
            deduction.setRemainingAmount(deductedAmount);
            if (extInvoiceService.deductRemainingAmount(deduction) <= 0) {
                log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 需要扣除的可用金额={}", invoice.getId(), lastRemainingAmount, lastRemainingAmount);
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]锁定失败，或已被使用，请选择其他发票重试");
            }

            final BigDecimal finalDeductedAmount = deductedAmount;
            leftAmount.updateAndGet(v1 -> v1.subtract(finalDeductedAmount));
            list.add(MatchRes.builder()
                    .invoiceId(invoice.getId())
                    .invoiceNo(invoice.getInvoiceNo())
                    .invoiceCode(invoice.getInvoiceCode())
                    .deductedAmount(deductedAmount)
                    .invoiceDate(invoice.getInvoiceDate())
                    .invoiceItems(
                            items
                                    .stream()
                                    .map(TDxRecordInvoiceDetailEntityConvertor.INSTANCE::toSettlementItem)
                                    .collect(Collectors.toList())
                    )
                    .build());

        }

        if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
            log.info("没有足够的待匹配的蓝票，回撤变更的发票");
            withdrawInvoices(list);
            throw new EnhanceRuntimeException("匹配失败，所选发票可用金额总和小于合并的业务单总额");
        }
        log.info("已匹配到的发票列表={}", CollectionUtils.flattenToString(list));
        return list;
    }
}
