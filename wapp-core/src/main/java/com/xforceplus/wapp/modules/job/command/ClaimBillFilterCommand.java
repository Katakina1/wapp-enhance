package com.xforceplus.wapp.modules.job.command;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.converters.TXfOriginClaimBillEntityConvertor;
import com.xforceplus.wapp.converters.TXfOriginClaimItemHyperEntityConvertor;
import com.xforceplus.wapp.converters.TXfOriginClaimItemSamsEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.modules.job.service.OriginClaimBillService;
import com.xforceplus.wapp.modules.job.service.OriginClaimItemHyperService;
import com.xforceplus.wapp.modules.job.service.OriginClaimItemSamsService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemHyperEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemSamsEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: wapp-generator
 * @description: 原始索赔单过滤步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 14:01
 **/
@Slf4j
public class ClaimBillFilterCommand implements Command {

    @Autowired
    private BillJobService billJobService;

    @Autowired
    private OriginClaimBillService service;
    @Autowired
    private OriginClaimItemHyperService itemHyperService;
    @Autowired
    private OriginClaimItemSamsService itemSamsService;

    @Autowired
    private DeductService deductService;

    private final static String NEGATIVE_SYMBOL = "-";
    private final static String HYPER = "Hyper";
    private final static String SAMS = "sams";

