package com.xforceplus.wapp.modules.claim.controller;

import com.xforceplus.wapp.modules.claim.dto.AgreeApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.dto.ApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.dto.RejectApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 索赔单业务逻辑
 */
@RestController("/claim")
@Api(tags = "索赔单业务逻辑")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @ApiOperation(value = "申请不定案", notes = "", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/applyVerdict")
    public Response applyVerdict(@RequestBody ApplyVerdictRequest request){
        claimService.applyClaimVerdict(request.getSettlementId(),request.getBillDeductIdList());
        return Response.ok("成功","");
    }

//    @ApiOperation(value = "驳回申请不定案", notes = "", response = Response.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "response", response = Response.class)})
//    @PostMapping(value = "/rejectApplyVerdict")
//    public Response rejectApplyVerdict(@RequestBody RejectApplyVerdictRequest request){
//        claimService.rejectClaimVerdict(request.getSettlementId());
//        return Response.ok("成功","");
//    }
//
//    @ApiOperation(value = "通过申请不定案", notes = "", response = Response.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "response", response = Response.class)})
//    @PostMapping(value = "/agreeApplyVerdict")
//    public Response agreeApplyVerdict(@RequestBody AgreeApplyVerdictRequest request){
//        claimService.agreeClaimVerdict(request.getSettlementId());
//        return Response.ok("成功","");
//    }
}
