package com.xforceplus.wapp.modules.job.command;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.converters.TXfOriginAgreementBillEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.service.OriginSapFbl5nService;
import com.xforceplus.wapp.modules.job.service.OriginSapZarrService;
import com.xforceplus.wapp.repository.dao.TXfOriginAgreementMergeDao;
import com.xforceplus.wapp.repository.dao.TXfOriginSapFbl5nDao;
import com.xforceplus.wapp.repository.dao.TXfOriginSapZarrDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementMergeEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapFbl5nEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapZarrEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: wapp-generator
 * @description: 原始协议单过滤步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 14:01
 **/
@Slf4j
@Component
public class AgreementFbl5nMergeCommand implements Command {

    /**
     * 一次从数据库中拉取的最大行数
     */
    private static final int BATCH_COUNT = 1000;
    @Autowired
    private SpeacialCompanyService speacialCompanyService;
    @Autowired
    private DeductService deductService;
    @Autowired
    private OriginSapFbl5nService originSapFbl5nService;
    @Autowired
    private OriginSapZarrService originSapZarrService;
    @Autowired
    private TXfOriginSapFbl5nDao tXfOriginSapFbl5nDao;
    @Autowired
    private TXfOriginSapZarrDao tXfOriginSapZarrDao;
    @Autowired
    private TXfOriginAgreementMergeDao tXfOriginAgreementMergeDao;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            log.info("开始过滤原始协议单Fbl5n数据入业务表={}", fileName);
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
            } catch (Exception e) {
                e.printStackTrace();
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
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_FBL5N_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_FBL5N_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        if (last == 0) {
            //如果第一页需要特殊处理把数据此次job数据清理一下（可能之前执行到一半重启，数据已经完成一半但是last没有更新）
            deleteOriginAgreementMerge(jobId);
        }
        // 先获取分页总数
        long pages;
        do {
            Page<TXfOriginSapFbl5nEntity> page = originSapFbl5nService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginSapFbl5nEntity>()
                            .lambda()
                            .eq(TXfOriginSapFbl5nEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginSapFbl5nEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filter(page.getRecords());
            context.put(TXfBillJobEntity.JOB_ENTRY_FBL5N_PROGRESS, last);
        } while (last < pages);
    }

    private void filter(List<TXfOriginSapFbl5nEntity> list) {
        List<String> companyCodeList = Stream.of("D073").collect(Collectors.toList());
        List<String> docTypeList = Stream.of("1D", "RV", "DA").collect(Collectors.toList());
        List<String> reasonCodeList = Stream.of("511", "527", "206", "317").collect(Collectors.toList());
        list.parallelStream()
                .map(fbl5n -> {
                    try {
                        TXfOriginAgreementMergeEntity tXfOriginAgreementMergeTmpEntity = new TXfOriginAgreementMergeEntity();
                        tXfOriginAgreementMergeTmpEntity.setJobId(fbl5n.getJobId());
                        tXfOriginAgreementMergeTmpEntity.setCustomerNo(fbl5n.getAccount());
                        // TODO fbl5n 供应商6D 供应商名称???
                        tXfOriginAgreementMergeTmpEntity.setCustomerName(getCustomer(fbl5n.getJobId(), fbl5n.getReference()));
                        tXfOriginAgreementMergeTmpEntity.setMemo(getMemo6D(fbl5n.getJobId(), fbl5n.getReference()));
                        tXfOriginAgreementMergeTmpEntity.setCompanyCode(fbl5n.getCompanyCode());
                        String amountInDocCurr = fbl5n.getAmountInDocCurr().replace(",", "");
                        if (StringUtils.isNotBlank(amountInDocCurr)) {
                            tXfOriginAgreementMergeTmpEntity.setWithAmount(new BigDecimal(amountInDocCurr));
                        }
                        tXfOriginAgreementMergeTmpEntity.setReasonCode(fbl5n.getReasonCode().replace(" ", ""));
                        if (StringUtils.isBlank(tXfOriginAgreementMergeTmpEntity.getReasonCode())) {
                            tXfOriginAgreementMergeTmpEntity.setReasonCode(getReasonCode(fbl5n.getJobId(), fbl5n.getReference()));
                        }
                        tXfOriginAgreementMergeTmpEntity.setReference(fbl5n.getReference());
                        tXfOriginAgreementMergeTmpEntity.setTaxCode(fbl5n.getTaxCode());
                        BigDecimal taxRate = TXfOriginAgreementBillEntityConvertor.TAX_CODE_TRANSLATOR.get(fbl5n.getTaxCode());
                        tXfOriginAgreementMergeTmpEntity.setTaxRate(taxRate);
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
                        if (fbl5n.getClearingDate() != null) {
                            tXfOriginAgreementMergeTmpEntity.setDeductDate(fmt.parse(fbl5n.getClearingDate()));
                        }
                        tXfOriginAgreementMergeTmpEntity.setDocumentType(fbl5n.getDocumentType());
                        tXfOriginAgreementMergeTmpEntity.setDocumentNumber(fbl5n.getDocumentNumber());
                        if (fbl5n.getPostingDate() != null) {
                            tXfOriginAgreementMergeTmpEntity.setPostDate(fmt.parse(fbl5n.getPostingDate()));
                        }
                        if (tXfOriginAgreementMergeTmpEntity.getWithAmount() != null &&
                                tXfOriginAgreementMergeTmpEntity.getTaxRate() != null) {
                            BigDecimal taxAmount = tXfOriginAgreementMergeTmpEntity.getWithAmount()
                                    .divide(tXfOriginAgreementMergeTmpEntity.getTaxRate().add(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP)
                                    .multiply(tXfOriginAgreementMergeTmpEntity.getTaxRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                            tXfOriginAgreementMergeTmpEntity.setTaxAmount(taxAmount);
                        }
                        tXfOriginAgreementMergeTmpEntity.setSource(1);
                        tXfOriginAgreementMergeTmpEntity.setCreateTime(new Date());
                        tXfOriginAgreementMergeTmpEntity.setUpdateTime(new Date());
                        return tXfOriginAgreementMergeTmpEntity;
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                    return null;
                })
                .filter(mergeTmpEntity -> {
                    try {
                        if (mergeTmpEntity == null) {
                            return false;
                        }
                        if (!companyCodeList.contains(mergeTmpEntity.getCompanyCode())) {
                            return false;
                        }
                        if (mergeTmpEntity.getWithAmount().compareTo(BigDecimal.ZERO) <= 0) {
                            return false;
                        }
                        if (!docTypeList.contains(mergeTmpEntity.getDocumentType())) {
                            return false;
                        }
                        if (StringUtils.isNotBlank(mergeTmpEntity.getReasonCode())) {
                            if (!reasonCodeList.contains(mergeTmpEntity.getReasonCode())) {
                                return false;
                            }
                        }
                        return true;
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    return false;
                })
                .forEach(mergeTmpEntity -> {
                    tXfOriginAgreementMergeDao.insert(mergeTmpEntity);
                });
    }

    private String getReasonCode(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        List<TXfOriginSapFbl5nEntity> list = tXfOriginSapFbl5nDao.selectList(queryWrapper);
        TXfOriginSapFbl5nEntity tXfOriginSapFbl5nEntity = list.stream().filter(fbl5n -> StringUtils.isNotBlank(fbl5n.getReasonCode())).findAny().orElse(null);
        if (tXfOriginSapFbl5nEntity != null) {
            return tXfOriginSapFbl5nEntity.getReasonCode();
        }
        return null;
    }

    private String getCustomer(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapZarrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapZarrEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapZarrEntity.REFERENCE, reference);
        List<TXfOriginSapZarrEntity> list = tXfOriginSapZarrDao.selectList(queryWrapper);
        return list.stream()
                .map(zarr -> {
                    if (StringUtils.isNotBlank(zarr.getCustomer())) {
                        return zarr.getCustomer();
                    }
                    return null;
                }).findAny().orElse(null);
    }

    private String getMemo6D(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapZarrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapZarrEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapZarrEntity.REFERENCE, reference);
        List<TXfOriginSapZarrEntity> list = tXfOriginSapZarrDao.selectList(queryWrapper);
        return list.stream()
                .map(zarr -> {
                    if (StringUtils.isNotBlank(zarr.getMemo())) {
                        return zarr.getMemo().replace("V#", "").substring(0, 6);
                    }
                    return null;
                }).findAny().orElse(null);
    }

    private void deleteOriginAgreementMerge(Integer jobId) {
        QueryWrapper<TXfOriginAgreementMergeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginAgreementMergeEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginAgreementMergeEntity.SOURCE, 1);
        tXfOriginAgreementMergeDao.delete(queryWrapper);
    }


}
