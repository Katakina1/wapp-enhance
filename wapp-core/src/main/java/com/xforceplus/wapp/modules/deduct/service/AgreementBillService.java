package com.xforceplus.wapp.modules.deduct.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfSettlementItemFlagEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.TXfSysLogModuleEnum;
import com.xforceplus.wapp.enums.TaxRateTransferEnum;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceDetailBean;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceDetailListResponse;
import com.xforceplus.wapp.modules.deduct.model.AgreementMergeData;
import com.xforceplus.wapp.modules.deduct.model.DeductInvoiceDetailData;
import com.xforceplus.wapp.modules.deduct.model.threadlocal.BlueInvoiceMatchHolder;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.syslog.util.SysLogUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemInvoiceDetailEntity;
import com.xforceplus.wapp.util.CodeGenerator;

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
public class AgreementBillService extends DeductService{
/*    @Autowired
    private StatementServiceImpl statementService;
    @Autowired
    private SettlementItemServiceImpl settlementItemService;*/
    @Autowired
    private BillSettlementService billSettlementService;
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;
/*    @Autowired
    private DeductInvoiceService deductInvoiceService;*/
    @Lazy
    @Autowired
    private DeductViewService deductViewService;
    @Autowired
    private DeductInvoiceDetailService deductInvoiceDetailService;
    @Autowired
    private SettlmentItemBatchService settlmentItemBatchService;
    @Autowired
    private SettlmentItemInvoiceDetailService settlmentItemInvoiceDetailService;
    @Autowired
    private OperateLogService operateLogService;
    @Autowired
    private TDxRecordInvoiceDetailDao tDxRecordInvoiceDetailDao;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;


	private final static BigDecimal TAX_RATE_16 = BigDecimal.valueOf(16);
	private final static BigDecimal TAX_RATE_17 = BigDecimal.valueOf(17);
	private final static BigDecimal TAX_RATE_10 = BigDecimal.valueOf(10);
	private final static BigDecimal TAX_RATE_11 = BigDecimal.valueOf(11);
	private final static BigDecimal TAX_RATE_9 = BigDecimal.valueOf(9);
	private final static BigDecimal TAX_RATE_13 = BigDecimal.valueOf(13);

	public final static BigDecimal MAX_DIFF_TAX_AMOUNT = new BigDecimal("0.05");

    /**
     * 手工组合并结算单
     * 主流程步骤：
     * 1.校验入参,初始化合并信息
     * 2.组合合并信息
     * 3.根据合并金额匹配蓝票
     * 4.转换对象并返回
     * @param request
     * @return
     */
    /*public List<MatchedInvoiceListResponse> preSettlementByManual(PreMakeSettlementRequest request){
        //1.校验入参,初始化合并信息
        if (CollectionUtils.isEmpty(request.getBillIds())) {
            throw new EnhanceRuntimeException("请至少选择一张业务单据");
        }
        if (StringUtils.isBlank(request.getPurchaserNo())) {
            throw new EnhanceRuntimeException("购方编号不可为空");
        }
        if (request.getTaxRate() == null) {
            throw new EnhanceRuntimeException("合并税率必传");
        }

        BigDecimal taxRate = TaxRateTransferEnum.transferTaxRate(request.getTaxRate());
        taxRate=convertTaxRate(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL,taxRate);

        //2.组合合并信息
        AgreementMergeData mergeData = new AgreementMergeData(request.getPurchaserNo(),
                request.getSellerNo(),request.getTaxRate());
        mergeData.setSettlementNo(CodeGenerator.generateCode(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL));
        mergeData.setInvoiceIdList(request.getInvoiceIds());
        mergeData.setInvoiceDetailList(request.getDetailDataList());
        //2.1 合并正数单据
        makeMergeDataByManual(mergeData,request.getBillIds());
        //2.2 合并负数单据
        makeMergeDataOrgAndNegative(mergeData);
        //2.3 校验合并信息
        checkMerge(mergeData);
        //记录系统日志
        SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,"组合合并信息完成");
        //3.根据合并金额匹配蓝票
        List<BlueInvoiceService.MatchRes> matchResList= null;
        if (CollectionUtils.isNotEmpty(mergeData.getInvoiceIdList())){
            log.info("根据指定蓝票ID列表匹配");
            matchResList = deductBlueInvoiceService.matchBlueInvoiceByIds(mergeData.getAmountWithoutTax()
                    ,mergeData.getInvoiceIdList(),false,TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        }else if (CollectionUtils.isNotEmpty(mergeData.getInvoiceDetailList())){
          log.info("根据指定蓝票明细列表匹配");
          matchResList = deductBlueInvoiceService.matchBlueInvoiceByDetail(mergeData.getAmountWithoutTax(),mergeData.getTaxRate()
                  ,mergeData.getInvoiceDetailList(),false,TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        }else {
            log.info("根据系统默认规则匹配");
            matchResList = deductBlueInvoiceService.matchBlueInvoice(mergeData.getSettlementNo()
                            , mergeData.getSellerOrg().getTaxNo(), mergeData.getPurchaserOrg().getTaxNo()
                            , taxRate, mergeData.getAmountWithoutTax(), mergeData.getNotQueryOil(), false
                            , TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        }
        //记录系统日志
        SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,"根据合并金额匹配蓝票完成");
        //4.转换对象并返回
        List<MatchedInvoiceListResponse> matchResponseList = new ArrayList<>();
        MatchedInvoiceListResponse matchResponse;
        if (CollectionUtils.isNotEmpty(matchResList)){
            for (BlueInvoiceService.MatchRes matchRes : matchResList) {
                matchResponse = new MatchedInvoiceListResponse();
                matchResponse.setMatchedAmount(matchRes.getDeductedAmount());
                matchResponse.setId(matchRes.getInvoiceId());
                matchResponse.setInvoiceNo(matchRes.getInvoiceNo() );
                matchResponse.setInvoiceCode(matchRes.getInvoiceCode());
                matchResponse.setInvoiceDate(DateUtils.format(matchRes.getInvoiceDate()));
                //商品名称逗号隔开返回
                String goodsName = matchRes.getInvoiceItems().stream()
                        .map(BlueInvoiceService.InvoiceItem::getGoodsName)
                        .collect(Collectors.joining(","));
                matchResponse.setGoodsName(goodsName);
                matchResponseList.add(matchResponse);
            }
        }
        //记录系统日志
        SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,"转换对象并返回完成");
        return matchResponseList;
    }*/

    /**
     * 手工组合并结算单
     * 主流程步骤：
     * 1.校验入参,初始化合并信息
     * 2.组合合并信息
     * 3.根据合并金额匹配蓝票
     * 4.转换对象并返回
     * @param request
     * @return
     */
	public MatchedInvoiceDetailListResponse preSettlementNewByManual(PreMakeSettlementRequest request) {
		// 1.校验入参,初始化合并信息
		if (CollectionUtils.isEmpty(request.getBillIds())) {
			throw new EnhanceRuntimeException("请至少选择一张业务单据");
		}
		if (StringUtils.isBlank(request.getPurchaserNo())) {
			throw new EnhanceRuntimeException("购方编号不可为空");
		}
		if (request.getTaxRate() == null) {
			throw new EnhanceRuntimeException("合并税率必传");
		}
		// 系统日志收集
		SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT, JSON.toJSONString(request));

		// 2.组合合并信息
		AgreementMergeData mergeData = new AgreementMergeData(request.getPurchaserNo(), request.getSellerNo(), request.getTaxRate());
		mergeData.setSettlementNo(CodeGenerator.generateCode(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL));
		// 2.0 合并正数单据
		makeMergeDataByManual(mergeData, request.getBillIds());
		// 2.1 设置目标税率
		mergeData.setTargetTaxRate(switchToTargetTaxRate(mergeData.getTaxRate(), true, mergeData.getTaxCode()));
		// 2.2 合并负数单据
		makeMergeDataOrgAndNegative(mergeData);
		// 重新计算税转金额
		mergeData.exchangeAmount();
		// 2.3 校验合并信息
		checkMerge(mergeData);
		// 3.根据合并金额匹配蓝票
		List<BlueInvoiceService.MatchRes> matchResList = null;
		if (mergeData.getPlusMinusFlag() == 0) {
			try {
				matchResList = preMatchBlueInvoiceByManual(mergeData, mergeData.getTargetTaxRate().movePointRight(2));
			} finally {
				// 清空ThreadLocal蓝票匹配缓存，以防影响后续匹配
				BlueInvoiceMatchHolder.clearContext();
			}
		} else {
			// 3.2 正负混合协议单，按合并总金额匹配蓝票明细
			matchResList = deductBlueInvoiceService.matchBlueInvoice(mergeData.getSettlementNo(),
					mergeData.getSellerOrg().getTaxNo(), mergeData.getPurchaserOrg().getTaxNo(),
					mergeData.getTargetTaxRate().movePointRight(2), mergeData.getAmountWithoutTax(),
					mergeData.getNotQueryOil(), false, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
		}
		// 4.转换对象并返回
		List<MatchedInvoiceDetailBean> matchDetailBeanList = new ArrayList<>();
		MatchedInvoiceDetailBean matchDetailBean;
		if (CollectionUtils.isNotEmpty(matchResList)) {
			for (BlueInvoiceService.MatchRes matchRes : matchResList) {
				for (BlueInvoiceService.InvoiceItem invoiceItem : matchRes.getInvoiceItems()) {
					matchDetailBean = new MatchedInvoiceDetailBean();
					matchDetailBean.setInvoiceId(matchRes.getInvoiceId());
					matchDetailBean.setBusinessNo(invoiceItem.getBusinessNo());
					matchDetailBean.setInvoiceDetailId(invoiceItem.getItemId());
					matchDetailBean.setUuid(matchRes.getInvoiceCode() + matchRes.getInvoiceNo());
					matchDetailBean.setInvoiceNo(matchRes.getInvoiceNo());
					matchDetailBean.setInvoiceCode(matchRes.getInvoiceCode());
					matchDetailBean.setGoodsName(invoiceItem.getGoodsName());
					matchDetailBean.setModel(invoiceItem.getModel());
					matchDetailBean.setInvoiceDate(DateUtils.format(matchRes.getInvoiceDate()));
					matchDetailBean.setGoodsNum(invoiceItem.getGoodsNum());
					matchDetailBean.setUnit(invoiceItem.getUnit());
					matchDetailBean.setNum(invoiceItem.getNum());
					matchDetailBean.setUnitPrice(invoiceItem.getUnitPrice());
					matchDetailBean.setDetailAmount(invoiceItem.getDetailAmount());
					matchDetailBean.setTaxRate(invoiceItem.getTaxRate());
					matchDetailBean.setTaxAmount(invoiceItem.getTaxAmount());
					matchDetailBean.setMatchedNum(invoiceItem.getMatchedNum() != null ? invoiceItem.getMatchedNum().toPlainString() : "");
					matchDetailBean.setMatchedUnitPrice(invoiceItem.getMatchedUnitPrice() != null ? invoiceItem.getMatchedUnitPrice().toPlainString() : "");
					matchDetailBean.setMatchedDetailAmount(invoiceItem.getMatchedDetailAmount() != null ? invoiceItem.getMatchedDetailAmount().toPlainString() : "");
					// 计算匹配税额
					if (StringUtils.isNotBlank(matchDetailBean.getMatchedDetailAmount())) {
						matchDetailBean.setMatchedTaxAmount(new BigDecimal(matchDetailBean.getMatchedDetailAmount()).multiply(new BigDecimal(matchDetailBean.getTaxRate()).movePointLeft(2)).setScale(2, RoundingMode.HALF_UP).toPlainString());
					}
					matchDetailBean.setLeftNum(invoiceItem.getLeftNum() != null ? invoiceItem.getLeftNum().toPlainString() : "");
					if (StringUtils.isNotBlank(matchDetailBean.getLeftNum()) && StringUtils.isNotBlank(matchDetailBean.getUnitPrice())) {
						BigDecimal leftDetailAmount = new BigDecimal(matchDetailBean.getLeftNum()).multiply(new BigDecimal(matchDetailBean.getUnitPrice())).setScale(2, RoundingMode.HALF_UP);
						// 弥补数量向上取整导致剩余不含税减少
						leftDetailAmount = leftDetailAmount.add(new BigDecimal(matchDetailBean.getUnitPrice()).subtract(new BigDecimal(matchDetailBean.getMatchedUnitPrice())).multiply(new BigDecimal(matchDetailBean.getMatchedNum())));
						matchDetailBean.setLeftDetailAmount(leftDetailAmount.toPlainString());
					} else {
						matchDetailBean.setLeftDetailAmount(invoiceItem.getLeftDetailAmount() != null ? invoiceItem.getLeftDetailAmount().toPlainString() : "");
					}
					matchDetailBeanList.add(matchDetailBean);
				}
			}
		}
		matchDetailBeanList = matchDetailBeanList.stream().sorted(Comparator.comparing(MatchedInvoiceDetailBean::getBusinessNo)).collect(Collectors.toList());
		MatchedInvoiceDetailListResponse matchResponse = new MatchedInvoiceDetailListResponse();
		matchResponse.setMergeAmountWithoutTax(mergeData.getAmountWithoutTax().toPlainString());
		matchResponse.setMergeAmountWithTax(mergeData.getAmountWithTax().toPlainString());
		matchResponse.setMergeTaxAmount(mergeData.getTaxAmount().toPlainString());
		matchResponse.setTargetTaxRate(mergeData.getTargetTaxRate().toPlainString());
		matchResponse.setTaxCode(mergeData.getTaxCode());
		matchResponse.setDetailList(matchDetailBeanList);
		return matchResponse;
	}

