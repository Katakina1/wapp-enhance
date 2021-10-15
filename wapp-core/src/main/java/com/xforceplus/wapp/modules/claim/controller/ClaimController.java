package com.xforceplus.wapp.modules.claim.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.ApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    private DeductService deductService;

    @Autowired
    private ClaimService claimService;

    @ApiOperation(value = "申请不定案", notes = "", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/applyVerdict")
    public Response applyVerdict(@RequestBody ApplyVerdictRequest request) {
        claimService.applyClaimVerdict(request.getSettlementId(), request.getBillDeductIdList());
        return Response.ok("成功", "");
    }


    @GetMapping
    public R claims(DeductListRequest request){
        final Page<TXfBillDeductEntity> page = deductService.deductByPage(request, XFDeductionBusinessTypeEnum.CLAIM_BILL);
        final PageResult<TXfBillDeductEntity> of = PageResult.of(page.getRecords(), page.getTotal(), page.getPages(), page.getSize());
        return R.ok(of);
    }
}
