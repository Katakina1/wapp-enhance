package com.xforceplus.wapp.modules.agreement.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 17:17
 **/
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/agreement")
public class AgreementController {

    @Autowired
    private ClaimService deductService;


    @GetMapping
    public R agreements(DeductListRequest request) {
        final PageResult<DeductListResponse> page = deductService.deductByPage(request, XFDeductionBusinessTypeEnum.EPD_BILL);
        return R.ok(page);
    }
}
