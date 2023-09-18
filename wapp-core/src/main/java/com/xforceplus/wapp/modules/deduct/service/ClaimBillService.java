package com.xforceplus.wapp.modules.deduct.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.deduct.model.ClaimDoItemMatchTemp;
import com.xforceplus.wapp.util.BigDecimalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfInvoiceDeductTypeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductItemServiceImpl;
import com.xforceplus.wapp.modules.exceptionreport.event.NewExceptionReportEvent;
import com.xforceplus.wapp.modules.exceptionreport.listener.ExceptionReportProcessListener;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 类描述：扣除单通用方法
 *
 * @ClassName DeductionService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 11:38
 */
@Service
@Slf4j
public class ClaimBillService extends DeductService{
    @Value("${claimBill.diff-amount-rate:0.05}")
    private String diffAmountRate;

    @Autowired
    private TaxRateConfig taxRateConfig;
    @Autowired
    private DeductItemRefBatchService deductItemRefBatchService;
    @Autowired
    private BillDeductItemServiceImpl billDeductItemService;
    @Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;
    @Autowired
    private BillSettlementService billSettlementService;
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;
    @Autowired
    private DeductInvoiceDetailService deductInvoiceDetailService;

	/**
	 * <pre>
	 * <p>匹配索赔单 索赔单明细 单线程执行，
	 * <p>每次导入 只会执行一次，针对当月的索赔明细有效
	 * </pre>
	 * @return
	 */
    public boolean matchClaimBill() {
        int limit = 50;
		/**
		 * 查询未匹配明细的索赔单
		 */
        Long deductId = 1L;
        int count = 0;
        List<TXfBillDeductEntity> tXfBillDeducts = new ArrayList<>();
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,null, limit, TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
            for (final TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
                String sellerNo = tXfBillDeductEntity.getSellerNo();
                String purcharseNo = tXfBillDeductEntity.getPurchaserNo();
                if (StringUtils.isEmpty(sellerNo) || StringUtils.isEmpty(purcharseNo)) {
                    log.info("发现购销对信息不合法 跳过明细匹配：sellerNo : {} purcharseNo : {}",sellerNo,purcharseNo);
                    continue;
                }
                BigDecimal taxRate = tXfBillDeductEntity.getTaxRate();
                Map<BigDecimal, BigDecimal> taxRateMap = taxRateConfig.bulidTaxRateMap(taxRate);
                if (StringUtils.isEmpty(sellerNo) || StringUtils.isEmpty(purcharseNo) || Objects.isNull(taxRate)) {
                    log.warn("索赔单{} 主信息 不符合要求，sellerNo:{},purcharseNo:{},taxRate:{}",tXfBillDeductEntity.getBusinessNo(), sellerNo,purcharseNo,taxRate);
                    continue;
                }
                if (StringUtils.isEmpty(tXfBillDeductEntity.getBusinessNo())  ) {
                    log.warn("索赔单 主信息单号为空 跳过匹配 sellerNo:{},purcharseNo:{},taxRate:{} ",sellerNo,purcharseNo,taxRate);
                    continue;
                }
                /**
                 * 查询已匹配金额
                 */
                BigDecimal matchAmount = tXfBillDeductItemRefDao.queryRefMatchAmountByBillId(tXfBillDeductEntity.getId());
                matchAmount = Objects.isNull(matchAmount) ? BigDecimal.ZERO : matchAmount;
                //业务单据还需匹配的含税金额 magaofeng@xforceplus
                BigDecimal billAmount = tXfBillDeductEntity.getAmountWithTax().subtract(matchAmount);
                List<TXfBillDeductItemEntity> matchItem = new ArrayList<>();

                log.info("matchClaimBill claminno:{}, matchAmount:{}, billAmount:{}", tXfBillDeductEntity.getBusinessNo(), matchAmount, billAmount);
                /**
                 * 查询符合条件的明细
                 */
                Long itemId = 1L;
                boolean matched = false;
                matchAmount = BigDecimal.ZERO;
                if (billAmount.compareTo(BigDecimal.ZERO) > 0) {
                    List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(sellerNo, taxRate, itemId, limit, tXfBillDeductEntity.getBusinessNo());
                    while (billAmount.compareTo(BigDecimal.ZERO) > 0) {
                        if (CollectionUtils.isEmpty(tXfBillDeductItemEntities)) {
                            taxRate = taxRateMap.get(taxRate);

                            if (Objects.isNull(taxRate)) {
                                log.warn("ID:{},BatchNo:{}, BusinessNo:{}索赔单，未找到足够的索赔单明细，结束匹配", tXfBillDeductEntity.getId(), tXfBillDeductEntity.getBatchNo(), tXfBillDeductEntity.getBusinessNo());
                                break;
                            }

                            if (tXfBillDeductEntity.getTaxRate().compareTo(taxRate) == 0) {
                                log.info("ID:{},BatchNo:{}, BusinessNo:{}, {}与原主单税率[{}]一致，且税率一致的明细已匹配完，跳过匹配", tXfBillDeductEntity.getId(), tXfBillDeductEntity.getBatchNo(),
                                        tXfBillDeductEntity.getBusinessNo(), taxRate, tXfBillDeductEntity.getTaxRate());
                                continue;
                            }

                            itemId = 0L;
                            tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(sellerNo, taxRate, itemId, limit, tXfBillDeductEntity.getBusinessNo());
                            continue;
                        }
                        BigDecimal total = tXfBillDeductItemEntities.stream().map(TXfBillDeductItemEntity::getRemainingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (billAmount.compareTo(total) > 0) {
                            billAmount = billAmount.subtract(total);
                            matchAmount = matchAmount.add(total);
                        } else {
                            billAmount = BigDecimal.ZERO;
                        }
                        matchItem.addAll(tXfBillDeductItemEntities);
                        if (billAmount.compareTo(BigDecimal.ZERO) == 0) {
                            matched = true;
                            break;
                        }
                        itemId = tXfBillDeductItemEntities.stream().mapToLong(TXfBillDeductItemEntity::getId).max().getAsLong();
                        tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(sellerNo, taxRate, itemId, limit, tXfBillDeductEntity.getBusinessNo());
                    }
                }
                /**
                 * 匹配失败，明细金额不足
                 */
                // 完全没有匹配到明细
                boolean noMatch = BigDecimal.ZERO.compareTo(matchAmount) == 0 && CollectionUtil.isEmpty(matchItem);
                log.info("matchClaimBill claminno:{}, noMatch:{}, matched:{} ,matchAmount:{}, tXfBillDeductEntity.getAmountWithTax():{}", tXfBillDeductEntity.getBusinessNo(), noMatch, matched,
                        matchAmount, tXfBillDeductEntity.getAmountWithTax());
                if (matched || (!noMatch && isModifyItem(tXfBillDeductEntity.getBusinessNo(), matchAmount, billAmount))) {
                    // 匹配成功 或 部分匹配（容差内）
                    ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.NOT_MATCH_CLAIM_DETAIL);
                    // 全部匹配，消除部分匹配异常
                    ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.PART_MATCH_CLAIM_DETAIL);
                    // 只要有数据就能处理 供应商编号的列外报告
                    ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.VENDOR_NO_FAIL);
                    // 添加日志履历
                    operateLogService.addDeductLog(tXfBillDeductEntity.getId(), tXfBillDeductEntity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(tXfBillDeductEntity.getStatus()), "", OperateLogEnum.CLAIM_MATCH_ITEM_SUCCESS, "", 0L, "系统");
                } else {
                    //判断明细是否有部分
                    boolean isDetails = tXfBillDeductEntity.getAmountWithTax().compareTo(billAmount) == 0;
                    if(isDetails) {//当明细不存在，判断是否是供应商编号错误
                        List<TXfBillDeductItemEntity> tempList = tXfBillDeductItemExtDao.queryBillItemByClaimNo(tXfBillDeductEntity.getBusinessNo(), tXfBillDeductEntity.getSellerNo());
                        if(tempList != null && tempList.size() > 0) {//判断是否是供应商编号错误
                            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
                            newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.VENDOR_NO_FAIL);
                            newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
                            applicationContext.publishEvent(newExceptionReportEvent);
                            log.error("索赔单匹配明细失败:{},供应商编号错误例外报告:{},金额差异:{}", tXfBillDeductEntity.getBusinessNo(), JSON.toJSONString(newExceptionReportEvent), billAmount.toPlainString());
                        }else {
                            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
                            newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.NOT_MATCH_CLAIM_DETAIL);
                            newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
                            applicationContext.publishEvent(newExceptionReportEvent);
                            // 只要有数据就能处理 供应商编号的列外报告
                            ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.VENDOR_NO_FAIL);

