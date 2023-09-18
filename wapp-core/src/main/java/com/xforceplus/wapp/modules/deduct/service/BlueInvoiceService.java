package com.xforceplus.wapp.modules.deduct.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.Data;

/**
 * 类描述： 根据金额匹配 蓝票
 *
 * @ClassName BuleInvoiceService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/16 18:03
 */
@Service
public class BlueInvoiceService {

    /**
     * 大象底账业务表
     */
/*    @Autowired
    private RecordInvoiceService invoiceService;
    @Autowired
    private RecordInvoiceExtService extInvoiceService;*/

    // /**
    //  * 大象底账明细表
    //  */
    // @Autowired
    // private RecordInvoiceDetailService invoiceItemService;
    /**
     * 蓝冲用途的发票服务
     */
/*    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;*/

/*    public List<MatchRes> matchInvoiceInfo(BigDecimal amount, TXfDeductionBusinessTypeEnum deductionEnum, String settlementNo, String sellerTaxNo, String purchserTaxNo, BigDecimal taxRate) {
        switch (deductionEnum) {
            case AGREEMENT_BILL:
                return obtainAvailableInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, taxRate, true, deductionEnum);
            case CLAIM_BILL:
                return obtainAvailableInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, taxRate, false, deductionEnum);
            case EPD_BILL:
                return obtainAvailableInvoices(amount, settlementNo, sellerTaxNo, purchserTaxNo, taxRate, true, deductionEnum);
            default:
                log.error("未识别的单据类型{}", deductionEnum);
                return Collections.emptyList();
        }
    }*/

    /**
     * @param amount
     * @param settlementNo
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @param withItems
     * @return
     */
    /*public List<MatchRes> obtainAvailableInvoices(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate, boolean withItems, TXfDeductionBusinessTypeEnum deductionEnum) {
		if (BigDecimal.ZERO.compareTo(amount) >= 0) {
			throw new NoSuchInvoiceException("非法的负数待匹配金额" + amount);
		}
        log.info("收到匹配蓝票任务 待匹配金额amount={} settlementNo={} sellerTaxNo={} purchaserTaxNo={} taxRate={} withItems={}", amount, settlementNo, sellerTaxNo, purchaserTaxNo, taxRate, withItems);
        if (withItems) {
            return obtainAvailableInvoicesWithItems(amount, settlementNo, sellerTaxNo, purchaserTaxNo, taxRate, deductionEnum);
        } else {
            return obtainAvailableInvoicesWithoutItems(amount, settlementNo, sellerTaxNo, purchaserTaxNo, taxRate, deductionEnum);
        }
    }*/

