package com.xforceplus.wapp.modules.claim.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.dto.SettlementApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
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
    private DeductViewService deductViewService;

    @Autowired
    private ClaimService claimService;

    @GetMapping
    @ApiOperation(value = "索赔单列表")
    public R claims(DeductListRequest request){

        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final PageResult<DeductListResponse> page = deductViewService.deductClaimByPage(request);
        return R.ok(page);
    }

    @ApiOperation(value = "申请不定案", notes = "", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply-verdict")
    public R claimApplyVerdict(@RequestBody SettlementApplyVerdictRequest request) {
        request.setSellerNo(UserUtil.getUser().getUsercode());
        try {
            claimService.applyClaimVerdictByBillDeductId(request.getBillDeductIdList());
        }catch (Exception e){
            return R.fail(e.getMessage());
        }
        return R.ok("成功", "");
    }




}
