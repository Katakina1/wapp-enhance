package com.xforceplus.wapp.modules.epd.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.dto.NegativeAndOverDueSummary;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 17:17
 **/
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/epd")
@Api(tags = "EPD单API")
public class EPDController {

    @Autowired
    private DeductViewService deductService;


    @GetMapping("summary")
    @ApiOperation(value = "EPD页头统计")
    public R summary(DeductListRequest request) {
        request.setTaxRate(null);
        //只显示未超期的数据列表
        request.setOverdue(0);
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final List<SummaryResponse> summary = deductService.summary(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok(summary);
    }

    @GetMapping
    @ApiOperation(value = "EPD列表")
    public R epds( DeductListRequest request) {
        //只显示未超期的数据列表
        request.setOverdue(0);
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final PageResult<DeductListResponse> page = deductService.deductByPage(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        final BigDecimal sum = deductService.sumDueAndNegative(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        page.setExt(NegativeAndOverDueSummary.builder().negativeOverDueAmount(sum.toPlainString()).build());
        return R.ok(page);
    }

    @PostMapping("settlement")
    @ApiOperation("生成结算单")
    public R makeSettlement(@RequestBody MakeSettlementRequest request){
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        deductService.makeSettlement(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok( "结算单生成完毕");
    }

    @PostMapping("pre-settlement")
    @ApiOperation("结算单预匹配发票")
    public R preSettlement(@RequestBody PreMakeSettlementRequest request) {
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final List<MatchedInvoiceListResponse> matchedInvoice = deductService.getMatchedInvoice(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok(matchedInvoice);
    }
}
