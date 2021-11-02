package com.xforceplus.wapp.modules.deduct.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.mapstruct.DeductMapper;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.mapstruct.MatchedInvoiceMapper;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 18:58
 **/
@Service
public class DeductViewService extends ServiceImpl<TXfBillDeductExtDao, TXfBillDeductEntity> {

    @Autowired
    private DeductMapper deductMapper;

    @Autowired
    private OverdueServiceImpl overdueService;

    @Autowired
    private AgreementBillService agreementBillService;

    @Autowired
    private DeductInvoiceService deductInvoiceService;

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private MatchedInvoiceMapper matchedInvoiceMapper;

    @Autowired
    private BlueInvoiceService blueInvoiceService;

    @Autowired
    private DeductService deductService;

    @Autowired
    private SettlementService settlementService;

    private List<BigDecimal> taxRates;

    public DeductViewService(@Value("${wapp.tax-rate}") String rates){
        if (StringUtils.isBlank(rates)){
            throw new EnhanceRuntimeException("缺少配置 wapp.tax-rate");
        }

        final String[] split = rates.split("[,]");
        taxRates=new ArrayList<>();
        for (String rate : split) {
            if (StringUtils.isBlank(rate.trim())){
                throw new EnhanceRuntimeException("[wapp.tax-rate]税率配置以英文逗号隔开，切不能有空");
            }

            final BigDecimal rateDecimal = new BigDecimal(rate.trim());

            if (rateDecimal.compareTo(BigDecimal.ZERO) < 0){
                throw new EnhanceRuntimeException("[wapp.tax-rate]指定的税率不能是负数");
            }

            if (rateDecimal.compareTo(BigDecimal.ONE) > 0){
                throw new EnhanceRuntimeException("[wapp.tax-rate]指定的税率只能为小数位保留2位的小数");
            }

            taxRates.add(rateDecimal.setScale(4));
        }
    }



    public List<SummaryResponse> summary(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum) {

        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapper(request, typeEnum);


        wrapper.select(TXfBillDeductEntity.TAX_RATE + " as taxRate", "count(1) as count");

        final List<Map<String, Object>> map = this.getBaseMapper().selectMaps(wrapper.groupBy(TXfBillDeductEntity.TAX_RATE));
        return toSummary(map);
    }

