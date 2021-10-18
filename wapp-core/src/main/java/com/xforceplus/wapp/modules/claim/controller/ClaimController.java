package com.xforceplus.wapp.modules.claim.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 索赔单业务逻辑
 */
@RestController
@RequestMapping(EnhanceApi.BASE_PATH+"/claim")
@Api(tags = "索赔单业务逻辑")
public class ClaimController {

    @Autowired
    private DeductViewService deductViewService;


    @GetMapping
    @ApiOperation(value = "索赔单列表")
    public R claims(DeductListRequest request){
        final PageResult<DeductListResponse> page = deductViewService.deductClaimByPage(request);
        return R.ok(page);
    }
}