	/**
	 * 正数协议单预匹配
	 * 
	 * @return
	 */
	private List<BlueInvoiceService.MatchRes> preMatchBlueInvoiceByManual(AgreementMergeData mergeData,	BigDecimal taxRate) {
		// 清空ThreadLocal缓存
		BlueInvoiceMatchHolder.clearContext();
		// 仅正数协议单，每一张协议单分别匹配再合并返回
		// 按发票号码代码组合匹配记录
		Map<String, BlueInvoiceService.MatchRes> invoiceMatchResMap = new HashMap<>();
		List<BlueInvoiceService.MatchRes> plusMatchResList;
		// 要根据转换后的税率计算出待匹配的金额
		for (TXfBillDeductEntity deductEntity : mergeData.getMergeDeductList()) {
			plusMatchResList = deductBlueInvoiceService.matchBlueInvoice(deductEntity.getBusinessNo(),
					mergeData.getSellerOrg().getTaxNo(), mergeData.getPurchaserOrg().getTaxNo(), taxRate,
					deductEntity.getAmountWithoutTax(), mergeData.getNotQueryOil(), false,
					TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);

			// 已获取成品油发票，则不可再查询成品油发票
			if (!mergeData.getNotQueryOil()) {
				for (BlueInvoiceService.MatchRes matchRes : plusMatchResList) {
					if (Integer.valueOf(1).equals(matchRes.getIsOil())) {
						mergeData.setNotQueryOil(true);
					}
				}
			}
			//设置默认匹配的业务单号
			if (plusMatchResList != null) {
				for (BlueInvoiceService.MatchRes matchRes : plusMatchResList) {
					matchRes.invoiceItems.forEach(item -> {
						item.setBusinessNo(deductEntity.getBusinessNo());
					});
				}
			}
			AgreementMergeData.mergeMatchResList(invoiceMatchResMap, plusMatchResList);
			// 继续匹配下一张协议单，需要将本次匹配的明细金额放入上下文，以防止同一明细被重复匹配
			BlueInvoiceMatchHolder.put(invoiceMatchResMap);
		}
		// 合并最终返回列表
		return Lists.newArrayList(invoiceMatchResMap.values());
	}

	/**
	 * <pre>
	 * 手工合并结算单 主流程步骤： 
	 * 1.校验入参,初始化合并信息 
	 * 2.如用户指定了蓝票明细匹配,则校验匹配金额 
	 * 3.组合合并信息 
	 * 4.执行合并结算单
	 * </pre>
	 * @param request
	 * @return
	 */
	public TXfSettlementEntity makeSettlementByManual(MakeSettlementRequest request) {
		// 1.校验入参,初始化合并信息
		if (CollectionUtils.isEmpty(request.getBillIds())) {
			throw new EnhanceRuntimeException("请至少选择一张业务单据");
		}
		// 校验协议单状态
		QueryWrapper<TXfBillDeductEntity> deductEntityQ = new QueryWrapper<>();
		deductEntityQ.in(TXfBillDeductEntity.ID, request.getBillIds());
		deductEntityQ.eq(TXfBillDeductEntity.STATUS, TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
		deductEntityQ.orderByAsc(TXfBillDeductEntity.BUSINESS_NO);
		List<TXfBillDeductEntity> notOverduePlusDeductList = tXfBillDeductDao.selectList(deductEntityQ);
		if (CollectionUtils.isEmpty(notOverduePlusDeductList) || notOverduePlusDeductList.size() < request.getBillIds().size()) {
			throw new EnhanceRuntimeException("您选择的单据不存在，或已被使用/锁定，请刷新重试");
		}

		if (CollectionUtils.isEmpty(request.getInvoiceIds()) && CollectionUtils.isEmpty(request.getDetailDataList())) {
			throw new EnhanceRuntimeException("请至少选择一张待匹配发票或明细");
		}
		if (CollectionUtils.isNotEmpty(request.getInvoiceIds()) && CollectionUtils.isNotEmpty(request.getDetailDataList())) {
			throw new EnhanceRuntimeException("按发票主信息与按发票明细匹配不可同时支持");
		}
		if (StringUtils.isBlank(request.getPurchaserNo())) {
			throw new EnhanceRuntimeException("购方编号不可为空");
		}
		if (request.getTaxRate() == null) {
			throw new EnhanceRuntimeException("合并税率必传");
		}
		// 2.组合合并信息
		AgreementMergeData mergeData = new AgreementMergeData(request.getPurchaserNo(), request.getSellerNo(), request.getTaxRate());
		mergeData.setSettlementNo(CodeGenerator.generateCode(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL));
		// 2.1 设置指定发票或明细
		mergeData.setInvoiceIdList(request.getInvoiceIds());
		if (CollectionUtils.isNotEmpty(request.getDetailDataList())) {
			// 保留原始用户提交明细数据
			List<DeductInvoiceDetailData> originInvoiceDetailList = new ArrayList<>();
			BeanUtil.copyList(request.getDetailDataList(), originInvoiceDetailList, DeductInvoiceDetailData.class);
			mergeData.setOriginInvoiceDetailList(originInvoiceDetailList);
			// 保存用于匹配的明细列表
			List<DeductInvoiceDetailData> matchInvoiceDetailList = new ArrayList<>();
			for (DeductInvoiceDetailData detailData : request.getDetailDataList()) {
				DeductInvoiceDetailData matchInvoiceDetail = new DeductInvoiceDetailData();
				BeanUtil.copyProperties(detailData, matchInvoiceDetail);
				matchInvoiceDetail.setLeftDetailAmount(matchInvoiceDetail.getMatchedDetailAmount());
				matchInvoiceDetail.setLeftNum(matchInvoiceDetail.getMatchedNum());
				matchInvoiceDetailList.add(matchInvoiceDetail);
			}
			mergeData.setMatchInvoiceDetailList(matchInvoiceDetailList);
		}
		// 2.2 合并正数单据
		makeMergeDataByManual(mergeData, request.getBillIds());
		mergeData.setTargetTaxRate(switchToTargetTaxRate(request.getTaxRate(), true, mergeData.getTaxCode()));
		// 2.3 如用户指定了蓝票明细匹配,则校验匹配金额
		if (CollectionUtils.isNotEmpty(request.getDetailDataList())) {
			checkDetailByManual(request, mergeData.getTargetTaxRate());
		}

		// 3.执行合并结算单
		TXfSettlementEntity settlementEntity;
		try {
			// 获取对应负数协议业务单列表,如存在则合并，并验证合并后金额必须为正数
			makeMergeDataOrgAndNegative(mergeData);
			mergeData.exchangeAmount();
			// 保存结算单
			settlementEntity = makeSettlement(mergeData);
		} catch (Exception e) {
			log.error("协议单手工合并异常 sellerNo:{} purcharseNo:{} taxRate:{} ", mergeData.getSellerNo(),
					mergeData.getPurchaserNo(), mergeData.getTaxRate(), e);
			// 回滚发票占用
			if (CollectionUtils.isNotEmpty(mergeData.getAllMatchResList())) {
				List<Long> deductIdList = mergeData.getMergeDeductList().stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());
				deductBlueInvoiceService.withdrawBlueInvoiceByDeduct(deductIdList);
			}
			throw new EnhanceRuntimeException("合并结算单异常：" + e.getMessage());
		}
		return settlementEntity;
	}

  /**
   * 校验手工提交的待匹配明细
   * @param request
   */
  private void checkDetailByManual(MakeSettlementRequest request,BigDecimal targetTaxRate){
      //1 获取明细待匹配总金额(并校验入参)
      BigDecimal totalMatchedDetailAmount = new BigDecimal("0.00");
      BigDecimal totalMatchedTaxAmount = new BigDecimal("0.00");
      Set<Long> oilInvoiceIdSet = new HashSet<>();
      for (DeductInvoiceDetailData detailData : request.getDetailDataList()){
        if (detailData.getInvoiceId() == null){
          throw new EnhanceRuntimeException("发票ID不可为空");
        }
        if (detailData.getInvoiceDetailId() == null){
          throw new EnhanceRuntimeException("发票明细ID不可为空");
        }
        if (detailData.getMatchedDetailAmount() == null){
          throw new EnhanceRuntimeException("明细匹配不含税金额不可为空["+detailData.getInvoiceDetailId()+"]");
        }
        if (detailData.getMatchedTaxAmount() == null){
          throw new EnhanceRuntimeException("明细匹配税额不可为空["+detailData.getInvoiceDetailId()+"]");
        }
        //获取发票主信息
        TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectById(detailData.getInvoiceId());
        if (invoiceEntity == null){
          throw new EnhanceRuntimeException("发票头信息获取失败["+detailData.getInvoiceId()+"]");
        }
/*        if (invoiceEntity.getTaxRate().compareTo(request.getTaxRate().movePointRight(2))!= 0){
          throw new EnhanceRuntimeException("提交税率与目标税率不一致 提交税率["
                  +request.getTaxRate().toPlainString()+"] 目标税率["
                  +invoiceEntity.getTaxRate().movePointLeft(2).toPlainString()+"]");
        }*/

        //校验税额公式
        if (detailData.getMatchedTaxAmount().compareTo(detailData.getMatchedDetailAmount()
                .multiply(targetTaxRate).setScale(2,RoundingMode.HALF_UP)) != 0){
          throw new EnhanceRuntimeException("提交匹配税额计算有误["+detailData.getInvoiceDetailId()+"]");
        }

        //判断是否成品油,一次匹配最多选择一张成品油发票
        if (Integer.valueOf(1).equals(invoiceEntity.getIsOil())) {
          oilInvoiceIdSet.add(invoiceEntity.getId());
          if (oilInvoiceIdSet.size()>1){
            throw new EnhanceRuntimeException("成品油发票不可超过一张");
          }
        }
        //获取发票明细信息
        TDxRecordInvoiceDetailEntity invoiceDetailEntity = tDxRecordInvoiceDetailDao.selectById(detailData.getInvoiceDetailId());
        if (invoiceDetailEntity == null){
          throw new EnhanceRuntimeException("发票明细信息获取失败["+detailData.getInvoiceDetailId()+"]");
        }
        //获取推荐信息
        MatchedInvoiceDetailBean matchedBean =deductBlueInvoiceService.gainMatchedInvoiceDetail(invoiceDetailEntity,invoiceEntity);
        if (matchedBean == null){
          throw new EnhanceRuntimeException("发票明细不符合推荐要求["+detailData.getInvoiceDetailId()+"]");
        }
        detailData.setLeftDetailAmount(new BigDecimal(matchedBean.getLeftDetailAmount()));
        //判断金额是否超限
        if (detailData.getMatchedDetailAmount().compareTo(detailData.getLeftDetailAmount())>0){
          throw new EnhanceRuntimeException("发票明细剩余金额不足["+detailData.getInvoiceDetailId()+"]");
        }
        //校验单据数量，如果原始明细存在单价数量，则校验必输
        if (StringUtils.isNotBlank(matchedBean.getNum()) && StringUtils.isNotBlank(matchedBean.getUnitPrice())){
          //校验单价
          if (detailData.getMatchedUnitPrice() == null){
            throw new EnhanceRuntimeException("发票明细匹配单价不可为空["+detailData.getInvoiceDetailId()+"]");
          }
          if (detailData.getMatchedUnitPrice().compareTo(new BigDecimal(matchedBean.getUnitPrice()))>0){
            throw new EnhanceRuntimeException("发票明细匹配单价不可超过原始单价["+detailData.getInvoiceDetailId()+"]");
          }
          //校验数量
          if (detailData.getMatchedNum() == null){
            throw new EnhanceRuntimeException("发票明细匹配数量不可为空["+detailData.getInvoiceDetailId()+"]");
          }
          detailData.setLeftNum(new BigDecimal(matchedBean.getLeftNum()));
          if (detailData.getMatchedNum().compareTo(detailData.getLeftNum())>0){
            throw new EnhanceRuntimeException("发票明细剩余数量不足["+detailData.getInvoiceDetailId()+"]");
          }
/*          if (BigDecimalUtil.isInteger(detailData.getLeftNum())
                  && !BigDecimalUtil.isInteger(detailData.getMatchedNum())){
            throw new EnhanceRuntimeException("发票明细匹配数量须为正数["+detailData.getInvoiceDetailId()+"]");
          }*/
          //校验乘法公式
          if (detailData.getMatchedDetailAmount().compareTo(detailData.getMatchedUnitPrice()
                  .multiply(detailData.getMatchedNum()).setScale(2,RoundingMode.HALF_UP)) != 0){
            throw new EnhanceRuntimeException("发票明细匹配单价乘数量不等于不含税["+detailData.getInvoiceDetailId()+"]");
          }
        }

        totalMatchedDetailAmount = totalMatchedDetailAmount.add(detailData.getMatchedDetailAmount());
        totalMatchedTaxAmount = totalMatchedTaxAmount.add(detailData.getMatchedTaxAmount());
      }
      //2 获取待匹配总金额
      MatchedInvoiceDetailListResponse matchedResponse = preSettlementNewByManual(request);
      if (matchedResponse == null || CollectionUtils.isEmpty(matchedResponse.getDetailList())){
        throw new EnhanceRuntimeException("获取待匹配总金额失败");
      }

      BigDecimal totalMatchAmount = new BigDecimal("0.00");
      BigDecimal totalMatchTaxAmount = new BigDecimal("0.00");
      for (MatchedInvoiceDetailBean matchedInvoiceDetail : matchedResponse.getDetailList()){
        if (StringUtils.isNotBlank(matchedInvoiceDetail.getMatchedDetailAmount())) {
          totalMatchAmount = totalMatchAmount.add(new BigDecimal(matchedInvoiceDetail.getMatchedDetailAmount()));
        }
        if (StringUtils.isNotBlank(matchedInvoiceDetail.getMatchedTaxAmount())) {
          totalMatchTaxAmount = totalMatchTaxAmount.add(new BigDecimal(matchedInvoiceDetail.getMatchedTaxAmount()));
        }

      }
      //3比较金额是否相等
      if (totalMatchAmount.compareTo(totalMatchedDetailAmount) != 0){
        log.error("提交的明细匹配金额与单据总金额不相等 totalMatchAmount：{} totalMatchedDetailAmount：{}"
                ,totalMatchAmount.toPlainString(),totalMatchedDetailAmount.toPlainString());
        throw new EnhanceRuntimeException("提交的明细匹配金额与单据总金额不相等");
      }
/*      if (totalMatchTaxAmount.compareTo(totalMatchedTaxAmount) != 0){
        log.error("提交的明细匹配税额与单据总税额不相等 totalMatchTaxAmount：{} totalMatchedTaxAmount：{}"
                ,totalMatchTaxAmount.toPlainString(),totalMatchedTaxAmount.toPlainString());
        throw new EnhanceRuntimeException("提交的明细匹配税额与单据总税额不相等");
      }*/
    }

