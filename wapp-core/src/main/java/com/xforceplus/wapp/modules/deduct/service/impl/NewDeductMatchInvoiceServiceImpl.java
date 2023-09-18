package com.xforceplus.wapp.modules.deduct.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.converters.TDxRecordInvoiceDetailEntityConvertor;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfSysLogModuleEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.modules.deduct.service.NewDeductMatchInvoiceService;
import com.xforceplus.wapp.modules.syslog.util.SysLogUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.repository.dao.TDxTaxCurrentDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDetailDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemRefExtDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxTaxCurrentEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import com.xforceplus.wapp.util.BigDecimalUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author just
 *
 */
@Slf4j
@Service
public class NewDeductMatchInvoiceServiceImpl implements NewDeductMatchInvoiceService{
	@Autowired
    private CompanyService companyService;
	@Autowired
    protected TXfBillDeductItemRefExtDao tXfBillDeductItemRefDao;//查询索赔主信息和明细信息对应关系
	@Autowired
    private TXfBillDeductDao tXfBillDeductDao;//查询业务单主信息
	@Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;//查询业务单明细信息
	@Autowired
    private TDxTaxCurrentDao tDxTaxCurrentDao; //查询税号当前所属期
	@Autowired
	private TDxRecordInvoiceDao dxRecordInvoiceDao;//抵账表查询
	@Autowired
	private TDxRecordInvoiceDetailDao dxRecordInvoiceDetailDao;//抵账明细表
	@Autowired
	private TXfBillDeductInvoiceDetailDao billDeductInvoiceDetailDao;//业务单发票明细关系表 Mapper 接口
	
	
	@Override
	public R<List<BlueInvoiceService.InvoiceItem>> deductMatchInvoice(long decductId){
		TXfBillDeductEntity billDeduct = tXfBillDeductDao.selectById(decductId);
		if(billDeduct == null) {
			return R.fail(decductId + "找不到业务单信息");
		}
		return this.deductMatchInvoice(billDeduct);
	}
	
	@Override
	public R<List<BlueInvoiceService.MatchRes>> deductMatchInvoiceByClaim(TXfBillDeductEntity tXfBillDeductEntity){
		R<List<BlueInvoiceService.InvoiceItem>> result = this.deductMatchInvoice(tXfBillDeductEntity);
		if(result.isOk()) {
			List<BlueInvoiceService.MatchRes> list = new ArrayList<>();
			result.getResult().forEach(item->{
				
			});
			return R.ok(list);
		}
		return R.fail(result.getMessage());
	}
	
