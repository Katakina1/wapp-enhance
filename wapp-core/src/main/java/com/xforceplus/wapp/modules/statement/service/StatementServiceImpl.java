package com.xforceplus.wapp.modules.statement.service;


import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.xforceplus.wapp.enums.settlement.SettlementApproveStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.BillRefQueryHistoryDataService;
import com.xforceplus.wapp.modules.deduct.service.BillRefQueryService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.enums.TXfAmountSplitRuleEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementItemFlagEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.billdeduct.converters.BillDeductConverter;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductItemServiceImpl;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductServiceImpl;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.converters.PreInvoiceConverter;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceItemDaoService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.settlement.converters.SettlementItemConverter;
import com.xforceplus.wapp.modules.settlement.service.SettlementItemServiceImpl;
import com.xforceplus.wapp.modules.statement.converters.StatementConverter;
import com.xforceplus.wapp.modules.statement.dto.StatementExportDto;
import com.xforceplus.wapp.modules.statement.dto.StatementRequest;
import com.xforceplus.wapp.modules.statement.models.BaseInformation;
import com.xforceplus.wapp.modules.statement.models.Claim;
import com.xforceplus.wapp.modules.statement.models.ClaimConfirm;
import com.xforceplus.wapp.modules.statement.models.ConfirmItem;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.modules.statement.models.Settlement;
import com.xforceplus.wapp.modules.statement.models.SettlementCount;
import com.xforceplus.wapp.modules.statement.models.VendorExportAgreementStatementModel;
import com.xforceplus.wapp.modules.statement.models.VendorExportClaimStatementModel;
import com.xforceplus.wapp.modules.statement.models.VendorExportEPDStatementModel;
import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.threadpool.ThreadPoolManager;
import com.xforceplus.wapp.threadpool.callable.ExportStatementCallable;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

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
    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExportCommonService exportCommonService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private BillRefQueryService billRefQueryService;
    @Autowired
    private BillRefQueryHistoryDataService billRefQueryHistoryDataService;

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

    public Tuple2<List<Settlement>, Page<?>> page(UserEntity user, Long current, Long size, @NonNull Integer type, Integer settlementStatus, String settlementNo, String purchaserNo, String invoiceType, String businessNo, String taxRate) {
        log.info("结算单分页查询,入参,type:{},settlementStatus:{},settlementNo:{},purchaserNo:{},invoiceType:{},businessNo:{},taxRate:{},分页数据,current:{},size:{}",
                type, settlementStatus, settlementNo, purchaserNo, invoiceType, businessNo, taxRate, current, size);
        LambdaQueryChainWrapper<TXfSettlementEntity> wrapper = new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(TXfSettlementEntity::getSettlementType, type);
//                .eq(TXfSettlementEntity::getSellerNo, UserUtil.getUser().getUsercode());
        if(user != null) {//2022-07-20新增
        	wrapper.eq(TXfSettlementEntity::getSellerNo, user.getUsercode());
        }
        if (Objects.nonNull(settlementStatus)) {
            wrapper.eq(TXfSettlementEntity::getSettlementStatus, settlementStatus);
        } else {
            Set<Integer> collect = Arrays.stream(TXfSettlementStatusEnum.values())
                    .filter(it -> it.getCode() < 8 || it.getValue() == 10)
                    .map(TXfSettlementStatusEnum::getValue).collect(Collectors.toSet());
            wrapper.in(TXfSettlementEntity::getSettlementStatus, collect);
        }
        if (StringUtils.isNotBlank(settlementNo)) {
            wrapper.likeRight(TXfSettlementEntity::getSettlementNo, settlementNo);
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
            List<TXfBillDeductEntity> deductEntityList = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo,TXfBillDeductEntity::getBusinessNo).likeRight(TXfBillDeductEntity::getBusinessNo, businessNo)
                    .list().stream().collect(Collectors.toList());
            List<String> settlementNoList = deductEntityList.stream().map(TXfBillDeductEntity::getRefSettlementNo).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(settlementNoList)) {
                return Tuple.of(Lists.newArrayList(), new Page<>(current, size));
            }
            wrapper.in(TXfSettlementEntity::getSettlementNo, settlementNoList);
        }
        Page<TXfSettlementEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("结算单分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        List<Settlement> list = statementConverter.map(page.getRecords());
        List<String> settlementNoList = list.stream().map(Settlement::getSettlementNo).distinct().collect(Collectors.toList());
        Map<String,Set<String>> deductSettlementNo2BusinessNoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(settlementNoList)) {
            List<TXfBillDeductEntity> deductEntityList = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo, TXfBillDeductEntity::getBusinessNo)
                    .in(TXfBillDeductEntity::getRefSettlementNo, settlementNoList)
                    .list().stream().collect(Collectors.toList());
            deductSettlementNo2BusinessNoMap = deductEntityList.stream().collect(Collectors.groupingBy(TXfBillDeductEntity::getRefSettlementNo,
                    Collectors.mapping(TXfBillDeductEntity::getBusinessNo, Collectors.toSet())));
        }
		log.info("结算单关联的业务单 {}", deductSettlementNo2BusinessNoMap);
        Map<String, Set<String>> finalDeductSettlementNo2BusinessNoMap = deductSettlementNo2BusinessNoMap;
        list.forEach(settlement -> {
            Set<String> businessNoSet = finalDeductSettlementNo2BusinessNoMap.get(settlement.getSettlementNo());
            if(CollectionUtils.isNotEmpty(businessNoSet)) {
                settlement.setBusinessNo(Joiner.on(",").join(businessNoSet));
            }
        });
        return Tuple.of(list, page);
    }



    public List<SettlementCount> count(UserEntity user, @NonNull Integer type, String settlementNo, String purchaserNo, String invoiceType,
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
                .groupBy(TXfSettlementEntity::getSettlementStatus);
        if(user != null) {//2022-07-20新增
        	lambda.eq(TXfSettlementEntity::getSellerNo, UserUtil.getUser().getUsercode());
        }
        if (StringUtils.isNotBlank(settlementNo)) {
            lambda.likeRight(TXfSettlementEntity::getSettlementNo, settlementNo);
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
                    .select(TXfBillDeductEntity::getRefSettlementNo).likeRight(TXfBillDeductEntity::getBusinessNo, businessNo)
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
        List<PreInvoice> preInvoices = preInvoiceConverter.map(page.getRecords());
        // 填充红字信息(主要是申请状态)
        billRefQueryService.fullPreInvoiceRedNotification(preInvoices);

        // 历史数据填充红字信息(主要是申请状态)
        billRefQueryHistoryDataService.fullPreInvoiceRedNotification(preInvoices);
        log.debug("待开票列表查询,填充红字信息后的分页数据:{}", preInvoices);
        return Tuple.of(preInvoices, page);
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
        Map<Long, List<TXfSettlementItemEntity>> itemMap = items.stream()
                .collect(Collectors.groupingBy(TXfSettlementItemEntity::getThridId));
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
                        .flatMap(List::stream).map(itemMap::get).flatMap(Collection::stream).collect(Collectors.toSet())))
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
        if (CollectionUtils.isNotEmpty(ids)) {
          val splitType = ImmutableMap
                  .<TXfAmountSplitRuleEnum, Consumer<TXfSettlementItemEntity>>builder()
                  .put(TXfAmountSplitRuleEnum.SplitNon, entity -> {})
                  .put(TXfAmountSplitRuleEnum.SplitPrice, entity -> entity.setUnitPrice(entity.getAmountWithoutTax().divide(entity.getQuantity(), 10, RoundingMode.HALF_UP)))
                  .put(TXfAmountSplitRuleEnum.SplitQuantity, entity -> entity.setQuantity(entity.getAmountWithoutTax().divide(entity.getUnitPrice(), 10, RoundingMode.HALF_UP)))
                  .put(TXfAmountSplitRuleEnum.SplitPriceAndQuantity, entity -> { entity.setQuantity(null);entity.setUnitPrice(null);
                  }).build();
          List<TXfSettlementItemEntity> entities = settlementItemService.listByIds(ids);
          entities.forEach(it -> {
            it.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getValue());
            splitType.get(type).accept(it);
          });
          settlementItemService.updateBatchById(entities);
        }

