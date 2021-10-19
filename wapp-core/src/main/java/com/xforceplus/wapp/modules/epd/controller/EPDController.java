package com.xforceplus.wapp.modules.epd.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceMatchListRequest;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceMatchListResponse;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    @GetMapping("invoice")
    public R invoiceList(InvoiceMatchListRequest request) {

        PageResult<InvoiceMatchListResponse> pageResult=new PageResult<>();
        return R.ok();
    }
}