    /**
     * 检查mergeData
     * @param mergeData
     */
    private void checkMerge(AgreementMergeData mergeData){
        //校验合并后金额必须为正数,否则不处理返回
        if (BigDecimal.ZERO.compareTo(mergeData.getAmountWithoutTax())>=0){
            log.warn("协议业务单合并金额不为正数 mergeData:{}", mergeData);
            SysLogUtil.sendLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,SysLogUtil.getTraceInfo()
                    ,mergeData.getSettlementNo(),"N","协议业务单合并金额不为正数");
            //记录履历
            if(CollectionUtils.isNotEmpty(mergeData.getMergeDeductList())){
                mergeData.getMergeDeductList().forEach(entity -> operateLogService.addDeductLog(entity.getId(),
                        entity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(entity.getStatus()),
                        entity.getRefSettlementNo(), OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED,
                        "合并金额不为正数", 0L, "系统"));
            }
            throw new EnhanceRuntimeException("生成结算单的单据总额必须大于0，请返回重新选择正数单据");
        }
        //校验合并税额差异不能超过5分钱
        if (MAX_DIFF_TAX_AMOUNT.compareTo(mergeData.getDiffTaxAmount().abs())<0) {
            log.warn("协议业务单合并税额差异超限 mergeData:{} diffTaxAmount:{}"
                    , mergeData,mergeData.getDiffTaxAmount().toPlainString());
            SysLogUtil.sendLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,SysLogUtil.getTraceInfo()
                    ,mergeData.getSettlementNo(),"N","协议业务单合并税额差异超限");
            //记录履历
            if(CollectionUtils.isNotEmpty(mergeData.getMergeDeductList())){
                mergeData.getMergeDeductList().forEach(entity -> operateLogService.addDeductLog(entity.getId(),
                        entity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(entity.getStatus()),
                        entity.getRefSettlementNo(), OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED,
                        "合并税额差异超限", 0L, "系统"));
            }
            throw new EnhanceRuntimeException("协议业务单合并税额差异超限");
        }
    }

    /**
     * 拆分合并信息，满足合并税额差异不超过5分钱
     * @return
     */
    public List<AgreementMergeData> splitMergeForTaxAmountDiff(AgreementMergeData mergeData){
        List<AgreementMergeData> mergeDataList = new ArrayList<>();
        //1.判断税额差异是否超限
        if (MAX_DIFF_TAX_AMOUNT.compareTo(mergeData.getDiffTaxAmount().abs())>=0){
            log.warn("协议单合并后税额差异未超过限额：{}",MAX_DIFF_TAX_AMOUNT.toPlainString());
            mergeDataList.add(mergeData);
            return mergeDataList;
        }
        //2.判断税额差异情况,获取正数差异待处理列表
        LinkedList<TXfBillDeductEntity> plusDiffDeductList =  mergeData.getPlusOverDiffDeductList();//默认正向差异
        if (BigDecimal.ZERO.compareTo(mergeData.getDiffTaxAmount()) > 0){
            //负向差异
            plusDiffDeductList =  mergeData.getPlusLowerDiffDeductList();
        }
        //3.剔除超限的差异单据
        int diffNum = mergeData.getDiffTaxAmount().abs().subtract(MAX_DIFF_TAX_AMOUNT).movePointRight(2)
                .setScale(0,RoundingMode.DOWN).intValue();
        if (plusDiffDeductList.size()<diffNum){
            log.warn("正数差异协议单个数不足,请检查负数差异是否过大 diffNum：{} plusDiffDeductList.size：{}"
                    ,diffNum,plusDiffDeductList.size());
            throw new EnhanceRuntimeException("正数差异协议单个数不足,请检查负数差异是否过大！");
        }
        List<TXfBillDeductEntity> movedPlusDiffDeductList = plusDiffDeductList.subList(0,diffNum);
        TXfBillDeductEntity plusDeduct;
        for (TXfBillDeductEntity movedPlusDiffDeduct : movedPlusDiffDeductList){
            for (int i =0;i<mergeData.getPlusDeductList().size();i++){
                plusDeduct = mergeData.getPlusDeductList().get(i);
                if (plusDeduct.getId().equals(movedPlusDiffDeduct.getId())){
                    //剔除该协议单，并计算合并金额
                    mergeData.getPlusDeductList().remove(i--);
                    mergeData.addAmountWithoutTax(plusDeduct.getAmountWithoutTax().negate());
                    mergeData.addTaxAmount(plusDeduct.getTaxAmount().negate());
                    mergeData.addAmountWithTax(plusDeduct.getAmountWithoutTax()
                            .add(plusDeduct.getTaxAmount().negate()));
                }
            }
        }
        //4.判断合并金额是否大于0,否则合并失败
        if (BigDecimal.ZERO.compareTo(mergeData.getAmountWithoutTax())>=0){
            log.warn("因最大税额容差超限，剔除正数差异协议单后，合并不含税小于等于0");
            throw new EnhanceRuntimeException("剔除正数差异协议单后，合并不含税小于等于0");
        }
        //5.重新计算差异税额
        mergeData.calDiffInfo();
        mergeDataList.add(mergeData);
        //6.按最大限额对剔除的协议单列表重新组合，生成合并信息
        TXfBillDeductEntity movedPlusDiffDeduct = null;
        AgreementMergeData mergeDataTemp = null;
        int maxDiffNum = MAX_DIFF_TAX_AMOUNT.movePointRight(2).setScale(0,RoundingMode.DOWN).intValue();
        int indexNum = 0;
        for (int i = 0;i< movedPlusDiffDeductList.size();i++){
            movedPlusDiffDeduct = movedPlusDiffDeductList.get(i);
            if (++indexNum == 1){
                //创建新的合并对象
                mergeDataTemp = new AgreementMergeData();
                mergeDataTemp.setSettlementNo(CodeGenerator.generateCode(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL));
                mergeDataTemp.setPurchaserNo(mergeData.getPurchaserNo());
                mergeDataTemp.setPurchaserOrg(mergeData.getPurchaserOrg());
                mergeDataTemp.setSellerNo(mergeData.getSellerNo());
                mergeDataTemp.setSellerOrg(mergeData.getSellerOrg());
                mergeDataTemp.setTaxRate(mergeData.getTaxRate());
                mergeDataTemp.setTargetTaxRate(mergeData.getTargetTaxRate());
            }
            //添加正数协议单并计算合并税额
            mergeDataTemp.addPlusDeduct(movedPlusDiffDeduct);
            mergeDataTemp.addAmountWithoutTax(movedPlusDiffDeduct.getAmountWithoutTax());
            mergeDataTemp.addTaxAmount(movedPlusDiffDeduct.getTaxAmount());
            mergeDataTemp.addAmountWithTax(movedPlusDiffDeduct.getAmountWithoutTax()
                    .add(movedPlusDiffDeduct.getTaxAmount()));
            // 达到差额上线或最后一条，则加入返回列表
            if (indexNum == maxDiffNum || i == movedPlusDiffDeductList.size()-1 ){
                mergeDataTemp.calDiffInfo();
                mergeDataList.add(mergeDataTemp);
                //初始化,进入下一轮
                indexNum = 0;
                mergeDataTemp = null;
            }
        }

        return mergeDataList;
    }

    /**
     * 合并正数单据信息（手工调用）
     * @param mergeData
     * @param billIdList
     */
    private void makeMergeDataByManual(AgreementMergeData mergeData,List<Long> billIdList){
        //获取用户提交的正数未超期协议单列表，并进行合并
        QueryWrapper<TXfBillDeductEntity> deductEntityQ = new QueryWrapper<>();
        deductEntityQ.in(TXfBillDeductEntity.ID,billIdList);
        deductEntityQ.eq(TXfBillDeductEntity.STATUS,TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
        deductEntityQ.orderByAsc(TXfBillDeductEntity.BUSINESS_NO);
        List<TXfBillDeductEntity> notOverduePlusDeductList = tXfBillDeductDao.selectList(deductEntityQ);
        if (CollectionUtils.isEmpty(notOverduePlusDeductList) || notOverduePlusDeductList.size()<billIdList.size()) {
            throw new EnhanceRuntimeException("您选择的单据不存在，或已被使用/锁定，请刷新重试");
        }
        Set<String>  taxCodeSet = new HashSet<>();//协议单税码集合
        for (TXfBillDeductEntity notOverduePlusDeduct : notOverduePlusDeductList){
            if (notOverduePlusDeduct.getTaxRate().compareTo(mergeData.getTaxRate())!=0){
                throw new EnhanceRuntimeException("单据税率与参数不一致");
            }
            if (!mergeData.getPurchaserNo().equals(notOverduePlusDeduct.getPurchaserNo())){
                throw new EnhanceRuntimeException("单据扣款公司代码与参数不一致");
            }
            if (BigDecimal.ZERO.compareTo(notOverduePlusDeduct.getAmountWithoutTax())>=0){
                throw new EnhanceRuntimeException("单据不含税金额不可为负数");
            }
            if (!TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode().equals(notOverduePlusDeduct.getStatus())){
                throw new EnhanceRuntimeException("单据状态须为:"+TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getDesc());
            }
            if (!TXfDeductStatusEnum.UNLOCK.getCode().equals(notOverduePlusDeduct.getLockFlag())){
                throw new EnhanceRuntimeException("单据已被锁定");
            }
            //执行合并
            mergeData.addAmountWithoutTax(notOverduePlusDeduct.getAmountWithoutTax());
            mergeData.addAmountWithTax(notOverduePlusDeduct.getAmountWithTax());
            mergeData.addTaxAmount(notOverduePlusDeduct.getTaxAmount());
            mergeData.addPlusDeduct(notOverduePlusDeduct);
            //添加税码
            taxCodeSet.add(notOverduePlusDeduct.getAgreementTaxCode());
        }

        //校验税码  13%和9%税率的协议单不可多个税码同时选择
        String agreementTaxNo = null;
        if ((new BigDecimal("0.13").equals(mergeData.getTaxRate().setScale(2, RoundingMode.DOWN)))
                || new BigDecimal("0.09").equals(mergeData.getTaxRate().setScale(2, RoundingMode.DOWN)) && !taxCodeSet.isEmpty()){
          if (taxCodeSet.size()>1){
            log.error("提交的协议单ID列表包含不同税码:{}",taxCodeSet);
            throw new EnhanceRuntimeException("提交的协议单ID列表包含不同税码["+taxCodeSet+"]");
          }
          mergeData.setTaxCode((String)taxCodeSet.toArray()[0]);
          agreementTaxNo = mergeData.getTaxCode();
        }

        //根据合并信息获取正数已过期单据，并执行合并
        List<TXfBillDeductEntity> overDueDeductList = deductViewService.getOverDueNegativeBills(mergeData.getPurchaserNo()
                , mergeData.getSellerNo(), TXfDeductionBusinessTypeEnum.AGREEMENT_BILL, mergeData.getTaxRate(),agreementTaxNo);
        if (CollectionUtils.isNotEmpty(overDueDeductList)) {
            for (TXfBillDeductEntity overDueDeduct : overDueDeductList) {
                if (BigDecimal.ZERO.compareTo(overDueDeduct.getAmountWithoutTax())<0){
                    if (billIdList.contains(overDueDeduct.getId())){
                      log.error("提交的协议单ID列表包含超期ID:{}",overDueDeduct.getId());
                      throw new EnhanceRuntimeException("提交的协议单ID列表中不可包含已超期协议单["+overDueDeduct.getId()+"]");
                    }
                    //执行合并(正数已过期)
                    mergeData.addAmountWithoutTax(overDueDeduct.getAmountWithoutTax());
                    mergeData.addAmountWithTax(overDueDeduct.getAmountWithTax());
                    mergeData.addTaxAmount(overDueDeduct.getTaxAmount());
                    mergeData.addPlusDeduct(overDueDeduct);
                }
            }
        }
    }

    /**
     * 获取合并信息中的购销组织信息及负数单据合并
     */
    public void makeMergeDataOrgAndNegative(AgreementMergeData mergeData){
        //校验购销对
        if (StringUtils.isBlank(mergeData.getSellerNo()) || StringUtils.isBlank(mergeData.getPurchaserNo())){
            log.info("购销对编码不可为空 跳过协议业务单合并 sellerNo:{} purchaserNo:{}"
                    ,mergeData.getSellerNo(),mergeData.getPurchaserNo());
            throw new EnhanceRuntimeException("购销对编码不可为空");
        }
        TAcOrgEntity purchaserOrg = queryOrgInfo(mergeData.getPurchaserNo(),false);
        TAcOrgEntity sellerOrg = queryOrgInfo(mergeData.getSellerNo(), true);
        if (Objects.isNull(purchaserOrg) || Objects.isNull(sellerOrg)) {
            log.info("购销方信息获取失败 sellerNo:{} purchaserNo:{} "
                    ,mergeData.getSellerNo(),mergeData.getPurchaserNo());
            throw new EnhanceRuntimeException("购销方信息获取失败");
        }
        mergeData.setSellerOrg(sellerOrg);
        mergeData.setPurchaserOrg(purchaserOrg);

        String agreementTaxCode = null;
        if ((new BigDecimal("0.13").equals(mergeData.getTaxRate().setScale(2, RoundingMode.DOWN))
                || new BigDecimal("0.09").equals(mergeData.getTaxRate().setScale(2, RoundingMode.DOWN)))){
          agreementTaxCode = mergeData.getTaxCode();
        }
        //获取对应负数协议业务单列表
        List<TXfBillDeductEntity> minusDeductList = tXfBillDeductExtDao.querySpecialNegativeBillList(
                mergeData.getPurchaserNo(), mergeData.getSellerNo(), mergeData.getTaxRate(),agreementTaxCode
                ,TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue()
                , TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode()
                , TXfDeductStatusEnum.UNLOCK.getCode());

        if (CollectionUtils.isNotEmpty(minusDeductList)){
            //存在负数明细,执行合并
            mergeData.setPlusMinusFlag(1);
            for (TXfBillDeductEntity minusDeduct : minusDeductList){
                mergeData.addAmountWithoutTax(minusDeduct.getAmountWithoutTax());
                mergeData.addAmountWithTax(minusDeduct.getAmountWithTax());
                mergeData.addTaxAmount(minusDeduct.getTaxAmount());
                mergeData.addMinusDeduct(minusDeduct);
            }
        }
        //计算税额差异
        mergeData.calDiffInfo();
        log.info("执行协议业务单自动合并  mergeData:{}", mergeData);
    }

    /**
     * <pre>
     * 合并结算单
     * 主流程步骤：
     * 1.获取对应负数协议业务单列表,如存在则合并，并验证合并后金额必须为正数
     * 2.如不存在负数，则遍历每一个正数协议业务单，匹配蓝票明细，生成与蓝票关系及与蓝票明细关系；
     *   如存在负数，则对合并后正数金额匹配蓝票明细，将蓝票明细匹配到所有正负协议单上（无占用金额，标记正负混合，
     *   即该种情况无法精确映射每一张协议单和发票及红字信息表之间的关系，只能全部映射，供用户人工识别）
     * 3.生成结算单主信息及明细信息
     * 4.生成业务单和结算单关系信息
     * 5.调用统一保存及更新事务逻辑
     * </pre>
     */
    public TXfSettlementEntity makeSettlement(AgreementMergeData mergeData){
        //1.校验合并信息
        checkMerge(mergeData);
        /**
         * 2. 如不存在负数，则遍历每一个正数协议业务单，匹配蓝票明细，生成与蓝票关系及与蓝票明细关系；
         *    如存在负数，则对合并后正数金额匹配蓝票明细，将蓝票明细匹配到所有正负协议单上（无占用金额，标记正负混合，
         *    即该种情况无法精确映射每一张协议单和发票及红字信息表之间的关系，只能全部映射，供用户人工识别）
         */
        List<TXfBillDeductEntity> mergeDeductList = mergeData.getMergeDeductList();
        Map<Long,List<TXfBillDeductInvoiceEntity>> deductInvoiceMap = new HashMap<>();//key-业务单ID
        Map<Long,List<TXfBillDeductInvoiceDetailEntity>> deductInvoiceDetailMap = new HashMap<>();//key-业务单ID
        if (mergeData.getPlusMinusFlag() == 0) {
			// 2.1 不存在负数 遍历每一个正数协议业务单，匹配蓝票明细，生成与蓝票关系及与蓝票明细关系
			for (TXfBillDeductEntity deductEntity : mergeDeductList) {
				// 2.1.1 获取协议单匹配蓝票明细
				List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList = makeDeductInvoiceDetail(mergeData, deductEntity);
				deductInvoiceDetailMap.put(deductEntity.getId(), deductInvoiceDetailList);
				// 2.1.2 获取协议单匹配蓝票主信息
				List<TXfBillDeductInvoiceEntity> deductInvoiceList = makeDeductInvoice(deductEntity, deductInvoiceDetailList);
				deductInvoiceMap.put(deductEntity.getId(), deductInvoiceList);
				// 2.1.3 提交占用关系事务
				try {
					List<Supplier<Boolean>> successSuppliers = new ArrayList<>();// 待提交事务列表
					successSuppliers.add(() -> makeDeductInvoiceCommit(deductInvoiceList, deductInvoiceDetailList));
					transactionalService.execute(successSuppliers);
				} catch (Exception e) {
					log.error("协议单发票关系占用保存事务异常！", e);
					throw new EnhanceRuntimeException("协议单发票关系占用保存事务异常");
				}
			}
        }else {
            //2.2  存在负数，则对合并后正数金额匹配蓝票明细，将蓝票明细匹配到所有正负协议单上
            //2.2.1 对合并后正数金额匹配蓝票明细
            List<TXfBillDeductInvoiceDetailEntity> mergeInvoiceDetailList = makeDeductInvoiceDetail(mergeData);
            //2.2.2 对合并后正数金额匹配蓝票主信息
            List<TXfBillDeductInvoiceEntity> mergeInvoiceList = makeDeductInvoice(mergeInvoiceDetailList);
            //2.2.3 将蓝票明细匹配到所有正负协议单上
            List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList;
            TXfBillDeductInvoiceDetailEntity deductInvoiceDetail;
            List<TXfBillDeductInvoiceEntity> deductInvoiceList;
            TXfBillDeductInvoiceEntity deductInvoice;
            boolean isLastDeduct = false;//是否最后一条协议单，最后一条通过轧差算占用不含税
            Map<Long,BigDecimal> itemUsedAmountMap = new HashMap<>();
            int mergeDeductIndex = 0;
            for (TXfBillDeductEntity mergeDeduct : mergeDeductList) {
				if (++mergeDeductIndex == mergeDeductList.size()) {
					isLastDeduct = true;
				}
                //添加系统日志
                SysLogUtil.sendLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,
                        SysLogUtil.getTraceInfo(),
                        String.valueOf(mergeDeduct.getId()),
                        "Y",
                        "正负混合协议单生成蓝票明细占用关系 amountWithoutTax:"+mergeDeduct.getAmountWithoutTax().toPlainString());
                deductInvoiceDetailList = new ArrayList<>();
                deductInvoiceList = new ArrayList<>();

				TXfBillDeductInvoiceDetailEntity mergeInvoiceDetail;
				for (int i = 0; i < mergeInvoiceDetailList.size(); i++) {
					mergeInvoiceDetail = mergeInvoiceDetailList.get(i);
					deductInvoiceDetail = new TXfBillDeductInvoiceDetailEntity();
					BeanUtil.copyProperties(mergeInvoiceDetail, deductInvoiceDetail);
					deductInvoiceDetail.setId(idSequence.nextId());
					deductInvoiceDetail.setDeductId(mergeDeduct.getId());
					deductInvoiceDetail.setBusinessNo(mergeDeduct.getBusinessNo());
					deductInvoiceDetail.setBusinessType(mergeDeduct.getBusinessType());
					deductInvoiceDetail.setPlusMinusFlag(mergeData.getPlusMinusFlag());
					// 计算占用金额及数量
					if (isLastDeduct) {
						// 最后一张协议单通过轧差算占用金额
						BigDecimal usedAmount = itemUsedAmountMap.get(deductInvoiceDetail.getInvoiceDetailId());
						if (usedAmount == null) {
							throw new EnhanceRuntimeException("轧差需用的占用金额为NULL");
						}
						deductInvoiceDetail.setUseAmountWithoutTax(mergeInvoiceDetail.getUseAmountWithoutTax().subtract(usedAmount));
					} else {
						// 通过占比算占用金额
						deductInvoiceDetail.setUseAmountWithoutTax(mergeDeduct.getAmountWithoutTax()
								.divide(mergeData.getAmountWithoutTax(), 15, RoundingMode.HALF_UP)
								.multiply(mergeInvoiceDetail.getUseAmountWithoutTax())
								.setScale(2, RoundingMode.HALF_UP));
						BigDecimal usedAmount = itemUsedAmountMap.get(deductInvoiceDetail.getInvoiceDetailId());
						if (usedAmount == null) {
							itemUsedAmountMap.put(deductInvoiceDetail.getInvoiceDetailId(), deductInvoiceDetail.getUseAmountWithoutTax());
						} else {
							itemUsedAmountMap.put(deductInvoiceDetail.getInvoiceDetailId(), usedAmount.add(deductInvoiceDetail.getUseAmountWithoutTax()));
						}
					}

					// 如果实际占用不含税为0，则跳过该条明细，不生成关系记录
					if (BigDecimal.ZERO.compareTo(deductInvoiceDetail.getUseAmountWithoutTax()) == 0) {
						// 增加系统日志
						SysLogUtil.sendInfoLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT, "正负混合实际占用明细不含税为0 invoiceDetailId:" + mergeInvoiceDetail.getInvoiceDetailId());
						continue;
					}
					deductInvoiceDetail.setUseTaxAmount(mergeDeduct.getTaxAmount().divide(mergeData.getTaxAmount(), 15, RoundingMode.HALF_UP).multiply(mergeInvoiceDetail.getUseTaxAmount()).setScale(2, RoundingMode.HALF_UP));
					deductInvoiceDetail.setUseAmountWithTax(deductInvoiceDetail.getUseAmountWithoutTax().add(deductInvoiceDetail.getUseTaxAmount()));
					deductInvoiceDetail.setUseQuantity(mergeDeduct.getAmountWithoutTax().divide(mergeData.getAmountWithoutTax(), 15, RoundingMode.HALF_UP).multiply(mergeInvoiceDetail.getUseQuantity()).setScale(15, RoundingMode.HALF_UP));
					deductInvoiceDetail.setUseUnitPrice(mergeInvoiceDetail.getUseUnitPrice());
					deductInvoiceDetail.setStatus(0);// 0-正常 1-撤销
					deductInvoiceDetail.setCreateTime(new Date());
					deductInvoiceDetail.setUpdateTime(new Date());
					deductInvoiceDetailList.add(deductInvoiceDetail);
				}
                for (TXfBillDeductInvoiceEntity mergeInvoice : mergeInvoiceList){
                    deductInvoice = new TXfBillDeductInvoiceEntity();
//                    deductInvoice.setId(idSequence.nextId());
                    deductInvoice.setBusinessNo(mergeDeduct.getBusinessNo());
                    deductInvoice.setBusinessType(mergeDeduct.getBusinessType());
                    deductInvoice.setInvoiceCode(mergeInvoice.getInvoiceCode());
                    deductInvoice.setInvoiceNo(mergeInvoice.getInvoiceNo());
                    deductInvoice.setUseAmount(mergeInvoice.getUseAmount());
                    deductInvoice.setStatus(0);//0-正常 1-撤销
                    deductInvoice.setThridId(mergeDeduct.getId());
                    deductInvoice.setCreateTime(new Date());
                    deductInvoice.setUpdateTime(new Date());
                    deductInvoiceList.add(deductInvoice);
                }
                deductInvoiceDetailMap.put(mergeDeduct.getId(), deductInvoiceDetailList);
                deductInvoiceMap.put(mergeDeduct.getId(), deductInvoiceList);
                //2.1.3 提交占用关系事务
                try {
                    List<Supplier<Boolean>> successSuppliers = new ArrayList<>();//待提交事务列表
                    //保存业务单和发票的关系
                    successSuppliers.add(() ->makeDeductInvoiceCommit(deductInvoiceMap.get(mergeDeduct.getId()), deductInvoiceDetailMap.get(mergeDeduct.getId())));
                    transactionalService.execute(successSuppliers);
                }catch (Exception e){
                    log.error("协议单发票关系占用保存事务异常！",e);
                    throw new EnhanceRuntimeException("协议单发票关系占用保存事务异常");
                }
            }
        }

        // 3.生成结算单主信息及明细信息
		TXfSettlementEntity settlementEntity = makeSettlementMain(mergeData);
		List<TXfSettlementItemInvoiceDetailEntity> itemInvoiceDetailEntityList = new ArrayList<>();
		List<TXfSettlementItemEntity> settlementItemEntityList = makeSettlementItemAndInvoiceDetail(mergeData, settlementEntity, deductInvoiceDetailMap, itemInvoiceDetailEntityList);

		// 3.1 校验结算单税额和协议单税额差异
		BigDecimal targetTaxRate = mergeData.getTargetTaxRate();
		log.info("协议单目标税率：{}", targetTaxRate.toPlainString());
		BigDecimal targetTaxAmount = mergeData.getTaxAmount();
		log.info("协议单目标税额：{}", targetTaxAmount.toPlainString());
		BigDecimal diffAmount = targetTaxAmount.abs().subtract(settlementEntity.getTaxAmount().abs()).abs();
		log.info("协议单和结算单税额差异：{}", diffAmount.toPlainString());
		if (MAX_DIFF_TAX_AMOUNT.compareTo(diffAmount) < 0) {
			throw new EnhanceRuntimeException("协议单和结算单税额差异超限 最大差异[" + MAX_DIFF_TAX_AMOUNT.toPlainString() + "]  当前差异[" + diffAmount.toPlainString() + "]");
		}


        //4.生成业务单和结算单关系信息
        List<TXfBillSettlementEntity> billSettlementList = makeBillSettlementList(mergeDeductList,settlementEntity);

		// 5.调用统一保存及更新事务逻辑
		try {
			List<Supplier<Boolean>> successSuppliers = new ArrayList<>();// 待提交事务列表
			successSuppliers.add(() -> makeSettlementCommit(mergeData, deductInvoiceMap, deductInvoiceDetailMap,
					settlementEntity, billSettlementList, settlementItemEntityList, itemInvoiceDetailEntityList));
			transactionalService.execute(successSuppliers);
		} catch (Exception e) {
			log.error("协议单统一保存事务异常！", e);
			throw new EnhanceRuntimeException("协议单统一保存事务异常");
		}
        // 添加日志
        mergeData.getMergeDeductList().forEach(entity -> operateLogService.addDeductLog(entity.getId(), entity.getBusinessType(),
                TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT, settlementEntity.getSettlementNo(),
                OperateLogEnum.AGREEMENT_MERGE_SETTLEMENT, settlementEntity.getSettlementNo(), UserUtil.getUserId(), UserUtil.getUserName()));
        return settlementEntity;
    }

    /**
     * 协议单匹配蓝票明细(仅正数)
     */
	private List<TXfBillDeductInvoiceDetailEntity> makeDeductInvoiceDetail(AgreementMergeData mergeData, TXfBillDeductEntity deductEntity) {
		// 根据正数协议单匹配蓝票明细
		BigDecimal taxRate = mergeData.getTargetTaxRate().movePointRight(2);

		List<BlueInvoiceService.MatchRes> matchResList = null;
		if (CollectionUtils.isNotEmpty(mergeData.getInvoiceIdList())) {
			log.info("根据指定蓝票ID列表匹配");
			matchResList = deductBlueInvoiceService.matchBlueInvoiceByIds(deductEntity.getAmountWithoutTax(), mergeData.getInvoiceIdList(), true, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
		} else if (CollectionUtils.isNotEmpty(mergeData.getMatchInvoiceDetailList())) {
			log.info("根据指定蓝票明细列表匹配");
			// 根据正数协议单获取待匹配的发票明细占用列表
			matchResList = deductBlueInvoiceService.matchBlueInvoiceByDetail(deductEntity.getAmountWithoutTax(), taxRate, mergeData.getMatchInvoiceDetailList(), true, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
		} else {
			log.info("根据系统默认规则匹配");
			matchResList = deductBlueInvoiceService.matchBlueInvoice(deductEntity.getBusinessNo(), mergeData.getSellerOrg().getTaxNo(), mergeData.getPurchaserOrg().getTaxNo(), taxRate, deductEntity.getAmountWithoutTax(), mergeData.getNotQueryOil(), true, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
		}

		if (CollectionUtils.isEmpty(matchResList)) {
			log.info("协议单(仅正数)未匹配到任何蓝票 deductId:{}", deductEntity.getId());
			operateLogService.addDeductLog(deductEntity.getId(), deductEntity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(deductEntity.getStatus()), deductEntity.getRefSettlementNo(), OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED, "未匹配到任何蓝票", 0L, "系统");
			throw new NoSuchInvoiceException("协议单未匹配到任何蓝票");
		}

		List<String> invoiceNos = matchResList.stream().map(BlueInvoiceService.MatchRes::getInvoiceNo).collect(Collectors.toList());
		// 匹配成功后打印日志
		operateLogService.addDeductLog(deductEntity.getId(), deductEntity.getBusinessType(),
				TXfDeductStatusEnum.getEnumByCode(deductEntity.getStatus()), deductEntity.getRefSettlementNo(),
				OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED, "匹配到蓝票:" + invoiceNos.toString(), 0L, "系统");

		mergeData.addMatchResList(String.valueOf(deductEntity.getId()), matchResList);
		// 生成协议单发票明细关系记录
		List<TXfBillDeductInvoiceDetailEntity> detailEntityList = new ArrayList<>();
		TXfBillDeductInvoiceDetailEntity detailEntity;
		for (BlueInvoiceService.MatchRes matchRes : matchResList) {
			if (CollectionUtils.isEmpty(matchRes.getInvoiceItems())) {
				log.warn("协议单(仅正数)未匹配到任何蓝票明细 deductId:{} invoiceCode:{} invoiceNo:{}", deductEntity.getId(), matchRes.getInvoiceCode(), matchRes.getInvoiceNo());
				operateLogService.addDeductLog(deductEntity.getId(), deductEntity.getBusinessType(),
						TXfDeductStatusEnum.getEnumByCode(deductEntity.getStatus()), deductEntity.getRefSettlementNo(),
						OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED, "未匹配到任何蓝票明细", 0L, "系统");
				throw new NoSuchInvoiceException("协议单未匹配到任何蓝票明细");
			}
			if (Integer.valueOf(1).equals(matchRes.getIsOil())) {
				// 已获取成品油发票，则本批次合并不可再查询成品油发票
				mergeData.setNotQueryOil(true);
			}
			for (BlueInvoiceService.InvoiceItem invoiceItem : matchRes.getInvoiceItems()) {
				detailEntity = new TXfBillDeductInvoiceDetailEntity();
				detailEntity.setId(idSequence.nextId());
				detailEntity.setDeductId(deductEntity.getId());
				detailEntity.setInvoiceDetailId(invoiceItem.getItemId());
				//2023-08-25新增WALMART-3538 begin
				invoiceItem.setBusinessNo(deductEntity.getBusinessNo());
				//2023-08-25新增WALMART-3538 end
				detailEntity.setBusinessNo(deductEntity.getBusinessNo());
				detailEntity.setBusinessType(deductEntity.getBusinessType());
				detailEntity.setInvoiceId(matchRes.getInvoiceId());
				detailEntity.setInvoiceCode(matchRes.getInvoiceCode());
				detailEntity.setInvoiceNo(matchRes.getInvoiceNo());
				detailEntity.setPlusMinusFlag(mergeData.getPlusMinusFlag());
				if (StringUtils.isNotBlank(invoiceItem.getTaxRate())) {
					detailEntity.setTaxRate(new BigDecimal(invoiceItem.getTaxRate()).movePointLeft(2));
				}
				detailEntity.setUseAmountWithoutTax(invoiceItem.getMatchedDetailAmount());
				if (invoiceItem.getMatchedDetailAmount() != null && invoiceItem.getMatchedTaxAmount() != null) {
					detailEntity.setUseAmountWithTax(
							invoiceItem.getMatchedDetailAmount().add(invoiceItem.getMatchedTaxAmount()));
				}
				detailEntity.setUseTaxAmount(invoiceItem.getMatchedTaxAmount());
				detailEntity.setUseQuantity(invoiceItem.getMatchedNum());
				detailEntity.setUseUnitPrice(invoiceItem.getMatchedUnitPrice());
				detailEntity.setIsOil(matchRes.getIsOil());
				detailEntity.setStatus(0);// 0正常 1撤销
				detailEntity.setCreateTime(new Date());
				detailEntity.setUpdateTime(new Date());
				detailEntityList.add(detailEntity);
			}
		}
		return detailEntityList;
	}
	
    /**
     * 协议单匹配蓝票明细(正负混合)
     */
    private List<TXfBillDeductInvoiceDetailEntity> makeDeductInvoiceDetail(AgreementMergeData mergeData){
        //根据正负混合合并金额匹配蓝票明细
        BigDecimal taxRate = mergeData.getTargetTaxRate().movePointRight(2);
        List<BlueInvoiceService.MatchRes> matchResList = null;
        if (CollectionUtils.isNotEmpty(mergeData.getInvoiceIdList())){
            log.info("根据指定蓝票ID列表匹配");
            matchResList = deductBlueInvoiceService.matchBlueInvoiceByIds(mergeData.getAmountWithoutTax(), mergeData.getInvoiceIdList(),true,TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        }else if (CollectionUtils.isNotEmpty(mergeData.getMatchInvoiceDetailList())){
            log.info("根据指定蓝票明细列表匹配");
            matchResList = deductBlueInvoiceService.matchBlueInvoiceByDetail(mergeData.getAmountWithoutTax(),taxRate ,mergeData.getMatchInvoiceDetailList(),true,TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        } else {
            log.info("根据系统默认规则匹配");
            matchResList = deductBlueInvoiceService.matchBlueInvoice(mergeData.getSettlementNo(), mergeData.getSellerOrg().getTaxNo(), mergeData.getPurchaserOrg().getTaxNo(), taxRate, mergeData.getAmountWithoutTax(), mergeData.getNotQueryOil(), true, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        }
        if (CollectionUtils.isEmpty(matchResList)){
            log.info("协议单(正负混合)未匹配到任何蓝票 settlementNo:{}",mergeData.getSettlementNo());
            throw new NoSuchInvoiceException("协议单(正负混合)未匹配到任何蓝票");
        }
		mergeData.addMatchResList(mergeData.getSettlementNo(), matchResList);
        //生成协议单发票明细关系记录
		List<TXfBillDeductInvoiceDetailEntity> detailEntityList = new ArrayList<>();
		TXfBillDeductInvoiceDetailEntity detailEntity;
		for (BlueInvoiceService.MatchRes matchRes : matchResList) {
			if (CollectionUtils.isEmpty(matchRes.getInvoiceItems())) {
				log.warn("协议单(正负混合)未匹配到任何蓝票明细 settlementNo:{} invoiceCode:{} invoiceNo:{}", mergeData.getSettlementNo(), matchRes.getInvoiceCode(), matchRes.getInvoiceNo());
				throw new NoSuchInvoiceException("协议单(正负混合)未匹配到任何蓝票明细");
            }
			if (Integer.valueOf(1).equals(matchRes.getIsOil())) {
				// 已获取成品油发票，则本批次合并不可再查询成品油发票
				mergeData.setNotQueryOil(true);
			}
			for (BlueInvoiceService.InvoiceItem invoiceItem : matchRes.getInvoiceItems()) {
				detailEntity = new TXfBillDeductInvoiceDetailEntity();
				detailEntity.setInvoiceDetailId(invoiceItem.getItemId());
				detailEntity.setBusinessNo(invoiceItem.getBusinessNo());
				detailEntity.setInvoiceId(matchRes.getInvoiceId());
				detailEntity.setInvoiceCode(matchRes.getInvoiceCode());
				detailEntity.setInvoiceNo(matchRes.getInvoiceNo());
				if (StringUtils.isNotBlank(invoiceItem.getTaxRate())) {
					detailEntity.setTaxRate(new BigDecimal(invoiceItem.getTaxRate()).movePointLeft(2));
				}
				detailEntity.setUseAmountWithoutTax(invoiceItem.getMatchedDetailAmount());
				if (invoiceItem.getMatchedDetailAmount() != null && invoiceItem.getMatchedTaxAmount() != null) {
					detailEntity.setUseAmountWithTax(invoiceItem.getMatchedDetailAmount().add(invoiceItem.getMatchedTaxAmount()));
				}
				detailEntity.setUseTaxAmount(invoiceItem.getMatchedTaxAmount());
				detailEntity.setUseQuantity(invoiceItem.getMatchedNum());
				detailEntity.setUseUnitPrice(invoiceItem.getMatchedUnitPrice());
				detailEntity.setIsOil(matchRes.getIsOil());
				detailEntityList.add(detailEntity);
			}
        }
        return detailEntityList;
    }



    /**
     * 生成结算单主信息
     */
    private TXfSettlementEntity makeSettlementMain(AgreementMergeData mergeData){
        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        tXfSettlementEntity.setId(idSequence.nextId());
        tXfSettlementEntity.setSettlementNo(mergeData.getSettlementNo());
        tXfSettlementEntity.setSellerNo(mergeData.getSellerNo());
        tXfSettlementEntity.setSellerTaxNo(mergeData.getSellerOrg().getTaxNo());
        tXfSettlementEntity.setSellerAddress(defaultValue(mergeData.getSellerOrg().getAddress()));
        tXfSettlementEntity.setSellerBankAccount(mergeData.getSellerOrg().getAccount());
        tXfSettlementEntity.setSellerBankName(mergeData.getSellerOrg().getBank());
        tXfSettlementEntity.setSellerName(defaultValue(mergeData.getSellerOrg().getOrgName()));
        tXfSettlementEntity.setSellerTel(defaultValue(mergeData.getSellerOrg().getPhone()));
        tXfSettlementEntity.setPurchaserNo(mergeData.getPurchaserNo());
        tXfSettlementEntity.setPurchaserTaxNo(mergeData.getPurchaserOrg().getTaxNo());
        tXfSettlementEntity.setPurchaserAddress(mergeData.getPurchaserOrg().getAddress());
        tXfSettlementEntity.setPurchaserBankAccount(mergeData.getPurchaserOrg().getAccount());
        tXfSettlementEntity.setPurchaserBankName(mergeData.getPurchaserOrg().getBank());
        tXfSettlementEntity.setPurchaserName(defaultValue(mergeData.getPurchaserOrg().getOrgName()));
        tXfSettlementEntity.setPurchaserTel(defaultValue(mergeData.getPurchaserOrg().getPhone()));
        tXfSettlementEntity.setAvailableAmount(tXfSettlementEntity.getAmountWithoutTax());
        tXfSettlementEntity.setTaxRate(mergeData.getTargetTaxRate());
        tXfSettlementEntity.setBatchNo(StringUtils.EMPTY);
        tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CONFIRM.getCode());
        tXfSettlementEntity.setSettlementType(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
        tXfSettlementEntity.setBusinessType(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
        tXfSettlementEntity.setPriceMethod(0);
        if (mergeData.getTaxRate().compareTo(BigDecimal.ZERO) == 0) {
            tXfSettlementEntity.setInvoiceType(InvoiceTypeEnum.GENERAL_INVOICE.getValue());
        }else {
            tXfSettlementEntity.setInvoiceType(InvoiceTypeEnum.SPECIAL_INVOICE.getValue());
        }
        //初始化主信息金额信息，具体值会通过结算单明细累加计算
        tXfSettlementEntity.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfSettlementEntity.setAmountWithTax(new BigDecimal("0.00"));
        tXfSettlementEntity.setTaxAmount(new BigDecimal("0.00"));

        tXfSettlementEntity.setRemark(StringUtils.EMPTY);
        tXfSettlementEntity.setCreateUser(0L);
        tXfSettlementEntity.setCreateTime(DateUtils.getNow());
        tXfSettlementEntity.setUpdateUser(0L);
        tXfSettlementEntity.setUpdateTime(tXfSettlementEntity.getCreateTime());

        return tXfSettlementEntity;
    }

    /**
     * 生成结算单明细
     */
	private List<TXfSettlementItemEntity> makeSettlementItemAndInvoiceDetail(AgreementMergeData mergeData,
			TXfSettlementEntity settlementEntity,
			Map<Long, List<TXfBillDeductInvoiceDetailEntity>> deductInvoiceDetailMap,
			List<TXfSettlementItemInvoiceDetailEntity> itemInvoiceDetailList) {
		List<TXfSettlementItemEntity> settlementItemList = new ArrayList<>();
		if (Integer.valueOf(1).equals(mergeData.getPlusMinusFlag())) {
			// 正负混合
			List<BlueInvoiceService.MatchRes> matchResList = mergeData.getMatchResList(mergeData.getSettlementNo());
			for (BlueInvoiceService.MatchRes matchRes : matchResList) {
				settlementItemList.addAll(makeSettlementItemAndInvoiceDetail(mergeData, matchRes, settlementEntity, itemInvoiceDetailList, deductInvoiceDetailMap, null));
			}
		} else {
			// 仅正数
			if (CollectionUtils.isNotEmpty(mergeData.getMatchInvoiceDetailList())) {
				// 用户手工指定匹配明细，按指定明细生成结算单明细
				List<BlueInvoiceService.MatchRes> matchResList = mergeData.getMergeMatchResList();
				for (BlueInvoiceService.MatchRes matchRes : matchResList) {
					// 生成结算单明细
					settlementItemList.addAll(makeSettlementItemAndInvoiceDetail(mergeData, matchRes, settlementEntity, itemInvoiceDetailList, deductInvoiceDetailMap, null));
				}
			} else {
				// 定时任务智能匹配，协议单蓝票明细按一比一方式生成结算单明细
				for (Long deductId : deductInvoiceDetailMap.keySet()) {
					List<BlueInvoiceService.MatchRes> matchResList = mergeData.getMatchResList(String.valueOf(deductId));
					for (BlueInvoiceService.MatchRes matchRes : matchResList) {
						settlementItemList.addAll(makeSettlementItemAndInvoiceDetail(mergeData, matchRes, settlementEntity, itemInvoiceDetailList, deductInvoiceDetailMap, deductId));
					}
				}
			}
		}
		//排序，解决结算单明细和业务单匹配时顺序不一致
		if(settlementItemList != null && settlementItemList.size() > 0) {
			settlementItemList = settlementItemList.stream().sorted(Comparator.comparing(TXfSettlementItemEntity::getBusinessNo)).collect(Collectors.toList());
		}
		return settlementItemList;
	}

    /**
     * 生成结算单明细
     */
    private List<TXfSettlementItemEntity> makeSettlementItemAndInvoiceDetail(AgreementMergeData mergeData
            ,BlueInvoiceService.MatchRes matchRes
            ,TXfSettlementEntity settlementEntity
            ,List<TXfSettlementItemInvoiceDetailEntity> itemInvoiceDetailList
            ,Map<Long,List<TXfBillDeductInvoiceDetailEntity>> deductInvoiceDetailMap
            ,Long deductId){

        List<TXfSettlementItemEntity> settlementItemList = new ArrayList<>();
        TXfSettlementItemEntity settlementItem;
        TXfSettlementItemInvoiceDetailEntity itemInvoiceDetail;
        Map<String, TaxCodeBean> taxCodeMap = queryTaxCode(matchRes.getInvoiceItems());
        for(BlueInvoiceService.InvoiceItem invoiceItem : matchRes.getInvoiceItems()){
			log.info("生成结算单明细前数据:{}", JSON.toJSON(invoiceItem));
            settlementItem = new TXfSettlementItemEntity();
            settlementItem.setId(idSequence.nextId());
            settlementItem.setBusinessNo(invoiceItem.getBusinessNo());
            settlementItem.setThridId(defaultValue(invoiceItem.getItemId()));
            settlementItem.setSettlementNo(mergeData.getSettlementNo());
            settlementItem.setIsOil(Optional.ofNullable(matchRes.getIsOil()).orElse(0));
            settlementItem.setGoodsTaxNo(invoiceItem.getGoodsNum());
            settlementItem.setGoodsNoVer("33.0");
            if (StringUtils.isNotBlank(invoiceItem.getTaxRate())) {
                settlementItem.setTaxRate(mergeData.getTargetTaxRate());
            }
            if (invoiceItem.getMatchedTaxAmount() != null) {
                settlementItem.setTaxAmount(defaultValue(invoiceItem.getMatchedTaxAmount()).negate());
                settlementEntity.setTaxAmount(settlementEntity.getTaxAmount().add(settlementItem.getTaxAmount()));
            }
            if (invoiceItem.getMatchedDetailAmount() != null) {
                settlementItem.setAmountWithoutTax(defaultValue(invoiceItem.getMatchedDetailAmount()).negate());
                settlementEntity.setAmountWithoutTax(settlementEntity.getAmountWithoutTax().add(settlementItem.getAmountWithoutTax()));
            }
            if (settlementItem.getAmountWithoutTax() != null && settlementItem.getTaxAmount() !=null) {
                settlementItem.setAmountWithTax(settlementItem.getAmountWithoutTax().add(settlementItem.getTaxAmount()));
                settlementEntity.setAmountWithTax(settlementEntity.getAmountWithTax().add(settlementItem.getAmountWithTax()));
            }

            settlementItem.setRemark(StringUtils.EMPTY);
            if (invoiceItem.getMatchedNum() != null) {
                settlementItem.setQuantity(defaultValue(invoiceItem.getMatchedNum()).negate());
            }
            settlementItem.setQuantityUnit(defaultValue(invoiceItem.getUnit()));
            if (invoiceItem.getMatchedUnitPrice() != null) {
                settlementItem.setUnitPrice(defaultValue(invoiceItem.getMatchedUnitPrice()));
            }
            settlementItem.setItemCode(defaultValue(invoiceItem.getGoodsNum()));
            settlementItem.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
            settlementItem.setItemName(defaultValue(invoiceItem.getGoodsName()));
            settlementItem.setZeroTax(StringUtils.EMPTY);
            settlementItem.setTaxPre(StringUtils.EMPTY);
            settlementItem.setTaxPreCon(StringUtils.EMPTY);
            settlementItem.setItemSpec(defaultValue(invoiceItem.getModel()));
            if (invoiceItem.getMatchedUnitPrice()!=null && settlementItem.getTaxRate()!= null) {
                settlementItem.setUnitPriceWithTax(defaultValue(invoiceItem.getMatchedUnitPrice()).multiply(BigDecimal.ONE.add(settlementItem.getTaxRate())).setScale(15, RoundingMode.HALF_UP));
            }
            settlementItem.setCreateUser(0L);
            settlementItem.setCreateTime(new Date());
            settlementItem.setUpdateUser(0L);
            settlementItem.setUpdateTime(new Date());
            fixTaxCode(settlementItem, taxCodeMap);
            checkItem(settlementItem);
            //如果明细为待匹配税编，则结算单状态也置为待匹配税编
            if (TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode().equals(settlementItem.getItemFlag())){
                settlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode());
            }
            log.info("生成结算单明细后数据:{}",JSON.toJSON(settlementItem));
            settlementItemList.add(settlementItem);

            //生成结算单明细与发票明细关系记录
            itemInvoiceDetail = new TXfSettlementItemInvoiceDetailEntity();
            itemInvoiceDetail.setId(idSequence.nextId());
            itemInvoiceDetail.setSettlementItemId(settlementItem.getId());
            itemInvoiceDetail.setInvoiceDetailId(invoiceItem.getItemId());
            itemInvoiceDetail.setSettlementId(settlementEntity.getId());
            itemInvoiceDetail.setSettlementNo(settlementEntity.getSettlementNo());
            itemInvoiceDetail.setSettlementType(settlementEntity.getSettlementType());
            itemInvoiceDetail.setInvoiceId(matchRes.getInvoiceId());
            itemInvoiceDetail.setInvoiceCode(invoiceItem.getInvoiceCode());
            itemInvoiceDetail.setInvoiceNo(matchRes.getInvoiceNo());
            itemInvoiceDetail.setPlusMinusFlag(mergeData.getPlusMinusFlag());
            itemInvoiceDetail.setTaxRate(settlementItem.getTaxRate());
            itemInvoiceDetail.setUseAmountWithoutTax(invoiceItem.getMatchedDetailAmount());
            if (invoiceItem.getMatchedDetailAmount() != null && invoiceItem.getMatchedTaxAmount() != null) {
                itemInvoiceDetail.setUseAmountWithTax(invoiceItem.getMatchedDetailAmount().add(invoiceItem.getMatchedTaxAmount()));
            }
            itemInvoiceDetail.setUseTaxAmount(invoiceItem.getMatchedTaxAmount());
            itemInvoiceDetail.setUseQuantity(invoiceItem.getMatchedNum());
            itemInvoiceDetail.setIsOil(matchRes.getIsOil());
            itemInvoiceDetail.setStatus(0);
            itemInvoiceDetail.setCreateTime(new Date());
            itemInvoiceDetail.setUpdateTime(new Date());
            itemInvoiceDetailList.add(itemInvoiceDetail);

            log.info("setSettlementItemId:{}, itemInvoiceDetail:{},invoiceItem:{}", JSON.toJSONString(deductInvoiceDetailMap), JSON.toJSONString(itemInvoiceDetail), JSON.toJSONString(invoiceItem));
            //回写协议单发票明细关系表的settlement_item_id字段
            if (Integer.valueOf(1).equals(mergeData.getPlusMinusFlag())){
                //存在正负混合
                for (Long tempDeductId : deductInvoiceDetailMap.keySet()){
                    for (TXfBillDeductInvoiceDetailEntity deductInvoiceDetail : deductInvoiceDetailMap.get(tempDeductId)){
                        if (itemInvoiceDetail.getInvoiceDetailId().equals(deductInvoiceDetail.getInvoiceDetailId()) && StringUtils.equalsAnyIgnoreCase(deductInvoiceDetail.getBusinessNo(), invoiceItem.getBusinessNo())){
                            deductInvoiceDetail.setSettlementItemId(settlementItem.getId());
                        }
                    }
                }
            }else {
                //仅存在正数
                if (deductId == null){
                  for (Long tempDeductId : deductInvoiceDetailMap.keySet()){
                    for (TXfBillDeductInvoiceDetailEntity deductInvoiceDetail : deductInvoiceDetailMap.get(tempDeductId)){
                      if (itemInvoiceDetail.getInvoiceDetailId().equals(deductInvoiceDetail.getInvoiceDetailId()) && StringUtils.equalsAnyIgnoreCase(deductInvoiceDetail.getBusinessNo(), invoiceItem.getBusinessNo())){
                        deductInvoiceDetail.setSettlementItemId(settlementItem.getId());
                      }
                    }
                  }
                }else {
                  for (TXfBillDeductInvoiceDetailEntity deductInvoiceDetail : deductInvoiceDetailMap.get(deductId)) {
                    if (itemInvoiceDetail.getInvoiceDetailId().equals(deductInvoiceDetail.getInvoiceDetailId()) && StringUtils.equalsAnyIgnoreCase(deductInvoiceDetail.getBusinessNo(), invoiceItem.getBusinessNo())) {
                      deductInvoiceDetail.setSettlementItemId(settlementItem.getId());
                    }
                  }
                }
            }
        }
        return settlementItemList;
    }

    private List<TXfBillSettlementEntity> makeBillSettlementList(List<TXfBillDeductEntity> mergeDeductList
            ,TXfSettlementEntity settlementEntity){
        List<TXfBillSettlementEntity> billSettlementList = new ArrayList<>();
        TXfBillSettlementEntity billSettlement;
        for (TXfBillDeductEntity mergeDeduct : mergeDeductList){
            billSettlement = new TXfBillSettlementEntity();
            billSettlement.setId(idSequence.nextId());
            billSettlement.setBillId(mergeDeduct.getId());
            billSettlement.setBusinessNo(mergeDeduct.getBusinessNo());
            billSettlement.setBusinessType(mergeDeduct.getBusinessType());
            billSettlement.setBiilStatus(mergeDeduct.getStatus());
            billSettlement.setBiilAmountWithoutTax(mergeDeduct.getAmountWithoutTax());
            billSettlement.setBiilTaxAmount(mergeDeduct.getTaxAmount());
            billSettlement.setSettlementNo(settlementEntity.getSettlementNo());
            billSettlement.setSettlmentStatus(settlementEntity.getSettlementStatus());
            billSettlement.setSellerNo(settlementEntity.getSellerNo());
            billSettlement.setSellerName(settlementEntity.getSellerName());
            billSettlement.setPurchaserNo(settlementEntity.getPurchaserNo());
            billSettlement.setPurchaserName(settlementEntity.getPurchaserName());
            billSettlement.setSettlmentAmountWithoutTax(settlementEntity.getAmountWithoutTax());
            billSettlement.setSettlmentTaxAmount(settlementEntity.getTaxAmount());
            billSettlement.setStatus(0);
            billSettlement.setCreateTime(new Date());
            billSettlement.setUpdateTime(new Date());
            billSettlementList.add(billSettlement);
        }
        return billSettlementList;
    }
    /**
     * 提交协议单发票占用关系
     */
    private boolean makeDeductInvoiceCommit(List<TXfBillDeductInvoiceEntity> deductInvoiceList
            ,List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList){
        //1.保存业务单发票关系
        for (TXfBillDeductInvoiceEntity deductInvoice : deductInvoiceList){
            tXfBillDeductInvoiceDao.insert(deductInvoice);
        }
        //2.保存业务单发票明细关系
        deductInvoiceDetailService.saveBatch(deductInvoiceDetailList);
        return true;
    }

    /**
     * 提交合并结算单
     */
    private boolean makeSettlementCommit(AgreementMergeData mergeData
            ,Map<Long,List<TXfBillDeductInvoiceEntity>> deductInvoiceMap
            ,Map<Long,List<TXfBillDeductInvoiceDetailEntity>> deductInvoiceDetailMap
            ,TXfSettlementEntity settlementEntity
            ,List<TXfBillSettlementEntity> billSettlementList
            ,List<TXfSettlementItemEntity> settlementItemEntityList
            ,List<TXfSettlementItemInvoiceDetailEntity> itemInvoiceDetailEntityList){
        //1.更新业务单状态
        List<Long> deductIdList = new ArrayList<>();
        for (TXfBillDeductEntity deductEntity : mergeData.getMergeDeductList()){
            deductIdList.add(deductEntity.getId());
        }
        QueryWrapper<TXfBillDeductEntity> deductEntityQ = new QueryWrapper<>();
        deductEntityQ.in(TXfBillDeductEntity.ID,deductIdList);
        TXfBillDeductEntity deductEntityU = new TXfBillDeductEntity();
        deductEntityU.setStatus(TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode());
        deductEntityU.setRefSettlementNo(settlementEntity.getSettlementNo());
        deductEntityU.setUpdateTime(new Date());
        tXfBillDeductDao.update(deductEntityU,deductEntityQ);
        /*
        //2.保存业务单发票关系
        for (Long deductId : deductInvoiceMap.keySet()){
            for (TXfBillDeductInvoiceEntity deductInvoice : deductInvoiceMap.get(deductId)){
                tXfBillDeductInvoiceDao.insert(deductInvoice);
            }
        }
        //3.保存业务单发票明细关系
        for (Long deductId : deductInvoiceDetailMap.keySet()){
            deductInvoiceDetailService.saveBatch(deductInvoiceDetailMap.get(deductId));
        }
        */
        //2.回填结算单明细ID
        for (Long deductId : deductInvoiceDetailMap.keySet()){
            deductInvoiceDetailService.updateBatchById(deductInvoiceDetailMap.get(deductId));
        }
        //4.保存结算单
        tXfSettlementDao.insert(settlementEntity);
        //5.保存结算单明细
        settlmentItemBatchService.saveBatch(settlementItemEntityList);
        //6.保存业务单结算单关系
        billSettlementService.saveBatch(billSettlementList);
        //7.保存结算单明细与发票明细关系
        settlmentItemInvoiceDetailService.saveBatch(itemInvoiceDetailEntityList);
        //8.保存操作日志
        operateLogService.add(settlementEntity.getId(),OperateLogEnum.CREATE_SETTLEMENT,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlementEntity.getSettlementStatus()).getDesc(), "", 0L, "系统");

        return true;
    }



    /**
     *
     * @param deductionEnum
     * @param tXfBillDeductStatusEnum
     * @param targetStatus
     * @return
     */
    /*public boolean mergeEPDandAgreementSettlement(TXfDeductionBusinessTypeEnum deductionEnum, TXfDeductStatusEnum tXfBillDeductStatusEnum, TXfDeductStatusEnum targetStatus ) {
        Map<String, BigDecimal> nosuchInvoiceSeller = new HashMap<>();
        *//**
         * 1获取超期时间 判断超过此日期的正数单据
         *//*
        Integer referenceDate =  defaultSettingService.getOverdueDay(deductionEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL ? DefaultSettingEnum.AGREEMENT_OVERDUE_DEFAULT_DAY : DefaultSettingEnum.EPD_OVERDUE_DEFAULT_DAY);
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitablePositiveBill(referenceDate, deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode(), TXfDeductStatusEnum.UNLOCK.getCode());
        if (CollectionUtils.isEmpty(tXfBillDeductEntities)) {
            log.info("未找到符合条件的单据，跳过合并单据");
            return false;
        }
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
        	log.info("mergeEPDandAgreementSettlement one TXfBillDeductEntity data:{}", JSON.toJSONString(tmp));
            *//**
             * 1查询 同一购销对，同一税率 下所有的负数单据
             *//*
            String sellerNo = tmp.getSellerNo();
//			if (!StringUtils.equals(sellerNo, "172164")) {
//				continue;
//			}
            String purchaserNo = tmp.getPurchaserNo();
            if (org.apache.commons.lang3.StringUtils.isEmpty(sellerNo) || org.apache.commons.lang3.StringUtils.isEmpty(purchaserNo)) {
                log.info("发现购销对信息不合法 跳过{}单据合并：sellerNo : {} purcharseNo : {}",deductionEnum.getDes(),sellerNo,purchaserNo);
                continue;
            }
            TXfBillDeductEntity negativeBill = tXfBillDeductExtDao.querySpecialNegativeBill(tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(), deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode(), TXfDeductStatusEnum.UNLOCK.getCode());
            BigDecimal mergeAmount =  tmp.getAmountWithoutTax();
            BigDecimal negativeBillAmount = BigDecimal.ZERO;
            if (Objects.nonNull(negativeBill)) {
                negativeBillAmount = negativeBill.getAmountWithoutTax();
                mergeAmount = negativeBillAmount.add(mergeAmount);
            }
            //当前结算单 金额 大于 剩余发票金额
            if (nosuchInvoiceSeller.containsKey(tmp.getSellerNo()) && nosuchInvoiceSeller.get(tmp.getSellerNo()).compareTo(mergeAmount) < 0) {
                log.info(" {} 单据匹配合并失败销方蓝票不足->sellerNo : {} purcharseNo : {}",deductionEnum.getDes(),sellerNo,purchaserNo);
                continue;
            }
            TAcOrgEntity purchaserOrgEntity = queryOrgInfo(purchaserNo,false);
            TAcOrgEntity sellerOrgEntity = queryOrgInfo(sellerNo, true);
            if (Objects.isNull(purchaserOrgEntity) || Objects.isNull(sellerOrgEntity)) {
                log.info(" 购销方信息不完整 sellerNo : {} sellerOrgEntity：{}  purcharseNo : {}  purchaserOrgEntity：{}", sellerNo,sellerOrgEntity,purchaserNo,purchaserOrgEntity);
                continue;
            }
            if (mergeAmount.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    Integer expireScale =  overdueService.oneOptBySellerNo(deductionEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL ? ServiceTypeEnum.AGREEMENT : ServiceTypeEnum.EPD, sellerNo);
					Date expireDate = DateUtils.addDate(DateUtils.getNow(), 0 - expireScale);
                    excuteMergeAndMatch(deductionEnum, tmp, negativeBill, tXfBillDeductStatusEnum, expireDate, targetStatus);
                } catch (NoSuchInvoiceException n ) {
                    log.info(" {} 单据匹配合并失败销方蓝票不足->sellerNo : {} purcharseNo : {}",deductionEnum.getDes(),sellerNo,purchaserNo);
                }
                catch (Exception e) {
                    log.error("{}单合并异常 销方:{}，购方:{}，税率:{} ", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(), e);
                }
            } else {
                log.warn("{}单合并失败：合并收金额不为负数 购方:{}，购方:{}，税率:{}   ", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate() );
            }
        }
        return false;
    }*/

    /**
     * 1、执行EPD和协议的合并和匹配
     */
	/*public void excuteMergeAndMatch(TXfDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tmp,
			TXfBillDeductEntity negativeBill, TXfDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfDeductStatusEnum targetStatus) {
		List<Supplier<Boolean>> successSuppliers = new ArrayList<>();
		successSuppliers.add(() -> {
			//正负结算单合并
			TXfSettlementEntity tXfSettlementEntity = executeMerge(deductionEnum, tmp, negativeBill,
					tXfBillDeductStatusEnum, referenceDate, targetStatus);
			//结算单匹配蓝票
			executeMatch(deductionEnum, tXfSettlementEntity, targetStatus.getCode(), null);
			// 2022-08-08 新增，
			List<TXfBillDeductEntity> billList = tXfBillDeductExtDao.queryBillBySettlementNoAndBusinessType(tXfSettlementEntity.getSettlementNo(), deductionEnum.getValue());
			log.info("excuteMergeAndMatch SettlementNo:{}, tXfBillDeductExtDao:{}", tXfSettlementEntity.getSettlementNo(), JSON.toJSONString(billList));
			if (billList != null && billList.size() > 0) {
				billList.forEach(item -> {
					billSettlementService.addBillSettlement(billSettlementService.bulidBillSettlementEntity(tXfSettlementEntity, item));
				});
			}
			return true;
		});
		transactionalService.execute(successSuppliers);
	}*/

  /**
   * 根据原税率转换目标税率
   * @param originTaxRate
   * @param isMovePointLeft
   * @param taxCode 税码
   * @return
   */
	  public static BigDecimal switchToTargetTaxRate(BigDecimal originTaxRate,Boolean isMovePointLeft,String taxCode){
      BigDecimal targetTaxRate = TaxRateTransferEnum.transferTaxRate(originTaxRate);
      targetTaxRate=convertTaxRate(targetTaxRate,taxCode);
      if (isMovePointLeft){
        targetTaxRate = targetTaxRate.movePointLeft(2);
      }
      log.info("执行税率转换 originTaxRate:{} targetTaxRate:{}",
              originTaxRate.toPlainString(),targetTaxRate.toPlainString());
      return targetTaxRate;
    }

    private static BigDecimal convertTaxRate(BigDecimal taxRate,String taxCode){
        //当协议单的税率是16%、17%这两种时，默认匹配13%税率的蓝票；
        if (taxRate.compareTo(TAX_RATE_17)==0 || taxRate.compareTo(TAX_RATE_16)==0){
            taxRate=TAX_RATE_13;
        }
        //当业务单税率为10%、11%这两种时，默认匹配9%税率的蓝票
        if (taxRate.compareTo(TAX_RATE_10)==0 || taxRate.compareTo(TAX_RATE_11)==0){
            taxRate=TAX_RATE_9;
        }
        //判断是否要13%转9%
        if ("T139".equals(taxCode) && taxRate.compareTo(TAX_RATE_13)==0){
          taxRate=TAX_RATE_9;
        }
        //判断是否要9%转13%
        if ("T913".equals(taxCode) && taxRate.compareTo(TAX_RATE_9)==0){
          taxRate=TAX_RATE_13;
        }

        return taxRate;
    }
    /**
     * 1、执行结算单匹配蓝票
     * @param deductionEnum
     * @param tXfSettlementEntity
     */
    /*public void executeMatch(TXfDeductionBusinessTypeEnum deductionEnum, TXfSettlementEntity tXfSettlementEntity, Integer targetStatus, List<BlueInvoiceService.MatchRes> matchResList) {
        //匹配蓝票
        String sellerTaxNo = tXfSettlementEntity.getSellerTaxNo();
        try {
            if (CollectionUtils.isEmpty(matchResList)) {
                BigDecimal taxRate = TaxRateTransferEnum.transferTaxRate(tXfSettlementEntity.getTaxRate());
                taxRate=convertTaxRate(deductionEnum,taxRate);
                matchResList = blueInvoiceService.matchInvoiceInfo(tXfSettlementEntity.getAmountWithoutTax(), deductionEnum, tXfSettlementEntity.getSettlementNo(),sellerTaxNo,tXfSettlementEntity.getPurchaserTaxNo(),taxRate );
            }
            if (CollectionUtils.isEmpty(matchResList)) {
//                NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
//                TXfBillDeductEntity tXfBillDeductEntity =  tXfBillDeductExtDao.queryOneBillBySettlementNo(tXfSettlementEntity.getSettlementNo());
//                newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
//                newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.NOT_MATCH_BLUE_INVOICE);
//                newExceptionReportEvent.setType(deductionEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL? ExceptionReportTypeEnum.AGREEMENT:ExceptionReportTypeEnum.EPD);
//                applicationContext.publishEvent(newExceptionReportEvent);
                log.error("{} 类型单据 销方:{}  蓝票不足，匹配失败 ", deductionEnum.getDes(), sellerTaxNo);
                throw new NoSuchInvoiceException();
            }
            //匹配税编
            Integer status = matchInfoTransfer(matchResList, tXfSettlementEntity.getSettlementNo(),tXfSettlementEntity.getId(),deductionEnum);
            averageDiffAmount(tXfSettlementEntity.getSettlementNo());
            if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode()){
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode());
            }else if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()){
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CONFIRM.getCode());
            } else if(status == TXfSettlementItemFlagEnum.NORMAL.getCode()){
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
            }
            //更新结算状态为- 1.存在锁定、取消的协议单、EPD进行->撤销-- 2.税编匹配失败 ->待确认税编 3,存在反算明细->待确认明细 4->结算单进入待拆票状态
            TXfBillDeductEntity checkEntity = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(), targetStatus, TXfDeductStatusEnum.UNLOCK.getCode());
			if (checkEntity.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) < 0) {
                log.error("{}单 存在锁定、取消的 sellerNo: {} purchaserNo: {} taxRate:{}", deductionEnum.getDes(),tXfSettlementEntity.getSellerNo(), tXfSettlementEntity.getPurchaserNo(),tXfSettlementEntity.getTaxRate() );
                throw new EnhanceRuntimeException("存在已锁定的业务单");
            }
            TXfSettlementEntity updadte = new TXfSettlementEntity();
            updadte.setId(tXfSettlementEntity.getId());
            updadte.setSettlementStatus(tXfSettlementEntity.getSettlementStatus());
            updadte.setUpdateTime(new Date());
            tXfSettlementDao.updateById(updadte);
        } catch (NoSuchInvoiceException n ) {
            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
            TXfBillDeductEntity tXfBillDeductEntity =  tXfBillDeductExtDao.queryOneBillBySettlementNo(tXfSettlementEntity.getSettlementNo());
            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
            newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.NOT_MATCH_BLUE_INVOICE );
            newExceptionReportEvent.setType( deductionEnum==TXfDeductionBusinessTypeEnum.AGREEMENT_BILL? ExceptionReportTypeEnum.AGREEMENT: ExceptionReportTypeEnum.EPD );
            applicationContext.publishEvent(newExceptionReportEvent);
            log.error(" {}单据匹配合并失败销方蓝票不足->sellerNo : {} purcharseNo : {} 金额 {} 税率 {}",deductionEnum.getDes(),tXfSettlementEntity.getSellerNo(),tXfSettlementEntity.getPurchaserNo(),tXfSettlementEntity.getAmountWithoutTax(),tXfSettlementEntity.getTaxRate());
            throw n;
        }
        catch (Exception e) {
//            if (CollectionUtils.isNotEmpty(matchResList)) {
//                List<String> invoiceList = matchResList.stream().map(x -> x.getInvoiceCode() + "=---" + x.getInvoiceNo()).collect(Collectors.toList());
//                log.error(" 结算匹配蓝票 回撤匹配信息 单  回撤匹配信息:{},{}", e,invoiceList );
//                blueInvoiceService.withdrawInvoices(matchResList);
//            }
            log.error("结算单匹配蓝票失败："+e.getMessage(), e);
            throw e;
        }
    }*/

    /**
     * 平摊因每个蓝票最后一条明细四舍五入导致的税额差值
     * @param settlementNo 结算单编号
     */
    /*public void averageDiffAmount(String settlementNo) {
        log.info("计算平摊金额，结算单号：{}", settlementNo);
        new LambdaQueryChainWrapper<>(statementService.getBaseMapper())
                .eq(TXfSettlementEntity::getSettlementNo, settlementNo)
                .oneOpt()
                .ifPresent(settlement -> {
                    log.info("计算平摊金额，结算单：{}", settlement);
                    List<TXfSettlementItemEntity> itemList = new LambdaQueryChainWrapper<>(settlementItemService.getBaseMapper())
                            .eq(TXfSettlementItemEntity::getSettlementNo, settlementNo)
                            .list();
                    log.info("计算平摊金额，结算单明细：{}", itemList);
                    BigDecimal itemTaxAmountSum = itemList.stream().map(TXfSettlementItemEntity::getTaxAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    log.info("计算平摊金额，结算单明细税额总和：{}", itemTaxAmountSum);
                    BigDecimal settlementTaxAmountAbs = settlement.getTaxAmount().abs();
                    BigDecimal itemTaxAmountSumAbs = itemTaxAmountSum.abs();
                    BigDecimal diffAmount = settlementTaxAmountAbs.subtract(itemTaxAmountSumAbs);
                    log.info("计算平摊金额，结算单与明细税额差：{}", diffAmount);
                    int compare = settlement.getTaxAmount().compareTo(itemTaxAmountSum);
                    if (diffAmount.compareTo(BigDecimal.ZERO) == 0 || compare == 0) {
                        log.info("计算平摊金额，结算单与明细税额差为0，不平摊。");
                        return;
                    }
                    if (compare > 0) {
                        log.info("计算平摊金额，结算单比明细多明细累加");
                        saveAverageAmount(BigDecimal::add, diffAmount.abs(), itemList);
                    } else {
                        log.info("计算平摊金额，结算单比明细多明细递减");
                        saveAverageAmount(BigDecimal::subtract, diffAmount.abs(), itemList);
                    }
                });
    }*/
/*
    public void saveAverageAmount(BiFunction<BigDecimal,BigDecimal,BigDecimal> func, BigDecimal diffAmount, List<TXfSettlementItemEntity> itemList) {
            BigDecimal averageAmount = new BigDecimal("0.01");
            for (TXfSettlementItemEntity entity : itemList) {
                log.info("计算平摊金额，结算单比明细差额保存明细：{}", entity);
                BigDecimal updateAmount;
                if (diffAmount.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
                if (diffAmount.compareTo(averageAmount) < 0) {
                    updateAmount = diffAmount;
                    diffAmount = BigDecimal.ZERO;
                } else {
                    updateAmount = func.apply(entity.getTaxAmount(), averageAmount);
                    //校验计算的税额 和实际是否相差超过1分钱
                    BigDecimal taxAmount= entity.getAmountWithoutTax().add(updateAmount).multiply(entity.getTaxRate()).divide(BigDecimal.ONE.add(entity.getTaxRate()),2,RoundingMode.HALF_UP );
                    if(taxAmount.abs().subtract(updateAmount.abs()).compareTo(averageAmount)>0){
                        continue;
                    }
                    diffAmount = diffAmount.subtract(averageAmount);
                }
                BigDecimal amountWithTax = entity.getAmountWithoutTax().add(updateAmount);
                new LambdaUpdateChainWrapper<>(settlementItemService.getBaseMapper())
                        .eq(TXfSettlementItemEntity::getId, entity.getId())
                        .set(TXfSettlementItemEntity::getTaxAmount, updateAmount)
                        .set(TXfSettlementItemEntity::getAmountWithTax, amountWithTax)
                        .update();
                log.info("计算平摊金额，结算单比明细差额保存明细ID：{}，新的税额：{}，新的不含税：{}", entity.getId(), updateAmount, amountWithTax);
            }
    }*/


    /**
     * 1、手动合并结算单
     * @param ids
     * @param xfDeductionBusinessTypeEnum
     * @return
     */
    /*public TXfSettlementEntity mergeSettlementByManual(List<Long> ids, TXfDeductionBusinessTypeEnum xfDeductionBusinessTypeEnum, List<BlueInvoiceService.MatchRes> matchResList) {
        if (CollectionUtils.isEmpty(ids)) {
            log.error("选择的{} 单据列表{}，查询符合条件结果为空", xfDeductionBusinessTypeEnum.getDes(), ids);
            throw new EnhanceRuntimeException("至少选择一张单据");
        }
        //去重
        ids = ids.stream().distinct().collect(Collectors.toList());

        String idsStr = StringUtils.join(ids, ",");
        idsStr = "(" + idsStr + ")";
        TXfDeductStatusEnum statusEnum;
        TXfDeductStatusEnum targetStatus;
        switch (xfDeductionBusinessTypeEnum) {
            case AGREEMENT_BILL:
                statusEnum = TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT;
                targetStatus = TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT;
                break;
            case EPD_BILL:
                statusEnum = TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT;
                targetStatus = TXfDeductStatusEnum.EPD_MATCH_SETTLEMENT;
                break;
            default:
                throw new EnhanceRuntimeException("手动合并结算单仅支持协议单和EPD");
        }

        List<TXfBillDeductEntity> entities = tXfBillDeductExtDao.querySuitableBillById(idsStr, xfDeductionBusinessTypeEnum.getValue(), statusEnum.getCode(), TXfDeductStatusEnum.UNLOCK.getCode());
        if (CollectionUtils.isEmpty(entities)) {
            log.error("选择的{} 单据列表{}，查询符合条件结果为空", xfDeductionBusinessTypeEnum.getDes(), ids);
            throw new EnhanceRuntimeException("未查询到待匹配结算单的单据");
        }
        if (entities.size() != 1) {
            log.error("选择的{} 单据列表{}，查询符合条件结果分组为{}", xfDeductionBusinessTypeEnum.getDes(), ids, entities.size());
            throw new EnhanceRuntimeException("您选择的单据为多税率或购销方不一致");
        }
        final TXfBillDeductEntity deductBill = entities.get(0);
        if (deductBill.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("选择的{} 单据列表{}，查询结果总金额为{}",xfDeductionBusinessTypeEnum.getDes(),ids,deductBill.getAmountWithoutTax());
            throw new EnhanceRuntimeException("选择单据的总金额不能小于0");
        }
        TXfSettlementEntity tXfSettlementEntity = trans2Settlement(Collections.singletonList(deductBill), xfDeductionBusinessTypeEnum);
        final int byIdResult = tXfBillDeductExtDao.updateBillById(idsStr, tXfSettlementEntity.getSettlementNo(), xfDeductionBusinessTypeEnum.getValue(), statusEnum.getCode(), TXfDeductStatusEnum.UNLOCK.getCode(), targetStatus.getCode());
        if (byIdResult != ids.size()){
            throw new EnhanceRuntimeException("您选择的单据状态发生了变更，请返回重新选择");
        }
        TXfBillDeductEntity  tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(),targetStatus.getCode(), TXfDeductStatusEnum.UNLOCK.getCode());
        checkDeduct(tmp, tXfSettlementEntity, xfDeductionBusinessTypeEnum);
		executeMatch(xfDeductionBusinessTypeEnum, tXfSettlementEntity, targetStatus.getCode(), matchResList);
        // 2022-08-08 新增，
		List<TXfBillDeductEntity> billList = tXfBillDeductExtDao.queryBillBySettlementNoAndBusinessType(tXfSettlementEntity.getSettlementNo(), xfDeductionBusinessTypeEnum.getValue());
		log.info("mergeSettlementByManual SettlementNo:{}, tXfBillDeductExtDao:{}", tXfSettlementEntity.getSettlementNo(), JSON.toJSONString(billList));
		if (billList != null && billList.size() > 0) {
			billList.forEach(item -> {
				billSettlementService.addBillSettlement(billSettlementService.bulidBillSettlementEntity(tXfSettlementEntity, item));
			});
		}
        return tXfSettlementEntity;
    }*/

	/**
	 * 1、执行单据合并业务单
	 *
	 * @param tmp
	 * @param tXfBillDeductStatusEnum
	 * @param referenceDate
	 * @param targetSatus
	 * @return
	 */
   /* private TXfSettlementEntity executeMerge(TXfDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tmp, TXfBillDeductEntity negativeBill, TXfDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfDeductStatusEnum targetSatus) {
        String purchaserNo = tmp.getPurchaserNo();
        String sellerNo = tmp.getSellerNo();
        BigDecimal taxRate = tmp.getTaxRate();
        Integer type = deductionEnum.getValue();
        Integer status = tXfBillDeductStatusEnum.getCode();
        Integer targetStatus = targetSatus.getCode();
        List<TXfBillDeductEntity> tXfBillDeductEntities = new ArrayList<>();
        tXfBillDeductEntities.add(tmp);
        if (Objects.nonNull(negativeBill)) {
            tXfBillDeductEntities.add(negativeBill);
        }
        TXfSettlementEntity tXfSettlementEntity =  trans2Settlement(tXfBillDeductEntities,deductionEnum);
        tXfBillDeductExtDao.updateMergeNegativeBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, type, status, targetStatus, TXfDeductStatusEnum.UNLOCK.getCode());
        tXfBillDeductExtDao.updateMergePositiveBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, referenceDate, type, status, targetStatus, TXfDeductStatusEnum.UNLOCK.getCode());
        *//**
         * 1、更新完成后，进行此结算下的数据校验，校验通过，提交，失败，回滚：表示有的新的单子进来，不满足条件了,回滚操作
         *//*
        tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(), targetStatus, TXfDeductStatusEnum.UNLOCK.getCode());
        checkDeduct(tmp, tXfSettlementEntity, deductionEnum);
        return tXfSettlementEntity;
    }*/

    /**
     * 1、检查合并后的结果
     * @param tmp
     * @param tXfSettlementEntity
     * @param xfDeductionBusinessTypeEnum
     */
    /*private void checkDeduct(TXfBillDeductEntity  tmp, TXfSettlementEntity tXfSettlementEntity, TXfDeductionBusinessTypeEnum xfDeductionBusinessTypeEnum) {
        if (tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0 ) {
            *//**
             * tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0 说明在更新过程钟，新的单据被更新到,而且更新到的负数大于正数，合并失败
             *//*
            log.error("{}单 超期的正值单据+负数单据，小于 0  sellerNo: {} purchaserNo: {} taxRate:{}",xfDeductionBusinessTypeEnum.getDes(), tmp.getSellerNo(), tmp.getPurchaserNo(), tmp.getTaxRate());
            throw new RuntimeException("");
        }
        if ( tmp.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) <0) {
            *//**
             * tmp.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) <0 说明被合并的单据发生了 取消或锁定,需要回撤操作
             *//*
            log.error("{}单 存在锁定、取消的 sellerNo: {} purchaserNo: {} taxRate:{}", xfDeductionBusinessTypeEnum.getDes(),tmp.getSellerNo(), tmp.getPurchaserNo(), tmp.getTaxRate());
            throw new RuntimeException("");
        }
    }*/


}