/*        new LambdaUpdateChainWrapper<>(getBaseMapper()).eq(TXfSettlementEntity::getSettlementNo, settlementNo)
                .set(TXfSettlementEntity::getSettlementStatus, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getValue())
                .set(TXfSettlementEntity::getUpdateUser, UserUtil.getUserId())
                .update();*/

        new LambdaUpdateChainWrapper<>(getBaseMapper())
                .eq(TXfSettlementEntity::getSettlementNo, settlementNo)
                .set(TXfSettlementEntity::getSettlementStatus, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode())
                .set(TXfSettlementEntity::getUpdateUser, UserUtil.getUserId())
                .update();
        //操作日志
        new LambdaQueryChainWrapper<>(getBaseMapper()).eq(TXfSettlementEntity::getSettlementNo, settlementNo).oneOpt()
                .ifPresent(it -> {
                    operateLogService.add(it.getId(), OperateLogEnum.CONFIRM_SETTLEMENT,
                            TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getDesc(), "",UserUtil.getUserId(), UserUtil.getUserName());
                    operateLogService.addDeductLog(it.getSettlementNo(), it.getBusinessType(), TXfSettlementStatusEnum.getTXfSettlementStatusEnum(it.getSettlementStatus()), OperateLogEnum.SETTLEMENT_CONFIRM_DEDUCT, "", UserUtil.getUserId(), UserUtil.getUserName());
                });
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



    public boolean export(StatementRequest request) {
        final Long userId = UserUtil.getUserId();
        StatementExportDto dto = new StatementExportDto();
        dto.setRequest(request);
        dto.setUserId(userId);
        dto.setUserEntity(UserUtil.getUser());
        dto.setLoginName(UserUtil.getLoginName());
        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        excelExportlogEntity.setCreateDate(new Date());
        //这里的userAccount是userid
        excelExportlogEntity.setUserAccount(dto.getUserId().toString());
        excelExportlogEntity.setUserName(dto.getLoginName());
        excelExportlogEntity.setConditions(JSON.toJSONString(request));
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);
        boolean count = this.excelExportLogService.save(excelExportlogEntity);
        dto.setLogId(excelExportlogEntity.getId());
        ThreadPoolManager.submitCustomL1(new ExportStatementCallable(this, dto));
        return count;
    }

	public boolean doExport(StatementExportDto exportDto) {
		boolean flag = true;
		StatementRequest request = exportDto.getRequest();
		// 这里的userAccount是userid
		final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
		excelExportlogEntity.setEndDate(new Date());
		excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
		TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
		// 这里的userAccount是userName
		messagecontrolEntity.setUserAccount(exportDto.getLoginName());
		messagecontrolEntity.setContent(getSuccContent());
		// 主信息
		Tuple2<List<Settlement>, Page<?>> queryDeductListResponse = page(exportDto.getUserEntity(), request.getCurrent(), request.getSize(), request.getType(),
				request.getSettlementStatus(), request.getSettlementNo(), request.getPurchaserNo(), request.getInvoiceType(), request.getBusinessNo(), request.getTaxRate());
		log.info("doExport statementPage:{}", JSON.toJSONString(queryDeductListResponse));
		if (queryDeductListResponse._1 == null ||  queryDeductListResponse._1.size() == 0) {
			log.info("结算单导出--未查到数据");
			return false;
		}
		ServiceTypeEnum typeEnum = ValueEnum.getEnumByValue(ServiceTypeEnum.class, request.getType()).orElseThrow(() -> new RuntimeException("超期配置类型不正确"));
		final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), typeEnum.getMessage() + "结算单");
		ExcelWriter excelWriter;
		ByteArrayInputStream in = null;
		String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			excelWriter = EasyExcel.write(out).excelType(ExcelTypeEnum.XLSX).build();
			WriteSheet writeSheet = EasyExcel.writerSheet(0, typeEnum.getMessage() + "结算单信息").build();
			// 创建一个sheet
            if(ServiceTypeEnum.CLAIM.equals(typeEnum)){
            	List<VendorExportClaimStatementModel> exportList = new LinkedList<>();
            	queryDeductListResponse._1.forEach(item->{
            		VendorExportClaimStatementModel billModel = new VendorExportClaimStatementModel();
            		BeanUtil.copyProperties(item, billModel);
					billModel.setInvoiceType(InvoiceTypeEnum.getByCodeValue(item.getInvoiceType()).getResultTip());
					billModel.setSettlementStatus(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(item.getSettlementStatus()).getDesc());
					billModel.setNum(exportList.size() + 1);
            		exportList.add(billModel);
            	});
                writeSheet.setClazz(VendorExportClaimStatementModel.class);
                excelWriter.write(exportList, writeSheet);
            }else if(ServiceTypeEnum.AGREEMENT.equals(typeEnum)){
            	List<VendorExportAgreementStatementModel> exportList = new LinkedList<>();
            	queryDeductListResponse._1.forEach(item->{
            		VendorExportAgreementStatementModel billModel = new VendorExportAgreementStatementModel();
            		BeanUtil.copyProperties(item, billModel);
					billModel.setSettlementStatus(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(item.getSettlementStatus()).getDesc());
					billModel.setNum(exportList.size() + 1);
            		exportList.add(billModel);
            	});
                writeSheet.setClazz(VendorExportAgreementStatementModel.class);
                excelWriter.write(exportList, writeSheet);
            }else{
            	List<VendorExportEPDStatementModel> exportList = new LinkedList<>();
            	queryDeductListResponse._1.forEach(item->{
            		VendorExportEPDStatementModel billModel = new VendorExportEPDStatementModel();
            		BeanUtil.copyProperties(item, billModel);
					billModel.setSettlementStatus(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(item.getSettlementStatus()).getDesc());
					billModel.setNum(exportList.size() + 1);
            		exportList.add(billModel);
            	});
                writeSheet.setClazz(VendorExportEPDStatementModel.class);
                excelWriter.write(exportList, writeSheet);
            }
			excelWriter.finish();
			// 推送sftp
			String ftpFilePath = ftpPath + "/" + excelFileName;
			in = new ByteArrayInputStream(out.toByteArray());
			ftpUtilService.uploadFile(ftpPath, excelFileName, in);
			messagecontrolEntity.setUrl(exportCommonService.getUrl(excelExportlogEntity.getId()));
			excelExportlogEntity.setFilepath(ftpFilePath);
			messagecontrolEntity.setTitle(typeEnum.getMessage() + "结算单导出成功");
		} catch (Exception e) {
			log.error(typeEnum.getMessage() + "结算单导出失败:" + e.getMessage(), e);
			excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
			excelExportlogEntity.setErrmsg(e.getMessage());
			messagecontrolEntity.setTitle(typeEnum.getMessage() + "结算单导出失败");
			messagecontrolEntity.setContent(exportCommonService.getFailContent(e.getMessage()));
			flag = false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
			excelExportLogService.updateById(excelExportlogEntity);
			commonMessageService.sendMessage(messagecontrolEntity);
		}
		return flag;
	}

	public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }

    public boolean updateSettlementStatus(Long settlementId, TXfSettlementStatusEnum statusEnum) {
        return updateSettlementStatus(settlementId, statusEnum, null, null, null, null);
    }

    public boolean updateSettlementStatus(Long settlementId, TXfSettlementStatusEnum statusEnum, SettlementApproveStatusEnum approveStatusEnum, String approveRemark) {
        return updateSettlementStatus(settlementId, statusEnum, null, null, approveStatusEnum, approveRemark);
    }

    /**
     * 修改结算单状态 - 审核
     * @param settlementId 结算单id
     * @param statusEnum 状态枚举
     * @param revertRemark 撤销原因
     * @param approveTypeEnum 撤销提交类型
     * @param approveStatusEnum 审核结果
     * @param approveRemark 审核备注
     */
    public boolean updateSettlementStatus(Long settlementId, TXfSettlementStatusEnum statusEnum, String revertRemark, SettlementApproveTypeEnum approveTypeEnum, SettlementApproveStatusEnum approveStatusEnum, String approveRemark) {

        LambdaUpdateChainWrapper<TXfSettlementEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());
        updateChainWrapper.set(TXfSettlementEntity::getSettlementStatus, statusEnum.getCode());
        updateChainWrapper.set(TXfSettlementEntity::getUpdateTime, new Date());
        updateChainWrapper.set(TXfSettlementEntity::getUpdateUser, UserUtil.getUserId());
        updateChainWrapper.set(StringUtils.isNotEmpty(revertRemark), TXfSettlementEntity::getRevertRemark, revertRemark);
        updateChainWrapper.set(Objects.nonNull(approveTypeEnum), TXfSettlementEntity::getApproveType, Optional.ofNullable(approveTypeEnum).map(SettlementApproveTypeEnum::getCode).orElse(SettlementApproveTypeEnum.DEFAULT.getCode()));
        updateChainWrapper.set(StringUtils.isNotEmpty(approveRemark), TXfSettlementEntity::getApproveRemark, approveRemark);
        Optional.ofNullable(approveStatusEnum)
                .ifPresent(approveEnum -> {
                    updateChainWrapper.set(TXfSettlementEntity::getApproveStatus, approveEnum.getCode());
                    if (SettlementApproveStatusEnum.APPROVING == approveEnum) {
                        updateChainWrapper.set(TXfSettlementEntity::getApproveRequestTime, new Date());
                        updateChainWrapper.set(TXfSettlementEntity::getApproveTime, null);
                    } else if (SettlementApproveStatusEnum.isApprove(approveEnum.getCode())) {
                        updateChainWrapper.set(TXfSettlementEntity::getApproveTime, new Date());
                    }
                });

        updateChainWrapper.eq(TXfSettlementEntity::getId, settlementId);
        return updateChainWrapper.update();
    }
}

