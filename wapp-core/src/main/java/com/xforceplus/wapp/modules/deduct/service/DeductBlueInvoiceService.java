package com.xforceplus.wapp.modules.deduct.service;

import static com.xforceplus.wapp.handle.InvoiceHandler.GOODS_LIST_TEXT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.converters.TDxRecordInvoiceDetailEntityConvertor;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfSysLogModuleEnum;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceExtService;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceDetailBean;
import com.xforceplus.wapp.modules.deduct.model.DeductInvoiceDetailData;
import com.xforceplus.wapp.modules.deduct.model.threadlocal.BlueInvoiceMatchHolder;
import com.xforceplus.wapp.modules.syslog.util.SysLogUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDetailDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemInvoiceDetailDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemInvoiceDetailEntity;
import com.xforceplus.wapp.util.BigDecimalUtil;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务单匹配蓝票服务
 */
@Service
@Slf4j
public class DeductBlueInvoiceService {

  @Autowired
  private RecordInvoiceService invoiceService;
  @Autowired
  private RecordInvoiceExtService extInvoiceService;
  /**
   * 蓝冲用途的发票服务
   */
  @Autowired
  private BlueInvoiceRelationService blueInvoiceRelationService;
  @Autowired
  private TDxRecordInvoiceDetailDao tDxRecordInvoiceDetailDao;
  @Autowired
  private TXfBillDeductInvoiceDetailDao deductInvoiceDetailDao;
  @Autowired
  private TXfSettlementItemInvoiceDetailDao settlementItemInvoiceDetailDao;
  @Autowired
  private TXfBillDeductInvoiceDao deductInvoiceDao;
  @Autowired
  private TXfSettlementDao tXfSettlementDao;
  @Autowired
  private TXfSettlementItemDao tXfSettlementItemDao;
  @Autowired
  private TXfBillDeductDao tXfBillDeductDao;

  @Transactional(rollbackFor = Exception.class)
  public List<BlueInvoiceService.MatchRes> matchBlueInvoiceByDetail(BigDecimal matchAmount,BigDecimal taxRate
          ,List<DeductInvoiceDetailData> detailDataList,boolean isCommitInvoiceRemain
          ,TXfDeductionBusinessTypeEnum businessTypeEnum){
    List<DeductInvoiceDetailData> matchedDetailDataList = new ArrayList<>();
    DeductInvoiceDetailData matchedDetailData;
    BigDecimal leftMatchAmount = matchAmount;
    for (DeductInvoiceDetailData detailData : detailDataList){
      if (BigDecimal.ZERO.compareTo(detailData.getLeftDetailAmount())>=0){
        log.info("手工匹配明细金额无剩余，继续一条明细 itemId:{}",detailData.getInvoiceDetailId());
        continue;
      }
      matchedDetailData = new DeductInvoiceDetailData();
      BeanUtil.copyProperties(detailData,matchedDetailData);
      if (leftMatchAmount.compareTo(detailData.getLeftDetailAmount())<=0){
        //已完成匹配，扣减明细剩余占用金额后返回
        matchedDetailData.setMatchedDetailAmount(leftMatchAmount);
        //扣减剩余不含税和数量
        detailData.setLeftDetailAmount(detailData.getLeftDetailAmount()
                .subtract(matchedDetailData.getMatchedDetailAmount()));
        //计算占用税额、数量
        calMatchedInvoiceDetailUse(matchedDetailData,detailData,taxRate);
        matchedDetailDataList.add(matchedDetailData);
        break;
      }
      //未完成匹配，扣减明细占用金额后继续下一条
      leftMatchAmount = leftMatchAmount.subtract(detailData.getLeftDetailAmount());
      matchedDetailData.setMatchedDetailAmount(detailData.getLeftDetailAmount());
      detailData.setLeftDetailAmount(new BigDecimal("0.00"));
      //计算占用税额、数量
      calMatchedInvoiceDetailUse(matchedDetailData,detailData,taxRate);
      matchedDetailDataList.add(matchedDetailData);
    }

    if (CollectionUtils.isEmpty(matchedDetailDataList)){
      log.error("匹配占用明细详情失败 matchAmount:{} detailDataList:{}"
              ,matchAmount.toPlainString(),JSON.toJSONString(detailDataList));
      throw new EnhanceRuntimeException("匹配占用明细详情失败");
    }

    return matchBlueInvoiceByDetail(matchedDetailDataList,isCommitInvoiceRemain,businessTypeEnum);
  }


