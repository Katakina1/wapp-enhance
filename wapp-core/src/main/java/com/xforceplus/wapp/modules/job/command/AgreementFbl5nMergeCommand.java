package com.xforceplus.wapp.modules.job.command;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.converters.TXfOriginAgreementBillEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.service.OriginAgreementMergeService;
import com.xforceplus.wapp.modules.job.service.OriginSapFbl5nService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.dao.TXfOriginSapFbl5nDao;
import com.xforceplus.wapp.repository.dao.TXfOriginSapZarrDao;
import com.xforceplus.wapp.repository.dao.TXfSupplierSapNo6dMappingDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementMergeEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapFbl5nEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapZarrEntity;
import com.xforceplus.wapp.repository.entity.TXfSupplierSapNo6dMappingEntity;
import com.xforceplus.wapp.sequence.IDSequence;

import lombok.extern.slf4j.Slf4j;

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
    private OriginSapFbl5nService originSapFbl5nService;
    @Autowired
    private TXfOriginSapFbl5nDao tXfOriginSapFbl5nDao;
    @Autowired
    private TXfOriginSapZarrDao tXfOriginSapZarrDao;
    @Autowired
    private OriginAgreementMergeService originAgreementMergeService;
    @Autowired
    private IDSequence idSequence;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;
    @Autowired
    private TXfSupplierSapNo6dMappingDao supplierSapNo6dMappingDao;

    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    /**
     * 过滤fbl5n数据
     */
    private List<String> companyCodeList = Stream.of("D073").collect(Collectors.toList());
    private List<String> reasonCodeList = Stream.of("511", "527", "206", "317").collect(Collectors.toList());
    private List<String> docTypeList = Stream.of("1D", "RV","DA").collect(Collectors.toList());

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            log.info("开始过滤原始协议单fbl5n数据入merge表={}", fileName);
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                context.put(TXfBillJobEntity.REMARK, e.getMessage());
            }
        } else {
            log.info("协议单zarr跳过数据梳理步骤, 当前任务={}, 状态={}", fileName, jobStatus);
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
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_AGREEMENT_FBL5N_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_AGREEMENT_FBL5N_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        // 先获取分页总数
        long start = System.currentTimeMillis();
        long pages;
        do {
            Page<TXfOriginSapFbl5nEntity> page = originSapFbl5nService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginSapFbl5nEntity>()
                            .lambda()
                            .eq(TXfOriginSapFbl5nEntity::getCheckStatus, 0)
                            .eq(TXfOriginSapFbl5nEntity::getJobId, jobId)
                            //过滤数据集1
                            .in(TXfOriginSapFbl5nEntity::getCompanyCode, companyCodeList)
                            .in(TXfOriginSapFbl5nEntity::getDocumentType, docTypeList)
                            .orderByAsc(TXfOriginSapFbl5nEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            long finalLast = last;
            filter(page.getRecords(), jobId);
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryAgreementFbl5nProgress(finalLast);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } while (last < pages);
        log.info("协议单:{} fbl5n数据入merge表花费{}ms", jobId, System.currentTimeMillis() - start);
    }

    private void filter(List<TXfOriginSapFbl5nEntity> list, Integer jobId) {
        long start = System.currentTimeMillis();

        List<Future<TXfOriginAgreementMergeEntity>> featureList = new ArrayList<>();
        list.forEach(fbl5n -> {
            Future<TXfOriginAgreementMergeEntity> future = executorService.submit(() -> convertTXfOriginAgreementMergeEntity(fbl5n));
            featureList.add(future);
        });
        List<TXfOriginAgreementMergeEntity> newList = featureList.stream().map(f -> {
            try {
                return f.get();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            return null;
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            originAgreementMergeService.saveBatch(newList);
            log.info("协议单:{} fbl5n原始数据{}条转换后{}条花费{}ms", jobId, list.size(), newList.size(), System.currentTimeMillis() - start);
        }
    }

    private TXfOriginAgreementMergeEntity convertTXfOriginAgreementMergeEntity(TXfOriginSapFbl5nEntity fbl5n) {
        try {
            TXfOriginAgreementMergeEntity tXfOriginAgreementMergeTmpEntity = new TXfOriginAgreementMergeEntity();
            tXfOriginAgreementMergeTmpEntity.setId(idSequence.nextId());
            tXfOriginAgreementMergeTmpEntity.setJobId(fbl5n.getJobId());
            tXfOriginAgreementMergeTmpEntity.setCustomerNo(fbl5n.getAccount());
            tXfOriginAgreementMergeTmpEntity.setCustomerName(getCustomer(fbl5n.getJobId(), fbl5n.getReference()));
            tXfOriginAgreementMergeTmpEntity.setMemo(getMemo6D(fbl5n.getJobId(), fbl5n.getReference() ,fbl5n.getAccount()));
            tXfOriginAgreementMergeTmpEntity.setCompanyCode(fbl5n.getCompanyCode());
            String amountInDocCurr = fbl5n.getAmountInDocCurr().replace(",", "");
            if (StringUtils.isNotBlank(amountInDocCurr)) {
                tXfOriginAgreementMergeTmpEntity.setWithAmount(new BigDecimal(amountInDocCurr));
            }
            tXfOriginAgreementMergeTmpEntity.setReasonCode(fbl5n.getReasonCode());
            if (StringUtils.isBlank(tXfOriginAgreementMergeTmpEntity.getReasonCode())) {
                tXfOriginAgreementMergeTmpEntity.setReasonCode(getReasonCode(fbl5n.getJobId(), fbl5n.getReference()));
            }
            tXfOriginAgreementMergeTmpEntity.setReference(fbl5n.getReference());
            tXfOriginAgreementMergeTmpEntity.setTaxCode(fbl5n.getTaxCode());
            BigDecimal taxRate = Optional.ofNullable(TXfOriginAgreementBillEntityConvertor.TAX_CODE_TRANSLATOR.get(fbl5n.getTaxCode())).orElse(BigDecimal.ZERO);
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
                        .divide(tXfOriginAgreementMergeTmpEntity.getTaxRate().add(BigDecimal.ONE), 6, BigDecimal.ROUND_HALF_UP)
                        .multiply(tXfOriginAgreementMergeTmpEntity.getTaxRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                tXfOriginAgreementMergeTmpEntity.setTaxAmount(taxAmount);
            }
            tXfOriginAgreementMergeTmpEntity.setSource(1);
            tXfOriginAgreementMergeTmpEntity.setCreateTime(new Date());
            tXfOriginAgreementMergeTmpEntity.setUpdateTime(new Date());

            if (!companyCodeList.contains(tXfOriginAgreementMergeTmpEntity.getCompanyCode())) {
                return null;
            }
            //过滤数据集2
            if (tXfOriginAgreementMergeTmpEntity.getWithAmount().compareTo(BigDecimal.ZERO) >= 0) {
                return null;
            }
            if (!docTypeList.contains(tXfOriginAgreementMergeTmpEntity.getDocumentType())) {
                return null;
            }
            if (StringUtils.isNotBlank(tXfOriginAgreementMergeTmpEntity.getReasonCode())) {
                if (!reasonCodeList.contains(tXfOriginAgreementMergeTmpEntity.getReasonCode())) {
                    return null;
                }
            }
            return tXfOriginAgreementMergeTmpEntity;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    private String getReasonCode(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        queryWrapper.ne(TXfOriginSapFbl5nEntity.REASON_CODE, "");
        queryWrapper.isNotNull(TXfOriginSapFbl5nEntity.REASON_CODE);
        Page<TXfOriginSapFbl5nEntity> page = tXfOriginSapFbl5nDao.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            TXfOriginSapFbl5nEntity fbl5n = page.getRecords().get(0);
            return fbl5n.getReasonCode();
        }
        return null;
    }

    private String getCustomer(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapZarrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapZarrEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapZarrEntity.REFERENCE, reference);
        queryWrapper.ne(TXfOriginSapZarrEntity.CUSTOMER, "");
        queryWrapper.isNotNull(TXfOriginSapZarrEntity.CUSTOMER);
        Page<TXfOriginSapZarrEntity> page = tXfOriginSapZarrDao.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            TXfOriginSapZarrEntity zarr = page.getRecords().get(0);
            return zarr.getCustomer();
        }
        return null;
    }

    private String getMemo6D(Integer jobId, String reference,String sapNo) {
        QueryWrapper<TXfOriginSapZarrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapZarrEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapZarrEntity.REFERENCE, reference);
        queryWrapper.ne(TXfOriginSapZarrEntity.MEMO, "");
        queryWrapper.isNotNull(TXfOriginSapZarrEntity.MEMO);
        Page<TXfOriginSapZarrEntity> page = tXfOriginSapZarrDao.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            TXfOriginSapZarrEntity zarr = page.getRecords().get(0);
            return zarr.getMemo().replace("V#", "").substring(0, 6);
        }
        return get6DBySapNo(sapNo);
    }

    private String get6DBySapNo(String sapNo){
        QueryWrapper<TXfSupplierSapNo6dMappingEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfSupplierSapNo6dMappingEntity.SUPPLIER_SAP_NO,sapNo);
        TXfSupplierSapNo6dMappingEntity supplierSapNo6dMapping = supplierSapNo6dMappingDao.selectOne(queryWrapper);
        if(supplierSapNo6dMapping == null){
            return null;
        }
        return supplierSapNo6dMapping.getSupplier6d();
    }

}