    /**
     * 匹配蓝票明细
     * @param amount
     * @param settlementNo
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @return
     */
    /*private List<MatchRes> obtainAvailableInvoicesWithItems(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate, TXfDeductionBusinessTypeEnum deductionEnum) {
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity;

        // 按照发票先进先出  2022-08-23 新增 协议匹配蓝票时，假设期间是202007-202207，之前是从2020年的发票开始匹配，现在要改成先从2022年发票开始匹配，匹配近两年供应商蓝票规则不变。
        //https://jira.xforceplus.com/browse/PRJCENTER-10272
        String invoiceDateOrder = deductionEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL?"DESC":"ASC";

        // 解决死循环查询同一张发票问题
        List<Long> preIdList = Lists.newArrayList(0L);
        boolean notQueryOil = false;
        do {
            tDxRecordInvoiceEntity = extInvoiceService.obtainAvailableInvoice(preIdList, sellerTaxNo, purchaserTaxNo, taxRate, notQueryOil, invoiceDateOrder);
			log.info("settlementNo:{},查询到的匹配发票信息：{}", settlementNo, JSON.toJSON(tDxRecordInvoiceEntity));
			if(Objects.isNull(tDxRecordInvoiceEntity)) { //匹配不到发票信息
				break;
			}
			if (tDxRecordInvoiceEntity.getRemainingAmount() != null && tDxRecordInvoiceEntity.getRemainingAmount().compareTo(BigDecimal.ONE) <= 0) {
				log.info("settlementNo:{},invoiceNo:{},invoiceCode:{},可用金额金额不足1元,RemainingAmount:{}",settlementNo,tDxRecordInvoiceEntity.getInvoiceNo(), tDxRecordInvoiceEntity.getInvoiceCode(), tDxRecordInvoiceEntity.getRemainingAmount());
				break;
			}
			//2022-06-16,因为成品油只能匹配一张发票，如果成品油发票金额小于leftAmount金额，继续找下一张
			if(Optional.ofNullable(tDxRecordInvoiceEntity.getIsOil()).orElse(0) == 1 && leftAmount.get().compareTo(tDxRecordInvoiceEntity.getInvoiceAmount()) > 0) {
				notQueryOil = true;
				log.info("settlementNo:{},invoiceNo:{},invoiceCode:{},是成品油发票，将继续查找其他金额发票 leftAmount:{},InvoiceAmount:{}", settlementNo,tDxRecordInvoiceEntity.getInvoiceNo(),
						tDxRecordInvoiceEntity.getInvoiceCode(), leftAmount.get(), tDxRecordInvoiceEntity.getInvoiceAmount());
				continue;
			}
            preIdList.add(tDxRecordInvoiceEntity.getId());
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
                String uuid = tDxRecordInvoiceEntity.getInvoiceCode() + tDxRecordInvoiceEntity.getInvoiceNo();
                List<TDxRecordInvoiceDetailEntity> items = obtainAvailableItems(uuid, tDxRecordInvoiceEntity.getInvoiceAmount(), lastRemainingAmount, deductedAmount);
                // 如果该发票没有可用明细，那么跳过
                if (org.springframework.util.CollectionUtils.isEmpty(items)) {
                    log.info("settlementNo:{},丢弃没有明细的发票 号码={} 代码={}", settlementNo, tDxRecordInvoiceEntity.getInvoiceNo(), tDxRecordInvoiceEntity.getInvoiceCode());
                    continue;
                }
                
				// 累计明细金额
				AtomicReference<BigDecimal> accumulatedDetailAmount = new AtomicReference<>(BigDecimal.ZERO);
				items.forEach(v -> accumulatedDetailAmount.updateAndGet(v1 -> {
					try {
						return v1.add(new BigDecimal(v.getDetailAmount()));
					} catch (Exception e) {
						log.warn("跳过金额异常的明细，detailAmount={}, 明细={}", v.getDetailAmount(), v);
						return v1;
					}
				}));
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
                            .isOil(Optional.ofNullable(tDxRecordInvoiceEntity.getIsOil()).orElse(0))
                            .invoiceId(tDxRecordInvoiceEntity.getId())
                            .invoiceNo(tDxRecordInvoiceEntity.getInvoiceNo())
                            .invoiceCode(tDxRecordInvoiceEntity.getInvoiceCode())
                            .deductedAmount(deductedAmount)
                            .invoiceDate(tDxRecordInvoiceEntity.getInvoiceDate())
                            .invoiceItems(items.stream()
                                    .map(TDxRecordInvoiceDetailEntityConvertor.INSTANCE::toSettlementItem)
                                    .collect(Collectors.toList())
                            )
                            .build());
                    //如果第一张不是成品油发票，后面发票不能查询出成品油发票
                    if (Optional.ofNullable(tDxRecordInvoiceEntity.getIsOil()).orElse(0) == 1) {
                        log.info("settlementNo:{},第一张是成品油发票只匹配这张成品油发票", settlementNo);
                        notQueryOil = true;
                        break;
                    } 
                } else {
                    log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 更新后剩余可用金额={}",
                            tDxRecordInvoiceEntity.getId(), lastRemainingAmount, deduction.getRemainingAmount());
                }
            }
        } while (Objects.nonNull(tDxRecordInvoiceEntity) && BigDecimal.ZERO.compareTo(leftAmount.get()) < 0);
        if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
            log.info("settlementNo:{},leftAmount:{},没有足够的待匹配的蓝票，回撤变更的发票", settlementNo, leftAmount.get());
            withdrawInvoices(list);
            throw new NoSuchInvoiceException();
        }
		log.info("settlementNo:{},已匹配的发票列表={}", settlementNo, JSON.toJSONString(list));
        return list;
    }*/

    /**
     * 匹配蓝票金额，不匹配明细
     * @param amount
     * @param settlementNo
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @return
     */
/*    private List<MatchRes> obtainAvailableInvoicesWithoutItems(BigDecimal amount, String settlementNo, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate, TXfDeductionBusinessTypeEnum typeEnum) {
        return obtainAvailableInvoicesWithoutItems(amount, settlementNo, sellerTaxNo, purchaserTaxNo, taxRate, true, true, typeEnum);
    }*/

