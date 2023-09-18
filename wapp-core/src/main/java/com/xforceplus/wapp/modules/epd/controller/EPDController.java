package com.xforceplus.wapp.modules.epd.controller;

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
import com.xforceplus.wapp.service.CommEpdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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


    @Autowired
    private CommEpdService commEpdService;


    @GetMapping("summary")
    @ApiOperation(value = "EPD页头统计")
    public R summary(DeductListRequest request) {
        request.setTaxRate(null);
        //只显示未超期的数据列表
//        request.setOverdue(0);
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final List<SummaryResponse> summary = deductService.summary(request, TXfDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok(summary);
    }

    @GetMapping
    @ApiOperation(value = "EPD列表")
    public R epds( DeductListRequest request) {
        //只显示未超期的数据列表
//        request.setOverdue(0);
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final PageResult<DeductListResponse> page = deductService.deductByPage(request, TXfDeductionBusinessTypeEnum.EPD_BILL);

        if (Objects.nonNull(request.getTaxRate())&& BigDecimal.ZERO.compareTo( request.getTaxRate())<=0) {
            final BigDecimal sum = deductService.sumDueAndNegative(request, TXfDeductionBusinessTypeEnum.EPD_BILL);
            page.setExt(NegativeAndOverDueSummary.builder().negativeOverDueAmount(sum.toPlainString()).build());
        }
        return R.ok(page);
    }

    @PostMapping("settlement")
    @ApiOperation("生成结算单")
    public R makeSettlement(@RequestBody MakeSettlementRequest request){
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
/*        final TXfSettlementEntity tXfSettlementEntity = deductService.makeSettlement(request, TXfDeductionBusinessTypeEnum.EPD_BILL);
        Map<String, Object> map=new HashMap<>();
        map.put("settlementId",tXfSettlementEntity.getId());
        map.put("settlementNo",tXfSettlementEntity.getSettlementNo());
        return R.ok(map, "结算单生成完毕");*/
        return R.fail("EPD生成结算单功能已下线");
    }

    @PostMapping("pre-settlement")
    @ApiOperation("结算单预匹配发票")
    public R preSettlement(@RequestBody PreMakeSettlementRequest request) {
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        /*final List<MatchedInvoiceListResponse> matchedInvoice = deductService.getMatchedInvoice(request, TXfDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok(matchedInvoice);*/
        return R.fail("EPD预匹配发票功能已下线");
    }

    @PostMapping("cancel-settlement")
    public R cancelSettlement(@RequestBody SettlementCancelRequest request){
        commEpdService.destroyEpdSettlement(request.getSettlementId());
        return R.ok("结算单取消成功");
    }
    
    @ApiOperation(value = "EPD业务单导出")
    @PostMapping("/export")
    public R<Object> export(@ApiParam(value = "EPD业务单导出请求" ,required=true )@RequestBody DeductListRequest request) {
    	request.setSize(99999);
        request.setSellerNo(UserUtil.getUser().getUsercode());
    	request.setBusinessType(TXfDeductionBusinessTypeEnum.EPD_BILL.getValue());
        if(deductService.export(request)){
            return R.ok("单据导出正在处理，请在消息中心");
        }else{
            return R.fail("导出任务添加失败");
        }
    }
}