                            log.error("索赔单匹配明细失败:{},无索赔明细例外报告:{},金额差异:{}", tXfBillDeductEntity.getBusinessNo(), JSON.toJSONString(newExceptionReportEvent), billAmount.toPlainString());
                        }
                    }else { //
                        NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                        newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
                        newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.PART_MATCH_CLAIM_DETAIL);
                        newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
                        applicationContext.publishEvent(newExceptionReportEvent);
                        // 只要有数据就能处理 供应商编号的列外报告
                        ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.VENDOR_NO_FAIL);
                        log.error("索赔单匹配明细失败:{},发送明细金额不足例外报告:{} ，金额差异:{}", tXfBillDeductEntity.getBusinessNo(), JSON.toJSONString(newExceptionReportEvent), billAmount.toPlainString());
                    }
                    continue;
                }
                /**
                 * 匹配完成 进行绑定操作
                 */
                if (CollectionUtils.isNotEmpty(matchItem)) {
                    try {
                        List<Supplier<Boolean>> successSuppliers = new ArrayList<>();
                        AtomicReference<TXfBillDeductEntity> tmp = new AtomicReference<>();
                        successSuppliers.add(() -> {
                            tmp.set(doItemMatch(tXfBillDeductEntity, matchItem));
                            return true;
                        });
                        transactionalService.execute(successSuppliers);
                        successSuppliers = new ArrayList<>();
                        successSuppliers.add(() -> {
                            claimMatchBlueInvoice(tmp.get());
                            return true;
                        });
                        transactionalService.execute(successSuppliers);
                    } catch (Exception e) {
                        log.error("索赔单 明细匹配 蓝票匹配异常："+e.getMessage(), e);
                    }
                }
                count++;
            }
            deductId =  tXfBillDeductEntities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
            /**
             * 执行下一批匹配
             */
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,null,  limit, TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
            tXfBillDeducts.addAll(tXfBillDeductEntities);
        }
        List<String> businessNos = tXfBillDeducts.stream().map(TXfBillDeductEntity::getBusinessNo).collect(Collectors.toList());
        log.info("claimDeductScheduler本次执行数量:{},业务单号:{}",count,businessNos);
        return true;
    }



	/**
	 * 执行扣除明细，匹配主信息
	 * 
	 * @param tXfBillDeductEntity
	 * @param tXfBillDeductItemEntitys
	 * @return
	 */
	public TXfBillDeductEntity doItemMatch(TXfBillDeductEntity tXfBillDeductEntity, List<TXfBillDeductItemEntity> tXfBillDeductItemEntitys) {
		log.info("doItemMatch:{}", JSON.toJSONString(tXfBillDeductEntity));
		Long billId = tXfBillDeductEntity.getId();
		//业务单含税金额
		final AtomicReference<BigDecimal> billAmount = new AtomicReference<>(tXfBillDeductEntity.getAmountWithTax());
		// 主信息税率
        BigDecimal taxRate = tXfBillDeductEntity.getTaxRate();
		tXfBillDeductItemEntitys.sort((a, b) -> {
            // 税率相同优先匹配
            if (a.getTaxRate().compareTo(taxRate) == 0 && b.getTaxRate().compareTo(taxRate) == 0) {
                return a.getId().compareTo(b.getId());
            } else if (a.getTaxRate().compareTo(taxRate) == 0) {
                return -1;
            } else if (b.getTaxRate().compareTo(taxRate) == 0) {
                return 1;
            }
            // 不同税率， 单价越大越先匹配
            return b.getPrice().compareTo(a.getPrice());
        });

        List<TXfBillDeductItemRefEntity> tXfBillDeductItemRefEntities = Lists.newArrayList();
        List<ClaimDoItemMatchTemp> tempList = Lists.newArrayList();
        boolean matchTaxNoFlag = true;
		for (TXfBillDeductItemEntity tXfBillDeductItemEntity : tXfBillDeductItemEntitys) {
            if (billAmount.get().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            ClaimDoItemMatchTemp matchTemp = new ClaimDoItemMatchTemp();
            // 重新匹配税编，可能存在
            String resStr = this.fixTaxCode(tXfBillDeductItemEntity);
            if (StringUtils.isNotBlank(resStr)) {
                log.info("索赔单匹配明细补全税编失败:[{}]-[{}]", tXfBillDeductItemEntity.getId(), resStr);
            }
            matchTemp.setItem(tXfBillDeductItemEntity);

            //判断税编是否存在
            if (matchTaxNoFlag && StringUtils.isEmpty(tXfBillDeductItemEntity.getGoodsTaxNo()) && BigDecimal.ZERO.compareTo(tXfBillDeductItemEntity.getTaxRate()) != 0) {
                matchTaxNoFlag = false;
            }

            BigDecimal amount = tXfBillDeductItemEntity.getRemainingAmount().min(billAmount.get());
            // 判断原数量是否整数， 整数返回的数量就为整数
            boolean isInteger = BigDecimalUtil.isInteger(tXfBillDeductItemEntity.getQuantity());
            // 明细剩余可用数量
            log.info("明细剩余可用数量,tXfBillDeductItemEntity:{}",JSON.toJSON(tXfBillDeductItemEntity));
            BigDecimal canUseQuantityTotal = getCanUseQuantity(tXfBillDeductItemEntity.getRemainingAmount(), tXfBillDeductItemEntity.getQuantity(), tXfBillDeductItemEntity.getAmountWithTax(), isInteger);
            // 此次使用数量
            BigDecimal canUseQuantity =  getCanUseQuantity(amount, canUseQuantityTotal, tXfBillDeductItemEntity.getRemainingAmount(), isInteger);

            matchTemp.setIntegerFlag(isInteger);
            matchTemp.setCanUseQuantity(canUseQuantityTotal);
            matchTemp.setUseQuantity(canUseQuantity);
            matchTemp.setRemainingAmount(tXfBillDeductItemEntity.getRemainingAmount());
            matchTemp.setUseAmount(amount);
            matchTemp.setTaxRate(tXfBillDeductItemEntity.getTaxRate());
            tempList.add(matchTemp);

            billAmount.accumulateAndGet(amount, BigDecimal::subtract);
            //构建业务单和明细的关联关系
            TXfBillDeductItemRefEntity tXfBillDeductItemRefEntity = new TXfBillDeductItemRefEntity();
            tXfBillDeductItemRefEntity.setId(idSequence.nextId());
            tXfBillDeductItemRefEntity.setCreateTime(DateUtils.getNowDate());
            tXfBillDeductItemRefEntity.setDeductId(billId);
            // 使用额度 == 含税金额
            tXfBillDeductItemRefEntity.setUseAmount(amount);
            tXfBillDeductItemRefEntity.setDeductItemId(tXfBillDeductItemEntity.getId());
            tXfBillDeductItemRefEntity.setPrice(tXfBillDeductItemEntity.getPrice());
            tXfBillDeductItemRefEntity.setQuantity(canUseQuantity);
            //计算不含税金额再计算税额
            BigDecimal amountOutWithTax = amount.divide(BigDecimal.ONE.add(tXfBillDeductItemEntity.getTaxRate()), 2, RoundingMode.HALF_UP );
            tXfBillDeductItemRefEntity.setTaxAmount(amount.subtract(amountOutWithTax));
            tXfBillDeductItemRefEntity.setAmountWithTax(tXfBillDeductItemRefEntity.getUseAmount());
            tXfBillDeductItemRefEntity.setCreateTime(new Date());
            tXfBillDeductItemRefEntity.setUpdateTime(new Date());
            matchTemp.setItemRefEntity(tXfBillDeductItemRefEntity);
        }

		// 判断最后一条明细是否拆分且取整异常，并处理
        changeSplit(tempList);

        tempList.forEach(temp -> {
            TXfBillDeductItemEntity itemUpdate = temp.getItemUpdate();
            itemUpdate.setRemainingAmount(temp.getRemainingAmount().subtract(temp.getUseAmount()));
            tXfBillDeductItemDao.updateById(itemUpdate);

            tXfBillDeductItemRefEntities.add(temp.getItemRefEntity());
        });
		//批量保存税编关系
		deductItemRefBatchService.saveBatch(tXfBillDeductItemRefEntities);
		TXfBillDeductEntity tmp = new TXfBillDeductEntity();
		tmp.setId(billId);
		
		if (!matchTaxNoFlag) {
			NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
			newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
			newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.NOT_MATCH_GOODS_TAX);
			newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
			applicationContext.publishEvent(newExceptionReportEvent);
			log.error("索赔单 {}  发送税编匹配例外报告 {} ", tXfBillDeductEntity.getBusinessNo(), newExceptionReportEvent);
		} else {
			//当原来出现税编匹配失败的例外报告，现在匹配通过，自动变成已经处理
			ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.NOT_MATCH_GOODS_TAX);

			// 添加日志履历
            operateLogService.addDeductLog(tXfBillDeductEntity.getId(), tXfBillDeductEntity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(tXfBillDeductEntity.getStatus()), "", OperateLogEnum.CLAIM_MATCH_ITEM_GOODS_TAX_NO_SUCCESS, "", 0L, "系统");
		}
		
		//计算索赔主信息和明细信息的容差
        diffAmountCalculate(tXfBillDeductEntity, tXfBillDeductItemRefEntities);
        
        if(hasZeroTaxRate(tXfBillDeductEntity.getId())){//索赔明细包含0税率明细发送例外报告
            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
            newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.CLAIM_DETAIL_ZERO_TAX_RATE);
            newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
            //税差总额计算
            BigDecimal taxAmountBalance = tXfBillDeductEntity.getTaxAmount().subtract(tXfBillDeductItemRefEntities.stream().map(TXfBillDeductItemRefEntity::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            newExceptionReportEvent.setTaxBalance(taxAmountBalance);
            applicationContext.publishEvent(newExceptionReportEvent);
            log.error("索赔单 {}  发送0税率明细税差 {} ", tXfBillDeductEntity.getBusinessNo(), newExceptionReportEvent);
        }else {
            //计算税差
            final BigDecimal taxAmountBalance = tXfBillDeductEntity.getTaxAmount().subtract(tXfBillDeductItemRefEntities.stream().map(TXfBillDeductItemRefEntity::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            if (taxAmountBalance.compareTo(BigDecimal.ZERO) != 0) {
                NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
                newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.WITH_DIFF_TAX);
                newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
                //税差总额计算
                newExceptionReportEvent.setTaxBalance(taxAmountBalance);
                applicationContext.publishEvent(newExceptionReportEvent);
                log.error("索赔单 {}  发送税差例外报告 {} ", tXfBillDeductEntity.getBusinessNo(), newExceptionReportEvent);
            }
        }
		/**
		 * 如果存在未匹配的税编，状态未待匹配税编，
		 */
        Integer status = matchTaxNoFlag ? TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode()  : TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode() ;
        tmp.setStatus(status);
        tmp.setUpdateTime(new Date());
        tXfBillDeductExtDao.updateById(tmp);
        tXfBillDeductEntity.setStatus(status);
        return tXfBillDeductEntity;
    }

    private void changeSplit(List<ClaimDoItemMatchTemp> tempList) {
        // 最后一条匹配明细，判断是否拆分
        ClaimDoItemMatchTemp lastTemp = tempList.get(tempList.size() - 1);
        log.info("最后一条匹配明细:[{}]", JSON.toJSONString(lastTemp));
        if (lastTemp.needChangeSplit()) {
            log.info("索赔单明细匹配，最后一条明细拆分取整异常");
            BigDecimal overAmount = lastTemp.getRemainingAmount().subtract(lastTemp.getUseAmount());
            boolean change = false;
            for (int i = tempList.size() - 2; i >= 0; i--) {
                ClaimDoItemMatchTemp temp = tempList.get(i);
                if (!temp.needChangeSplit(overAmount)) {
                    log.info("替换最后一条明细进行拆分:[{}]-[{}]", temp, overAmount);
                    change = true;
                    // 可以使用当前明细进行拆分，重新计算使用金额
                    BigDecimal amount = temp.getUseAmount().subtract(overAmount);
                    // 此次使用数量
                    BigDecimal canUseQuantity = getCanUseQuantity(amount, temp.getCanUseQuantity(), temp.getRemainingAmount(), temp.isIntegerFlag());
                    temp.setUseAmount(amount);
                    temp.setUseQuantity(canUseQuantity);

                    TXfBillDeductItemRefEntity itemRefEntity = temp.getItemRefEntity();
                    // 使用额度 == 含税金额
                    itemRefEntity.setUseAmount(amount);
                    itemRefEntity.setQuantity(canUseQuantity);
                    //计算不含税金额再计算税额
                    BigDecimal amountOutWithTax = amount.divide(BigDecimal.ONE.add(temp.getTaxRate()), 2, RoundingMode.HALF_UP );
                    itemRefEntity.setTaxAmount(amount.subtract(amountOutWithTax));
                    itemRefEntity.setAmountWithTax(amount);
                    break;
                }
            }
            // 可以使用当前明细进行拆分，重新计算使用金额
            lastTemp.setUseAmount(lastTemp.getRemainingAmount());
            if (change) {
                // 最后一条明细剩余金额全部被使用
                lastTemp.setUseQuantity(lastTemp.getCanUseQuantity());

                BigDecimal amount = lastTemp.getUseAmount();
                TXfBillDeductItemRefEntity lastItemRefEntity = lastTemp.getItemRefEntity();
                // 使用额度 == 含税金额
                lastItemRefEntity.setUseAmount(amount);
                lastItemRefEntity.setQuantity(lastTemp.getUseQuantity());
                //计算不含税金额再计算税额
                BigDecimal lastAmountOutWithTax = amount.divide(BigDecimal.ONE.add(lastTemp.getTaxRate()), 2, RoundingMode.HALF_UP);
                lastItemRefEntity.setTaxAmount(amount.subtract(lastAmountOutWithTax));
                lastItemRefEntity.setAmountWithTax(amount);
            }
        }
    }

    /**
	 * <pre>
	 *  1索赔单金额大于明细汇总金额时， 通过配置来决定是否要把差额填充到明细单价最高的行， 
	 *  2然后根据数量不变反算单价。
	 * </re>
	 * @param billDeductEntity 单据
	 * @param items            匹配到的明细
	 */
    private void diffAmountCalculate(TXfBillDeductEntity billDeductEntity, List<TXfBillDeductItemRefEntity> items) {
    	//索赔明细信息总金额
        BigDecimal itemAmount = items.stream().map(TXfBillDeductItemRefEntity::getUseAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //索赔主信息和明细信息的金额查
        BigDecimal diffAmount = billDeductEntity.getAmountWithTax().subtract(itemAmount);
        boolean isModifyItem = isModifyItem(billDeductEntity.getBusinessNo(), itemAmount, diffAmount);
        log.info("diffAmountCalculate -----businessNo:{},itemAmount:{},diffAmount:{},isModifyItem result:{}", billDeductEntity.getBusinessNo(), itemAmount, diffAmount);
        if (isModifyItem) {
        	//平容差
        	int listLen = 0;
        	BigDecimal tempDiffAmount = new BigDecimal(diffAmount.toPlainString());//用来计算最后一行金额
        	for (TXfBillDeductItemRefEntity tXfBillDeductItemRefEntity : items) {
        		listLen ++;
        		long deductItemId = tXfBillDeductItemRefEntity.getDeductItemId();
        		TXfBillDeductItemEntity itemEntity = billDeductItemService.getBaseMapper().selectById(deductItemId);
        		//算新含税金额
        		BigDecimal itemDiffAmount = null;
        		if(listLen == items.size()) {
        			itemDiffAmount = tempDiffAmount;
        		}else {
        			itemDiffAmount = tXfBillDeductItemRefEntity.getUseAmount().divide(itemAmount, 6, RoundingMode.HALF_UP).multiply(diffAmount).setScale(2, RoundingMode.HALF_UP);
        		}
        		tempDiffAmount = tempDiffAmount.subtract(itemDiffAmount);
        		BigDecimal useAmount = itemDiffAmount.add(tXfBillDeductItemRefEntity.getUseAmount());
        		//算不含税金额
        		BigDecimal amountOutWithTax = useAmount.divide(BigDecimal.ONE.add(itemEntity.getTaxRate()), 2, RoundingMode.HALF_UP );
        		//税额
        		tXfBillDeductItemRefEntity.setTaxAmount(useAmount.subtract(amountOutWithTax));
//        		if(tXfBillDeductItemRefEntity.getQuantity().compareTo(BigDecimal.ZERO)!=0){
                // 修改原始数据，后面方法计算总税差需要用到新税额
                new LambdaUpdateChainWrapper<>(deductItemRefBatchService.getBaseMapper())
                        .eq(TXfBillDeductItemRefEntity::getId, tXfBillDeductItemRefEntity.getId())
                        .set(TXfBillDeductItemRefEntity::getUseAmount, useAmount)
                        .set(TXfBillDeductItemRefEntity::getTaxAmount, tXfBillDeductItemRefEntity.getTaxAmount())
                        .set(TXfBillDeductItemRefEntity::getAmountWithTax, useAmount)
                        .set(TXfBillDeductItemRefEntity::getDiffAmount, itemDiffAmount)
                        .set(TXfBillDeductItemRefEntity::getUpdateTime, new Date())
                        .set(TXfBillDeductItemRefEntity::getPrice, useAmount.divide(tXfBillDeductItemRefEntity.getQuantity().abs(), 15, RoundingMode.HALF_UP))
                        .update();
//                }
			}
            
//            
//                    .ifPresent(it -> new LambdaQueryChainWrapper<>(billDeductItemService.getBaseMapper())
//                            .eq(TXfBillDeductItemEntity::getId, it.getDeductItemId())
//                            .oneOpt()
//                            .map(TXfBillDeductItemEntity::getTaxRate)
//                            .ifPresent(t -> {
//                                BigDecimal useAmount = it.getUseAmount().add(diffAmount);
//                                //更新
//                                BigDecimal decimal = useAmount.multiply(t).setScale(2, RoundingMode.HALF_UP);
//                                //修改原始数据，后面方法计算总税差需要用到新税额
//                                it.setTaxAmount(decimal);
//                                new LambdaUpdateChainWrapper<>(deductItemRefBatchService.getBaseMapper())
//                                        .eq(TXfBillDeductItemRefEntity::getId, it.getId())
//                                        .set(TXfBillDeductItemRefEntity::getUseAmount, useAmount)
//                                        .set(TXfBillDeductItemRefEntity::getTaxAmount, decimal)
//                                        .set(TXfBillDeductItemRefEntity::getAmountWithTax, useAmount.add(it.getTaxAmount()))
//                                        .set(TXfBillDeductItemRefEntity::getDiffAmount, diffAmount)
//                                        .set(TXfBillDeductItemRefEntity::getPrice, useAmount.divide(it.getQuantity().abs(), 5, RoundingMode.HALF_UP))
//                                        .update();
//                            }));
        }
    }

    private boolean isModifyItem(String businessNo, BigDecimal itemAmount, BigDecimal diffAmount) {
        boolean result = "-1".equalsIgnoreCase(diffAmountRate) || diffAmount.compareTo(BigDecimal.ZERO) > 0 && itemAmount.compareTo(BigDecimal.ZERO) > 0
                && diffAmount.divide(itemAmount, 2, RoundingMode.HALF_UP).compareTo(new BigDecimal(diffAmountRate)) <= 0;
        log.info("businessNo:{},itemAmount:{},diffAmount:{},isModifyItem result:{}", businessNo, itemAmount, diffAmount, result);
        return result;
    }

	/**
	 * 重新补充税编
	 * 
	 * @param deductId
	 * @param businessNo
	 */
    @Transactional(rollbackFor = Exception.class)
    public void reMatchClaimTaxCode(Long deductId, String businessNo) {
        //https://jira.xforceplus.com/browse/WALMART-363 问题对reMatchClaimTaxCode1重写
        List<TXfBillDeductItemEntity> itemEntities = tXfBillDeductItemExtDao.queryItemsByBillId(deductId, TXfInvoiceDeductTypeEnum.CLAIM.getCode(), TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
        //fixTaxCode方法更新税编相关字段，并且返回错误信息
        String errorMsg = itemEntities.stream().map(item -> {
            String fixTaxCode = this.fixTaxCode(item);
            if (BigDecimal.ZERO.compareTo(item.getTaxRate()) == 0) {
                log.info("0税率匹配税编失败不阻断:[{}]-[{}]", item.getId(), fixTaxCode);
                return "";
            }
            return fixTaxCode;
        }).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
		log.info("业务单businessNo:{}, deductId:{} 匹配税编处理结果信息：{}", businessNo, deductId, errorMsg);
        Optional<TXfBillDeductItemEntity> report = itemEntities.stream().filter(x -> StringUtils.isEmpty(x.getGoodsTaxNo())).findAny();
        if (report.isPresent()) {
			log.info("业务单 businessNo:{}, deductId:{} 存在未匹配税编的明细：{}", businessNo, deductId, report.get());
            // 生成例外报告 isAddReport 没有多大作用，因为例外报告新增更新功能，不会重新生成新的例外报告，而且还能更新异常原因。
            TXfBillDeductEntity tXfBillDeductEntity = tXfBillDeductExtDao.selectById(deductId);
            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
            newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.NOT_MATCH_GOODS_TAX);
            newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
            newExceptionReportEvent.setMessage(errorMsg);
            applicationContext.publishEvent(newExceptionReportEvent);
        } else {
			log.info("业务单businessNo:{}, deductId:{}  明细匹配税编完成。", businessNo, deductId);
            for(TXfBillDeductItemEntity tmp:itemEntities){
                tXfBillDeductItemExtDao.updateById(tmp);
            }
            TXfBillDeductEntity tXfBillDeductEntityTmp = new TXfBillDeductEntity();
            tXfBillDeductEntityTmp.setId(deductId);
            tXfBillDeductEntityTmp.setUpdateTime(new Date());
            tXfBillDeductEntityTmp.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
            tXfBillDeductExtDao.updateById(tXfBillDeductEntityTmp);
            // 修改例外报告为已处理
            ExceptionReportProcessListener.publishClaimProcessEvent(businessNo,deductId, ExceptionReportCodeEnum.NOT_MATCH_GOODS_TAX);

            // 添加日志履历
            operateLogService.addDeductLog(deductId, TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE, "", OperateLogEnum.CLAIM_MATCH_ITEM_GOODS_TAX_NO_SUCCESS, "", 0L, "系统");
        }
    }

    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public boolean reMatchClaimTaxCode1(Long deductId, String businessNo, boolean isAddReport) {
        List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryItemsByBillId(deductId, TXfInvoiceDeductTypeEnum.CLAIM.getCode(), TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
//        https://jira.xforceplus.com/browse/WALMART-363
//        匹配后出现明细补充好税编但是结算单状态没有改变的情况，需要在下次定时任务时重试
//        tXfBillDeductItemEntities =   tXfBillDeductItemEntities.stream().filter(x -> StringUtils.isEmpty(x.getGoodsTaxNo())).collect(Collectors.toList());
//        if(CollectionUtils.isEmpty(tXfBillDeductItemEntities)){
//            return false;
//        }
        int errorNum = tXfBillDeductItemEntities.size();
        tXfBillDeductItemEntities.forEach(this::fixTaxCode);
        tXfBillDeductItemEntities =   tXfBillDeductItemEntities.stream().filter(x -> StringUtils.isNotEmpty(x.getGoodsTaxNo())).collect(Collectors.toList());
        int currentNum = tXfBillDeductItemEntities.size();
        if (CollectionUtils.isNotEmpty(tXfBillDeductItemEntities)) {
            for(TXfBillDeductItemEntity tmp:tXfBillDeductItemEntities){
                tXfBillDeductItemExtDao.updateById(tmp);
            }
        }
		/**
		 * 表示税编补充完整，更新索赔单状态信息
		 */
        if (currentNum == errorNum) {
            TXfBillDeductEntity tXfBillDeductEntityTmp = new TXfBillDeductEntity();
            tXfBillDeductEntityTmp.setId(deductId);
            tXfBillDeductEntityTmp.setUpdateTime(new Date());
            tXfBillDeductEntityTmp.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
            tXfBillDeductExtDao.updateById(tXfBillDeductEntityTmp);
            ExceptionReportProcessListener.publishClaimProcessEvent(businessNo,deductId, ExceptionReportCodeEnum.NOT_MATCH_GOODS_TAX);

            // 添加日志履历
            operateLogService.addDeductLog(deductId, TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE, "", OperateLogEnum.CLAIM_MATCH_ITEM_GOODS_TAX_NO_SUCCESS, "", 0L, "系统");
            return true;

        }else{
            if (isAddReport) {
				/**
				 * 依然存在未匹配的税编
				 */
                TXfBillDeductEntity tXfBillDeductEntity = tXfBillDeductExtDao.selectById(deductId);
                NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
                newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.NOT_MATCH_GOODS_TAX);
                newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
                applicationContext.publishEvent(newExceptionReportEvent);
            }
            return false;
        }
    }

	/**
	 * 索赔单 匹配蓝票
	 *
	 * @return
	 */
    public boolean claimMatchBlueInvoice() {
        Long deductId = 1L;
        Integer limit = 50;
        //Map<String, BigDecimal> nosuchInvoiceSeller = new HashMap<>();
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,null, limit
                , TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue()
                , TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
        while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
          for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
            try {
              List<Supplier<Boolean>> successSuppliers = new ArrayList<>();
              successSuppliers.add(() -> {
                claimMatchBlueInvoice(tXfBillDeductEntity);
                return true;
              });
              transactionalService.execute(successSuppliers);
            } catch (Exception e) {
              log.error("蓝票匹配索赔异常,单据：" + tXfBillDeductEntity.getBusinessNo(), e);
            }
          }
          deductId = tXfBillDeductEntities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
          tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId, null, limit
                  , TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue()
                  , TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
        }
        return false;
    }

	/**
	 * 索赔单 匹配蓝票
	 *
	 * @return
	 */
    public boolean claimMatchBlueInvoice(TXfBillDeductEntity tXfBillDeductEntity) {
        log.info("索赔单匹配蓝票执行开始 businessNo:{}",tXfBillDeductEntity.getBusinessNo());
        List<BlueInvoiceService.MatchRes> matchResList = new ArrayList<>();
		try {
			if (tXfBillDeductEntity.getStatus().compareTo(TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode()) != 0) {
				log.info("{} 类型单据{} 状态为{} 跳过匹配蓝票 ", "索赔单", tXfBillDeductEntity.getBusinessNo(), tXfBillDeductEntity.getStatus());
				return false;
			}
			TAcOrgEntity tAcSellerOrgEntity = queryOrgInfo(tXfBillDeductEntity.getSellerNo(), true);
			TAcOrgEntity tAcPurcharserOrgEntity = queryOrgInfo(tXfBillDeductEntity.getPurchaserNo(), false);
			if (Objects.isNull(tAcPurcharserOrgEntity) || Objects.isNull(tAcSellerOrgEntity)) {
				log.info(" 购销方信息不完整 sellerNo : {} sellerOrgEntity{}  purcharseNo : {} purchaserOrgEntity：{}", tXfBillDeductEntity.getSellerNo(), tAcSellerOrgEntity, tXfBillDeductEntity.getPurchaserNo(), tAcPurcharserOrgEntity);
				return false;
			}
			// 获取索赔明细去匹配蓝票
			boolean isAllZero = true;
			List<TXfBillDeductItemRefEntity> billDeductItemRefList = queryBillDeductItemRef(tXfBillDeductEntity.getId());
			for (TXfBillDeductItemRefEntity deductItemRef : billDeductItemRefList) {
				TXfBillDeductItemEntity tXfBillDeductItem = tXfBillDeductItemDao.selectById(deductItemRef.getDeductItemId());
				boolean isZero = tXfBillDeductItem.getTaxRate().compareTo(BigDecimal.ZERO) == 0;
				if (!isZero) {
					isAllZero = false;
				}
				try {
					List<BlueInvoiceService.MatchRes> matchItemResList = deductBlueInvoiceService
							.matchBlueInvoiceWithoutTrans(tXfBillDeductEntity.getBusinessNo(),
									tAcSellerOrgEntity.getTaxNo(), tAcPurcharserOrgEntity.getTaxNo(),
									tXfBillDeductItem.getTaxRate().multiply(BigDecimal.valueOf(100)),
									deductItemRef.getUseAmount(), true, true, TXfDeductionBusinessTypeEnum.CLAIM_BILL);

					if (CollectionUtils.isNotEmpty(matchItemResList)) {
						matchResList.addAll(matchItemResList);
					}
				} catch (NoSuchInvoiceException e) {
					log.error("索赔单明细匹配蓝票明细异常 isZero:{}", isZero, e);
					if (!isZero) {
						// 不为0税率，则抛出异常
						throw e;
					}
				}
			}
            if (CollectionUtils.isEmpty(matchResList) && !isAllZero) {
                log.error("{} 类型单据 销方:{}  蓝票不足，匹配失败 单号 {}", "索赔单", tXfBillDeductEntity.getSellerNo(), tXfBillDeductEntity.getBusinessNo());
                throw new NoSuchInvoiceException("整单匹配蓝票失败!");
            }
			// 通知例外报告解除该索赔单未匹配蓝票例外记录
			ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.NOT_MATCH_BLUE_INVOICE);

			TXfBillDeductEntity tmp = new TXfBillDeductEntity();
			tmp.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
			tmp.setId(tXfBillDeductEntity.getId());
			tmp.setUpdateTime(new Date());
			tXfBillDeductExtDao.updateById(tmp);
			if (CollectionUtils.isNotEmpty(matchResList)) {
				// 建立索赔单与发票明细占用关系记录
				List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList = makeDeductInvoiceDetail(matchResList, tXfBillDeductEntity);
				deductInvoiceDetailService.saveBatch(deductInvoiceDetailList);
				// 建立索赔单与发票占用关系记录
				List<TXfBillDeductInvoiceEntity> deductInvoiceList = makeDeductInvoice(tXfBillDeductEntity, deductInvoiceDetailList);
				for (TXfBillDeductInvoiceEntity deductInvoice : deductInvoiceList) {
					tXfBillDeductInvoiceDao.insert(deductInvoice);
				}
			}
			// 匹配到蓝票需要处理例外报告
			ExceptionReportProcessListener.publishClaimProcessEvent(tXfBillDeductEntity, ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE);
        }catch (NoSuchInvoiceException n ) {
			NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
			newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
			newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE);
			newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
			newExceptionReportEvent.setMessage(n.getMessage());
			applicationContext.publishEvent(newExceptionReportEvent);
			log.info("索赔单单据匹配合并失败销方蓝票不足->sellerNo : {} purcharseNo : {}, businessNo {}", tXfBillDeductEntity.getSellerNo(), tXfBillDeductEntity.getPurchaserNo(), tXfBillDeductEntity.getBusinessNo());
			// 回滚发票金额占用
			if (CollectionUtils.isNotEmpty(matchResList)) {
				deductBlueInvoiceService.withdrawBlueInvoice(matchResList);
			}
			throw n;
		} catch (Exception e) {
			NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
			newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
			newExceptionReportEvent.setReportCode(ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE);
			newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
			newExceptionReportEvent.setMessage(e.getMessage());
			applicationContext.publishEvent(newExceptionReportEvent);
			log.error("索赔单 匹配蓝票 异常,单据id:{},BusinessNo:{}", tXfBillDeductEntity.getId(), tXfBillDeductEntity.getBusinessNo(), e);
			// 回滚发票金额占用
			if (CollectionUtils.isNotEmpty(matchResList)) {
				deductBlueInvoiceService.withdrawBlueInvoice(matchResList);
			}
			throw e;
		}
		return true;
    }


  /**
   * 索赔单匹配蓝票明细
   */
  private List<TXfBillDeductInvoiceDetailEntity> makeDeductInvoiceDetail(
          List<BlueInvoiceService.MatchRes> matchResList
          , TXfBillDeductEntity deductEntity){
    if (CollectionUtils.isEmpty(matchResList)){
      log.info("索赔单未匹配到任何蓝票 deductId:{}",deductEntity.getId());
      throw new NoSuchInvoiceException("索赔单未匹配到任何蓝票");
    }
    //生成索赔单发票明细关系记录
    List<TXfBillDeductInvoiceDetailEntity> detailEntityList = new ArrayList<>();
    TXfBillDeductInvoiceDetailEntity detailEntity;
    for (BlueInvoiceService.MatchRes matchRes : matchResList){
      if (CollectionUtils.isEmpty(matchRes.getInvoiceItems())){
        log.warn("索赔单未匹配到任何蓝票明细 deductId:{} invoiceCode:{} invoiceNo:{}"
                ,deductEntity.getId(),matchRes.getInvoiceCode(),matchRes.getInvoiceNo());
        throw new NoSuchInvoiceException("索赔单未匹配到任何蓝票明细");
      }
      for (BlueInvoiceService.InvoiceItem invoiceItem : matchRes.getInvoiceItems()){
        detailEntity = new TXfBillDeductInvoiceDetailEntity();
        detailEntity.setId(idSequence.nextId());
        detailEntity.setDeductId(deductEntity.getId());
        detailEntity.setInvoiceDetailId(invoiceItem.getItemId());
        detailEntity.setBusinessNo(deductEntity.getBusinessNo());
        detailEntity.setBusinessType(deductEntity.getBusinessType());
        detailEntity.setInvoiceId(matchRes.getInvoiceId());
        detailEntity.setInvoiceCode(matchRes.getInvoiceCode());
        detailEntity.setInvoiceNo(matchRes.getInvoiceNo());
        detailEntity.setPlusMinusFlag(0);
        if (StringUtils.isNotBlank(invoiceItem.getTaxRate())) {
            detailEntity.setTaxRate(new BigDecimal(invoiceItem.getTaxRate()).movePointLeft(2));
        }
        detailEntity.setUseAmountWithoutTax(invoiceItem.getMatchedDetailAmount());
        if (invoiceItem.getMatchedDetailAmount() != null && invoiceItem.getMatchedTaxAmount() != null) {
            detailEntity.setUseAmountWithTax(invoiceItem.getMatchedDetailAmount()
                    .add(invoiceItem.getMatchedTaxAmount()));
        }
        detailEntity.setUseTaxAmount(invoiceItem.getMatchedTaxAmount());
        detailEntity.setUseQuantity(invoiceItem.getMatchedNum());
        detailEntity.setIsOil(matchRes.getIsOil());
        detailEntity.setStatus(0);//0正常 1撤销
        detailEntity.setCreateTime(new Date());
        detailEntity.setUpdateTime(new Date());
        detailEntityList.add(detailEntity);
      }
    }
    return detailEntityList;
  }

    private List<TXfBillDeductItemRefEntity> queryBillDeductItemRef(Long deductId){
        QueryWrapper<TXfBillDeductItemRefEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfBillDeductItemRefEntity.DEDUCT_ID,deductId);
        queryWrapper.eq(TXfBillDeductItemRefEntity.STATUS, 0);
        return tXfBillDeductItemRefDao.selectList(queryWrapper);
    }

    private boolean hasZeroTaxRate(Long deductId) {
        List<TXfBillDeductItemRefEntity> billDeductItemRefList = queryBillDeductItemRef(deductId);
        if(CollectionUtils.isEmpty(billDeductItemRefList)){
            return false;
        }
        List<Long> deductItemIdList = billDeductItemRefList.stream().map(TXfBillDeductItemRefEntity::getDeductItemId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(deductItemIdList)){
            return false;
        }

        int count = 0;
        if (deductItemIdList.size()>1000){
          int fromIndex = 0;
          List<Long> subDeductItemIdList;
          do {
              if (fromIndex+1000 > deductItemIdList.size()){
                  subDeductItemIdList = deductItemIdList.subList(fromIndex, deductItemIdList.size());
              }else {
                  subDeductItemIdList = deductItemIdList.subList(fromIndex, fromIndex + 1000);
              }
            QueryWrapper<TXfBillDeductItemEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(TXfBillDeductItemEntity.ID, subDeductItemIdList);
            queryWrapper.eq(TXfBillDeductItemEntity.TAX_RATE, BigDecimal.ZERO);
            count += tXfBillDeductItemDao.selectCount(queryWrapper);
            fromIndex+=1000;
          }while (fromIndex<deductItemIdList.size());
        }else {
          QueryWrapper<TXfBillDeductItemEntity> queryWrapper = new QueryWrapper<>();
          queryWrapper.in(TXfBillDeductItemEntity.ID, deductItemIdList);
          queryWrapper.eq(TXfBillDeductItemEntity.TAX_RATE, BigDecimal.ZERO);
          count = tXfBillDeductItemDao.selectCount(queryWrapper);
        }

        return count > 0 ? true : false;
    }

	/**
	 * 合并 索赔单为结算单
	 * 
	 * @return
	 */
    public boolean mergeClaimSettlement() {

		/**
		 * 查询符合条件的索赔单，购销一致维度，状态为待生成结算单
		 */
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitableClaimBill(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
		/**
		 * 查询索赔单明细，组装结算单明细信息
		 */
		int count = 0;
        for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
            try {
                doMergeClaim(tXfBillDeductEntity);
            } catch (Exception e) {

                log.error("索赔单组合结算失败: purchase_no :{} ,seller_no:{} status: {} Exception:{}",tXfBillDeductEntity.getPurchaserNo(),tXfBillDeductEntity.getSellerNo(), TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getDesc(),e);
            }
            count++;
        }
        List<String> businessNos = tXfBillDeductEntities.stream().map(TXfBillDeductEntity::getBusinessNo).collect(Collectors.toList());
        log.info("claimSettlementScheduler本次执行数量:{},业务单号:{}",count,businessNos);
        return true;
    }

    /**
     * <p>
     * 1、业务单生成结算单
     * 2、@TODO,此方法应该是有问题的，如果数据一直在跑，会影响修改数据
     * </p>
     * @param tXfBillDeductEntity
     */
    @Transactional(rollbackFor = Exception.class)
    public void doMergeClaim(TXfBillDeductEntity tXfBillDeductEntity) {
    	//业务单转成成结算单保存
        TXfSettlementEntity tXfSettlementEntity =  trans2Settlement(Arrays.asList(tXfBillDeductEntity), TXfDeductionBusinessTypeEnum.CLAIM_BILL);
        
        //修改索赔单的状态
        tXfBillDeductExtDao.updateSuitableClaimBill(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode(), TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode(), 
        		tXfSettlementEntity.getSettlementNo(), tXfBillDeductEntity.getPurchaserNo(), tXfBillDeductEntity.getSellerNo());
        
        //查询批次下的索赔单信息
        List<TXfBillDeductEntity> claimList = tXfBillDeductExtDao.queryBillBySettlementNoAndBusinessType(tXfSettlementEntity.getSettlementNo(), TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue());
        //把索赔单和结算单关联关系保存
		if (claimList != null && claimList.size() > 0) {
			claimList.forEach(item -> {
				billSettlementService.addBillSettlement(billSettlementService.bulidBillSettlementEntity(tXfSettlementEntity, item));

                // 添加日志履历
                operateLogService.addDeductLog(item.getId(), item.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(item.getStatus()), item.getRefSettlementNo(), OperateLogEnum.CLAIM_MERGE_SETTLEMENT, item.getRefSettlementNo(), 0L, "系统");
			});
		}
    }

    /**
     * 获取可用数量
     * @param remainingAmount 明细剩余可用金额
     * @param amountWithTax 明细总含税金额
     * @param quantity 明细总数量
     * @param useAmount 此次使用金额
     * @return 此次可用数量
     */
    public BigDecimal getCanUseQuantity(BigDecimal remainingAmount, BigDecimal amountWithTax, BigDecimal quantity, BigDecimal useAmount) {
        // 判断原数量是否整数， 整数返回的数量就为整数
        boolean isInteger = BigDecimalUtil.isInteger(quantity);
        // 明细剩余可用数量
        BigDecimal canUseQuantityTotal = getCanUseQuantity(remainingAmount, quantity, amountWithTax, isInteger);

        return getCanUseQuantity(useAmount, canUseQuantityTotal, remainingAmount, isInteger);

    }

    private BigDecimal getCanUseQuantity(BigDecimal useAmount, BigDecimal canUseQuantityTotal, BigDecimal remainingAmount, Boolean isInteger) {
        log.info("getCanUseQuantity,useAmount:{},canUseQuantityTotal:{},remainingAmount:{},isInteger:{}",
                useAmount,canUseQuantityTotal,remainingAmount,isInteger);
        if (useAmount.compareTo(remainingAmount) == 0) {
            return canUseQuantityTotal;
        }
        // 正负判断
        int sign = canUseQuantityTotal.signum();
        BigDecimal canUserQuantity = useAmount.multiply(canUseQuantityTotal).divide(remainingAmount, isInteger ? 0 : 15, BigDecimal.ROUND_HALF_DOWN);
        if (canUserQuantity.compareTo(canUseQuantityTotal) == 0 && canUseQuantityTotal.abs().compareTo(BigDecimal.ONE) > 0) {
            canUserQuantity = canUseQuantityTotal.subtract(sign > 0 ? BigDecimal.ONE : BigDecimal.ONE.negate());
        } else if (canUserQuantity.compareTo(BigDecimal.ZERO) == 0) {
            canUserQuantity = sign > 0 ? BigDecimal.ONE : BigDecimal.ONE.negate();
        }
        log.info("getCanUseQuantity,canUserQuantity:{}", canUserQuantity);
        return canUserQuantity;
    }

}
