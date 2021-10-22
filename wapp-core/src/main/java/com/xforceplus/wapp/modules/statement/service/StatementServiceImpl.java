package com.xforceplus.wapp.modules.statement.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.billdeduct.converters.BillDeductConverter;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductItemServiceImpl;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductServiceImpl;
import com.xforceplus.wapp.modules.preinvoice.converters.PreInvoiceConverter;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceItemDaoService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.converters.SettlementItemConverter;
import com.xforceplus.wapp.modules.settlement.service.SettlementItemServiceImpl;
import com.xforceplus.wapp.modules.statement.converters.StatementConverter;
import com.xforceplus.wapp.modules.statement.models.*;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.*;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class StatementServiceImpl extends ServiceImpl<TXfSettlementDao, TXfSettlementEntity> {
    private final StatementConverter statementConverter;
    private final TXfBillDeductExtDao billDeductExtDao;
    private final PreinvoiceService preinvoiceService;
    private final PreInvoiceConverter preInvoiceConverter;
    private final SettlementItemServiceImpl settlementItemService;
    private final SettlementItemConverter settlementItemConverter;
    private final BillDeductServiceImpl billDeductService;
    private final BillDeductConverter billDeductConverter;
    private final BillDeductItemServiceImpl billDeductItemService;
    private final PreInvoiceItemDaoService preInvoiceItemDaoService;

    public StatementServiceImpl(StatementConverter statementConverter, TXfBillDeductExtDao billDeductExtDao, PreinvoiceService preinvoiceService, PreInvoiceConverter preInvoiceConverter, SettlementItemServiceImpl settlementItemService, SettlementItemConverter settlementItemConverter, BillDeductServiceImpl billDeductService, BillDeductConverter billDeductConverter, BillDeductItemServiceImpl billDeductItemService, PreInvoiceItemDaoService preInvoiceItemDaoService) {
        this.statementConverter = statementConverter;
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
                .eq(TXfSettlementEntity::getSettlementType, type);
        if (Objects.nonNull(settlementStatus)) {
            wrapper.eq(TXfSettlementEntity::getSettlementStatus, settlementStatus);
        } else {
            Set<Integer> collect = Arrays.stream(TXfSettlementStatusEnum.values())
                    .filter(it -> it.getCode() < 8)
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
                .filter(it -> it.getValue() < 8)
                .map(it -> SettlementCount.builder().status(it.getCode().toString()).total(0).build())
                .collect(Collectors.toMap(SettlementCount::getStatus, Function.identity()));
        QueryWrapper<TXfSettlementEntity> wrapper = new QueryWrapper<>();
        val lambda = wrapper.lambda();
        lambda.eq(TXfSettlementEntity::getSettlementType, type)
                .in(TXfSettlementEntity::getSettlementStatus, tabMap.keySet())
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
        log.debug("预制发票详情信息:{}", "");
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
    public List<? extends BaseConfirm> claimConfirmItem(@NonNull String settlementNo) {
        val items = new LambdaQueryChainWrapper<>(settlementItemService.getBaseMapper())
                .eq(TXfSettlementItemEntity::getSettlementNo, settlementNo)
                .eq(TXfSettlementItemEntity::getItemFlag, 2).list();
        if (CollectionUtils.isEmpty(items)) {
            return Lists.newArrayList();
        }
        Map<Long, TXfSettlementItemEntity> itemMap = items.stream().filter(it -> Objects.nonNull(it.getThridId()))
                .collect(Collectors.toMap(TXfSettlementItemEntity::getThridId, Function.identity(), (o, n) -> o));
        if (itemMap.isEmpty()) {
            return Lists.newArrayList();
        }
        Map<Long, List<Long>> deIdAndDeItemIdsMap = billDeductItemService.listByRefItemIds(itemMap.keySet())
                .stream().collect(Collectors.groupingBy(TXfBillDeductItemRefEntity::getDeductId,
                        Collectors.mapping(TXfBillDeductItemRefEntity::getDeductItemId, Collectors.toList())));
        if (deIdAndDeItemIdsMap.isEmpty()) {
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

    public List<? extends BaseConfirm> confirmItem(@NonNull String settlementNo) {
        val items = new LambdaQueryChainWrapper<>(settlementItemService.getBaseMapper())
                .eq(TXfSettlementItemEntity::getSettlementNo, settlementNo)
                .eq(TXfSettlementItemEntity::getItemFlag, 2).list();
        return settlementItemConverter.mapItem(items);
    }
}