    /**
     * <p>
     * 匹配蓝票金额，不匹配明细
     * 1:因为索赔没有成品油商品，所以当期没考虑成品油
     * </p>
     * @param amount
     * @param settlementNo
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @param deductRemainingAmount
     * @param notQueryOil
     * @return
     */
    /*public List<MatchRes> obtainAvailableInvoicesWithoutItems(BigDecimal amount, String settlementNo, String sellerTaxNo,
    		String purchaserTaxNo, BigDecimal taxRate, boolean deductRemainingAmount, boolean notQueryOil, TXfDeductionBusinessTypeEnum typeEnum) {
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        int page=1;
        int size=10;
        loop:while (true) {
            log.info("obtainAvailableInvoicesWithoutItems settlementNo:{},是否查询成品油发票：{}", settlementNo, !notQueryOil);
            Page<TDxRecordInvoiceEntity> pageResult = extInvoiceService.obtainAvailableInvoices(sellerTaxNo, purchaserTaxNo, taxRate,page,size, notQueryOil, typeEnum);
            log.info("obtainAvailableInvoicesWithoutItems settlementNo:{},查询到的匹配发票信息：{}", settlementNo, JSON.toJSON(pageResult));
            final List<TDxRecordInvoiceEntity> records = pageResult.getRecords();
            if (CollectionUtils.isNotEmpty(records)) {
                //如果查询条件有成品油则判断集合中是否有成品油发票，如果有则只匹配第一张成品油发票
                if (!notQueryOil) {
                    Optional<TDxRecordInvoiceEntity> first = records.stream().filter(it -> Optional.ofNullable(it.getIsOil()).orElse(0) == 1 ).findFirst();
                    if (first.isPresent()) {
                        log.info("settlementNo:{},这次匹配的一批发票有成品油则只匹配这张成品油发票：{}", first.get());
                        records.clear();
                        records.add(first.get());
                    }
                }
                for (TDxRecordInvoiceEntity invoiceEntity : records) {
                    // 排除蓝冲用途的发票（正常的发票）
                    if (!blueInvoiceRelationService.existsByBlueInvoice(invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode())) {
                        BigDecimal lastRemainingAmount = invoiceEntity.getRemainingAmount();
                        BigDecimal deductedAmount = leftAmount.get().compareTo(lastRemainingAmount) >= 0 ?
                                // 如果蓝票的剩余可用金额不够抵扣，那么deductedAmount = lastRemainingAmount
                                lastRemainingAmount :
                                // 如果蓝票的剩余可用金额抵扣后仍有剩余，那么deductedAmount = leftAmount.get()
                                leftAmount.get();
                        TDxRecordInvoiceEntity deduction = new TDxRecordInvoiceEntity();
                        deduction.setId(invoiceEntity.getId());
                        // 设置需要扣除的金额
                        deduction.setRemainingAmount(deductedAmount);
                        // 如果需要立即扣除可用额度
                        if (deductRemainingAmount) {
                            if (extInvoiceService.deductRemainingAmount(deduction) <= 0) {
                                log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 需要扣除的可用金额={}", invoiceEntity.getId(), lastRemainingAmount, lastRemainingAmount);
                                continue ;
                            }
                        }
                        leftAmount.updateAndGet(v1 -> v1.subtract(deductedAmount));
                        list.add(MatchRes.builder()
                                .isOil(Optional.ofNullable(invoiceEntity.getIsOil()).orElse(0))
                                .invoiceId(invoiceEntity.getId())
                                .invoiceNo(invoiceEntity.getInvoiceNo())
                                .invoiceCode(invoiceEntity.getInvoiceCode())
                                .deductedAmount(deductedAmount)
                                .invoiceDate(invoiceEntity.getInvoiceDate())
                                .build());
                        //在查询成品油发票的前提下没有查到成品油则下次不查询成品油发票
                        boolean isOil = Optional.ofNullable(invoiceEntity.getIsOil()).orElse(0) == 1;
                        log.info("settlementNo:{},是否是成品油：{}", settlementNo, isOil);
                        if (!isOil) {
                            notQueryOil = true;
                            log.info("下次不查询成品油发票");
                        }
                        if(BigDecimal.ZERO.compareTo(leftAmount.get()) >= 0 || isOil){
                            log.info("跳出循环");
                            break loop;
                        }
                    }
                }
                page++;
            } else {
                break;
            }
        }
        if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
            log.info("settlementNo:{},leftAmount:{},没有足够的待匹配的蓝票，回撤变更的发票", settlementNo, leftAmount.get());
            // 如果已立即扣除可用额度
            if (deductRemainingAmount) {
                withdrawInvoices(list);
            }
			throw new NoSuchInvoiceException("找不到" + taxRate + "税率的蓝票");
        }
        list = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MatchRes :: getInvoiceId))), ArrayList::new));
        log.info("settlementNo:{},已匹配到的发票列表={}", settlementNo, JSON.toJSONString(list));
        return list;
    }*/

