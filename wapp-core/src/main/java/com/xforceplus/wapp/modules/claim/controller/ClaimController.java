package com.xforceplus.wapp.modules.claim.controller;

import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.client.LockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.dto.SettlementApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.service.CommClaimService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

/**
 * 索赔单业务逻辑
 */
@RestController
@RequestMapping(EnhanceApi.BASE_PATH+"/claim")
@Api(tags = "索赔单业务逻辑")
@Slf4j
public class ClaimController {

    @Autowired
    private DeductViewService deductViewService;

    @Autowired
    private ClaimService claimService;
    
    @Autowired
    private CommClaimService commClaimService;

    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;

    @Autowired
    private LockClient lockClient;

    @GetMapping
    @ApiOperation(value = "索赔单列表")
    public R<Object> claims(DeductListRequest request){
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final PageResult<DeductListResponse> page = deductViewService.deductClaimByPage(request);
        return R.ok(page);
    }

    @ApiOperation(value = "申请不定案", notes = "", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply-verdict")
    public R<Object> claimApplyVerdict(@RequestBody SettlementApplyVerdictRequest request) {
        request.setSellerNo(UserUtil.getUser().getUsercode());
        try {
            claimService.applyClaimVerdictByBillDeductId(request.getBillDeductIdList());
        }catch (Exception e){
            return R.fail(e.getMessage());
        }
        return R.ok("成功", "");
    }

    @ApiOperation(value = "索赔撤销", notes = "", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/destroy-claim")
    public R destroyClaimSettlement(@RequestParam Long settlementId) {
        Asserts.isFalse(CommonUtil.isEdit(settlementId), "参数有误");

        log.info("旧版组合结算单撤销:{}", settlementId);
        commClaimService.destroyClaimSettlement(settlementId);
        return R.ok();
    }
    
    @ApiOperation(value = "索赔业务单导出")
    @PostMapping("/export")
    public R<Object> export(@ApiParam(value = "索赔业务单导出请求" ,required=true )@RequestBody DeductListRequest request) {
    	request.setSize(99999);
    	request.setBusinessType(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue());
        request.setSellerNo(UserUtil.getUser().getUsercode());
        if(deductViewService.export(request)){
            return R.ok("单据导出正在处理，请在消息中心");
        }else{
            return R.fail("导出任务添加失败");
        }
    }

}