    /**
     * 一次从数据库中拉取的最大行数
     */
    private static final int BATCH_COUNT = 1000;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
                return true;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                context.put(TXfBillJobEntity.REMARK, e.getMessage());
            } finally {
                saveContext(context);
            }
        } else {
            log.info("跳过数据梳理步骤, 当前任务={}, 状态={}", fileName, jobStatus);
        }
        return false;
    }

    /**
     * 是否是当前步骤的前置状态
     *
     * @param jobStatus
     * @return
     */
    private boolean isValidJobStatus(int jobStatus) {
        return Objects.equals(BillJobStatusEnum.SAVE_COMPLETE.getJobStatus(), jobStatus);
    }

    /**
     * 保存context瞬时状态入库
     *
     * @param context
     * @return
     */
    private boolean saveContext(Context context) {
        TXfBillJobEntity tXfBillJobEntity = BeanUtils.mapToBean(context, TXfBillJobEntity.class);
        return billJobService.updateById(tXfBillJobEntity);
    }

    /**
     * @param jobId
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void process(int jobId, Context context) {
        if (Objects.isNull(context.get(TXfBillJobEntity.JOB_ENTRY_OBJECT))) {
            context.put(TXfBillJobEntity.JOB_ENTRY_OBJECT, BillJobEntryObjectEnum.BILL.getCode());
        }
        Object jobEntryObject = context.get(TXfBillJobEntity.JOB_ENTRY_OBJECT);
        BillJobEntryObjectEnum jeo = BillJobEntryObjectEnum.fromCode(Integer.parseInt(String.valueOf(jobEntryObject)));
        assert jeo != null;
        switch (jeo) {
            case BILL:
                processBill(jobId, context);
                processItemHyper(jobId, context);
                processItemSams(jobId, context);
                break;
            case ITEM:
                processItemHyper(jobId, context);
                processItemSams(jobId, context);
                break;
            case ITEM_SAMS:
                processItemSams(jobId, context);
                break;
            default:
                log.error("未知的job_entry_object={}", jobEntryObject);
                context.put(TXfBillJobEntity.REMARK, String.format("未知的job_entry_object=%s", String.valueOf(jobEntryObject)));
        }
    }

    /**
     * @param jobId
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void processBill(int jobId, Context context) {
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        // 先获取分页总数
        long pages;
        do {
            Page<TXfOriginClaimBillEntity> page = service.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginClaimBillEntity>()
                            .lambda()
                            .eq(TXfOriginClaimBillEntity::getJobId, jobId)
                            .orderBy(true, true, TXfOriginClaimBillEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filterBill(page.getRecords());
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
        } while (last < pages);
        // 全部处理完成后清空处理进度
        context.put(TXfBillJobEntity.JOB_ENTRY_OBJECT, BillJobEntryObjectEnum.ITEM.getCode());
        context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, 0);
    }

    /**
     * @param jobId
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void processItemHyper(int jobId, Context context) {
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        // 先获取分页总数
        long pages;
        List<String> negativeExchangeNos = obtainNegativeExchangeNos(jobId, HYPER);
        do {
            Page<TXfOriginClaimItemHyperEntity> page = itemHyperService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginClaimItemHyperEntity>()
                            .lambda()
                            .eq(TXfOriginClaimItemHyperEntity::getJobId, jobId)
                            .orderBy(true, true, TXfOriginClaimItemHyperEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filterItemHyper(negativeExchangeNos, page.getRecords());
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
        } while (last < pages);
        // 全部处理完成后清空处理进度
        context.put(TXfBillJobEntity.JOB_ENTRY_OBJECT, BillJobEntryObjectEnum.ITEM_SAMS.getCode());
        context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, 0);
    }

    /**
     * @param jobId
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void processItemSams(int jobId, Context context) {
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        // 先获取分页总数
        long pages;
        List<String> negativeExchangeNos = obtainNegativeExchangeNos(jobId, SAMS);
        do {
            Page<TXfOriginClaimItemSamsEntity> page = itemSamsService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginClaimItemSamsEntity>()
                            .lambda()
                            .eq(TXfOriginClaimItemSamsEntity::getJobId, jobId)
                            .orderBy(true, true, TXfOriginClaimItemSamsEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filterItemSams(negativeExchangeNos, page.getRecords());
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
        } while (last < pages);
    }

    /**
     * 过滤转换入库
     *
     * @param list
     */
    private void filterBill(List<TXfOriginClaimBillEntity> list) {
        List<DeductBillBaseData> newList = list
                .stream()
                // 索赔单不含税金额为负数
                .filter(v -> v.getCostAmount().startsWith(NEGATIVE_SYMBOL))
                .map(TXfOriginClaimBillEntityConvertor.INSTANCE::toClaimBillData)
                .collect(Collectors.toList());
        deductService.receiveData(newList, null, XFDeductionBusinessTypeEnum.CLAIM_BILL);
    }

    /**
     * 过滤转换入库
     *
     * @param negativeExchangeNos
     * @param list
     */
    private void filterItemHyper(List<String> negativeExchangeNos, List<TXfOriginClaimItemHyperEntity> list) {
        List<ClaimBillItemData> newList = list
                .stream()
                // 索赔单不含税金额为负数
                .filter(v -> negativeExchangeNos.contains(v.getClaimNbr()))
                // 索赔单明细不含税金额为负数
                .filter(v -> v.getLineCost().startsWith(NEGATIVE_SYMBOL))
                .map(TXfOriginClaimItemHyperEntityConvertor.INSTANCE::toClaimBillItemData)
                .collect(Collectors.toList());
        deductService.receiveItemData(newList, null);
    }

    /**
     * 过滤转换入库
     *
     * @param negativeExchangeNos
     * @param list
     */
    private void filterItemSams(List<String> negativeExchangeNos, List<TXfOriginClaimItemSamsEntity> list) {
        List<ClaimBillItemData> newList = list
                .stream()
                // 索赔单不含税金额为负数
                .filter(v -> negativeExchangeNos.contains(v.getClaimNumber()))
                // 索赔单明细不含税金额为负数
                .filter(v -> v.getShipCost().startsWith(NEGATIVE_SYMBOL))
                .map(TXfOriginClaimItemSamsEntityConvertor.INSTANCE::toClaimBillItemData)
                .collect(Collectors.toList());
        deductService.receiveItemData(newList, null);
    }

    /**
     * 获取负数的索赔号列表
     *
     * @param jobId
     * @param storeType
     * @return
     */
    private List<String> obtainNegativeExchangeNos(int jobId, String storeType) {
        return service.list(
                new QueryWrapper<TXfOriginClaimBillEntity>()
                        .lambda()
                        .eq(TXfOriginClaimBillEntity::getJobId, jobId)
                        .eq(TXfOriginClaimBillEntity::getStoreType, storeType)
                        .likeLeft(TXfOriginClaimBillEntity::getAmountWithTax, NEGATIVE_SYMBOL)
        )
                .stream()
                .map(TXfOriginClaimBillEntity::getExchangeNo)
                .collect(Collectors.toList());
    }
}