    public PageResult<DeductListResponse> deductByPage(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum) {

        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapper(request, typeEnum);

        Page<TXfBillDeductEntity> page = new Page<>(request.getPage(), request.getSize());
        final Page<TXfBillDeductEntity> pageResult = this.page(page, wrapper);
        final List<DeductListResponse> responses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pageResult.getRecords())) {
            final List<DeductListResponse> list = pageResult.getRecords().stream().map(x -> {
                final DeductListResponse deductListResponse = deductMapper.toResponse(x);
                deductListResponse.setOverdue(checkOverdue(typeEnum, x.getSellerNo(), x.getDeductDate()) ? 1 : 0);
                if (Objects.equals(deductListResponse.getLock(),TXfBillDeductStatusEnum.LOCK.getCode())){
                    deductListResponse.setRefSettlementNo(null);
                }
                return deductListResponse;
            }).collect(Collectors.toList());
            responses.addAll(list);
        }
        return PageResult.of(responses, pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    public BigDecimal sumDueAndNegative(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum){
        return sumDueAndNegative(request.getPurchaserNo(),request.getSellerNo(),typeEnum,request.getTaxRate());
    }

    public BigDecimal sumDueAndNegative(String purchaserNo,String sellerNo, XFDeductionBusinessTypeEnum typeEnum,BigDecimal taxRate){
        final TXfBillDeductEntity deductEntity = getSumDueAndNegativeBill(purchaserNo, sellerNo, typeEnum, taxRate);
        return Optional.ofNullable(deductEntity).map(TXfBillDeductEntity::getAmountWithoutTax).orElse(BigDecimal.ZERO);
    }

    public TXfBillDeductEntity getSumDueAndNegativeBill(String purchaserNo, String sellerNo, XFDeductionBusinessTypeEnum typeEnum, BigDecimal taxRate){
        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapperOverDueNegativeBills(purchaserNo, sellerNo, typeEnum, taxRate);

        wrapper.groupBy(TXfBillDeductEntity.PURCHASER_NO,TXfBillDeductEntity.SELLER_NO,TXfBillDeductEntity.TAX_RATE);

        wrapper.select("sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,sum(amount_with_tax) as amount_with_tax,seller_no,purchaser_no, tax_rate");

        return this.getBaseMapper().selectOne(wrapper);
    }


    public List<TXfBillDeductEntity> getOverDueNegativeBills(String purchaserNo, String sellerNo, XFDeductionBusinessTypeEnum typeEnum, BigDecimal taxRate){
        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapperOverDueNegativeBills(purchaserNo, sellerNo, typeEnum, taxRate);
        wrapper.select(TXfBillDeductEntity.ID);
        return this.getBaseMapper().selectList(wrapper);
    }

    private QueryWrapper<TXfBillDeductEntity> wrapperOverDueNegativeBills(String purchaserNo, String sellerNo, XFDeductionBusinessTypeEnum typeEnum, BigDecimal taxRate){
        DeductListRequest sumRequest=new DeductListRequest();
        sumRequest.setOverdue(null);
        sumRequest.setSellerNo(sellerNo);
        sumRequest.setPurchaserNo(purchaserNo);
        sumRequest.setTaxRate(taxRate);
        switch (typeEnum){
            case CLAIM_BILL:
                sumRequest.setStatus(TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
                break;
            case EPD_BILL:
                sumRequest.setStatus(TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
                break;
            case AGREEMENT_BILL:
                sumRequest.setStatus(TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
                break;
        }

        sumRequest.setLockFlag(TXfBillDeductStatusEnum.UNLOCK.getCode());

        return doWrapper(sumRequest, typeEnum, x->{
            overDueWrapper(sellerNo,typeEnum,1,x);
            x.or(s->s.lt(TXfBillDeductEntity.AMOUNT_WITHOUT_TAX,BigDecimal.ZERO));
        });
    }

    /**
     * 单据类型，仅支持协议单和EPD，索赔单默认返回false
     *
     * @param typeEnum 单据类型，仅支持协议单和EPD，索赔单默认返回false
     * @param sellerNo 销方编号
     * @param deductDate 扣款日期
     * @return
     */
    public boolean checkOverdue(XFDeductionBusinessTypeEnum typeEnum, String sellerNo, Date deductDate) {
        if (typeEnum == XFDeductionBusinessTypeEnum.CLAIM_BILL) {
            return false;
        }
        final Date date = getOverdueDate(typeEnum,sellerNo);
        return date.after(deductDate);
    }


    /**
     * 获取超期日期，精确到天
     *
     * @param typeEnum 单据类型
     * @param sellerNo 销方编号
     * @return Date
     */
    public Date getOverdueDate(XFDeductionBusinessTypeEnum typeEnum, String sellerNo){
        final int overdue = getOverdue(typeEnum, sellerNo);
        final DateTime dateTime = DateUtil.offsetDay(new Date(), -overdue + 1);
        return dateTime.setField(DateField.HOUR, 0)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0)
                .toJdkDate();
    }


    private int getOverdue(XFDeductionBusinessTypeEnum typeEnum, String sellerNo) {
        ServiceTypeEnum serviceTypeEnum = null;
        switch (typeEnum) {
            case CLAIM_BILL:
                serviceTypeEnum = ServiceTypeEnum.CLAIM;
                break;
            case AGREEMENT_BILL:
                serviceTypeEnum = ServiceTypeEnum.AGREEMENT;
                break;
            case EPD_BILL:
                serviceTypeEnum = ServiceTypeEnum.EPD;
                break;
            default:
                throw new EnhanceRuntimeException("业务单据类型有误:" + typeEnum.getDes());
        }

        final Integer overdue = overdueService.oneOptBySellerNo(serviceTypeEnum, sellerNo);
        return overdue;
    }


    private List<SummaryResponse> toSummary(List<Map<String, Object>> objs) {

        Map<BigDecimal,SummaryResponse> summaries=new HashMap<>();
        for (BigDecimal taxRate : taxRates) {
            final SummaryResponse summaryResponse = new SummaryResponse(0, taxRate);
            summaries.put(taxRate,summaryResponse);
        }
        for (Map<String, Object> obj : objs) {
            final Object taxRate = obj.get("taxRate");
            final Object count = obj.get("count");
             SummaryResponse summaryResponse = null;
            if (taxRate instanceof BigDecimal){
                summaryResponse=summaries.get(((BigDecimal) taxRate).setScale(4));
            }else {
                summaryResponse=summaries.get(new BigDecimal(taxRate.toString()).setScale(4));
            }
            Optional.ofNullable(summaryResponse).ifPresent(x->x.setCount((Integer) count));
        }

        List<SummaryResponse> summaryResponses = new ArrayList<>(summaries.values());

        summaryResponses.sort(Comparator.comparing(SummaryResponse::getTaxRate));
        final SummaryResponse summaryResponse = new SummaryResponse();
        summaryResponse.setAll(true);
        summaryResponse.setTaxRate("-1");
        summaryResponse.setTaxRateText("全部");
        summaryResponse.setCount(summaryResponses.stream().map(SummaryResponse::getCount).reduce(0, Integer::sum));
        summaryResponses.add(summaryResponse);
        return summaryResponses;
    }


    private QueryWrapper<TXfBillDeductEntity> wrapper(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum) {
        return doWrapper(request,typeEnum,x->{
            if (typeEnum !=XFDeductionBusinessTypeEnum.CLAIM_BILL){
                // 小于0的不展示
                x.gt(TXfBillDeductEntity.AMOUNT_WITHOUT_TAX,BigDecimal.ZERO);
            }else {
                x.eq("1",1);
            }
        });
    }

    /**
     * 封装查询wrapper
     * @param request 参数
     * @param typeEnum 单据类型
     * @param and 额外的and拼接
     * @return
     */
    private QueryWrapper<TXfBillDeductEntity> doWrapper(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum, Consumer<QueryWrapper<TXfBillDeductEntity>> and) {
        TXfBillDeductEntity deductEntity = new TXfBillDeductEntity();
        deductEntity.setBusinessNo(request.getBillNo());
        deductEntity.setPurchaserNo(request.getPurchaserNo());
        if (request.getTaxRate() != null &&
                request.getTaxRate().compareTo(new BigDecimal(-1)) != 0
        ) {
            deductEntity.setTaxRate(request.getTaxRate());
        }

        deductEntity.setStatus(request.getStatus());

        deductEntity.setDeductInvoice(request.getDeductInvoice());

        deductEntity.setSellerNo(request.getSellerNo());

        deductEntity.setLockFlag(request.getLockFlag());

        QueryWrapper<TXfBillDeductEntity> wrapper = Wrappers.query(deductEntity);
        //扣款日期>>Begin
        final String deductDateBegin = request.getDeductDateBegin();
        if (StringUtils.isNotBlank(deductDateBegin)) {
            wrapper.ge(TXfBillDeductEntity.DEDUCT_DATE, deductDateBegin);
        }

        //扣款日期>>End
        String deductDateEnd = request.getDeductDateEnd();
        if (StringUtils.isNotBlank(deductDateEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(deductDateEnd, 1);
            wrapper.lt(TXfBillDeductEntity.DEDUCT_DATE, format);
        }
        // ===============================
        //定案、入账日期 >> begin
        final String verdictDateBegin = request.getVerdictDateBegin();
        if (StringUtils.isNotBlank(verdictDateBegin)) {
            wrapper.ge(TXfBillDeductEntity.VERDICT_DATE, verdictDateBegin);
        }
        //定案、入账日期 >> end
        String verdictDateEnd = request.getVerdictDateEnd();
        if (StringUtils.isNotBlank(verdictDateEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(verdictDateEnd, 1);
            wrapper.lt(TXfBillDeductEntity.VERDICT_DATE, format);
        }

        wrapper.eq(TXfBillDeductEntity.BUSINESS_TYPE, typeEnum.getValue());

        if (typeEnum != XFDeductionBusinessTypeEnum.CLAIM_BILL) {
            //协议单和EPD才有超期配置

            //超期判断
            if (request.getOverdue() != null) {
                overDueWrapper(request.getSellerNo(),typeEnum,request.getOverdue(),wrapper);
            }
        } else {
            // 索赔单只展示 生成结算单之后的数据
            wrapper.in(TXfBillDeductEntity.STATUS,
                    TXfBillDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode()
                    ,TXfBillDeductStatusEnum.CLAIM_WAIT_CHECK.getCode()
                    ,TXfBillDeductStatusEnum.CLAIM_DESTROY.getCode()
            );
        }
        if (and!=null) {
            wrapper.and(and);
        }
        return wrapper;
    }

    private void overDueWrapper(String sellerNo, XFDeductionBusinessTypeEnum typeEnum, Integer overDue, QueryWrapper<TXfBillDeductEntity> wrapper){
        final int overdue = getOverdue(typeEnum, sellerNo);

        final DateTime dateTime = DateUtil.offsetDay(new Date(), -overdue + 1);
        final Date date = dateTime.setField(DateField.HOUR, 0)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0)
                .toJdkDate();
        switch (overDue) {
            case 1:
                wrapper.lt(TXfBillDeductEntity.DEDUCT_DATE, date);
                break;
            case 0:
                wrapper.gt(TXfBillDeductEntity.DEDUCT_DATE, date);
                break;
        }
    }


    /**
     * @param request 列表单参数
     * @return
     */
    public PageResult<DeductListResponse> deductClaimByPage(DeductListRequest request) {
        final PageResult<DeductListResponse> result = deductByPage(request, XFDeductionBusinessTypeEnum.CLAIM_BILL);
        final List<DeductListResponse> responses = result.getRows();

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(responses)) {
            final List<String> settlementNos = responses.stream().map(DeductListResponse::getRefSettlementNo).distinct().collect(Collectors.toList());
            final Map<String, Integer> invoiceCount = getInvoiceCountBySettlement(settlementNos);
            final Map<String, Integer> settlementStatus = this.settlementService.getSettlementStatus(settlementNos);
            responses.forEach(x -> {
                final Integer count = Optional.ofNullable(invoiceCount.get(x.getRefSettlementNo())).orElse(0);
                x.setInvoiceCount(count);
                final Integer status = Optional.ofNullable(settlementStatus.get(x.getRefSettlementNo())).orElse(null);
                x.setSettlementStatus(status);
            });
        }
        return result;
    }

    private Map<String, Integer> getInvoiceCountBySettlement(List<String> settlementNos) {

        if (CollectionUtils.isEmpty(settlementNos)) {
            return Collections.emptyMap();
        }

        final QueryWrapper<TDxRecordInvoiceEntity> wrapper = Wrappers.<TDxRecordInvoiceEntity>query().select(TDxRecordInvoiceEntity.SETTLEMENTNO, "count(1) as count ")
                .in(TDxRecordInvoiceEntity.SETTLEMENTNO, settlementNos).groupBy(TDxRecordInvoiceEntity.SETTLEMENTNO);
        final List<Map<String, Object>> maps = tDxRecordInvoiceDao.selectMaps(wrapper);
        Map<String, Integer> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            maps.forEach(x -> {
                final Object settlement = x.get(TDxRecordInvoiceEntity.SETTLEMENTNO);
                final Integer count = (Integer) x.get("count");
                result.put(settlement.toString(), count);
            });
        }
        return result;
    }


    @Transactional
    public TXfSettlementEntity makeSettlement(MakeSettlementRequest request, XFDeductionBusinessTypeEnum type) {
        if (CollectionUtils.isEmpty(request.getInvoiceIds())) {
            throw new EnhanceRuntimeException("请至少选择一张业务单据");
        }
        final BigDecimal amount = checkAndGetTotalAmount(request, type);

        final List<BlueInvoiceService.MatchRes> matchRes = blueInvoiceService.obtainInvoiceByIds(amount, request.getInvoiceIds());
        final List<TXfBillDeductEntity> bills = getOverDueNegativeBills(request.getPurchaserNo(), request.getSellerNo(), type, request.getTaxRate());
        final List<Long> collect = bills.parallelStream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());
        List<Long> ids=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(collect)){
            ids.addAll(collect);
        }
        if (CollectionUtils.isNotEmpty(request.getBillIds())){
            ids.addAll(request.getBillIds());
        }
        return agreementBillService.mergeSettlementByManual(ids,type,matchRes);
    }

    public List<MatchedInvoiceListResponse> getMatchedInvoice(PreMakeSettlementRequest request, XFDeductionBusinessTypeEnum typeEnum){


        final BigDecimal amount = checkAndGetTotalAmount(request, typeEnum);

        final TAcOrgEntity purchaserOrg = deductService.queryOrgInfo(request.getPurchaserNo(), false);
        if (Objects.isNull(purchaserOrg)){
            throw new EnhanceRuntimeException("扣款公司代码:["+ request.getPurchaserNo()+"]不存在");
        }
        final TAcOrgEntity sellerOrg = deductService.queryOrgInfo(request.getSellerNo(), true);

        if (Objects.isNull(sellerOrg)){
            throw new EnhanceRuntimeException("供应商编号:["+ request.getSellerNo()+"]不存在");
        }

        final List<BlueInvoiceService.MatchRes> matchRes = blueInvoiceService.obtainAvailableInvoicesWithoutItems(amount, null,
                sellerOrg.getTaxNo(), purchaserOrg.getTaxNo(), request.getTaxRate().movePointRight(2), false);

        return this.matchedInvoiceMapper.toMatchInvoice(matchRes);
    }

    private BigDecimal checkAndGetTotalAmount(PreMakeSettlementRequest request, XFDeductionBusinessTypeEnum typeEnum){
        final List<Long> billId = request.getBillIds();
        BigDecimal amount=BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(billId)) {
            final QueryWrapper<TXfBillDeductEntity> wrapper = Wrappers.query();
            wrapper.select(
                    "sum(amount_without_tax) amount_without_tax"
                    ,TXfBillDeductEntity.TAX_RATE
                    ,TXfBillDeductEntity.SELLER_NO
                    ,TXfBillDeductEntity.PURCHASER_NO
            );
            TXfBillDeductStatusEnum statusEnum;
            switch (typeEnum){
                case AGREEMENT_BILL:
                    statusEnum=TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT;
                    break;
                case EPD_BILL:
                    statusEnum=TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT;
                    break;
                default:throw new EnhanceRuntimeException("手动合并结算单仅支持协议单和EPD");
            }
            wrapper.in(TXfBillDeductEntity.ID, billId)
                    .eq(TXfBillDeductEntity.BUSINESS_TYPE,typeEnum.getValue())
                    .eq(TXfBillDeductEntity.STATUS,statusEnum.getCode())
                    .eq(TXfBillDeductEntity.LOCK_FLAG,TXfBillDeductStatusEnum.UNLOCK.getCode())
                    .groupBy(TXfBillDeductEntity.SELLER_NO,TXfBillDeductEntity.PURCHASER_NO,TXfBillDeductEntity.TAX_RATE)
            ;
            final List<TXfBillDeductEntity> entities = getBaseMapper().selectList(wrapper);
            if (CollectionUtils.isNotEmpty(entities)) {
                if (entities.size() > 1) {
                    throw new EnhanceRuntimeException("您选择了存在多税率或者多购销方的单据，不能完成此操作");
                }
            } else {
                throw new EnhanceRuntimeException("您选择的单据不存在，或已被使用/锁定，请刷新重试");
            }
            TXfBillDeductEntity entity  = entities.get(0);
            if (!Objects.equals(entity.getPurchaserNo(),request.getPurchaserNo())){
                throw new EnhanceRuntimeException("单据扣款公司代码与参数不一致");
            }

            if (entity.getTaxRate().compareTo(request.getTaxRate())!=0){
                throw new EnhanceRuntimeException("单据税率与参数不一致");
            }

            amount=amount.add(entity.getAmountWithoutTax());
        }

        final BigDecimal decimal = sumDueAndNegative(request.getPurchaserNo(), request.getSellerNo(), typeEnum,request.getTaxRate());
        amount = amount.add(decimal);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EnhanceRuntimeException("负数和超期单据总额小于0，生成结算单的单据总额必须大于0，请返回重新选择正数单据");
        }
        return amount;
    }

    @Builder
    private static class PreSettlementDto{
        BigDecimal amount;
        String sellerTaxNo;
        String purchaserTaxNo;
    }

}