    /**
     * 匹配蓝票信息，根据已有的蓝票明细
     * @param matchedDetailDataList
     * @param isCommitInvoiceRemain
     * @param businessTypeEnum
     * @return
     */
	private List<BlueInvoiceService.MatchRes> matchBlueInvoiceByDetail(List<DeductInvoiceDetailData> matchedDetailDataList, boolean isCommitInvoiceRemain, TXfDeductionBusinessTypeEnum businessTypeEnum) {
		Map<Long, BlueInvoiceService.MatchRes> matchResMap = new HashMap<>();
		Map<Long, TDxRecordInvoiceEntity> invoiceMap = new HashMap<>();
		BlueInvoiceService.MatchRes matchRes;
		TDxRecordInvoiceEntity invoice;
		TDxRecordInvoiceDetailEntity invoiceDetail;
		// 1.遍历匹配明细请求记录，校验明细剩余额度是否够用
		for (DeductInvoiceDetailData matchedDetailData : matchedDetailDataList) {
			// 1.1获取发票主信息及明细信息
			invoice = extInvoiceService.getById(matchedDetailData.getInvoiceId());
			if (invoice == null) {
				throw new NoSuchInvoiceException("不存在发票信息:" + matchedDetailData.getInvoiceId());
			}
			// 初始化发票剩余可用金额
			if (invoice.getRemainingAmount() == null) {
				invoice.setRemainingAmount(invoice.getInvoiceAmount());
			}

			invoiceDetail = tDxRecordInvoiceDetailDao.selectById(matchedDetailData.getInvoiceDetailId());
			if (invoiceDetail == null) {
				throw new NoSuchInvoiceException("不存在发票明细信息:" + matchedDetailData.getInvoiceDetailId());
			}
			MatchedInvoiceDetailBean matchedInvoiceDetailBean = gainMatchedInvoiceDetail(invoiceDetail, invoice);
			if (matchedInvoiceDetailBean == null) {
				log.info("未获取到可匹配明细 uuid:{} itemId: {}", invoiceDetail.getUuid(), invoiceDetail.getId());
				throw new EnhanceRuntimeException("不可匹配发票明细 ID[" + invoiceDetail.getId() + "]");
			}
			// 1.2 计算明细抵扣金额
			BlueInvoiceService.InvoiceItem invoiceItem = new BlueInvoiceService.InvoiceItem();
			invoiceItem.setItemId(invoiceDetail.getId());
			invoiceItem.setInvoiceCode(invoiceDetail.getInvoiceCode());
			invoiceItem.setInvoiceNo(invoiceDetail.getInvoiceNo());
			invoiceItem.setDetailNo(invoiceDetail.getDetailNo());
			invoiceItem.setGoodsName(invoiceDetail.getGoodsName());
			invoiceItem.setModel(invoiceDetail.getModel());
			invoiceItem.setUnit(invoiceDetail.getUnit());
			invoiceItem.setTaxRate(invoiceDetail.getTaxRate());
			invoiceItem.setGoodsNum(invoiceDetail.getGoodsNum());
			invoiceItem.setNum(invoiceDetail.getNum());
			invoiceItem.setUnitPrice(invoiceDetail.getUnitPrice());
			invoiceItem.setDetailAmount(invoiceDetail.getDetailAmount());
			invoiceItem.setTaxAmount(invoiceDetail.getTaxAmount());
			invoiceItem.setMatchedNum(matchedDetailData.getMatchedNum());
			invoiceItem.setMatchedUnitPrice(matchedDetailData.getMatchedUnitPrice());
			invoiceItem.setMatchedDetailAmount(matchedDetailData.getMatchedDetailAmount());
			invoiceItem.setMatchedTaxAmount(matchedDetailData.getMatchedTaxAmount());
			invoiceItem.setLeftDetailAmount(matchedDetailData.getLeftDetailAmount());
			invoiceItem.setLeftNum(matchedDetailData.getLeftNum());

			// 1.3 根据发票ID获取已匹配信息，没有则创建
			matchRes = matchResMap.get(matchedDetailData.getInvoiceId());
			if (matchRes == null) {
				matchRes = BlueInvoiceService.MatchRes.builder().build();
				matchRes.setInvoiceNo(invoice.getInvoiceNo());
				matchRes.setInvoiceCode(invoice.getInvoiceCode());
				matchRes.setIsOil(Optional.ofNullable(invoice.getIsOil()).orElse(0));
				matchRes.setInvoiceId(invoice.getId());
				matchRes.setDeductedAmount(matchedDetailData.getMatchedDetailAmount());
				matchRes.setInvoiceDate(invoice.getInvoiceDate());
				matchRes.setInvoiceItems(Lists.newArrayList(invoiceItem));
				matchResMap.put(matchedDetailData.getInvoiceId(), matchRes);
				invoiceMap.put(matchedDetailData.getInvoiceId(), invoice);
				continue;
			}
			// 1.4 已存在匹配记录，则计算发票扣减金额并添加明细
			matchRes.setDeductedAmount(matchRes.getDeductedAmount().add(matchedDetailData.getMatchedDetailAmount()));
			matchRes.getInvoiceItems().add(invoiceItem);
		}
		// 2.扣减发票剩余可占用额度
		List<BlueInvoiceService.MatchRes> matchResList = new ArrayList<>();
		for (Long key : matchResMap.keySet()) {
			matchRes = matchResMap.get(key);
			invoice = invoiceMap.get(key);
			// 2.1 判断发票剩余金额是否足够
			if (matchRes.getDeductedAmount().compareTo(invoice.getRemainingAmount()) > 0) {
				log.error("发票已无剩余可用金额 invoiceId:{},remainingAmount:{},deductedAmount:{}", invoice.getId(),invoice.getRemainingAmount().toPlainString(), matchRes.getDeductedAmount().toPlainString());
				throw new EnhanceRuntimeException("发票不含税金额不足，发票ID[" + invoice.getId() + "],发票剩余匹配金额:" + invoice.getRemainingAmount().toPlainString() + ",明细匹配金额:" + matchRes.getDeductedAmount().toPlainString());
			}
			// 2.2 扣减发票剩余额度
			TDxRecordInvoiceEntity deduction = new TDxRecordInvoiceEntity();
			deduction.setId(invoice.getId());
			// 设置需要扣除的金额
			deduction.setRemainingAmount(matchRes.getDeductedAmount());
			if (isCommitInvoiceRemain && extInvoiceService.deductRemainingAmount(deduction) <= 0) {
				log.warn("锁定并更新发票剩余可用金额失败 invoiceId:{} remainingAmount:{} deductedAmount:{}", invoice.getId(), invoice.getRemainingAmount().toPlainString(), matchRes.getDeductedAmount().toPlainString());
				throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]锁定失败，或已被使用，请选择其他发票重试");
			}
			// 2.3 添加到待返回匹配列表
			matchResList.add(matchRes);
		}
		return matchResList;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<BlueInvoiceService.MatchRes> matchBlueInvoiceByIds(BigDecimal matchAmount, List<Long> invoiceIds, boolean isCommitInvoiceRemain, TXfDeductionBusinessTypeEnum businessTypeEnum) {
		List<BlueInvoiceService.MatchRes> list = new ArrayList<>();
		AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(matchAmount);
		for (Long invoiceId : invoiceIds) {
			log.info("leftAmount:{}", leftAmount.get().toPlainString());
			final TDxRecordInvoiceEntity invoice = extInvoiceService.getById(invoiceId);
			if (invoice == null) {
				throw new EnhanceRuntimeException("参数不合法，发票ID[" + invoiceId + "]不存在");
			}
			if (!Objects.equals(invoice.getInvoiceStatus(), "0")) {
				throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]不是正常状态发票");
			}
			if (invoice.getInvoiceAmount().compareTo(BigDecimal.ZERO) < 0) {
				throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]负数金额不能参与匹配");
			}
			if (!Objects.equals(invoice.getRzhYesorno(), "1")) {
				log.info("发票[{}][{}]未认证，不能参与匹配", invoice.getInvoiceNo(), invoice.getInvoiceCode());
				throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]未认证，不能参与匹配");
			}
			if (Objects.isNull(invoice.getRemainingAmount())) {
				invoice.setRemainingAmount(invoice.getInvoiceAmount());
			}
			if (invoice.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
				throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode()+ "]可用金额[" + invoice.getRemainingAmount().toPlainString() + "]不足，不能参与匹配");
			}
			if (blueInvoiceRelationService.existsByBlueInvoice(invoice.getInvoiceNo(), invoice.getInvoiceCode())) {
				throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]是蓝冲用途的发票，不能参与匹配");
			}
			if (Optional.ofNullable(invoice.getIsOil()).orElse(0) == 1 && invoiceIds.size() > 1) {
				throw new EnhanceRuntimeException("成品油发票只能单张进行匹配！");
			}
			BigDecimal remainingAmount = invoice.getRemainingAmount();
			/*
			 * BigDecimal deductedAmount = leftAmount.get().compareTo(remainingAmount) >= 0
			 * ? // 如果蓝票的剩余可用金额不够抵扣，那么deductedAmount = lastRemainingAmount remainingAmount :
			 * // 如果蓝票的剩余可用金额抵扣后仍有剩余，那么deductedAmount = leftAmount.get() leftAmount.get();
			 */

			BigDecimal deductedAmount = null;
			// 协议单不含税金额，发票可配单金额
			if (leftAmount.get().compareTo(remainingAmount) >= 0) {
				// 剩余金额未用完，协议单取整，索赔单取全部
				if (businessTypeEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL) {
					deductedAmount = remainingAmount.setScale(0, RoundingMode.DOWN).setScale(4, RoundingMode.DOWN);
					log.info("协议单匹配蓝票占用金额取整 deductedAmount:{}", deductedAmount.toPlainString());
				} else {
					deductedAmount = remainingAmount;
				}
			} else {
				deductedAmount = leftAmount.get();
			}

			// 获取该发票的所有正数明细
			String uuid = invoice.getInvoiceCode() + invoice.getInvoiceNo();
			List<BlueInvoiceService.InvoiceItem> items = obtainAvailableItems(businessTypeEnum.getDes(), uuid, deductedAmount, businessTypeEnum);
			// 如果该发票没有可用明细，那么跳过
			if (CollectionUtils.isEmpty(items)) {
				log.info("丢弃没有明细的发票 号码={} 代码={}", invoice.getInvoiceNo(), invoice.getInvoiceCode());
				continue;
			}
			// 累计明细金额
			AtomicReference<BigDecimal> accumulatedDetailAmount = new AtomicReference<>(BigDecimal.ZERO);
			items.forEach(v -> accumulatedDetailAmount.updateAndGet(v1 -> {
				try {
					return v1.add(v.getMatchedDetailAmount());
				} catch (Exception e) {
					log.warn("跳过金额异常的明细，matchedDetailAmount = {}, 明细 = {}", v.getMatchedDetailAmount(), v);
					return v1;
				}
			}));

			if (BigDecimal.ZERO.compareTo(accumulatedDetailAmount.get()) == 0) {
				log.info("丢弃明细金额总和为0的发票 号码={} 代码={}", invoice.getInvoiceNo(), invoice.getInvoiceCode());
				continue;
			}
			// 异常情况，当明细不完整（只导入了一部分），如果累计明细金额小于待扣除的可用金额，那么以明细金额为准
			if (deductedAmount.compareTo(accumulatedDetailAmount.get()) > 0) {
				deductedAmount = accumulatedDetailAmount.get();
			}
			TDxRecordInvoiceEntity deduction = new TDxRecordInvoiceEntity();
			deduction.setId(invoice.getId());
			// 设置需要扣除的金额
			deduction.setRemainingAmount(deductedAmount);
			if (isCommitInvoiceRemain && extInvoiceService.deductRemainingAmount(deduction) <= 0) {
				log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 需要扣除的可用金额={}", invoice.getId(), remainingAmount, remainingAmount);
				throw new EnhanceRuntimeException("发票[" + invoice.getInvoiceNo() + "],[" + invoice.getInvoiceCode() + "]锁定失败，或已被使用，请选择其他发票重试");
			}

			final BigDecimal finalDeductedAmount = deductedAmount;
			leftAmount.updateAndGet(v1 -> v1.subtract(finalDeductedAmount));
			list.add(BlueInvoiceService.MatchRes.builder().isOil(Optional.ofNullable(invoice.getIsOil()).orElse(0))
					.invoiceId(invoice.getId()).invoiceNo(invoice.getInvoiceNo()).invoiceCode(invoice.getInvoiceCode())
					.deductedAmount(deductedAmount).invoiceDate(invoice.getInvoiceDate())
					.invoiceItems(items.stream().collect(Collectors.toList())).build());
		}

		if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
			log.info("没有足够的待匹配的蓝票，回撤变更的发票");
			if (isCommitInvoiceRemain) {
				withdrawBlueInvoice(list);
			}
			throw new EnhanceRuntimeException("匹配失败，所选发票可用金额总和小于合并的业务单总额");
		}
		log.info("已匹配到的发票列表={}", JSON.toJSONString(list));
		return list;
	}
  /**
   * 业务单匹配蓝票(非事务)
   */
  public List<BlueInvoiceService.MatchRes> matchBlueInvoiceWithoutTrans(String businessNo, String sellerTaxNo, String purchaserTaxNo
          , BigDecimal taxRate,BigDecimal matchAmount,boolean notQueryOil,boolean isCommitInvoiceRemain
          ,TXfDeductionBusinessTypeEnum businessTypeEnum) {
    return matchBlueInvoice(businessNo, sellerTaxNo, purchaserTaxNo, taxRate, matchAmount, notQueryOil, isCommitInvoiceRemain, businessTypeEnum);
  }

	/**
	 * 业务单匹配蓝票
	 */
	@Transactional(rollbackFor = Exception.class)
	public List<BlueInvoiceService.MatchRes> matchBlueInvoice(String businessNo, String sellerTaxNo,
			String purchaserTaxNo, BigDecimal taxRate, BigDecimal matchAmount, boolean notQueryOil,
			boolean isCommitInvoiceRemain, TXfDeductionBusinessTypeEnum businessTypeEnum) {
		List<BlueInvoiceService.MatchRes> list = new ArrayList<>();
		log.info("收到业务单匹配蓝票任务-待匹配金额matchAmount={} businessNo={} businessType:{} sellerTaxNo={} purchaserTaxNo={} taxRate={}", matchAmount, businessNo, businessTypeEnum.getDes(), sellerTaxNo, purchaserTaxNo, taxRate);
		// 校验待匹配金额必须为正数
		if (BigDecimal.ZERO.compareTo(matchAmount) >= 0) {
			throw new NoSuchInvoiceException("非正数待匹配金额" + matchAmount.toPlainString());
		}
		// 按照发票先进先出 2022-08-23 新增 协议匹配蓝票时，假设期间是202007-202207，
		// 之前是从2020年的发票开始匹配，现在要改成先从2022年发票开始匹配，匹配近两年供应商蓝票规则不变。
		// https://jira.xforceplus.com/browse/PRJCENTER-10272
		String invoiceDateOrder = businessTypeEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL ? "DESC" : "ASC";

		AtomicReference<BigDecimal> leftAmount = new AtomicReference<>(matchAmount);
		TDxRecordInvoiceEntity invoiceEntity;
		// 解决死循环查询同一张发票问题
		List<Long> preIdList = Lists.newArrayList(0L);
		do {
			invoiceEntity = extInvoiceService.obtainAvailableInvoice(preIdList, sellerTaxNo, purchaserTaxNo, taxRate, notQueryOil, invoiceDateOrder);
			if (invoiceEntity == null) { // 匹配不到发票信息
				log.info("未匹配到发票");
				break;
			}
			log.info("匹配到的发票信息：businessNo:{} invoiceCode:{} invoiceNo:{}", businessNo, invoiceEntity.getInvoiceCode(), invoiceEntity.getInvoiceNo());

			preIdList.add(invoiceEntity.getId());

			if (invoiceEntity.getRemainingAmount() != null && invoiceEntity.getRemainingAmount().compareTo(BigDecimal.ONE) <= 0) {
				log.info("可用金额金额不足1元 businessNo:{} invoiceNo:{} invoiceCode:{} remainingAmount:{}", businessNo, invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode(), invoiceEntity.getRemainingAmount());
				continue;
			}
			// 排除蓝冲用途的发票（正常的发票）
			if (blueInvoiceRelationService.existsByBlueInvoice(invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode())) {
				log.info("发票属于蓝冲用途 invoiceNo:{} invoiceCode:{}", invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode());
				continue;
			}

			BigDecimal remainingAmount = invoiceEntity.getRemainingAmount();
			BigDecimal deductedAmount = null;
			if (leftAmount.get().compareTo(remainingAmount) >= 0) {
				// 剩余金额未用完，协议单取整，索赔单取全部
				if (businessTypeEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL) {
					deductedAmount = remainingAmount.setScale(0, RoundingMode.DOWN).setScale(4, RoundingMode.DOWN);
					log.info("协议单匹配蓝票占用金额取整 deductedAmount:{}", deductedAmount.toPlainString());
				} else {
					deductedAmount = remainingAmount;
				}
			} else {
				deductedAmount = leftAmount.get();
			}

			// 获取该发票的所有正数明细
			String uuid = invoiceEntity.getInvoiceCode() + invoiceEntity.getInvoiceNo();
			List<BlueInvoiceService.InvoiceItem> items = obtainAvailableItems(businessNo, uuid, deductedAmount, businessTypeEnum);
			// 如果该发票没有可用明细，那么跳过
			if (CollectionUtils.isEmpty(items)) {
				log.info("丢弃没有明细的发票 businessNo:{} invoiceNo:{} invoiceCode:{}", businessNo, invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode());
				continue;
			}
			// 累计明细金额
			AtomicReference<BigDecimal> accumulatedDetailAmount = new AtomicReference<>(BigDecimal.ZERO);
			items.forEach(v -> accumulatedDetailAmount.updateAndGet(v1 -> {
				try {
					return v1.add(v.getMatchedDetailAmount());
				} catch (Exception e) {
					log.warn("跳过金额异常的明细，matchedDetailAmount:{}, 明细={}", v.getMatchedDetailAmount(), JSON.toJSONString(v));
					return v1;
				}
			}));

			if (BigDecimal.ZERO.compareTo(accumulatedDetailAmount.get()) == 0) {
				log.info("丢弃明细金额总和为0的发票 invoiceNo:{} invoiceCode:{}", invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode());
				continue;
			}
			// 异常情况，当明细不完整（只导入了一部分），如果累计明细金额小于待扣除的可用金额，那么以明细金额为准
			if (deductedAmount.compareTo(accumulatedDetailAmount.get()) > 0) {
				deductedAmount = accumulatedDetailAmount.get();
			}
			// 2022-06-16,因为成品油只能匹配一张发票
			if (Integer.valueOf(1).equals(invoiceEntity.getIsOil())) {
				log.info("成品油只能匹配一张发票 businessNo:{} invoiceNo:{} invoiceCode:{}", businessNo, invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode());
				notQueryOil = true;
			}

			TDxRecordInvoiceEntity deduction = new TDxRecordInvoiceEntity();
			deduction.setId(invoiceEntity.getId());
			// 设置需要扣除的金额,即本次实际占用金额
			deduction.setRemainingAmount(deductedAmount);
			if (isCommitInvoiceRemain && extInvoiceService.deductRemainingAmount(deduction) <= 0) {
				log.warn("锁定并更新发票剩余可用金额失败，跳过此发票处理，发票id={} 更新前剩余可用金额={} 本次欲扣除金额={}", invoiceEntity.getId(), remainingAmount.toPlainString(), deductedAmount.toPlainString());
				continue;
			}
			// 转换返回主信息及明细信息
			BigDecimal finalDeductedAmount = deductedAmount;
			leftAmount.updateAndGet(v1 -> v1.subtract(finalDeductedAmount));
			list.add(BlueInvoiceService.MatchRes.builder()
					.isOil(Optional.ofNullable(invoiceEntity.getIsOil()).orElse(0)).invoiceId(invoiceEntity.getId())
					.invoiceNo(invoiceEntity.getInvoiceNo()).invoiceCode(invoiceEntity.getInvoiceCode())
					.deductedAmount(deductedAmount).invoiceDate(invoiceEntity.getInvoiceDate())
					.invoiceItems(items.stream().collect(Collectors.toList())).build());
		} while (Objects.nonNull(invoiceEntity) && BigDecimal.ZERO.compareTo(leftAmount.get()) < 0);

		if (BigDecimal.ZERO.compareTo(leftAmount.get()) < 0) {
			log.info("没有足够的待匹配的蓝票，回撤变更的发票 businessNo:{},leftAmount:{}", businessNo, leftAmount.get());
			if (isCommitInvoiceRemain) {
				withdrawBlueInvoice(list);
			}
			throw new NoSuchInvoiceException();
		}
		log.info("已匹配的发票列表 businessNo:{},list:{}", businessNo, JSON.toJSONString(list));
		return list;
	}

  /**
   * 判断结算单来源 新版组合 ？  旧版组合？
   * @param settlementId 结算单id
   * @return true-新版组合生成 false-旧版组合生成
   */
  public boolean checkSettlementSource(Long settlementId) {
    TXfSettlementEntity settlementEntity = tXfSettlementDao.selectById(settlementId);
    Asserts.isNull(settlementEntity, "结算单信息不存在");


    QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper = new QueryWrapper<>();
    billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, settlementEntity.getSettlementNo());
    List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper);
    Asserts.isTrue(CollectionUtil.isEmpty(billDeductList), "业务单信息不存在");

