package com.xforceplus.wapp.modules.settlement.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.SettlementApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.settlement.dto.InvoiceMatchedRequest;
import com.xforceplus.wapp.modules.settlement.dto.SettlementUndoRedNotificationRequest;
import com.xforceplus.wapp.service.CommSettlementService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 19:51
 **/
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/settlement")
public class SettlementController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private CommSettlementService commSettlementService;

    @Autowired
    private InvoiceServiceImpl invoiceService;

    @Autowired
    private DeductViewService deductViewService;

    @ApiOperation(value = "申请不定案", notes = "", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/applyVerdict")
    public Response applyVerdict(@RequestBody SettlementApplyVerdictRequest request) {
        claimService.applyClaimVerdict(request.getSettlementId(), request.getBillDeductIdList());
        return Response.ok("成功", "");
    }

    @PostMapping("undo-red-notification")
    @ApiOperation(value = "撤销红字信息表")
    public R undoRedNotification(@RequestBody SettlementUndoRedNotificationRequest request){
        commSettlementService.applyDestroySettlementPreInvoice(request.getSettlementId());
        return R.ok("撤销红字信息表申请已提交成功");
    }


    @GetMapping("{settlementId}/matched-invoice")
    @ApiOperation("获取指定协议单已匹配的发票")
    public R invoiceList(@PathVariable Long settlementId,@RequestParam @ApiParam("1 协议单，2 EPD") int type) {
        XFDeductionBusinessTypeEnum typeEnum;
        switch (type){
            case 1:
                typeEnum=XFDeductionBusinessTypeEnum.AGREEMENT_BILL;
                break;
            case 2:
                typeEnum=XFDeductionBusinessTypeEnum.EPD_BILL;
                break;
            default:
                throw new EnhanceRuntimeException("单据类型不正确，应为(协议单:1；EPD:2)");
        }
        final List<MatchedInvoiceListResponse> matchedInvoice = deductViewService.getMatchedInvoice(settlementId, typeEnum);
        return R.ok(matchedInvoice);
    }

    @PostMapping("{settlementId}/matched-invoice")
    @ApiOperation("获取指定协议单已匹配的发票")
    public R saveInvoice(@PathVariable Long settlementId, InvoiceMatchedRequest request) {
        //TODO
        return R.ok();
    }

    @GetMapping("{settlementId}/details")
    public R details(@PathVariable long settlementId){

        return R.ok();
    }



    @ApiOperation(value = "推荐发票列表", notes = "", response = Response.class, tags = {"发票池",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "{settlementId}/recommended")
    public Response recommend(@PathVariable Long settlementId, InvoiceRecommendListRequest request){

        final PageResult<InvoiceDto> recommend = invoiceService.recommend(settlementId, request);

        return Response.ok("",recommend);
    }




}