	@Override
	public R<List<BlueInvoiceService.InvoiceItem>> deductMatchInvoice(TXfBillDeductEntity billDeduct) {
		if(billDeduct == null || StringUtils.isBlank(billDeduct.getBusinessNo()) || billDeduct.getBusinessType() == null) {
			return R.fail("deductMatchInvoice参数为空");
		}
		log.info("deductMatchInvoice begin businessNo:{}, business_type:{}", billDeduct.getBusinessNo(), billDeduct.getBusinessType());
		if (billDeduct.getBusinessType() == TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue() && billDeduct.getStatus().compareTo(TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode()) != 0) {
            log.error("claim status fail, businessNo:{}, status:{}, 跳过匹配蓝票", billDeduct.getBusinessNo(), billDeduct.getStatus());
            String msg = String.format("索赔单businessNo:%s, status:%s, 跳过匹配蓝票", billDeduct.getBusinessNo(), TXfDeductStatusEnum.getEnumByCode(billDeduct.getStatus()).getDesc());
            return R.fail(msg);
        }
		if (billDeduct.getBusinessType() == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue() && billDeduct.getStatus().compareTo(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_BLUE_INVOICE.getCode()) != 0) {
            log.error("agreement status fail, businessNo:{}, status:{}, 跳过匹配蓝票", billDeduct.getBusinessNo(), billDeduct.getStatus());
            String msg = String.format("协议businessNo:%s, status:%s, 跳过匹配蓝票", billDeduct.getBusinessNo(), TXfDeductStatusEnum.getEnumByCode(billDeduct.getStatus()).getDesc());
            return R.fail(msg);
        }
		TAcOrgEntity tAcSellerOrgEntity = queryOrgInfo(billDeduct.getSellerNo(), true);
		TAcOrgEntity tAcPurcharserOrgEntity = queryOrgInfo(billDeduct.getPurchaserNo(), false);
		if (Objects.isNull(tAcPurcharserOrgEntity) || Objects.isNull(tAcSellerOrgEntity)) {
			log.error("businessNo:{},购销方信息不完整sellerNo:{},sellerOrgEntity:{},purcharseNo:{},purchaserOrgEntity：{}", billDeduct.getBusinessNo(), billDeduct.getSellerNo(), tAcSellerOrgEntity, billDeduct.getPurchaserNo(), tAcPurcharserOrgEntity);
			String msg = String.format("索赔单businessNo:%s,购销方信息不完整", billDeduct.getBusinessNo());
            return R.fail(msg);
		}
		//索赔逻辑
		if(billDeduct.getBusinessType() == TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue()) {
			//查询索赔和索赔明细对应关系
			List<TXfBillDeductItemRefEntity> billDeductItemRefList = queryBillDeductItemRef(billDeduct.getId());
			if(billDeductItemRefList == null || billDeductItemRefList.size() == 0) {
				String msg = String.format("索赔单businessNo:%s,无索赔明细", billDeduct.getBusinessNo());
	            return R.fail(msg);
			}
			//查询业务单对应的明细信息
			List<Long> deductItemId = billDeductItemRefList.stream().map(TXfBillDeductItemRefEntity::getDeductItemId).collect(Collectors.toList());
			List<TXfBillDeductItemEntity> billDeductItem = tXfBillDeductItemDao.selectBatchIds(deductItemId);
			if(billDeductItem == null || billDeductItem.size() == 0) {
				String msg = String.format("索赔单businessNo:%s,索赔明细查询失败", billDeduct.getBusinessNo());
	            return R.fail(msg);
			}
			Map<Long, TXfBillDeductItemEntity> billDeductItemMap = billDeductItem.stream().collect(Collectors.toMap(TXfBillDeductItemEntity::getId, (p) -> p));
			List<BlueInvoiceService.InvoiceItem> allItems = new ArrayList<BlueInvoiceService.InvoiceItem>();
			for (TXfBillDeductItemRefEntity deductItemRef : billDeductItemRefList){
				TXfBillDeductItemEntity billDeductItemEntity = billDeductItemMap.get(deductItemRef.getDeductItemId());
				if(billDeductItemEntity == null) {
					String msg = String.format("索赔单businessNo:%s,索赔明细:%s,查询失败", billDeduct.getBusinessNo(), deductItemRef.getDeductId());
		            return R.fail(msg);
				}
				//零税率不参加匹配
				if(billDeductItemEntity.getTaxRate().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				//循环匹配
				R<List<BlueInvoiceService.InvoiceItem>> matchResult = matchInvoiceDetails(billDeduct, tAcSellerOrgEntity.getTaxNo(), tAcPurcharserOrgEntity.getTaxNo(), deductItemRef, billDeductItemEntity);
				if(!matchResult.isOk()) {
					return matchResult;
				}
				allItems.addAll(matchResult.getResult());
			}
			return R.ok(allItems);
		}else if(billDeduct.getBusinessType() == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue()) {
			//协议逻辑
			List<TDxRecordInvoiceEntity> invoiceList;
			//查询匹配的蓝票
			invoiceList = obtainAvailableInvoice(null, tAcSellerOrgEntity.getTaxNo(), tAcPurcharserOrgEntity.getTaxNo(), billDeduct.getTaxRate(), false, TXfDeductionBusinessTypeEnum.CLAIM_BILL);
			if(invoiceList == null || invoiceList.size() == 0) {
				String msg = String.format("协议单businessNo:%s, status:%s, 蓝票匹配失败无充足的蓝票", billDeduct.getBusinessNo(), TXfDeductStatusEnum.getEnumByCode(billDeduct.getStatus()).getDesc());
				return R.fail(msg);
			}
			List<BlueInvoiceService.InvoiceItem> allItems = new ArrayList<BlueInvoiceService.InvoiceItem>();
			List<BlueInvoiceService.InvoiceItem> items = null;
			for (TDxRecordInvoiceEntity tDxRecordInvoiceEntity : invoiceList) {
				items = obtainAvailableItems(tDxRecordInvoiceEntity.getUuid(), billDeduct.getId(), billDeduct.getAmountWithTax(), TXfDeductionBusinessTypeEnum.AGREEMENT_BILL, tAcSellerOrgEntity.getDiscountRate());
				log.info("___"+tDxRecordInvoiceEntity.getUuid()+"____" + billDeduct.getAmountWithTax().toPlainString() + ","+ billDeduct.getId()+":匹配结果："+ JSON.toJSONString(items));
				if(items == null || items.size() == 0) {
					continue;
				}
				allItems.addAll(items);
				if(isMatchResult(billDeduct.getAmountWithTax(), items)) {
					return R.ok(allItems);
				}
			}
			BigDecimal sumAmount = BigDecimal.ZERO;
			if(allItems == null || allItems.size() == 0) {
				sumAmount = allItems.stream().map(BlueInvoiceService.InvoiceItem::getMatchedDetailAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			}
			String msg = String.format("协议单businessNo:%s, status:%s, 已匹配金额:%s", billDeduct.getBusinessNo(), TXfDeductStatusEnum.getEnumByCode(billDeduct.getStatus()).getDesc(), sumAmount.toPlainString());
			return R.fail(msg);
		}
		String msg = String.format("业务单匹配失败businessNo:%s, status:%s", billDeduct.getBusinessNo(), TXfDeductStatusEnum.getEnumByCode(billDeduct.getStatus()).getDesc());
		return R.fail(msg);
	}

	
	private R<List<BlueInvoiceService.InvoiceItem>> matchInvoiceDetails(TXfBillDeductEntity billDeduct, String sellerTaxNo, String purcharserTaxNo, TXfBillDeductItemRefEntity deductItemRef, TXfBillDeductItemEntity billDeductItemEntity) {
		List<TDxRecordInvoiceEntity> invoiceList;
		//查询匹配的蓝票
		invoiceList = obtainAvailableInvoice(null, sellerTaxNo, purcharserTaxNo, billDeductItemEntity.getTaxRate(), false, TXfDeductionBusinessTypeEnum.CLAIM_BILL);
		if(invoiceList == null || invoiceList.size() == 0) {
			String msg = String.format("索赔单businessNo:%s, itemId:%s, 蓝票匹配失败无充足的蓝票", billDeduct.getBusinessNo(), deductItemRef.getDeductItemId());
			return R.fail(msg);
		}
		List<BlueInvoiceService.InvoiceItem> allItems = new ArrayList<BlueInvoiceService.InvoiceItem>();
		List<BlueInvoiceService.InvoiceItem> items = null;
		for (TDxRecordInvoiceEntity tDxRecordInvoiceEntity : invoiceList) {
			items = obtainAvailableItems(tDxRecordInvoiceEntity.getUuid(), billDeduct.getId(), deductItemRef.getAmountWithTax(), TXfDeductionBusinessTypeEnum.CLAIM_BILL, null);
			//判断是否全部匹配完成
			log.info(deductItemRef.getDeductItemId()+ "___"+ tDxRecordInvoiceEntity.getUuid()+"____" + deductItemRef.getAmountWithTax().toPlainString() + ","+ deductItemRef.getDeductId()+":匹配结果："+ JSON.toJSONString(items));
			if(items == null || items.size() == 0) {
				continue;
			}
			allItems.addAll(items);
			if(isMatchResult(deductItemRef.getAmountWithTax(), items)) {
				return R.ok(allItems);
			}
		}
		BigDecimal sumAmount = BigDecimal.ZERO;
		if(allItems == null || allItems.size() == 0) {
			sumAmount = allItems.stream().map(BlueInvoiceService.InvoiceItem::getMatchedDetailAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
		String msg = String.format("索赔单businessNo:%s, itemId:%s,已匹配金额:%s", billDeduct.getBusinessNo(), deductItemRef.getDeductItemId(), sumAmount.toPlainString());
		return R.fail(msg);
	}
	

	/**
	 * 判断金额是否完全匹配
	 * @param matchAmount
	 * @param items
	 * @return
	 */
	private boolean isMatchResult(BigDecimal matchAmount, List<BlueInvoiceService.InvoiceItem> items) {
		if(items == null || items.size() == 0) {
			return false;
		}
		BigDecimal sumAmount = items.stream().map(BlueInvoiceService.InvoiceItem::getMatchedDetailAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		return matchAmount.compareTo(sumAmount) == 0;
	}
	
	/**
	 * <pre>
	 * 获取发票明细数据UUID = invoiceCode + invoiceNo
	 * 1：查询明细后会计算折扣金额
	 * 2、过滤"（详见销货清单）","(详见销货清单）","（详见销货清单)","原价合计","折扣额合计" 行
	 * </pre>
	 * @param uuid
	 * @return
	 */
	public List<TDxRecordInvoiceDetailEntity> getInvoiceItems(String uuid){
		List<TDxRecordInvoiceDetailEntity> items = dxRecordInvoiceDetailDao.queryByUuid(uuid);
		if(items == null || items.size() == 0) {
			return null;
		}
		// 如存在折扣，则按折扣重算明细金额和单价
		Map<String, List<TDxRecordInvoiceDetailEntity>> groupMap = items.stream().collect(Collectors.groupingBy(TDxRecordInvoiceDetailEntity::getGoodsName));
		for (String key : groupMap.keySet()) {
			List<TDxRecordInvoiceDetailEntity> list = groupMap.get(key);
			if(list.size() == 1) { //不含折扣
				continue;
			}
			boolean includDiscount = false;//判断是否包含折扣行
			for (TDxRecordInvoiceDetailEntity tDxRecordInvoiceDetailEntity : list) {
				if(BigDecimal.ZERO.compareTo(new BigDecimal(tDxRecordInvoiceDetailEntity.getDetailAmount())) > 0) {
					includDiscount = true;
					break;
				}
			}
			if(includDiscount) {//包含折扣，重新计算
				list = list.stream().sorted(Comparator.comparing(TDxRecordInvoiceDetailEntity::getDetailAmount, (x, y) -> {
							return new BigDecimal(y).compareTo(new BigDecimal(x));
						})).collect(Collectors.toList());
				for (int i = 0; i < list.size(); i++) {
					if(BigDecimal.ZERO.compareTo(new BigDecimal(list.get(i).getDetailAmount())) > 0) {
						break;
					}
					TDxRecordInvoiceDetailEntity positive = list.get(i); //正数明细行
					TDxRecordInvoiceDetailEntity negative = list.get(list.size() - i - 1); //负数明细（不一定负）
					BigDecimal discountDetailAmount = new BigDecimal(negative.getDetailAmount());
					if (BigDecimal.ZERO.compareTo(discountDetailAmount) <= 0) {
						break;
					}
					// 重算原明细金额及单价
					BigDecimal originDetailAmount = new BigDecimal(positive.getDetailAmount()).add(discountDetailAmount).setScale(2, RoundingMode.DOWN);
					// 折前金额
					positive.setDetailAmount(originDetailAmount.toPlainString());
					positive.setDiscountDetailAmount(discountDetailAmount);
					if (StringUtils.isNotBlank(positive.getNum())) {
						BigDecimal originUnitPrice = originDetailAmount.divide(new BigDecimal(positive.getNum()), 15, RoundingMode.HALF_UP);
						positive.setUnitPrice(originUnitPrice.toPlainString());//重新计算单价
					}
				}
			}
		}
		items.clear();//清理
		groupMap.values().stream().forEach(item->{
			items.addAll(item);
		});
		//过滤负数和合计行
		return items.stream().filter(v -> {
			if (StringUtils.equalsAnyIgnoreCase(v.getGoodsName(), "(详见销货清单)", "（详见销货清单）", "(详见销货清单）", "（详见销货清单)", "原价合计", "折扣额合计")) {
				return false;
			}
			return new BigDecimal(v.getDetailAmount()).compareTo(BigDecimal.ZERO) >= 0;
		}).sorted(Comparator.comparing(TDxRecordInvoiceDetailEntity::getDetailNo, (x, y) -> {
			return new BigDecimal(x).compareTo(new BigDecimal(y));
		})).collect(Collectors.toList());
	}
	
	/**
	 * <pre>
	 * 根据之前的剩余金额，以及本次需要抵扣的可用金额，
	 * 按照明细顺序找到合适的明细行返回，返回的明细金额总和一定<=需要抵扣的可用金额
	 * 
	 * 根据发票ID，业务ID，待匹配金额，匹配蓝票明细
	 * 
	 * 1、查询发票明细行（加过处理过的）
	 * 2、查询发票明细已匹配的金额，数量信息
	 * 3、计算明细可匹配金额
	 * 4、生成匹配明细
	 * @TODO
	 * </pre>
	 * @param uuid 发票代码+发票号码 拼接
	 * @param deductId 业务单ID
	 * @param deductedAmount 扣除金额，待匹配金额
	 * @param businessTypeEnum 业务类型
	 * @param discountRate折让率，协议才有的字段
	 * @return
	 */
	private List<BlueInvoiceService.InvoiceItem> obtainAvailableItems(String uuid, Long deductId, BigDecimal deductedAmount, TXfDeductionBusinessTypeEnum businessTypeEnum, BigDecimal discountRate) {
		if(discountRate != null && discountRate.compareTo(BigDecimal.ONE) > 0) {
			discountRate = discountRate.divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
		}
		log.info("obtainAvailableItems-收到匹配蓝票明细任务 deductId:{}, uuid:{}, deductedAmount:{}, businessTypeEnum:{},discountRate:{}", deductId, uuid, deductedAmount, businessTypeEnum, discountRate);
		List<BlueInvoiceService.InvoiceItem> list = new ArrayList<>();
		//查询发票明细
		List<TDxRecordInvoiceDetailEntity> invoiceItems = getInvoiceItems(uuid);
		if(invoiceItems == null || invoiceItems.size() == 0) {
			log.info("obtainAvailableItems-查询发票明细失败,发票找不到明细 deductId:{}, uuid:{}, deductedAmount:{}, businessTypeEnum:{}", deductId, uuid, deductedAmount, businessTypeEnum);
			return null;
		}
		//查询发票明细对应已经匹配的金额
		List<Long> itemIds = invoiceItems.stream().map(TDxRecordInvoiceDetailEntity::getId).distinct().collect(Collectors.toList());
		List<TXfBillDeductInvoiceDetailEntity> billDeductInvoiceList = billDeductInvoiceDetailDao.queryByInvoiceItemIds(itemIds);
		Map<Long, List<TXfBillDeductInvoiceDetailEntity>> invoiceDetailGroupMap = null;
		if(billDeductInvoiceList != null && billDeductInvoiceList.size() > 0) {
			invoiceDetailGroupMap = billDeductInvoiceList.stream().collect(Collectors.groupingBy(TXfBillDeductInvoiceDetailEntity::getInvoiceDetailId));
		}
		//累加得到明细已经匹配的金额
		for (TDxRecordInvoiceDetailEntity item : invoiceItems) {
			if(invoiceDetailGroupMap != null && invoiceDetailGroupMap.containsKey(item.getId())) {
				List<TXfBillDeductInvoiceDetailEntity> detailsList = invoiceDetailGroupMap.get(item.getId());
				if (detailsList == null || detailsList.size() == 0) {
					continue;
				}
				detailsList.forEach(detail->{
					item.setUseAmountWithoutTax(detail.getUseAmountWithoutTax().add(item.getUseAmountWithoutTax() == null ? BigDecimal.ZERO : item.getUseAmountWithoutTax()));
					item.setUseAmountWithTax(detail.getUseAmountWithTax().add(item.getUseAmountWithTax() == null ? BigDecimal.ZERO : item.getUseAmountWithTax()));
					if(detail.getUseQuantity() != null) {
						item.setUseQuantity(detail.getUseQuantity().add(item.getUseQuantity() == null ? BigDecimal.ZERO : item.getUseQuantity()));
					}
					//统计协议使用金额
					if(detail.getBusinessType() == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue()) {
						item.setAgreementUseAmountWithoutTax(detail.getUseAmountWithoutTax().add(item.getAgreementUseAmountWithoutTax() == null ? BigDecimal.ZERO : item.getAgreementUseAmountWithoutTax()));
						item.setAgreementUseAmountWithTax(detail.getUseAmountWithTax().add(item.getAgreementUseAmountWithTax() == null ? BigDecimal.ZERO : item.getAgreementUseAmountWithTax()));
						if(detail.getUseQuantity() != null) {
							item.setAgreementUseQuantity(detail.getUseQuantity().add(item.getAgreementUseQuantity() == null ? BigDecimal.ZERO : item.getAgreementUseQuantity()));
						}
					}
				});
			}
		}
		
		// 总业务单需要匹配的金额
		BigDecimal needMatchAmount = BigDecimal.ZERO.add(deductedAmount);
		for (TDxRecordInvoiceDetailEntity item : invoiceItems) {
			BigDecimal availableAmount = new BigDecimal(item.getDetailAmount()).subtract(item.getUseAmountWithoutTax() == null ? BigDecimal.ZERO : item.getUseAmountWithoutTax());// 发票明细可用金额(不含税)
			BigDecimal availableQuantity = new BigDecimal(StringUtils.isBlank(item.getNum()) ? "0" : item.getNum()).subtract(item.getUseQuantity() == null ? BigDecimal.ZERO : item.getUseQuantity()); //发票明细可用数量
			//使用折让率
			if(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL == businessTypeEnum && discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal agreementAmount = new BigDecimal(item.getDetailAmount()).multiply(discountRate).subtract(item.getAgreementUseAmountWithoutTax() == null ? BigDecimal.ZERO : item.getAgreementUseAmountWithoutTax());
				if(availableAmount.compareTo(agreementAmount) >= 0) {
					availableAmount = agreementAmount;
				}
			}
			log.info("obtainAvailableItems-明细剩余金额deductId:{}, uuid:{}, itemId:{}, availableAmount:{}, availableQuantity:{}", deductId, uuid, item.getId(), availableAmount.toPlainString(), availableQuantity.toPlainString());
			if (BigDecimal.ZERO.compareTo(availableAmount) >= 0) {
				log.info("明细已无剩余可用金额 uuid:{} itemId:{}, availableAmount:{}, item.getDetailAmount:{}", uuid, item.getId(), availableAmount.toPlainString(), item.getDetailAmount());
				continue;
			}
			if(StringUtils.isNotBlank(item.getNum()) && BigDecimal.ZERO.compareTo(availableQuantity) >= 0) {
				log.info("明细已无剩余可用数量 uuid:{} itemId:{}, availableQuantity:{}, item.getNum:{}", uuid, item.getId(), availableQuantity.toPlainString(), item.getNum());
				continue;
			}
			//协议单明细剩余可匹配金额小于1元，不参与匹配
			if (businessTypeEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL && BigDecimal.ONE.compareTo(availableAmount) > 0) {
				log.info("协议单匹配蓝票明细且剩余可匹配金额小于1元，跳过该明细寻找下一条大于1元的明细,uuid:{} deductId:{}, itemId:{}, availableAmount:{}, item.getDetailAmount:{}", uuid, deductId, item.getId(), availableAmount.toPlainString(),availableQuantity.toPlainString());
				// 增加系统日志
				String msg = String.format("协议单未足额匹配蓝票明细且剩余待匹配金额小于1元,uuid:%s, deductId:%d, itemId:%d, availableAmount:%s, item.getDetailAmount:%s", uuid, deductId, item.getId(), availableAmount.toPlainString(),availableQuantity.toPlainString());
				SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.MATCH_INVOICE_DETAIL, msg);
				continue;
			}
			// 如果明细数量有剩余，通过剩余数量*单价算出剩余不含税,取最小剩余不含税
			if (StringUtils.isNotBlank(item.getUnitPrice()) && StringUtils.isNotBlank(item.getNum())) {
				// 剩余可用明细金额=剩余明细数量*单价取保留2位小数值
				BigDecimal leftDetailAmountM = availableQuantity.multiply(new BigDecimal(item.getUnitPrice())).setScale(2, RoundingMode.HALF_UP);
				if (leftDetailAmountM.compareTo(availableAmount) != 0) {// 重新计算数量
					log.info("obtainAvailableItems-重新计算数量,uuid:{} itemId:{},leftDetailAmountM:{},leftDetailAmount:{},totalDetailAmount:{}", uuid, item.getId(), leftDetailAmountM, availableAmount, item.getDetailAmount());
					if (availableQuantity.compareTo(new BigDecimal(item.getNum())) == 0) {
						availableQuantity = new BigDecimal(item.getDetailAmount()).divide(new BigDecimal(item.getUnitPrice()), 15, RoundingMode.HALF_UP);
						item.setNum(availableQuantity.toPlainString());
						leftDetailAmountM = availableQuantity.multiply(new BigDecimal(item.getUnitPrice())).setScale(2, RoundingMode.HALF_UP);
					}
					// 重算剩余明细金额<明细金额-占用金额-已匹配金额
					if (leftDetailAmountM.compareTo(availableAmount) < 0) {
						availableAmount = leftDetailAmountM;
					}
				}
			}

			// 剩余抵扣金额小于等于明细剩余可用金额，说明到这一条已经够了
			if (needMatchAmount.compareTo(availableAmount) <= 0) {
				BlueInvoiceService.InvoiceItem invoiceItem = calInvoiceDetailUse(item, needMatchAmount, availableQuantity, availableAmount);
				if (invoiceItem == null) {
					log.warn("明细占用计算失败，丢弃该条明细继续找下一条deductId:{}, uuid={} , itemId:{}", deductId, uuid, item.getId());
					continue;
				}
				list.add(invoiceItem);
				log.info("deductId:{}, uuid={} , itemId:{},已匹配的发票明细列表={}", deductId, uuid, item.getId(), JSON.toJSONString(list));
				return list;
			}
			log.info("未足额匹配，计算分摊金额及数量并继续下一条明细,deductId:{}, uuid={} , itemId:{},leftDetailAmount:{}", deductId, uuid, item.getId(), availableAmount.toPlainString());
			BigDecimal matchedDetailAmount = new BigDecimal(availableAmount.toPlainString());
			if (businessTypeEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL) {
				matchedDetailAmount = matchedDetailAmount.setScale(0, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN);
				log.info("协议单未足额匹配蓝票明细，占用金额取整处理 leftDetailAmount:{}", matchedDetailAmount.toPlainString());
			}
			//生成匹配明细信息
			BlueInvoiceService.InvoiceItem invoiceItem = calInvoiceDetailUse(item, matchedDetailAmount, availableQuantity, availableAmount);
			if (invoiceItem == null) {
				log.warn("明细占用计算失败，丢弃该条明细继续找下一条 uuid:{} itemId:{}", uuid, item.getId());
				continue;
			}
			// 剩余还需匹配金额 = 剩余抵扣金额-本条明细剩余可用金额
			needMatchAmount = needMatchAmount.subtract(matchedDetailAmount);
			// 增加系统日志
			String msg = String.format("未足额匹配 uuid:%s deductId:%d, itemId:%d, matchedDetailAmount:%s, needMatchAmount:%s", uuid, deductId, item.getId(), matchedDetailAmount.toPlainString(),needMatchAmount.toPlainString());
			SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.MATCH_INVOICE_DETAIL, msg);
			list.add(invoiceItem);
		}
		log.info("已匹配的发票明细列表={}", JSON.toJSONString(list));
		return list;
	}

	/**
	 * <pre>
	 * 1、生成计算明细占用情况（含税金额、不含税金额、数量）
	 * 
	 * 
	 * </pre>
	 * @param item 发票明细信息
	 * @param useAmount 匹配金额
	 * @param leftDetailQuantity 可用匹配数量
	 * @param leftDetailAmount 可用匹配金额
	 * @return
	 */
	private BlueInvoiceService.InvoiceItem calInvoiceDetailUse(TDxRecordInvoiceDetailEntity item, BigDecimal useAmount, BigDecimal leftDetailQuantity, BigDecimal leftDetailAmount) {
		log.info("底账明细表数据:{}", JSON.toJSON(item));
		log.info("计算明细占用情况，itemId：{}，useAmount：{}，leftDetailQuantity：{}，leftDetailAmount：{}", item.getId(), useAmount, leftDetailQuantity, leftDetailAmount);
		final BigDecimal detailTaxRate = new BigDecimal(item.getTaxRate()).movePointLeft(2);
		BigDecimal totalDetailAmount = new BigDecimal(item.getDetailAmount());
		BigDecimal matchedDetailAmount = useAmount;//匹配的明细金额
		BigDecimal matchedTaxAmount = useAmount.multiply(detailTaxRate).setScale(2, RoundingMode.HALF_UP);//匹配税额
		BigDecimal matchedNum = BigDecimal.ZERO;//占用数量
		BigDecimal matchedUnitPrice = TDxRecordInvoiceDetailEntityConvertor.INSTANCE.stringToBgDcmlOrZero(item.getUnitPrice());//占用单价
		// 计算占用明细数量
		if (StringUtils.isNotBlank(item.getNum())) {
			BigDecimal detailNum = new BigDecimal(item.getNum());
			//反算需要占用的数量
			matchedNum = useAmount.multiply(detailNum).divide(totalDetailAmount, 15, RoundingMode.HALF_UP).setScale(15, RoundingMode.HALF_UP);
			// 如果剩余数量是整数且占用数量不是整数，则占用数量需要向上取整，并反算单价
			if (BigDecimalUtil.isInteger(leftDetailQuantity) && !BigDecimalUtil.isInteger(matchedNum)) {
				matchedNum = new BigDecimal(matchedNum.intValue() + 1);
				// 反算单价
				matchedUnitPrice = matchedDetailAmount.divide(matchedNum, 15, RoundingMode.HALF_UP);
			}
			leftDetailQuantity = leftDetailQuantity.subtract(matchedNum);
			// 判断数量是否用超
			if (BigDecimal.ZERO.compareTo(leftDetailQuantity) > 0) {
				log.info("明细已无剩余可用数量 uuid:{} itemId:{} leftDetailQuantity:{}", item.getUuid(), item.getId(), leftDetailQuantity.toPlainString());
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
	
	/**
	 * 获取购方税号当前所属期
	 * @param purchaserTaxNo
	 * @return
	 */
	private String getTaxCurrent(String purchaserTaxNo) {
		QueryWrapper<TDxTaxCurrentEntity> taxCurrentQueryWrapper = new QueryWrapper<TDxTaxCurrentEntity>();
		taxCurrentQueryWrapper.eq(TDxTaxCurrentEntity.TAXNO, purchaserTaxNo);
		TDxTaxCurrentEntity taxCurrentEntity = tDxTaxCurrentDao.selectOne(taxCurrentQueryWrapper);
		if (taxCurrentEntity != null && StringUtils.isNotBlank(taxCurrentEntity.getCurrentTaxPeriod())) {
			return taxCurrentEntity.getCurrentTaxPeriod();
		}
		return null;
	}
	
	private Date getYearBefore(Date date, int year) {
		if(date == null) {
			date = new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, year);
		return calendar.getTime();
	}
	
	/**
     * 从底账表按照先进先出的方式获取一张合适的蓝票
     *
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @param notQueryOil
     * @param invoiceDateOrder DESC | ASC
     * @return
     */
	public List<TDxRecordInvoiceEntity> obtainAvailableInvoice(List<Long> preIdList, String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate, boolean notQueryOil, TXfDeductionBusinessTypeEnum businessTypeEnum) {
		log.info("obtainAvailableInvoice sellerTaxNo:{},purchaserTaxNo:{},taxRate:{} 是否查询成品油发票：{}", sellerTaxNo, purchaserTaxNo, taxRate, !notQueryOil);
		// 根据购方税号获取当前征期
		String currentTaxPeriod = getTaxCurrent(purchaserTaxNo);
		// 获取两年前的日期
		Date invoiceDate = getYearBefore(null, -2);
		// 按照发票先进先出 2022-08-23 新增 协议匹配蓝票时，假设期间是202007-202207，
		// 之前是从2020年的发票开始匹配，现在要改成先从2022年发票开始匹配，匹配近两年供应商蓝票规则不变。
		// https://jira.xforceplus.com/browse/PRJCENTER-10272
		String invoiceDateOrder = businessTypeEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL ? "DESC" : "ASC";
		// 按照发票先进先出 2022-08-23 新增
		// 协议匹配蓝票时，假设期间是202007-202207，之前是从2020年的发票开始匹配，现在要改成先从2022年发票开始匹配，匹配近两年供应商蓝票规则不变。
		// https://jira.xforceplus.com/browse/PRJCENTER-10272
		String preIdStr = (preIdList == null || preIdList.size() == 0) ? null : StringUtils.join(preIdList, ",");
		//格式化税率，发票上税率是整数
		if(taxRate.compareTo(BigDecimal.ONE) < 0) {
			taxRate = taxRate.multiply(new BigDecimal(100));
		}
		return dxRecordInvoiceDao.queryNoMatchBlueInvoice(preIdStr, sellerTaxNo, purchaserTaxNo, invoiceDate, currentTaxPeriod, taxRate, notQueryOil ? null : "1", invoiceDateOrder);
	}
	
	/**
	 * 根据业务单ID，查询业务单主信息和明细信息对应关系
	 * @param deductId
	 * @return
	 */
	private List<TXfBillDeductItemRefEntity> queryBillDeductItemRef(Long deductId) {
		QueryWrapper<TXfBillDeductItemRefEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(TXfBillDeductItemRefEntity.DEDUCT_ID, deductId);
		queryWrapper.eq(TXfBillDeductItemRefEntity.STATUS, 0);
		return tXfBillDeductItemRefDao.selectList(queryWrapper);
	}

	/**
	 * 查询购方，销方信息
	 * @param no
	 * @param iseller
	 * @return
	 */
	private TAcOrgEntity queryOrgInfo(String no, boolean iseller) {
		TAcOrgEntity res;
		if (iseller) {
			res = companyService.getOrgInfoByOrgCode(no, "8");
		} else {
			res = companyService.getOrgInfoByOrgCode(no, "5");
		}
		return res;
	}

}