//    List<Long> billDeductIdList = billDeductList.stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());

//    QueryWrapper<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailQ = new QueryWrapper<>();
//    deductInvoiceDetailQ.in(TXfBillDeductInvoiceDetailEntity.DEDUCT_ID, billDeductIdList);
//    deductInvoiceDetailQ.eq(TXfBillDeductInvoiceDetailEntity.STATUS,0);

    LambdaQueryWrapper<TXfSettlementItemEntity> settlementItemQueryWrapper = Wrappers.lambdaQuery(TXfSettlementItemEntity.class)
            .eq(TXfSettlementItemEntity::getSettlementNo, settlementEntity.getSettlementNo())
            .ge(TXfSettlementItemEntity::getTaxRate, BigDecimal.ZERO);

    // true = 中间表有数据 或 明细全为0税率
    return deductInvoiceDetailDao.queryCountJoin(settlementEntity.getSettlementNo()) > 0
            || tXfSettlementItemDao.selectCount(settlementItemQueryWrapper) == 0;
  }

  /**
   * 根据业务释放占用蓝票金额
   * @param deductIdList
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean withdrawBlueInvoiceByDeduct(List<Long> deductIdList){
    //获取业务单和发票明细关系记录
    QueryWrapper<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailQ = new QueryWrapper<>();
    deductInvoiceDetailQ.in(TXfBillDeductInvoiceDetailEntity.DEDUCT_ID,deductIdList);
    deductInvoiceDetailQ.eq(TXfBillDeductInvoiceDetailEntity.STATUS,0);
    List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList =  deductInvoiceDetailDao.selectList(deductInvoiceDetailQ);
    if (CollectionUtils.isEmpty(deductInvoiceDetailList)) {
      log.info("未找到占用发票明细记录 deductIdList:{}",JSON.toJSONString(deductIdList));
      return true;
    }

    List<TXfBillDeductEntity> deductList = tXfBillDeductDao.selectBatchIds(deductIdList);
    if (CollectionUtils.isEmpty(deductList)){
      log.info("获取协议单列表失败 deductIdList:{}",JSON.toJSONString(deductIdList));
      return false;
    }
    //获取结算单号及是否协议单
    String settlementNo = null;
    boolean isAgreement = false;
    for (TXfBillDeductEntity deduct: deductList){
      //获取结算单号
      if (StringUtils.isNotBlank(deduct.getRefSettlementNo())){
        if (StringUtils.isNotBlank(settlementNo)){
          if (!settlementNo.equals(deduct.getRefSettlementNo())){
            log.info("不支持多个结算单释放蓝票占用 deductIdList:{} settlementNo:{} refSettlementNo:{}"
                    ,JSON.toJSONString(deductIdList),settlementNo,deduct.getRefSettlementNo());
            return false;
          }
        }else {
          settlementNo = deduct.getRefSettlementNo();
        }
      }
      //判断是否协议单
      if (!isAgreement && TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(deduct.getBusinessType())){
        isAgreement = true;
      }
    }

    //生成占用关系
    List<BlueInvoiceService.MatchRes> matchResList = null;
    if (isAgreement && StringUtils.isNotBlank(settlementNo)){
      //通过结算单和蓝票明细占用关系生成原占用关系
      matchResList = getMatchResListBySettlementDetail(settlementNo);
    }else {
      //通过业务单和蓝票明细占用关系生成原占用关系
      matchResList = getMatchResListByDeductDetail(deductInvoiceDetailList);
    }
    if (CollectionUtils.isEmpty(matchResList)){
      log.info("生成待释放匹配列表失败 deductIdList:{}",JSON.toJSONString(deductIdList));
      return false;
    }

    //释放蓝票占用金额
    withdrawBlueInvoice(matchResList);
    //释放业务单和蓝票明细关系占用
    TXfBillDeductInvoiceDetailEntity deductInvoiceDetailU = new TXfBillDeductInvoiceDetailEntity();
    deductInvoiceDetailU.setStatus(1);
    deductInvoiceDetailU.setUpdateTime(new Date());
    deductInvoiceDetailDao.update(deductInvoiceDetailU,deductInvoiceDetailQ);
    //释放业务单和蓝票关系占用
    QueryWrapper<TXfBillDeductInvoiceEntity> deductInvoiceQ = new QueryWrapper<>();
    deductInvoiceQ.in(TXfBillDeductInvoiceEntity.THRID_ID,deductIdList);
    deductInvoiceQ.eq(TXfBillDeductInvoiceEntity.STATUS,0);
    TXfBillDeductInvoiceEntity deductInvoiceU = new TXfBillDeductInvoiceEntity();
    deductInvoiceU.setStatus(1);
    deductInvoiceU.setUpdateTime(new Date());
    deductInvoiceDao.update(deductInvoiceU,deductInvoiceQ);
    return true;
  }

  private List<BlueInvoiceService.MatchRes> getMatchResListByDeductDetail(List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList){
    List<BlueInvoiceService.MatchRes> matchResList = new ArrayList<>();

    //按明细计算待释放记录
    Map<String,BlueInvoiceService.MatchRes> invoiceMatchResMap = new HashMap<>();
    BlueInvoiceService.MatchRes matchRes;
    String matchKey;
    for (TXfBillDeductInvoiceDetailEntity deductInvoiceDetail : deductInvoiceDetailList){
      matchKey = deductInvoiceDetail.getInvoiceCode()+deductInvoiceDetail.getInvoiceNo();
      matchRes = invoiceMatchResMap.get(matchKey);
      if (matchRes == null){
        log.info("通过业务单生成占用关系");
        matchRes = BlueInvoiceService.MatchRes.builder().build();
        matchRes.setInvoiceId(deductInvoiceDetail.getInvoiceId());
        matchRes.setDeductedAmount(deductInvoiceDetail.getUseAmountWithoutTax());
        invoiceMatchResMap.put(matchKey,matchRes);
        matchResList.add(matchRes);
        continue;
      }
      matchRes.setDeductedAmount(matchRes.getDeductedAmount().add(deductInvoiceDetail.getUseAmountWithoutTax()));
    }
    return matchResList;
  }

  private List<BlueInvoiceService.MatchRes> getMatchResListBySettlementDetail(String settlementNo){
    List<BlueInvoiceService.MatchRes> matchResList = new ArrayList<>();
    //获取业务单和发票明细关系记录
    QueryWrapper<TXfSettlementItemInvoiceDetailEntity> settlementItemInvoiceDetailQ = new QueryWrapper<>();
    settlementItemInvoiceDetailQ.in(TXfSettlementItemInvoiceDetailEntity.SETTLEMENT_NO,settlementNo);
    List<TXfSettlementItemInvoiceDetailEntity> settlementItemInvoiceDetailList =  settlementItemInvoiceDetailDao
            .selectList(settlementItemInvoiceDetailQ);
    if (CollectionUtils.isEmpty(settlementItemInvoiceDetailList)) {
      log.info("未找到占用发票明细记录 settlementNo:{}",settlementNo);
      return null;
    }
    //按明细计算待释放记录
    Map<String,BlueInvoiceService.MatchRes> invoiceMatchResMap = new HashMap<>();
    BlueInvoiceService.MatchRes matchRes;
    String matchKey;
    for (TXfSettlementItemInvoiceDetailEntity settlementItemInvoiceDetail : settlementItemInvoiceDetailList){
      matchKey = settlementItemInvoiceDetail.getInvoiceCode()+settlementItemInvoiceDetail.getInvoiceNo();
      matchRes = invoiceMatchResMap.get(matchKey);
      if (matchRes == null){
        log.info("通过结算单生成占用关系");
        matchRes = BlueInvoiceService.MatchRes.builder().build();
        matchRes.setInvoiceId(settlementItemInvoiceDetail.getInvoiceId());
        matchRes.setDeductedAmount(settlementItemInvoiceDetail.getUseAmountWithoutTax());
        invoiceMatchResMap.put(matchKey,matchRes);
        matchResList.add(matchRes);
        continue;
      }
      matchRes.setDeductedAmount(matchRes.getDeductedAmount().add(settlementItemInvoiceDetail.getUseAmountWithoutTax()));
    }
    return matchResList;
  }



  /**
   * 撤回抵扣的发票金额，将抵扣金额返还到原有发票上
   *
   * @param list
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean withdrawBlueInvoice(List<BlueInvoiceService.MatchRes> list) {
    if (CollectionUtils.isEmpty(list)) {
      log.info("发票列表为空");
      return true;
    }
    log.info("开始撤回抵扣的发票金额，将抵扣金额返还到原有发票上, 发票列表={}", JSON.toJSONString(list));
    List<TDxRecordInvoiceEntity> invoices = list.stream().map(v -> {
      TDxRecordInvoiceEntity tDxRecordInvoiceEntity = new TDxRecordInvoiceEntity();
      tDxRecordInvoiceEntity.setId(v.getInvoiceId());
      tDxRecordInvoiceEntity.setRemainingAmount(v.getDeductedAmount());
      log.info("withdrawRemainingAmountById:{}",JSON.toJSONString(tDxRecordInvoiceEntity));
      return tDxRecordInvoiceEntity;
    }).collect(Collectors.toList());

    return extInvoiceService.withdrawRemainingAmountById(invoices);
  }

  public List<MatchedInvoiceDetailBean> gainInvoiceRecommendDetailList(TDxRecordInvoiceEntity invoiceEntity){
    String uuid = invoiceEntity.getInvoiceCode() + invoiceEntity.getInvoiceNo();
    log.info("发票明细推荐执行开始 uuid={}",uuid);
    List<MatchedInvoiceDetailBean> recommendDetailList = new ArrayList<>();
    MatchedInvoiceDetailBean recommendDetail;
    List<TDxRecordInvoiceDetailEntity> invoiceDetailList = invoiceService.getInvoiceDetailByUuid(uuid);
    for (TDxRecordInvoiceDetailEntity invoiceDetail : invoiceDetailList) {
      //符合推荐条件
      recommendDetail = gainMatchedInvoiceDetail(invoiceDetail,invoiceEntity);
      if (recommendDetail == null){
        log.info("明细不符合推荐要求 uuid:{} itemId:{} goodsName:{}"
                ,invoiceDetail.getUuid(),invoiceDetail.getId(),invoiceDetail.getGoodsName());
        continue;
      }
      recommendDetailList.add(recommendDetail);
    }
    log.info("已推荐的发票明细列表={}", JSON.toJSONString(recommendDetailList));
    return recommendDetailList;
  }
    /**
     * <pre>
     * 处理发票明细信息
     * 1、过滤商品名称为：原价合计，折扣额合计，（详见销货清单）等信息
     * 2、计算明细的折扣信息
     * 3、计算明细的占用情况
     * </pre>
     * @param invoiceDetail
     * @param invoiceEntity
     * @return
     */
	public MatchedInvoiceDetailBean gainMatchedInvoiceDetail(TDxRecordInvoiceDetailEntity invoiceDetail, TDxRecordInvoiceEntity invoiceEntity) {
		if (GOODS_LIST_TEXT.equalsIgnoreCase(invoiceDetail.getGoodsName())
				|| StringUtils.equalsIgnoreCase(invoiceDetail.getGoodsName(), "（详见销货清单）")
				|| StringUtils.equalsIgnoreCase(invoiceDetail.getGoodsName(), "(详见销货清单）")
				|| StringUtils.equalsIgnoreCase(invoiceDetail.getGoodsName(), "（详见销货清单)")
				|| "原价合计".equalsIgnoreCase(invoiceDetail.getGoodsName())
				|| "折扣额合计".equalsIgnoreCase(invoiceDetail.getGoodsName())) {
			log.info("明细商品名不符合推荐要求 uuid:{} itemId: goodsName:{}", invoiceDetail.getUuid(), invoiceDetail.getId(), invoiceDetail.getGoodsName());
			return null;
		}
		// 如存在折扣，则按折扣重算明细金额和单价
		reCalIfDiscountExist(invoiceDetail);

		// 获取明细不含税金额
		BigDecimal totalDetailAmount;
		try {
			totalDetailAmount = new BigDecimal(invoiceDetail.getDetailAmount());
		} catch (NumberFormatException e) {
			log.warn("明细不含税金额转换成数字失败，跳过此明细，uuid={}, detailAmount={}", invoiceDetail.getUuid(), invoiceDetail.getDetailAmount());
			return null;
		}
		// 数组[0]-占用不含税金额 数组[1]-占用数量
		BigDecimal[] usedArray = getInvoiceItemUsed(invoiceDetail.getId());
		// 计算明细剩余可用金额
		BigDecimal leftDetailAmount = totalDetailAmount.subtract(usedArray[0]);
		if (BigDecimal.ONE.compareTo(leftDetailAmount) > 0) {
			log.info("明细可用金额小于1元 uuid:{}, itemId: leftDetailAmount:{}", invoiceDetail.getUuid(), invoiceDetail.getId(), leftDetailAmount.toPlainString());
			return null;
		}

		// 计算明细剩余可用数量
		BigDecimal totalDetailQuantity = null;
		BigDecimal leftDetailQuantity = null;
		try {
			if (StringUtils.isNotBlank(invoiceDetail.getNum())) {
				totalDetailQuantity = new BigDecimal(invoiceDetail.getNum());
			}
		} catch (NumberFormatException e) {
			log.warn("明细数量转换成数字失败，跳过此明细，uuid={} num={}", invoiceDetail.getUuid(), invoiceDetail.getNum());
			return null;
		}
		if (totalDetailQuantity != null) {
			leftDetailQuantity = totalDetailQuantity.subtract(usedArray[1]);
			if (BigDecimal.ZERO.compareTo(leftDetailQuantity) >= 0) {
				log.info("明细已无剩余可用数量 uuid:{} itemId: leftDetailQuantity:{}", invoiceDetail.getUuid(), invoiceDetail.getId(), leftDetailQuantity.toPlainString());
				return null;
			}
		}

		// 如果明细数量有剩余，通过剩余数量*单价算出剩余不含税,取最小剩余不含税
		if (StringUtils.isNotBlank(invoiceDetail.getUnitPrice()) && StringUtils.isNotBlank(invoiceDetail.getNum())) {
			BigDecimal leftDetailAmountM = leftDetailQuantity.multiply(new BigDecimal(invoiceDetail.getUnitPrice())).setScale(2, RoundingMode.HALF_UP);
			if (leftDetailAmountM.compareTo(leftDetailAmount) < 0) {
				leftDetailAmount = leftDetailAmountM;
				// 再次校验是否小于1
				if (BigDecimal.ONE.compareTo(leftDetailAmount) > 0) {
					log.info("明细可用金额小于1元 uuid:{} itemId: leftDetailAmount:{}", invoiceDetail.getUuid(), invoiceDetail.getId(), leftDetailAmount.toPlainString());
					return null;
				}
			}
		}

		// 符合推荐条件
		MatchedInvoiceDetailBean matchedInvoiceDetail = new MatchedInvoiceDetailBean();
		matchedInvoiceDetail.setInvoiceId(invoiceEntity.getId());
		matchedInvoiceDetail.setInvoiceDetailId(invoiceDetail.getId());
		matchedInvoiceDetail.setUuid(invoiceDetail.getUuid());
		matchedInvoiceDetail.setInvoiceNo(invoiceDetail.getInvoiceNo());
		matchedInvoiceDetail.setInvoiceCode(invoiceDetail.getInvoiceCode());
		matchedInvoiceDetail.setGoodsName(invoiceDetail.getGoodsName());
		matchedInvoiceDetail.setModel(invoiceDetail.getModel());
		matchedInvoiceDetail.setInvoiceDate(DateUtils.format(invoiceEntity.getInvoiceDate()));
		matchedInvoiceDetail.setGoodsNum(invoiceDetail.getGoodsNum());
		matchedInvoiceDetail.setUnit(invoiceDetail.getUnit());
		matchedInvoiceDetail.setNum(invoiceDetail.getNum());
		matchedInvoiceDetail.setUnitPrice(invoiceDetail.getUnitPrice());
		matchedInvoiceDetail.setDetailAmount(invoiceDetail.getDetailAmount());
		matchedInvoiceDetail.setTaxRate(invoiceDetail.getTaxRate());
		matchedInvoiceDetail.setTaxAmount(invoiceDetail.getTaxAmount());
		if (leftDetailQuantity != null) {
			matchedInvoiceDetail.setLeftNum(leftDetailQuantity.toPlainString());
		}
		matchedInvoiceDetail.setLeftDetailAmount(leftDetailAmount.toPlainString());
		return matchedInvoiceDetail;
	}

  /**
   * 根据之前的剩余金额，以及本次需要抵扣的可用金额，按照明细顺序找到合适的明细行返回，返回的明细金额总和一定<=需要抵扣的可用金额
   *
   * @param uuid 发票代码+发票号码 拼接
   * @param deductedAmount 扣除金额
   * @return
   */
	private List<BlueInvoiceService.InvoiceItem> obtainAvailableItems(String businessNo,String uuid, BigDecimal deductedAmount, TXfDeductionBusinessTypeEnum businessTypeEnum) {
		log.info("收到匹配蓝票明细任务 businessNo:{},uuid:{} deductedAmount:{}", businessNo, uuid, deductedAmount);
		List<BlueInvoiceService.InvoiceItem> list = new ArrayList<>();
		List<TDxRecordInvoiceDetailEntity> items = invoiceService.getInvoiceDetailByUuid(uuid);
		log.info("businessNo:{},收到匹配蓝票明细任务-items:{}", businessNo, JSON.toJSON(items));
		// 明细剩余可抵扣金额
		BigDecimal leftDeductAmount = BigDecimal.ZERO.add(deductedAmount);
		for (TDxRecordInvoiceDetailEntity item : items) {
			if (GOODS_LIST_TEXT.equalsIgnoreCase(item.getGoodsName())
					|| StringUtils.equalsIgnoreCase(item.getGoodsName(), "（详见销货清单）")
					|| StringUtils.equalsIgnoreCase(item.getGoodsName(), "(详见销货清单）")
					|| StringUtils.equalsIgnoreCase(item.getGoodsName(), "（详见销货清单)")
					|| "原价合计".equalsIgnoreCase(item.getGoodsName()) || "折扣额合计".equalsIgnoreCase(item.getGoodsName())) {
				log.info("明细商品名不符合匹配要求 uuid:{} itemId: goodsName:{}", uuid, item.getId(), item.getGoodsName());
				continue;
			}
			// 如存在折扣，则按折扣重算明细金额和单价
			reCalIfDiscountExist(item);
			// 获取明细不含税金额
			BigDecimal totalDetailAmount;
			// 单价X数量
			BigDecimal totalQuantityUnitPriceAmount = null;
			// 被反算的实际数量
			BigDecimal totalActuQuantity = null;
			try {
				totalDetailAmount = new BigDecimal(item.getDetailAmount());
			} catch (NumberFormatException e) {
				log.warn("明细不含税金额转换成数字失败，跳过此明细，uuid={} detailAmount={}", uuid, item.getDetailAmount());
				continue;
			}
			// 获取明细已占用不含税金额
			// 数组[0]-占用不含税金额 数组[1]-占用数量
			BigDecimal[] usedArray = getInvoiceItemUsed(item.getId());
			log.info("obtainAvailableItems businessNo:{}, itemId:{},", businessNo, item.getId(), usedArray);
			// 计算明细剩余可用金额
			BigDecimal leftDetailAmount = totalDetailAmount.subtract(usedArray[0]);
			if (BlueInvoiceMatchHolder.available()) {
				BigDecimal matchedDetailAmountByT = BlueInvoiceMatchHolder.getMatchedDetailAmount(item);
				if (matchedDetailAmountByT != null) {
					log.info("businessNo:{}通过ThreadLocal获取到占用明细金额：{}-{}", businessNo, item.getId(), matchedDetailAmountByT.toPlainString());
					leftDetailAmount = leftDetailAmount.subtract(matchedDetailAmountByT);
				}
			}
			if (BigDecimal.ZERO.compareTo(leftDetailAmount) >= 0) {
				log.info("明细已无剩余可用金额 uuid:{} itemId: leftDetailAmount:{}", uuid, item.getId(), leftDetailAmount.toPlainString());
				continue;
			}
			// 计算明细剩余可用数量
			BigDecimal totalDetailQuantity = null;
			BigDecimal leftDetailQuantity = null;
			try {
				if (StringUtils.isNotBlank(item.getNum())) {
					totalDetailQuantity = new BigDecimal(item.getNum());
				}
			} catch (NumberFormatException e) {
				log.warn("明细数量转换成数字失败，跳过此明细，uuid={} num={}", uuid, item.getNum());
				continue;
			}
			// WALMART-2345 totalDetailQuantity会大于实际可用数量，当匹配完一个小金额协议单，剩余待匹配数量，
			// 再次匹配下一个协议单时，如果发票明细单价x数量大于不含税金额，需要保单价计算实际可用数量
			if (totalDetailQuantity != null) {
				leftDetailQuantity = totalDetailQuantity.subtract(usedArray[1]);
				if (BlueInvoiceMatchHolder.available()) {
					BigDecimal matchedDetailQuantityByT = BlueInvoiceMatchHolder.getMatchedDetailQuantity(item);
					if (matchedDetailQuantityByT != null) {
						log.info("businessNo:{},通过ThreadLocal获取到占用明细数量：{}-{}", businessNo, item.getId(), matchedDetailQuantityByT.toPlainString());
						leftDetailQuantity = leftDetailQuantity.subtract(matchedDetailQuantityByT);
					}
				}
				if (BigDecimal.ZERO.compareTo(leftDetailQuantity) >= 0) {
					log.info("明细已无剩余可用数量 uuid:{} itemId:{} leftDetailQuantity:{}", uuid, item.getId(), leftDetailQuantity.toPlainString());
					continue;
				}
				// 如果明细数量有剩余，通过剩余数量*单价算出剩余不含税,取最小剩余不含税
				if (StringUtils.isNotBlank(item.getUnitPrice()) && StringUtils.isNotBlank(item.getNum())) {
					// 剩余明细金额=剩余明细数量*单价取保留2位小数值
					BigDecimal leftDetailAmountM = leftDetailQuantity.multiply(new BigDecimal(item.getUnitPrice())).setScale(2, RoundingMode.HALF_UP);
					// 存在单价和数量乘积不等于不含税金额
					// 重新计算数量
					totalQuantityUnitPriceAmount = new BigDecimal(item.getUnitPrice()).multiply(new BigDecimal(item.getNum())).setScale(2, RoundingMode.HALF_UP);
					// 重算剩余明细金额<明细金额-占用金额-已匹配金额
					if (leftDetailAmountM.compareTo(leftDetailAmount) < 0) {
						leftDetailAmount = leftDetailAmountM;
					}
				}
			}

			// 剩余抵扣金额小于等于明细剩余可用金额，说明到这一条已经够了
			if (leftDeductAmount.compareTo(leftDetailAmount) <= 0) {
				log.info("已足额匹配，计算分摊金额及数量");
				BlueInvoiceService.InvoiceItem invoiceItem = calInvoiceDetailUse(item, leftDeductAmount, leftDetailQuantity, leftDetailAmount, totalActuQuantity);
				if (invoiceItem == null) {
					log.warn("明细占用计算失败，丢弃该条明细继续找下一条  uuid:{} itemId:{}", uuid, item.getId());
					continue;
				}
				list.add(invoiceItem);
				log.info("已匹配的发票明细列表={}", JSON.toJSONString(list));
				return list;
			}

			log.info("未足额匹配，计算分摊金额及数量并继续下一条明细 leftDetailAmount:{}", leftDetailAmount.toPlainString());
			// 增加系统日志
			SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.MATCH_INVOICE_DETAIL, "未足额匹配 leftDetailAmount:" + leftDetailAmount.toPlainString());

			BigDecimal matchedDetailAmount = new BigDecimal(leftDetailAmount.toPlainString());
			if (businessTypeEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL) {
				if (BigDecimal.ONE.compareTo(leftDetailAmount) > 0) {
					log.info("协议单未足额匹配蓝票明细且剩余待匹配金额小于1元，跳过该条明细寻找下一条大于1元的明细");
					// 增加系统日志
					SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.MATCH_INVOICE_DETAIL, "协议单未足额匹配蓝票明细且剩余待匹配金额小于1元");
					continue;
				}
				matchedDetailAmount = matchedDetailAmount.setScale(0, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN);
				log.info("协议单未足额匹配蓝票明细，占用金额取整处理 leftDetailAmount:{}", leftDetailAmount.toPlainString());
			}

			BlueInvoiceService.InvoiceItem invoiceItem = calInvoiceDetailUse(item, matchedDetailAmount, leftDetailQuantity, leftDetailAmount, totalActuQuantity);
			if (invoiceItem == null) {
				log.warn("明细占用计算失败，丢弃该条明细继续找下一条  uuid:{} itemId:{}", uuid, item.getId());
				continue;
			}
			// 剩余抵扣金额 = 剩余抵扣金额-本条明细剩余可用金额
			leftDeductAmount = leftDeductAmount.subtract(matchedDetailAmount);
			list.add(invoiceItem);
		}
		log.info("已匹配的发票明细列表={}", JSON.toJSONString(list));
		return list;
	}

	public void reCalIfDiscountExist(TDxRecordInvoiceDetailEntity item) {
		try {
			log.info("获取折扣明细-计算前信息：itemId:{} detailNo{} goodsName:{} unitPrice:{} detailAmount{}", item.getId(), item.getDetailNo(), item.getGoodsName(), item.getUnitPrice(), item.getDetailAmount());
			// 获取折扣明细序号
			String discountDetailNo = new BigDecimal(item.getDetailNo()).add(BigDecimal.ONE).setScale(0, RoundingMode.DOWN).toPlainString();
			QueryWrapper<TDxRecordInvoiceDetailEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq(TDxRecordInvoiceDetailEntity.UUID, item.getUuid());
			queryWrapper.eq(TDxRecordInvoiceDetailEntity.DETAIL_NO, discountDetailNo);
			queryWrapper.eq(TDxRecordInvoiceDetailEntity.GOODS_NAME, item.getGoodsName());
			TDxRecordInvoiceDetailEntity discountItem = tDxRecordInvoiceDetailDao.selectOne(queryWrapper);
			if (discountItem == null) {
				log.info("获取折扣明细-无折扣明细");
				return;
			}
			// 获取折扣明细金额
			BigDecimal discountDetailAmount = new BigDecimal(discountItem.getDetailAmount());
			if (BigDecimal.ZERO.compareTo(discountDetailAmount) <= 0) {
				log.info("获取折扣明细-无折扣明细(金额不为负数) {}", discountDetailAmount.toPlainString());
				return;
			}
			// 重算原明细金额及单价
			BigDecimal originDetailAmount = new BigDecimal(item.getDetailAmount()).add(discountDetailAmount).setScale(2, RoundingMode.DOWN);
			// 折前金额
			item.setDetailAmount(originDetailAmount.toPlainString());
			if (StringUtils.isNotBlank(item.getNum())) {
				BigDecimal originUnitPrice = originDetailAmount.divide(new BigDecimal(item.getNum()), 15, RoundingMode.HALF_UP);
				item.setUnitPrice(originUnitPrice.toPlainString());
			}
			log.info("获取折扣明细-计算后信息：itemId:{} detailNo{} goodsName:{} unitPrice:{} detailAmount{}", item.getId(), item.getDetailNo(), item.getGoodsName(), item.getUnitPrice(), item.getDetailAmount());
		} catch (Exception e) {
			log.error("获取折扣明细异常！{}", JSON.toJSONString(item), e);
		}
	}

	/**
	 * 获取发票明细占用信息
	 * 
	 * @param invoiceItemId
	 * @return 数组[0]-占用不含税金额 数组[1]-占用数量
	 */
	private BigDecimal[] getInvoiceItemUsed(Long invoiceItemId) {
		BigDecimal usedAmountWithoutTax = new BigDecimal("0.00");
		BigDecimal usedQuantity = new BigDecimal("0.00");
		QueryWrapper<TXfBillDeductInvoiceDetailEntity> detailQueryWrapper = new QueryWrapper<>();
		detailQueryWrapper.eq(TXfBillDeductInvoiceDetailEntity.INVOICE_DETAIL_ID, invoiceItemId);
		detailQueryWrapper.eq(TXfBillDeductInvoiceDetailEntity.STATUS, 0);
		List<TXfBillDeductInvoiceDetailEntity> detailEntityList = deductInvoiceDetailDao.selectList(detailQueryWrapper);
		if (CollectionUtils.isNotEmpty(detailEntityList)) {
			for (TXfBillDeductInvoiceDetailEntity detailEntity : detailEntityList) {
				usedAmountWithoutTax = usedAmountWithoutTax.add(detailEntity.getUseAmountWithoutTax());
				if (detailEntity.getUseQuantity() != null) {
					usedQuantity = usedQuantity.add(detailEntity.getUseQuantity());
				}
			}
		}

		BigDecimal[] retDecimal = new BigDecimal[2];
		retDecimal[0] = usedAmountWithoutTax;
		retDecimal[1] = usedQuantity;
		log.info("获取发票明细占用信息,itemId:{},retDecimal:{}", invoiceItemId, JSON.toJSON(retDecimal));
		return retDecimal;
	}

	/**
	 * 计算手工匹配明细税额及数量
	 * 
	 * @param matchedDetailData
	 */
	private void calMatchedInvoiceDetailUse(DeductInvoiceDetailData matchedDetailData,
			DeductInvoiceDetailData originDetailData, BigDecimal taxRate) {
		// 1.计算剩余不含税
		matchedDetailData.setLeftDetailAmount(originDetailData.getLeftDetailAmount());
		// 2.计算占用税额
		matchedDetailData.setMatchedTaxAmount(matchedDetailData.getMatchedDetailAmount()
				.multiply(taxRate.movePointLeft(2)).setScale(2, RoundingMode.HALF_UP));

		// 3.计算占用数量及剩余数量
		if (matchedDetailData.getMatchedUnitPrice() != null && matchedDetailData.getMatchedNum() != null
				&& BigDecimal.ZERO.compareTo(matchedDetailData.getMatchedUnitPrice()) != 0) {
			matchedDetailData.setMatchedNum(matchedDetailData.getMatchedDetailAmount()
					.divide(matchedDetailData.getMatchedUnitPrice(), 15, RoundingMode.HALF_UP));
			originDetailData.setLeftNum(originDetailData.getLeftNum().subtract(matchedDetailData.getMatchedNum()));
			matchedDetailData.setLeftNum(originDetailData.getLeftNum());
		}
	}

  /**
   * 计算明细占用情况（含税金额、不含税金额、数量）
   * @param item
   * @param useAmount
   */
  private BlueInvoiceService.InvoiceItem calInvoiceDetailUse(TDxRecordInvoiceDetailEntity item,
                                                             BigDecimal useAmount,
                                                             BigDecimal leftDetailQuantity,
                                                             BigDecimal leftDetailAmount,
                                                             BigDecimal totalActuQuantity){
    log.info("底账明细表数据:{}",JSON.toJSON(item));
    log.info("计算明细占用情况，itemId：{}，useAmount：{}，leftDetailQuantity：{}，leftDetailAmount：{}",
            item.getId(), useAmount, leftDetailQuantity, leftDetailAmount);
    final BigDecimal detailTaxRate = new BigDecimal(item.getTaxRate()).movePointLeft(2);
    BigDecimal totalDetailAmount = new BigDecimal(item.getDetailAmount());
    BigDecimal matchedDetailAmount = useAmount;
    BigDecimal matchedTaxAmount = matchedDetailAmount.multiply(detailTaxRate).setScale(2, RoundingMode.HALF_UP);
    BigDecimal matchedNum = BigDecimal.ZERO;
    BigDecimal matchedUnitPrice = TDxRecordInvoiceDetailEntityConvertor.INSTANCE.stringToBgDcmlOrZero(item.getUnitPrice());
    //计算占用明细数量
    if (StringUtils.isNotBlank(item.getNum())) {
      try {
        BigDecimal detailNum = new BigDecimal(item.getNum());
//        if(Objects.nonNull(totalActuQuantity)){
//          detailNum = totalActuQuantity;
//        }
        //计算实际可用金额
        //单价X数量<明细不含税金额取小值
        BigDecimal totalQuantityUnitPriceAmount =  new BigDecimal(item.getUnitPrice())
                .multiply(new BigDecimal(item.getNum())).setScale(2,RoundingMode.HALF_UP);
        if(totalQuantityUnitPriceAmount.compareTo(totalDetailAmount)==-1){
          totalDetailAmount = totalQuantityUnitPriceAmount;
          //单价X数量>明细不含税金额,保单价反算数量
        }

        matchedNum = matchedDetailAmount.multiply(detailNum).divide(totalDetailAmount,15,RoundingMode.HALF_UP)
                .setScale(15, RoundingMode.HALF_UP);
        if (BigDecimalUtil.isInteger(leftDetailQuantity) && !BigDecimalUtil.isInteger(matchedNum)){
          //如果剩余数量是整数且占用数量不是整数，则占用数量需要向上取整，并反算单价
          matchedNum = new BigDecimal(matchedNum.intValue()+1);
          //反算单价
          matchedUnitPrice = matchedDetailAmount.divide(matchedNum,15,RoundingMode.HALF_UP);
        }

        leftDetailQuantity = leftDetailQuantity.subtract(matchedNum);
        //判断数量是否用超
        if (BigDecimal.ZERO.compareTo(leftDetailQuantity) > 0){
          log.info("明细已无剩余可用数量 uuid:{} itemId:{} leftDetailQuantity:{}"
                  ,item.getUuid(),item.getId(),leftDetailQuantity.toPlainString());
          return null;
//          throw new EnhanceRuntimeException("明细已无剩余可用数量，发票明细ID[" + item.getId() + "]");
        }
      } catch (NumberFormatException e) {
        log.warn("明细数量占用计算异常 itemId={}", item.getId(),e);
        return null;
      }
    }

    BlueInvoiceService.InvoiceItem invoiceItem = new BlueInvoiceService.InvoiceItem();
    invoiceItem.setItemId(item.getId());
    invoiceItem.setInvoiceCode(item.getInvoiceCode());
    invoiceItem.setInvoiceNo(item.getInvoiceNo());
    invoiceItem.setDetailNo(item.getDetailNo());
    invoiceItem.setGoodsName(item.getGoodsName());
    invoiceItem.setModel(item.getModel());
    invoiceItem.setUnit(item.getUnit());
    invoiceItem.setTaxRate(item.getTaxRate());
    invoiceItem.setGoodsNum(item.getGoodsNum());

    invoiceItem.setNum(item.getNum());
    invoiceItem.setUnitPrice(item.getUnitPrice());
    invoiceItem.setDetailAmount(item.getDetailAmount());
    invoiceItem.setTaxAmount(item.getTaxAmount());

    invoiceItem.setMatchedNum(matchedNum);
    invoiceItem.setMatchedUnitPrice(matchedUnitPrice);
    invoiceItem.setMatchedDetailAmount(matchedDetailAmount);
    invoiceItem.setMatchedTaxAmount(matchedTaxAmount);
    invoiceItem.setLeftDetailAmount(leftDetailAmount.subtract(matchedDetailAmount));
    invoiceItem.setLeftNum(leftDetailQuantity);

    return invoiceItem;
  }


}
