package com.xforceplus.wapp.modules.epd.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

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
        final List<SummaryResponse> summary = deductService.summary(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok(summary);
    }

    @GetMapping
    @ApiOperation(value = "EPD列表")
    public R epds(@Valid DeductListRequest request) {
        final PageResult<DeductListResponse> page = deductService.deductByPage(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok(page);
    }

    @PostMapping("settlement")
    @ApiOperation("生成结算单")
    public R makeSettlement(@RequestBody MakeSettlementRequest request){
        final String settlementNo = deductService.makeSettlement(request, XFDeductionBusinessTypeEnum.AGREEMENT_BILL);
        return R.ok( Collections.singletonMap("settlementNo",settlementNo),"结算单生成完毕");
    }
}
