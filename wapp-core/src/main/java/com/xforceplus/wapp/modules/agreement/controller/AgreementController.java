package com.xforceplus.wapp.modules.agreement.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.dto.NegativeAndOverDueSummary;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.dto.SettlementCancelRequest;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.service.CommAgreementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 17:17
 **/
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/agreement")
@Api(tags = "协议单API")
public class AgreementController {

    @Autowired
    private DeductViewService deductService;

    @Autowired
    private CommAgreementService commAgreementService;

    // 使用全局配置 PropertyEditorRegistrar
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//    }

    @GetMapping("summary")
    @ApiOperation(value = "页头统计")
    public R summary(DeductListRequest request){
        request.setTaxRate(null);
        //只显示未超期的数据列表
        request.setOverdue(0);
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final List<SummaryResponse> summary = deductService.summary(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        return R.ok(summary);
    }

    @GetMapping
    @ApiOperation(value = "协议列表")
    public R agreements(DeductListRequest request) {
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        request.setOverdue(0);
        final PageResult<DeductListResponse> page = deductService.deductByPage(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);

        if (Objects.nonNull(request.getTaxRate())&& BigDecimal.ZERO.compareTo( request.getTaxRate())<=0) {
            final BigDecimal sum = deductService.sumDueAndNegative(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
            page.setExt(NegativeAndOverDueSummary.builder().negativeOverDueAmount(sum.toPlainString()).build());
        }

        return R.ok(page);
    }


    @PostMapping("settlement")
    @ApiOperation("生成结算单")
    public R makeSettlement(@RequestBody MakeSettlementRequest request){
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final TXfSettlementEntity tXfSettlementEntity = deductService.makeSettlement(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        Map<String, Object> map=new HashMap<>();
        map.put("settlementId",tXfSettlementEntity.getId());
        map.put("settlementNo",tXfSettlementEntity.getSettlementNo());
        return R.ok(map, "结算单生成完毕");
    }
//
//
    @PostMapping("pre-settlement")
    @ApiOperation("结算单预匹配发票")
    public R preSettlement(@RequestBody PreMakeSettlementRequest request) {
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final List<MatchedInvoiceListResponse> matchedInvoice = deductService.getMatchedInvoice(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        return R.ok(matchedInvoice);
    }

    @PostMapping("cancel-settlement")
    public R cancelSettlement(@RequestBody SettlementCancelRequest request){
        commAgreementService.destroyAgreementSettlement(request.getSettlementId());
        return R.ok("结算单取消成功");
    }


}
