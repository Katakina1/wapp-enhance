package com.xforceplus.wapp.modules.statement.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.modules.billdeduct.converters.BillDeductConverter;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductItemServiceImpl;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductServiceImpl;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.converters.PreInvoiceConverter;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceItemDaoService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.converters.SettlementItemConverter;
import com.xforceplus.wapp.modules.settlement.service.SettlementItemServiceImpl;
import com.xforceplus.wapp.modules.statement.converters.StatementConverter;
import com.xforceplus.wapp.modules.statement.models.*;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.*;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class StatementServiceImpl extends ServiceImpl<TXfSettlementDao, TXfSettlementEntity> {
    private final StatementConverter statementConverter;
    private final OperateLogService operateLogService;
    private final TXfBillDeductExtDao billDeductExtDao;
    private final PreinvoiceService preinvoiceService;
    private final PreInvoiceConverter preInvoiceConverter;
    private final SettlementItemServiceImpl settlementItemService;
    private final SettlementItemConverter settlementItemConverter;
    private final BillDeductServiceImpl billDeductService;
    private final BillDeductConverter billDeductConverter;
    private final BillDeductItemServiceImpl billDeductItemService;
    private final PreInvoiceItemDaoService preInvoiceItemDaoService;

    public StatementServiceImpl(StatementConverter statementConverter, OperateLogService operateLogService, TXfBillDeductExtDao billDeductExtDao, PreinvoiceService preinvoiceService, PreInvoiceConverter preInvoiceConverter, SettlementItemServiceImpl settlementItemService, SettlementItemConverter settlementItemConverter, BillDeductServiceImpl billDeductService, BillDeductConverter billDeductConverter, BillDeductItemServiceImpl billDeductItemService, PreInvoiceItemDaoService preInvoiceItemDaoService) {
        this.statementConverter = statementConverter;
        this.operateLogService = operateLogService;
        this.billDeductExtDao = billDeductExtDao;
        this.preinvoiceService = preinvoiceService;
        this.preInvoiceConverter = preInvoiceConverter;
        this.settlementItemService = settlementItemService;
        this.settlementItemConverter = settlementItemConverter;
        this.billDeductService = billDeductService;
        this.billDeductConverter = billDeductConverter;
        this.billDeductItemService = billDeductItemService;
        this.preInvoiceItemDaoService = preInvoiceItemDaoService;
    }

    public Tuple2<List<Settlement>, Page<?>> page(Long current, Long size, @NonNull Integer type, Integer settlementStatus,
                                                  String settlementNo, String purchaserNo, String invoiceType,
                                                  String businessNo, String taxRate) {
        log.info("结算单分页查询,入参,type:{},settlementStatus:{},settlementNo:{},purchaserNo:{}," +
                        "invoiceType:{},businessNo:{},taxRate:{},分页数据,current:{},size:{}",
                type, settlementStatus, settlementNo, purchaserNo, invoiceType, businessNo, taxRate, current, size);
        LambdaQueryChainWrapper<TXfSettlementEntity> wrapper = new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(TXfSettlementEntity::getSettlementType, type)
                .eq(TXfSettlementEntity::getSellerNo, UserUtil.getUser().getUsercode());
        if (Objects.nonNull(settlementStatus)) {
            wrapper.eq(TXfSettlementEntity::getSettlementStatus, settlementStatus);
        } else {
            Set<Integer> collect = Arrays.stream(TXfSettlementStatusEnum.values())
                    .filter(it -> it.getCode() < 8 || it.getValue() == 10)
                    .map(TXfSettlementStatusEnum::getValue).collect(Collectors.toSet());
            wrapper.in(TXfSettlementEntity::getSettlementStatus, collect);
        }
        if (StringUtils.isNotBlank(settlementNo)) {
            wrapper.eq(TXfSettlementEntity::getSettlementNo, settlementNo);
        }
        if (StringUtils.isNotBlank(purchaserNo)) {
            wrapper.eq(TXfSettlementEntity::getPurchaserNo, purchaserNo);
        }
        if (StringUtils.isNotBlank(invoiceType)) {
            wrapper.eq(TXfSettlementEntity::getInvoiceType, invoiceType);
        }
        if (StringUtils.isNotBlank(taxRate)) {
            wrapper.eq(TXfSettlementEntity::getTaxRate, taxRate);
        }
        if (StringUtils.isNotBlank(businessNo)) {
            List<String> nos = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo).eq(TXfBillDeductEntity::getBusinessNo, businessNo)
                    .list().stream().map(TXfBillDeductEntity::getRefSettlementNo)
                    .distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(nos)) {
                return Tuple.of(Lists.newArrayList(), new Page<>(current, size));
            }
            wrapper.in(TXfSettlementEntity::getSettlementNo, nos);
        }
        Page<TXfSettlementEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("税编分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(statementConverter.map(page.getRecords()), page);
    }

    public List<SettlementCount> count(@NonNull Integer type, String settlementNo, String purchaserNo, String invoiceType,
                                       String businessNo, String taxRate) {
        log.info("结算单tab统计,入参,type:{},settlementNo:{},purchaserNo:{},invoiceType:{},businessNo:{},taxRate:{}",
                type, settlementNo, purchaserNo, invoiceType, businessNo, taxRate);
        Map<String, SettlementCount> tabMap = Arrays.stream(TXfSettlementStatusEnum.values())
                .filter(it -> it.getValue() < 8 || it.getValue() == 10)
                .map(it -> SettlementCount.builder().status(it.getCode().toString()).total(0).build())
                .collect(Collectors.toMap(SettlementCount::getStatus, Function.identity()));
        QueryWrapper<TXfSettlementEntity> wrapper = new QueryWrapper<>();
        val lambda = wrapper.lambda()
                .eq(TXfSettlementEntity::getSettlementType, type)
                .in(TXfSettlementEntity::getSettlementStatus, tabMap.keySet())
                .eq(TXfSettlementEntity::getSellerNo, UserUtil.getUser().getUsercode())
                .groupBy(TXfSettlementEntity::getSettlementStatus);

        if (StringUtils.isNotBlank(settlementNo)) {
            lambda.eq(TXfSettlementEntity::getSettlementNo, settlementNo);
        }
        if (StringUtils.isNotBlank(purchaserNo)) {
            lambda.eq(TXfSettlementEntity::getPurchaserNo, purchaserNo);
        }
        if (StringUtils.isNotBlank(invoiceType)) {
            lambda.eq(TXfSettlementEntity::getInvoiceType, invoiceType);
        }
        if (StringUtils.isNotBlank(taxRate)) {
            lambda.eq(TXfSettlementEntity::getTaxRate, taxRate);
        }
        if (StringUtils.isNotBlank(businessNo)) {
            List<String> nos = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo).eq(TXfBillDeductEntity::getBusinessNo, businessNo)
                    .list().stream().map(TXfBillDeductEntity::getRefSettlementNo)
                    .distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(nos)) {
                return Lists.newArrayList(tabMap.values());
            }
            lambda.in(TXfSettlementEntity::getSettlementNo, nos);
        }
        wrapper.select("settlement_status as status, count(*) as total");
        getBaseMapper().selectMaps(wrapper)
                .forEach(it -> tabMap.computeIfPresent(it.get("status").toString(),
                        (k, v) -> new SettlementCount(it.get("status").toString(), (Integer) it.get("total"))));
        log.debug("结算单tab统计,结果:{}", tabMap.values());
        return Lists.newArrayList(tabMap.values());
    }

    public Tuple2<List<PreInvoice>, Page<?>> awaitingInvoicePage(Long current, Long size, @NonNull String settlementNo) {
        log.info("待开票列表查询,入参,settlementNo:{},分页数据,current:{},size:{}", settlementNo, current, size);
        val page = new LambdaQueryChainWrapper<>(preinvoiceService.getBaseMapper())
                .eq(TXfPreInvoiceEntity::getSettlementNo, settlementNo)
                .ne(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode())
                .page(new Page<>(current, size));
        log.debug("待开票列表查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(preInvoiceConverter.map(page.getRecords()), page);
    }

    public Tuple2<List<? extends BaseInformation>, Page<?>> baseInformationClaimPage(Long current, Long size,
                                                                                     @NonNull String settlementNo) {
        log.info("索赔明细列表查询,入参,settlementNo:{},分页数据,current:{},size:{}", settlementNo, current, size);
        val page = new LambdaQueryChainWrapper<>(billDeductService.getBaseMapper())
                .eq(TXfBillDeductEntity::getRefSettlementNo, settlementNo)
                .select(TXfBillDeductEntity::getId, TXfBillDeductEntity::getBusinessNo)
                .page(new Page<>(current, size));
        log.debug("索赔明细列表查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        List<Claim> map = page.getRecords().parallelStream().map(it -> {
            List<TXfBillDeductItemEntity> items = billDeductItemService.listByDeductId(it.getId());
            return billDeductConverter.map(it, items);
        }).collect(Collectors.toList());
        log.debug("索赔明细列表查询,总条数:{},分页转换数据:{}", page.getTotal(), map);
        return Tuple.of(map, page);
    }

    public Tuple2<List<? extends BaseInformation>, Page<?>> baseInformationAgreementPage(Long current, Long size,
                                                                                         @NonNull String settlementNo) {
        log.info("协议明细列表查询,入参,settlementNo:{},分页数据,current:{},size:{}", settlementNo, current, size);
        val page = new LambdaQueryChainWrapper<>(settlementItemService.getBaseMapper())
                .eq(TXfSettlementItemEntity::getSettlementNo, settlementNo)
                .page(new Page<>(current, size));
        log.debug("协议明细列表查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(settlementItemConverter.map(page.getRecords()), page);
    }

    public Optional<PreInvoice> preInvoice(@NonNull Long invoiceId) {
        log.info("预制发票详情信息查询,入参,invoiceId:{}", invoiceId);
        TXfPreInvoiceEntity invoice = preinvoiceService.getById(invoiceId);
        if (Objects.isNull(invoice)) {
            return Optional.empty();
        }
        List<TXfPreInvoiceItemEntity> items = preInvoiceItemDaoService.getByInvoiceId(invoiceId);
        PreInvoice map = preInvoiceConverter.map(invoice, items);
        log.debug("预制发票详情信息:{}", map);
        return Optional.ofNullable(map);
    }

    /**
     * 1. 通过settlementNo查询 结算单明细表（t_xf_settlement_item）
     * 2. 获取索赔明细ID和结算单明细ID对应关系
     * 3. 通过索赔的ID查询业务单匹配关系表（t_xf_bill_deduct_item_ref）获取索赔ID对应的索赔ID
     * 4. 通过索赔ID查询业务单据信息表（t_xf_bill_deduct）获取业务单编号
     * 5. 处理数据获取索赔明细ID与业务编号关系
     * 6. 处理数据获取结算单明细ID与业务编号关系
     * 7. 组装业务编号与结算单明细列表数据
     */
    public List<ClaimConfirm> claimConfirmItem(@NonNull String settlementNo) {
        log.info("结算单确认索赔列表查询,入参,settlementNo:{}", settlementNo);
        val items = new LambdaQueryChainWrapper<>(settlementItemService.getBaseMapper())
                .eq(TXfSettlementItemEntity::getSettlementNo, settlementNo)
                .eq(TXfSettlementItemEntity::getItemFlag, 2)
                .isNotNull(TXfSettlementItemEntity::getThridId).list();
        log.debug("结算单确认索赔列表查询[查询结算单明细],结果:{}", items);
        if (CollectionUtils.isEmpty(items)) {
            log.info("查询结算单明细为空");
            return Lists.newArrayList();
        }
        Map<Long, TXfSettlementItemEntity> itemMap = items.stream()
                .collect(Collectors.toMap(TXfSettlementItemEntity::getThridId, Function.identity(), (o, n) -> o));
        Map<Long, List<Long>> deIdAndDeItemIdsMap = billDeductItemService.listByRefItemIds(itemMap.keySet())
                .stream().collect(Collectors.groupingBy(TXfBillDeductItemRefEntity::getDeductId,
                        Collectors.mapping(TXfBillDeductItemRefEntity::getDeductItemId, Collectors.toList())));
        log.debug("结算单确认索赔列表查询[查询索赔单关系],结果:{}", items);
        if (deIdAndDeItemIdsMap.isEmpty()) {
            log.info("查询索赔单关系为空");
            return Lists.newArrayList();
        }
        return billDeductService.listBusinessNoByIds(deIdAndDeItemIdsMap.keySet()).stream()
                .collect(Collectors.groupingBy(TXfBillDeductEntity::getBusinessNo,
                        Collectors.mapping(TXfBillDeductEntity::getId, Collectors.toList())))
                .entrySet().stream()
                .map(it -> statementConverter.map(it.getKey(), it.getValue().parallelStream().map(deIdAndDeItemIdsMap::get)
                        .flatMap(List::stream).map(itemMap::get).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    public List<ConfirmItem> confirmItemList(@NonNull String settlementNo) {
        val items = new LambdaQueryChainWrapper<>(settlementItemService.getBaseMapper())
                .eq(TXfSettlementItemEntity::getSettlementNo, settlementNo)
                .eq(TXfSettlementItemEntity::getItemFlag, TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getValue())
                .list();
        return settlementItemConverter.mapItem(items);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public boolean confirmItem(String settlementNo, String sellerNo, List<Long> ids, @NonNull TXfAmountSplitRuleEnum type) {
        log.info("结算单确认,settlementNo:{},sellerNo:{},type:{},明细ID:{}", settlementNo, sellerNo, type.getDesc(), ids);
        val splitType = ImmutableMap
                .<TXfAmountSplitRuleEnum, Consumer<TXfSettlementItemEntity>>builder()
                .put(TXfAmountSplitRuleEnum.SplitPrice, entity -> entity.setUnitPrice(entity.getAmountWithoutTax().divide(entity.getQuantity(), 10, RoundingMode.HALF_UP)))
                .put(TXfAmountSplitRuleEnum.SplitQuantity, entity -> entity.setQuantity(entity.getAmountWithoutTax().divide(entity.getUnitPrice(), 10, RoundingMode.HALF_UP)))
                .build();
        List<TXfSettlementItemEntity> entities = settlementItemService.listByIds(ids);
        entities.forEach(it -> {
            it.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getValue());
            splitType.get(type).accept(it);
        });
        new LambdaUpdateChainWrapper<>(getBaseMapper()).eq(TXfSettlementEntity::getSettlementNo, settlementNo)
                .set(TXfSettlementEntity::getSettlementStatus, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getValue())
                .set(TXfSettlementEntity::getUpdateUser, UserUtil.getUserId())
                .update();
        settlementItemService.updateBatchById(entities);
        new LambdaUpdateChainWrapper<>(getBaseMapper())
                .eq(TXfSettlementEntity::getSettlementNo, settlementNo)
                .set(TXfSettlementEntity::getSettlementStatus, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode())
                .update();
        //操作日志
        new LambdaQueryChainWrapper<>(getBaseMapper()).eq(TXfSettlementEntity::getSettlementNo, settlementNo).oneOpt()
                .ifPresent(it -> operateLogService.add(it.getId(), OperateLogEnum.CONFIRM_SETTLEMENT,
                        TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getDesc(), UserUtil.getUserId(), UserUtil.getUserName()));
        CompletableFuture.runAsync(() -> {
            try {
                log.info("调用拆票方法参数,settlementNo:{},sellerNo:{}", settlementNo, sellerNo);
                long pStart = System.currentTimeMillis();
                preinvoiceService.splitPreInvoice(settlementNo, sellerNo);
                log.info("调用拆票方法耗时:{}", System.currentTimeMillis() - pStart);
            } catch (Exception e) {
                log.error("拆票方法异常,", e);
            }
        });
        return true;
    }

    public Settlement companyMessage(String settlementNo) {
        return new LambdaQueryChainWrapper<>(getBaseMapper()).eq(TXfSettlementEntity::getSettlementNo, settlementNo)
                .oneOpt().map(statementConverter::map).orElseGet(Settlement::new);
    }
}
