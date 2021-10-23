package com.xforceplus.wapp.modules.deduct.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.mapstruct.DeductMapper;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceMatchListResponse;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 18:58
 **/
@Service
public class DeductViewService extends ServiceImpl<TXfBillDeductExtDao,TXfBillDeductEntity> {

    @Autowired
    private DeductMapper deductMapper;

    @Autowired
    private OverdueServiceImpl overdueService;

    @Autowired
    private AgreementBillService agreementBillService;



    public List<SummaryResponse> summary(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum) {

        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapper(request, typeEnum);

        wrapper.select(TXfBillDeductEntity.TAX_RATE+" as taxRate", "count(1) as count");

        final List<Map<String, Object>> map = this.getBaseMapper().selectMaps(wrapper.groupBy(TXfBillDeductEntity.TAX_RATE));
        return toSummary(map);
    }

    public PageResult<DeductListResponse> deductByPage(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum){

        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapper(request, typeEnum);
        Page<TXfBillDeductEntity> page=new Page<>(request.getPage(),request.getSize());
        final Page<TXfBillDeductEntity> pageResult = this.page(page, wrapper);
        final List<DeductListResponse> responses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pageResult.getRecords())){
            final List<DeductListResponse> list = pageResult.getRecords().stream().map(x -> {
                final DeductListResponse deductListResponse = deductMapper.toResponse(x);
                deductListResponse.setOverdue(checkOverdue(typeEnum, x.getSellerNo(), x.getDeductDate()) ? 1 : 0);
                return deductListResponse;
            }).collect(Collectors.toList());
            responses.addAll(list);
        }
        return PageResult.of(responses,pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    private boolean checkOverdue(XFDeductionBusinessTypeEnum typeEnum,String sellerNo,Date deductDate ){
        final int overdue = getOverdue(typeEnum, sellerNo);
        final DateTime dateTime = DateUtil.offsetDay(new Date(), -overdue+1);
        final Date date = dateTime.setField(DateField.HOUR,0)
                .setField(DateField.MINUTE,0)
                .setField(DateField.SECOND,0)
                .setField(DateField.MILLISECOND,0)
                .toJdkDate();
        return date.before(deductDate);
    }

    private int getOverdue(XFDeductionBusinessTypeEnum typeEnum,String sellerNo){
        ServiceTypeEnum serviceTypeEnum = null;
        switch (typeEnum){
            case CLAIM_BILL:
                serviceTypeEnum= ServiceTypeEnum.CLAIM;
                break;
            case AGREEMENT_BILL:
                serviceTypeEnum=ServiceTypeEnum.AGREEMENT;
                break;
            case EPD_BILL:
                serviceTypeEnum=ServiceTypeEnum.EPD;
                break;
            default:
                throw new EnhanceRuntimeException("业务单据类型有误:"+typeEnum.getDes());
        }

        final Optional<Overdue> overdue = overdueService.oneOptBySellerNo(serviceTypeEnum, sellerNo);
        return overdue.get().getOverdueDay();
    }


    private List<SummaryResponse> toSummary(List<Map<String, Object>> objs){

        final List<SummaryResponse> summaryResponses = objs.stream().map(x -> {
            final Object taxRate = x.get("taxRate");
            final Object count = x.get("count");
            return new SummaryResponse((Integer) count, taxRate.toString());
        }).sorted(Comparator.comparing(SummaryResponse::getTaxRate)).collect(Collectors.toList());
        final SummaryResponse summaryResponse = new SummaryResponse();
        summaryResponse.setAll(true);
        summaryResponse.setTaxRate("-1");
        summaryResponse.setTaxRateText("全部");
        summaryResponse.setCount(summaryResponses.stream().map(SummaryResponse::getCount).reduce(0, Integer::sum));
        summaryResponses.add(summaryResponse);
        return summaryResponses;
    }


    private QueryWrapper<TXfBillDeductEntity> wrapper(DeductListRequest request,XFDeductionBusinessTypeEnum typeEnum){
        TXfBillDeductEntity deductEntity=new TXfBillDeductEntity();
        deductEntity.setBusinessNo(request.getBillNo());
        deductEntity.setPurchaserNo(request.getPurchaserNo());
        if (request.getTaxRate()!=null &&
                request.getTaxRate().compareTo(new BigDecimal(-1)) !=0
        ){
            deductEntity.setTaxRate(request.getTaxRate());
        }

        deductEntity.setStatus(request.getStatus());

        deductEntity.setDeductInvoice(request.getInvoiceNo());

        deductEntity.setSellerNo(request.getSellerNo());

        QueryWrapper<TXfBillDeductEntity> wrapper= Wrappers.query(deductEntity);
        //扣款日期>>Begin
        final String deductDateBegin = request.getDeductDateBegin();
        if (StringUtils.isNotBlank(deductDateBegin)){
            wrapper.ge(TXfBillDeductEntity.DEDUCT_DATE,deductDateBegin);
        }

        //扣款日期>>End
        String deductDateEnd = request.getDeductDateEnd();
        if (StringUtils.isNotBlank(deductDateEnd)){
            final String format = DateUtils.addDayToYYYYMMDD(deductDateEnd, 1);
            wrapper.le(TXfBillDeductEntity.DEDUCT_DATE,format);
        }
        // ===============================
        //定案、入账日期 >> begin
        final String verdictDateBegin = request.getVerdictDateBegin();
        if (StringUtils.isNotBlank(verdictDateBegin)){
            wrapper.ge(TXfBillDeductEntity.VERDICT_DATE,deductDateBegin);
        }
        //定案、入账日期 >> end
        String verdictDateEnd = request.getVerdictDateEnd();
        if (StringUtils.isNotBlank(verdictDateEnd)){
            final String format = DateUtils.addDayToYYYYMMDD(verdictDateEnd, 1);
            wrapper.le(TXfBillDeductEntity.VERDICT_DATE,format);
        }

        wrapper.eq(TXfBillDeductEntity.BUSINESS_TYPE,typeEnum.getValue());

        //超期判断
        if (request.getOverdue() != null ) {
            ServiceTypeEnum serviceTypeEnum = null;
            switch (typeEnum){
                case CLAIM_BILL:
                    serviceTypeEnum= ServiceTypeEnum.CLAIM;
                    break;
                case AGREEMENT_BILL:
                    serviceTypeEnum=ServiceTypeEnum.AGREEMENT;
                    break;
                case EPD_BILL:
                    serviceTypeEnum=ServiceTypeEnum.EPD;
                    break;
                default:
                    throw new EnhanceRuntimeException("业务单据类型有误:"+typeEnum.getDes());
            }

            final Optional<Overdue> overdue = overdueService.oneOptBySellerNo(serviceTypeEnum, request.getSellerNo());



            overdue.ifPresent(x->{
                final DateTime dateTime = DateUtil.offsetDay(new Date(), -x.getOverdueDay()+1);
                final Date date = dateTime.setField(DateField.HOUR,0)
                        .setField(DateField.MINUTE,0)
                        .setField(DateField.SECOND,0)
                        .setField(DateField.MILLISECOND,0)
                        .toJdkDate();
                switch (request.getOverdue()){
                    case 1:
                        wrapper.lt(TXfBillDeductEntity.DEDUCT_DATE,date);
                        break;
                    case 0:
                        wrapper.gt(TXfBillDeductEntity.DEDUCT_DATE,date);
                        break;
                }
            });
        }


        return wrapper;
    }



    /**
     * @param request 列表单参数
     * @return
     */
    public PageResult<DeductListResponse> deductClaimByPage(DeductListRequest request){
        final String usercode = UserUtil.getUser().getUsercode();
//        companyService.getOrgInfoByOrgCode();
        final PageResult<DeductListResponse> result = deductByPage(request, XFDeductionBusinessTypeEnum.CLAIM_BILL);
        final List<DeductListResponse> responses = result.getRows();

        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(responses)){
            final List<String> settlementNos = responses.stream().map(DeductListResponse::getRefSettlementNo).distinct().collect(Collectors.toList());
            final Map<String, Integer> invoiceCount = getInvoiceCountBySettlement(settlementNos);
            responses.forEach(x->{
                final Integer count = Optional.ofNullable(invoiceCount.get(x.getRefSettlementNo())).orElse(0);
                x.setInvoiceCount(count);
            });
        }
        return result;
    }

    private Map<String,Integer> getInvoiceCountBySettlement(List<String> settlementNos){

// TODO



        return Collections.emptyMap();
    }


    public Long makeSettlement(MakeSettlementRequest request, XFDeductionBusinessTypeEnum type){
        final List<String> billNos = request.getBillNos();
        if(CollectionUtils.isEmpty(billNos)){
            throw new EnhanceRuntimeException("请至少选择一张业务单据");
        }
        final TXfSettlementEntity tXfSettlementEntity = agreementBillService.mergeSettlementByManual(billNos, type);

        return tXfSettlementEntity.getId();

    }

}
