package com.xforceplus.wapp.modules.job.command;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.converters.TXfOriginEpdBillEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.model.EPDBillData;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.service.OriginEpdBillService;
import com.xforceplus.wapp.modules.job.service.OriginEpdLogItemService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: wapp-generator
 * @description: 原始EPD单过滤步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 14:01
 **/
@Slf4j
@Component
public class EpdBillFilterCommand implements Command {

    /**
     * 一次从数据库中拉取的最大行数
     */
    private static final int BATCH_COUNT = 1000;
    @Autowired
    private OriginEpdBillService service;
    @Autowired
    private OriginEpdLogItemService itemService;
    @Autowired
    private SpeacialCompanyService speacialCompanyService;
    @Autowired
    private DeductService deductService;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            log.info("开始过滤原始EPD单文件数据入业务表={}", fileName);
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
        long start = System.currentTimeMillis();
        do {
            Page<TXfOriginEpdBillEntity> page = service.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginEpdBillEntity>()
                            .lambda()
                            .eq(TXfOriginEpdBillEntity::getCheckStatus,0)
                            .eq(TXfOriginEpdBillEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginEpdBillEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filter(jobId, page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)));
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryProgress(last);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } while (last < pages);
        log.info("EPD单:{} 数据入业务表花费{}ms", jobId, System.currentTimeMillis() - start);
    }

    /**
     * 过滤转换入库
     *
     * @param jobId
     * @param list
     * @param jobName
     */
    private void filter(int jobId, List<TXfOriginEpdBillEntity> list, String jobName) {
        if(CollectionUtils.isEmpty(list)){
            log.info("EPD单:{} 没有数据入业务表", jobId);
            return;
        }
        List<String> accountList = list.stream().map(TXfOriginEpdBillEntity::getAccount).collect(Collectors.toList());
        Map<String,TXfBlackWhiteCompanyEntity> hitBlackOrWhiteBySapNoMap = speacialCompanyService.hitBlackOrWhiteBySapNo("1", accountList);
        List<DeductBillBaseData> newList = list
                .stream()
                // Document Type 文档类型 只取KO类型
                .filter(v -> Objects.equals("KO", v.getDocumentType()))
                .filter(v -> {
                    if (Objects.isNull(v.getAccount())) {
                        return false;
                    } else {
                        // 白名单供应商
                        TXfBlackWhiteCompanyEntity flag = hitBlackOrWhiteBySapNoMap.get(v.getAccount());
                        if(Objects.isNull(flag)){
                            log.warn("sap编号:{} 未配置白名单不能入库",v.getAccount());
                            return false;
                        }
                        return true;
                    }
                })
                .map(v -> {
                    try {
                        EPDBillData data = TXfOriginEpdBillEntityConvertor.INSTANCE.toEpdBillData(v);
                        if (StringUtils.isNotBlank(v.getAccount())) {
                            TXfBlackWhiteCompanyEntity blackWhiteCompanyEntity = hitBlackOrWhiteBySapNoMap.get(v.getAccount());
                            if (Objects.nonNull(blackWhiteCompanyEntity)) {
                                data.setSellerNo(CommonUtil.fillZero(blackWhiteCompanyEntity.getSupplier6d()));
                            }
                        }
                        data.setBatchNo(jobName);
                        return data;
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .peek(v ->
                        {
                            if (Objects.isNull(v.getTaxRate())) {
                                // 如税率不存在，根据发票号码去《EPD LOG 明细》中找
                                // 根据account和reference
                                String taxRate = getTaxRate(jobId, v.getMemo(), v.getReference());
                                if (NumberUtils.isNumber(taxRate)) {
                                    // LOG明细中的tax rate为整数，将小数点左移两位
                                    v.setTaxRate(new BigDecimal(taxRate).movePointLeft(2));
                                }else{
                                    log.warn("EPD-account:{} taxRate为空或者非法",v.getMemo());
                                }
                            }
                        }
                )
                // 无税率的EPD单是无效单据
                .filter(v -> Objects.nonNull(v.getTaxRate()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            long start = System.currentTimeMillis();
            deductService.receiveData(newList, TXfDeductionBusinessTypeEnum.EPD_BILL);
            log.info("EPD:{} 主信息调用创建业务单接口：{}条数据花费时间{}ms", jobId, newList.size(), System.currentTimeMillis() - start);

        }
    }

    /**
     * @param jobId
     * @param account
     * @param reference
     * @return
     */
    private String getTaxRate(int jobId, String account, String reference) {
        TXfOriginEpdLogItemEntity tXfOriginEpdLogItemEntity = itemService.getOne(
                new QueryWrapper<TXfOriginEpdLogItemEntity>()
                        // 只返回第一行数据，否则getOne可能会报错
                        .select("top 1 *")
                        .lambda()
                        .eq(TXfOriginEpdLogItemEntity::getCheckStatus, 0)
                        .eq(TXfOriginEpdLogItemEntity::getJobId, jobId)
                        // 《EPD LOG 明细》只选Doc. Type = Z1,Satus Message = Successfully updated
                        .eq(TXfOriginEpdLogItemEntity::getDocType, "Z1")
                        .eq(TXfOriginEpdLogItemEntity::getStatusMessage, "Successfully updated")
                        .eq(TXfOriginEpdLogItemEntity::getVendor, account)
                        .eq(TXfOriginEpdLogItemEntity::getReference, reference));
        if (Objects.nonNull(tXfOriginEpdLogItemEntity)) {
            return tXfOriginEpdLogItemEntity.getTaxRate();
        }
        return null;
    }
}
