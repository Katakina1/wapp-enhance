package com.xforceplus.wapp.modules.job.command;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.converters.TXfOriginClaimBillEntityConvertor;
import com.xforceplus.wapp.converters.TXfOriginClaimItemHyperEntityConvertor;
import com.xforceplus.wapp.converters.TXfOriginClaimItemSamsEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class ClaimBillFilterCommand implements Command {

    private static final String NEGATIVE_SYMBOL = "-";
    private static final String HYPER = "Hyper";
    private static final String SAMS = "sams";
    /**
     * 一次从数据库中拉取的最大行数
     */
    private static final int BATCH_COUNT = 1000;
    @Autowired
    private OriginClaimBillService service;
    @Autowired
    private OriginClaimItemHyperService itemHyperService;
    @Autowired
    private OriginClaimItemSamsService itemSamsService;
    @Autowired
    private DeductService deductService;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            log.info("开始过滤原始索赔单文件数据入业务表={}", fileName);
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
                return true;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                context.put(TXfBillJobEntity.REMARK, e.getMessage());
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
                context.put(TXfBillJobEntity.REMARK, String.format("未知的job_entry_object=%s", jobEntryObject));
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
                            .orderByAsc(TXfOriginClaimBillEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filterBill(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)));
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
        do {
            Page<TXfOriginClaimItemHyperEntity> page = itemHyperService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginClaimItemHyperEntity>()
                            .lambda()
                            .eq(TXfOriginClaimItemHyperEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginClaimItemHyperEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filterItemHyper(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)));
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
        do {
            Page<TXfOriginClaimItemSamsEntity> page = itemSamsService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginClaimItemSamsEntity>()
                            .lambda()
                            .eq(TXfOriginClaimItemSamsEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginClaimItemSamsEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filterItemSams(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)));
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
        } while (last < pages);
    }

    /**
     * 过滤转换入库
     *
     * @param list
     * @param jobName
     */
    private void filterBill(List<TXfOriginClaimBillEntity> list, String jobName) {
        List<DeductBillBaseData> newList = list
                .stream()
                // 索赔单不含税金额为正数
                .filter(v -> {
                    if (Objects.isNull(v.getCostAmount())) {
                        return false;
                    } else {
                        return !v.getCostAmount().startsWith(NEGATIVE_SYMBOL);
                    }
                })
                .map(v -> {
                    try {
                        DeductBillBaseData data = TXfOriginClaimBillEntityConvertor.INSTANCE.toClaimBillData(v);
                        data.setBatchNo(jobName);
                        return data;
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            deductService.receiveData(newList, TXfDeductionBusinessTypeEnum.CLAIM_BILL);
        }
    }

    /**
     * 过滤转换入库
     *
     * @param list
     * @param jobName
     */
    private void filterItemHyper(List<TXfOriginClaimItemHyperEntity> list, String jobName) {
        List<ClaimBillItemData> newList = list
                .stream()
                // 索赔单明细不含税金额为负数
                // .filter(v -> v.getLineCost().startsWith(NEGATIVE_SYMBOL))
                .map(v -> {
                            try {
                                return TXfOriginClaimItemHyperEntityConvertor.INSTANCE.toClaimBillItemData(v);
                            } catch (Exception e) {
                                log.warn(e.getMessage(), e);
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            deductService.receiveItemData(newList, null);
        }
    }

    /**
     * 过滤转换入库
     *
     * @param list
     * @param jobName
     */
    private void filterItemSams(List<TXfOriginClaimItemSamsEntity> list, String jobName) {
        List<ClaimBillItemData> newList = list
                .stream()
                // 索赔单明细不含税金额为负数
                // .filter(v -> v.getShipCost().startsWith(NEGATIVE_SYMBOL))
                .map(v -> {
                            try {
                                return TXfOriginClaimItemSamsEntityConvertor.INSTANCE.toClaimBillItemData(v);
                            } catch (Exception e) {
                                log.warn(e.getMessage(), e);
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            deductService.receiveItemData(newList, null);
        }
    }

}