    /**
     * 根据之前的剩余金额，以及本次需要抵扣的可用金额，按照明细顺序找到合适的明细行返回，返回的明细金额总和一定<=需要抵扣的可用金额
     *
     * @param uuid                  发票代码+发票号码 拼接
     * @param totalAmountWithoutTax
     * @param lastRemainingAmount
     * @param deductedAmount
     * @return
     */
   /* public List<TDxRecordInvoiceDetailEntity> obtainAvailableItems(String uuid, BigDecimal totalAmountWithoutTax, BigDecimal lastRemainingAmount, BigDecimal deductedAmount) {
        log.info("收到匹配蓝票明细任务 发票uuid={} totalAmountWithoutTax={} lastRemainingAmount={} deductedAmount={}", uuid, totalAmountWithoutTax, lastRemainingAmount, deductedAmount);
        List<TDxRecordInvoiceDetailEntity> list = new ArrayList<>();
        // 之前抵扣的金额
//        BigDecimal lastDeductedAmount = totalAmountWithoutTax.subtract(lastRemainingAmount);
        // 总共抵扣金额
//        BigDecimal totalDeductedAmount = lastDeductedAmount.add(deductedAmount);
        List<TDxRecordInvoiceDetailEntity> items = invoiceService.getInvoiceDetailByUuid(uuid);
//        BigDecimal accumulatedAmount = BigDecimal.ZERO;

        // 明细剩余可抵扣金额
        BigDecimal lastDeductAmount = BigDecimal.ZERO.add(deductedAmount);
        for (TDxRecordInvoiceDetailEntity item : items) {
            if (GOODS_LIST_TEXT.equalsIgnoreCase(item.getGoodsName()) || StringUtils.equalsIgnoreCase(item.getGoodsName(), "（详见销货清单）")
            		|| StringUtils.equalsIgnoreCase(item.getGoodsName(), "(详见销货清单）") || StringUtils.equalsIgnoreCase(item.getGoodsName(), "（详见销货清单)")
                    || "原价合计".equalsIgnoreCase(item.getGoodsName()) || "折扣额合计".equalsIgnoreCase(item.getGoodsName())) {
                continue;
            }
            // 获取明细不含税金额
            BigDecimal amountWithoutTax;
            try {
                amountWithoutTax = new BigDecimal(item.getDetailAmount());
            } catch (NumberFormatException e) {
                log.warn("明细不含税金额转换成数字失败，跳过此明细，uuid={} detailAmount={}", uuid, item.getDetailAmount());
                continue;
            }

            //先把明细加进返回列表去
            list.add(item);

            //剩余抵扣金额小于等于明细金额，说明到这一条已经够了
            if(lastDeductAmount.compareTo(amountWithoutTax)<=0){
                final BigDecimal bigDecimal = new BigDecimal(item.getTaxRate()).movePointLeft(2);
                item.setTaxAmount(lastDeductAmount.multiply(bigDecimal).setScale(2, RoundingMode.HALF_UP).toPlainString());
                item.setDetailAmount(lastDeductAmount.toPlainString());
                return list;
            }
            //剩余抵扣金额 = 剩余抵扣金额减去本条明细金额
            lastDeductAmount=lastDeductAmount.subtract(amountWithoutTax);


            // 顺序不能颠倒
//            if (accumulatedAmount.compareTo(lastDeductedAmount) < 0
//                    && accumulatedAmount.add(amountWithoutTax).compareTo(lastDeductedAmount) > 0) {
//                item.setDetailAmount(lastDeductedAmount.subtract(accumulatedAmount).toPlainString());
//            }
//            if (accumulatedAmount.compareTo(totalDeductedAmount) < 0
//                    && accumulatedAmount.add(amountWithoutTax).compareTo(totalDeductedAmount) > 0) {
//                item.setDetailAmount(totalDeductedAmount.subtract(accumulatedAmount).toPlainString());
//            }
//            accumulatedAmount = accumulatedAmount.add(amountWithoutTax);
//            if (accumulatedAmount.compareTo(lastDeductedAmount) > 0) {
//                list.add(item);
//            }
//            if (accumulatedAmount.compareTo(totalDeductedAmount) >= 0) {
//                return list;
//            }
            // 顺序不能颠倒
        }
        log.info("已匹配的发票明细列表={}", JSON.toJSONString(list));
        return list;
    }*/

