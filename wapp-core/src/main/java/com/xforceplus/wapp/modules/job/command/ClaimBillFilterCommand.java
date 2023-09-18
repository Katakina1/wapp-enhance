package com.xforceplus.wapp.modules.job.command;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.client.NbrRsp;
import com.xforceplus.wapp.client.WappHostClient;
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
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemHyperEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemSamsEntity;



import lombok.extern.slf4j.Slf4j;



import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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
    static final String HYPER = "Hyper";
    static final String SAMS = "sams";
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
    private WappHostClient wappHostClient;
    @Autowired
    private DeductService deductService;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;

    @Override
    public boolean execute(Context context) throws Exception {
    	log.info("5. start ClaimBillFilterCommand");
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            log.info("开始过滤原始索赔单文件数据入业务表={}", fileName);
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
                return true;
            } catch (Exception e) {
                log.error("ClaimBillFilterCommand error:", e);
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
    	log.info("start processBill");
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_CLAIM_BILL_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_CLAIM_BILL_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        //处理原始业务单合并 2022-04-25 索賠正負金額都要保留參與合并 magaofeng@xforceplus.com
        mergeClaim(jobId);
        // 先获取分页总数
        long pages;
        do {
            Page<TXfOriginClaimBillEntity> page = service.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginClaimBillEntity>()
                            .lambda()
                            .eq(TXfOriginClaimBillEntity::getCheckStatus,0)
                            .eq(TXfOriginClaimBillEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginClaimBillEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filterBill(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)),jobId);
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryClaimBillProgress(last);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } while (last < pages);
        // 下一步
        context.put(TXfBillJobEntity.JOB_ENTRY_OBJECT, BillJobEntryObjectEnum.ITEM.getCode());
    }
    
    /**
     * 合并负数金额
     * @param jobId
     */
    private void mergeClaim(int jobId){
    	log.info("start mergeClaim jobId:{}", jobId);
    	List<TXfOriginClaimBillEntity> negativeList = service.list(new QueryWrapper<TXfOriginClaimBillEntity>()
                .lambda().eq(TXfOriginClaimBillEntity::getJobId, jobId).like(TXfOriginClaimBillEntity::getCostAmount, "-%"));
        if(negativeList != null && negativeList.size() > 0){
        	log.info("mergeClaim jobid [{}] merge size:{}", jobId, negativeList.size());
			negativeList.forEach(item -> {
        		//索赔号
        		String claimNo = StringUtils.equalsIgnoreCase(item.getClaimNo(), "0") ? item.getExchangeNo() : item.getClaimNo();
        		//开始第一轮合并：根据jobId,相同供应商，相同索赔号，相同税率，相同暂扣发票，合并正负
        		List<TXfOriginClaimBillEntity> positiveList = service.list(new QueryWrapper<TXfOriginClaimBillEntity>()
                        .lambda().eq(TXfOriginClaimBillEntity::getJobId, jobId).eq(TXfOriginClaimBillEntity::getVendorNo, item.getVendorNo())
						.and(wrapper -> wrapper.eq(TXfOriginClaimBillEntity::getClaimNo, claimNo).or().eq(TXfOriginClaimBillEntity::getExchangeNo, claimNo))
						.eq(TXfOriginClaimBillEntity::getCheckStatus, 0)
						.eq(TXfOriginClaimBillEntity::getTaxRate, item.getTaxRate())
						.eq(TXfOriginClaimBillEntity::getInvoiceReference, item.getInvoiceReference()));
        		log.info("job:{},索赔号:{},开启第一轮合并size:{},负数金额:{}", jobId, claimNo, (positiveList== null ? 0: positiveList.size()),item.getCostAmount());
        		mergeDetails(jobId, item, positiveList);
        		//开始第一轮合并：第二轮合并
        		if(new BigDecimal(item.getCostAmount()).compareTo(BigDecimal.ZERO) < 0) {
        			positiveList = service.list(new QueryWrapper<TXfOriginClaimBillEntity>()
                            .lambda().eq(TXfOriginClaimBillEntity::getJobId, jobId).eq(TXfOriginClaimBillEntity::getVendorNo, item.getVendorNo())
    						.eq(TXfOriginClaimBillEntity::getCheckStatus, 0)
    						.eq(TXfOriginClaimBillEntity::getInvoiceReference, item.getInvoiceReference()));
            		log.info("job:{},索赔号:{},开启第二轮合并size:{},负数金额:{}", jobId, claimNo, (positiveList== null ? 0: positiveList.size()), item.getCostAmount());
            		mergeDetails(jobId, item, positiveList);
        		}
        		log.info("job:{},索赔号:{},负数金额:{},合并结束：nCostAmount:{},nAmountWithTax:{}", jobId, claimNo, (positiveList== null ? 0: positiveList.size()), item.getCostAmount(), item.getAmountWithTax());
    			//如果合并后金额还是负数
        		BigDecimal nCostAmount = new BigDecimal(item.getCostAmount());
        		BigDecimal nAmountWithTax = new BigDecimal(item.getAmountWithTax());
    			if(nCostAmount.compareTo(BigDecimal.ZERO) < 0){
    				updateClaimCheckStatus(jobId, item.getId(), nAmountWithTax, nCostAmount, 1, "成本金额必须是正数", item.getMergeId());
    			}
        		log.info("mergeClaim jobid [{}] ClaimNo:{}, id:{}, merge", jobId, item.getClaimNo(), item.getId());
        	});
        }
    }
    
    /**
     * 1开启合并
     * @param jobId
     * @param negativeClaimBill 负数索赔单
     * @param positiveList 参与合并的正数索赔单
     */
    private void mergeDetails(Integer jobId, TXfOriginClaimBillEntity negativeClaimBill, List<TXfOriginClaimBillEntity> positiveList) {
		if (positiveList == null || positiveList.size() == 0) {
			return;
		}
    	//负数不含税金额
		BigDecimal nCostAmount = new BigDecimal(negativeClaimBill.getCostAmount());
		BigDecimal nAmountWithTax = new BigDecimal(negativeClaimBill.getAmountWithTax());
		//索赔号
		for (int i = 0; i < positiveList.size(); i++) {
			TXfOriginClaimBillEntity v = positiveList.get(i);
			BigDecimal pCostAmount = new BigDecimal(v.getCostAmount());
			BigDecimal pAmountWithTax = new BigDecimal(v.getAmountWithTax());
			if(v.getId().longValue() != negativeClaimBill.getId().longValue() && pCostAmount.compareTo(BigDecimal.ZERO) > 0){
				String tmpCheckRemark = negativeClaimBill.getCheckRemark() == null ? "" : negativeClaimBill.getCheckRemark();
				String mergeId = negativeClaimBill.getMergeId() == null ? "" : negativeClaimBill.getMergeId();
				String vCheckRemark = v.getCheckRemark() == null ? "" : v.getCheckRemark();
				String vMergeId = v.getMergeId() == null ? "" : v.getMergeId();
				String negativeClaimNo = StringUtils.equalsIgnoreCase(negativeClaimBill.getClaimNo(), "0") ? negativeClaimBill.getExchangeNo() : negativeClaimBill.getClaimNo();
				String vClaimNo = StringUtils.equalsIgnoreCase(v.getClaimNo(), "0") ? v.getExchangeNo() : v.getClaimNo();
				
				nCostAmount = pCostAmount.add(nCostAmount);
				nAmountWithTax = pAmountWithTax.add(nAmountWithTax);
				//正数金额等于负数金额
				if(nCostAmount.compareTo(BigDecimal.ZERO) == 0){
					updateClaimCheckStatus(jobId, v.getId(), BigDecimal.ZERO, BigDecimal.ZERO, 1,
							vCheckRemark + "正数与[" + negativeClaimNo + "]合并金额:" + negativeClaimBill.getCostAmount() + "无需开票，", vMergeId + negativeClaimBill.getId() + "," );
					updateClaimCheckStatus(jobId, negativeClaimBill.getId(), BigDecimal.ZERO, BigDecimal.ZERO, 1,
							tmpCheckRemark + "负数被[" + vClaimNo + "]合并金额:" + v.getCostAmount() + "，", mergeId + v.getId()+ ",");
				}
				//正数金额大于负数金额
				if(nCostAmount.compareTo(BigDecimal.ZERO) > 0){
					updateClaimCheckStatus(jobId, v.getId(), nAmountWithTax, nCostAmount, 0,
							vCheckRemark + "正数被[" + negativeClaimNo + "]合并金额:" + negativeClaimBill.getCostAmount() + "，", mergeId + negativeClaimBill.getId() + ",");
					updateClaimCheckStatus(jobId, negativeClaimBill.getId(), BigDecimal.ZERO, BigDecimal.ZERO, 1, 
							tmpCheckRemark + "负数被[" + vClaimNo + "]合并金额:" + negativeClaimBill.getCostAmount().replace("-", "") + "，", mergeId + v.getId() + ",");
					negativeClaimBill.setCostAmount("0");
					negativeClaimBill.setAmountWithTax("0");
					break;
				}
				//正数金额小于负数金额
				if(nCostAmount.compareTo(BigDecimal.ZERO) < 0){
					negativeClaimBill.setMergeId(mergeId + v.getId() + ",");
					updateClaimCheckStatus(jobId, v.getId(), BigDecimal.ZERO, BigDecimal.ZERO, 1, 
							vCheckRemark + "正数被[" + negativeClaimNo + "]合并金额:-" + v.getCostAmount() + "无需开票，", vMergeId + negativeClaimBill.getId() + ",");
				}
			}
			negativeClaimBill.setCostAmount(nCostAmount.toPlainString());
			negativeClaimBill.setAmountWithTax(nAmountWithTax.toPlainString());
		}
    }
    
    private boolean updateClaimCheckStatus(Integer jobId, Long id, BigDecimal amountWithTax, BigDecimal costAmount, int checkStatus , String checkRemark, String mergeId){
    	TXfOriginClaimBillEntity entity = new TXfOriginClaimBillEntity();
    	entity.setId(id);
    	entity.setAmountWithTax(amountWithTax.toPlainString());
    	entity.setCostAmount(costAmount.toPlainString());
    	entity.setCheckStatus(checkStatus);
    	entity.setCheckRemark(checkRemark);
    	entity.setMergeId(mergeId);
    	return service.update(entity, new QueryWrapper<TXfOriginClaimBillEntity>()
                .lambda()
                .eq(TXfOriginClaimBillEntity::getId,id)
                .eq(TXfOriginClaimBillEntity::getJobId, jobId));
    }

    /**
     * @param jobId
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void processItemHyper(int jobId, Context context) {
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_CLAIM_ITEM_HYPER_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_CLAIM_ITEM_HYPER_PROGRESS, 0);
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
                            .eq(TXfOriginClaimItemHyperEntity::getCheckStatus,0)
                            .eq(TXfOriginClaimItemHyperEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginClaimItemHyperEntity::getId)
            );
            Mono<List<NbrRsp.HyperNbr>> hyperByNbrs = null;
			try {
				Set<String> itemNbr = page.getRecords().stream().filter(it->StringUtils.isNotBlank(it.getUpcNbr()))
                        .map(it->it.getUpcNbr().substring(0, it.getUpcNbr().length() - 1)).collect(Collectors.toSet());
				//log.info("itemNbr:{}",itemNbr);
				hyperByNbrs = wappHostClient.findHyperByNbrs(itemNbr);
				if (hyperByNbrs != null) {
					log.info("jobid:{}, query hyper items result:{}", jobId, JSON.toJSONString(hyperByNbrs.blockOptional()));
				}
			} catch (Exception e) {
				log.error("wappHostClient.findHyperByNbrs error,", e);
			}
            if(hyperByNbrs != null) {
            	Map<String, NbrRsp.HyperNbr> hyperNbrMap = hyperByNbrs.blockOptional().orElse(new ArrayList<>())
                        .stream().collect(Collectors.toMap(NbrRsp.HyperNbr::getItemNbr, Function.identity(), (old, now) -> old));
                filterItemHyper(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)),jobId, it->{
                    NbrRsp.HyperNbr hyper = hyperNbrMap.get(it.getItemNbr());
                    if (Objects.nonNull(hyper)) {
                        it.setItemSpec(hyper.getMdsDescSizs());
                        it.setUnit(hyper.getMdsDescUnit());
                    }
                });
            }else {
            	log.info("hyperByNbrs is null ,not match itemspec and unit jobid:{}", jobId);
            	filterItemHyper(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)),jobId, null);
            }
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryClaimItemHyperProgress(last);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
            // 总页数
            pages = page.getPages();
            log.info("last:{}-->pages:{}",last,pages);
        } while (last < pages);
        // 下一步
        context.put(TXfBillJobEntity.JOB_ENTRY_OBJECT, BillJobEntryObjectEnum.ITEM_SAMS.getCode());
    }

    /**
     * @param jobId
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void processItemSams(int jobId, Context context) {
		log.info("processItemSams jobId:{}", jobId);
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_CLAIM_ITEM_SAMS_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_CLAIM_ITEM_SAMS_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        // 先获取分页总数
        long pages;
        long start = System.currentTimeMillis();
        do {
            Page<TXfOriginClaimItemSamsEntity> page = itemSamsService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginClaimItemSamsEntity>()
                            .lambda()
                            .eq(TXfOriginClaimItemSamsEntity::getCheckStatus,0)
                            .eq(TXfOriginClaimItemSamsEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginClaimItemSamsEntity::getId)
            );
            Mono<List<NbrRsp.SamsNbr>> samsByNbrs = null;
            try {
            	Set<String> itemNbr = page.getRecords().stream().map(TXfOriginClaimItemSamsEntity::getItemNbr).collect(Collectors.toSet());
            	samsByNbrs = wappHostClient.findSamsByNbrs(itemNbr);
				if (samsByNbrs != null) {
					log.info("jobid:{}, query sam items result:{}", jobId, JSON.toJSONString(samsByNbrs.blockOptional()));
				}
			} catch (Exception e) {
				log.error("wappHostClient.findSamsByNbrs error,", e);
			}
            if(samsByNbrs != null ) {
            	Map<String, NbrRsp.SamsNbr> samsNbrMap = samsByNbrs.blockOptional().orElse(new ArrayList<>())
                        .stream().collect(Collectors.toMap(NbrRsp.SamsNbr::getItemNbr, Function.identity(), (old, now) -> old));
				filterItemSams(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)), jobId, it -> {
                    NbrRsp.SamsNbr sams = samsNbrMap.get(it.getItemNbr());
                    if (Objects.nonNull(sams)) {
                        if (StringUtils.isNotBlank(sams.getSpecification())) {
                            it.setItemSpec(sams.getSpecification());
                        }
                        if (StringUtils.isNotBlank(sams.getSellingUnit())) {
                            it.setUnit(sams.getSellingUnit());
                        }
                    }
                });
            }else {
            	log.info("samsByNbrs is null ,not match itemspec and unit jobid:{}", jobId);
            	filterItemSams(page.getRecords(), String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)), jobId, null);
            }
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryClaimItemSamsProgress(last);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
            // 总页数
            pages = page.getPages();
        } while (last < pages);
        log.info("索赔单:{} 数据入业务表花费{}ms", jobId, System.currentTimeMillis() - start);
    }

    /**
     * 过滤转换入库
     *
     * @param list
     * @param jobName
     */
    private void filterBill(List<TXfOriginClaimBillEntity> list, String jobName,Integer jobId) {
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
        log.info("filterBill size:{},jobId:{}",list.size(),jobId);
        if (CollectionUtils.isNotEmpty(newList)) {
            long start = System.currentTimeMillis();
            deductService.receiveData(newList, TXfDeductionBusinessTypeEnum.CLAIM_BILL);
            log.info("索赔单:{} 主信息调用创建业务单接口：{}条数据花费时间{}ms", jobId, newList.size(), System.currentTimeMillis() - start);

        }
    }

    /**
     * 过滤转换入库
     *
     * @param list
     * @param jobName
     */
    private void filterItemHyper(List<TXfOriginClaimItemHyperEntity> list, String jobName, Integer jobId, Consumer<ClaimBillItemData> update) {
    	String uuid = UUID.randomUUID().toString();
		log.info("filterItemHyper JOBID:{}, jobName:{}, list.size:{}, uuid:{}", jobId, jobName, list.size(), uuid);
        List<ClaimBillItemData> newList = list
                .stream()
                // 索赔单大卖场明细不含税金额应该为负数
                .filter(v -> v.getLineCost().trim().startsWith(NEGATIVE_SYMBOL))
                .map(v -> {
                            try {
                                final ClaimBillItemData claimBillItemData = TXfOriginClaimItemHyperEntityConvertor.INSTANCE.toClaimBillItemData(v);
                                //2022-09-18  https://jira.xforceplus.com/browse/PRJCENTER-10333
                                if(StringUtils.isNotBlank(claimBillItemData.getCnDesc())) {
                                	claimBillItemData.setCnDesc(claimBillItemData.getCnDesc().replace("�", ""));
                                }
                                claimBillItemData.setItemNo(claimBillItemData.getUpc());
                                if(update != null) {
                                	update.accept(claimBillItemData);
                                }
                                return claimBillItemData;
                            } catch (Exception e) {
                                log.warn("hyper明细转换出错:claimNo:["+v.getClaimNbr()+"],itemId:["+v.getId()+"],message:"+e.getMessage(), e);
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            long start = System.currentTimeMillis();
            boolean saveResult = deductService.receiveItemData(newList, null);
            log.info("索赔单JOBID:{} Hyper调用创建业务单接口:{}条数据花费时间:{}ms,uuid:{}, saveResult:{}", jobId, newList.size(), System.currentTimeMillis() - start, uuid, saveResult);

        }
    }

	/**
	 * 过滤转换入库
	 *
	 * @param list
	 * @param jobName
	 */
    private void filterItemSams(List<TXfOriginClaimItemSamsEntity> list, String jobName,Integer jobId, Consumer<ClaimBillItemData> update) {
    	String uuid = UUID.randomUUID().toString();
		log.info("filterItemSams JOBID:{}, jobName:{}, list.size:{}, uuid:{}", jobId, jobName, list.size(), uuid);
		List<ClaimBillItemData> newList = list
                .stream()
                // 索赔单明细不含税金额为负数
                //只要数量为负数的明细
                .filter(v -> v.getShipQty().startsWith(NEGATIVE_SYMBOL))
                .map(v -> {
                            try {
                            	ClaimBillItemData claimBillItemData = TXfOriginClaimItemSamsEntityConvertor.INSTANCE.toClaimBillItemData(v);
                            	//2022-09-18  https://jira.xforceplus.com/browse/PRJCENTER-10333
                                if(StringUtils.isNotBlank(claimBillItemData.getCnDesc())) {
                                	claimBillItemData.setCnDesc(claimBillItemData.getCnDesc().replace("�", ""));
                                }
                                if(update != null) {
                                	update.accept(claimBillItemData);
                                }
                                return claimBillItemData;
                            } catch (Exception e) {
                                log.warn(e.getMessage(), e);
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            long start = System.currentTimeMillis();
            boolean saveResult = deductService.receiveItemData(newList, null);
            log.info("索赔单JOBID:{} Sams调用创建业务单接口:{}条数据花费时间:{}ms,uuid:{}, saveResult:{}", jobId, newList.size(), System.currentTimeMillis() - start, uuid, saveResult);
        }
    }

}