    /**
     * 撤回抵扣的发票金额，将抵扣金额返还到原有发票上
     *
     * @param matchRes
     * @return
     */
/*    public boolean withdrawInvoice(MatchRes matchRes) {
        log.info("开始撤回抵扣的发票金额，将抵扣金额返还到原有发票上, 发票={}", matchRes);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
        tDxRecordInvoiceEntity.setId(matchRes.getInvoiceId());
        tDxRecordInvoiceEntity.setRemainingAmount(matchRes.getDeductedAmount());
        return extInvoiceService.withdrawRemainingAmount(tDxRecordInvoiceEntity) > 0;
    }*/

    /**
     * 撤回抵扣的发票金额，将抵扣金额返还到原有发票上
     * @return
     */
    /*public boolean withdrawInvoices(List<MatchRes> list) {
        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            log.info("开始撤回抵扣的发票金额，将抵扣金额返还到原有发票上, 发票列表为空，跳过此步骤");
        } else {
            log.info("开始撤回抵扣的发票金额，将抵扣金额返还到原有发票上, 发票列表={}", JSON.toJSONString(list));
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
    }*/

    @Data
    @Builder
    public static class MatchRes {
        String invoiceNo;
        String invoiceCode;
        /**
         * 是否成品油
         */
        Integer isOil;
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
        /**发票明细ID*/
        private Long itemId;
		// 2023-08-25新增WALMART-3538 begin
		/** 业务单号 */
		private String businessNo;
		// 2023-08-25新增WALMART-3538 end
        /**发票代码*/
        private String invoiceCode;
        /**发票号码*/
        private String invoiceNo;
        /**明细序号*/
        private String detailNo;
        /**货物或应税劳务名称*/
        private String goodsName;
        /**规格型号*/
        private String model;
        /**单位*/
        private String unit;
        /**税率*/
        private String taxRate;
        /**商品编码*/
        private String goodsNum;

        /**数量*/
        private String num;
        /**单价*/
        private String unitPrice;
        /**不含税金额*/
        private String detailAmount;
        /**税额*/
        private String taxAmount;

        /**匹配数量*/
        private BigDecimal matchedNum;
        /**匹配单价*/
        private BigDecimal matchedUnitPrice;
        /**匹配金额*/
        private BigDecimal matchedDetailAmount;
        /**匹配税额*/
        private BigDecimal matchedTaxAmount;
        /**剩余数量*/
        private BigDecimal leftNum;
        /**剩余金额*/
        private BigDecimal leftDetailAmount;
    }


   /* @Transactional
    public List<MatchRes> obtainInvoiceByIds(BigDecimal amount, List<Long> invoiceIds) {
        List<MatchRes> list = new ArrayList<>();
        AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(amount);
        for (Long invoiceId : invoiceIds) {
            log.info("leftAmount:{}",leftAmount);
            final TDxRecordInvoiceEntity invoice = extInvoiceService.getById(invoiceId);
            if (invoice == null) {
                throw new EnhanceRuntimeException("参数不合法，发票ID[" + invoiceId + "]不存在");
            }

            if (!Objects.equals(invoice.getInvoiceStatus(), "0")) {
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
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]可用金额[" + invoice.getRemainingAmount().toPlainString() + "]不足，不能参与匹配");
            }

            if (blueInvoiceRelationService.existsByBlueInvoice(invoice.getInvoiceNo(), invoice.getInvoiceCode())) {
                throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]是蓝冲用途的发票，不能参与匹配");
            }

            if (Optional.ofNullable(invoice.getIsOil()).orElse(0) == 1 && invoiceIds.size() > 1) {
                throw new EnhanceRuntimeException("成品油发票只能单张进行匹配！");
            }
            BigDecimal lastRemainingAmount = invoice.getRemainingAmount();
            BigDecimal deductedAmount = leftAmount.get().compareTo(lastRemainingAmount) >= 0 ?
                    // 如果蓝票的剩余可用金额不够抵扣，那么deductedAmount = lastRemainingAmount
                    lastRemainingAmount :
                    // 如果蓝票的剩余可用金额抵扣后仍有剩余，那么deductedAmount = leftAmount.get()
                    leftAmount.get();

            // 获取该发票的所有正数明细
            String uuid = invoice.getInvoiceCode() + invoice.getInvoiceNo();
            List<TDxRecordInvoiceDetailEntity> items = obtainAvailableItems(uuid, invoice.getInvoiceAmount(), lastRemainingAmount, deductedAmount);
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
                    .isOil(Optional.ofNullable(invoice.getIsOil()).orElse(0))
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
        log.info("已匹配到的发票列表={}", JSON.toJSONString(list));
        return list;
    }*/
}
