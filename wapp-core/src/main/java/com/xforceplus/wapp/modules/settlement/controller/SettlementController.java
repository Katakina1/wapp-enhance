package com.xforceplus.wapp.modules.settlement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.SettlementApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.settlement.dto.InvoiceMatchedRequest;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemTaxNoUpdatedRequest;
import com.xforceplus.wapp.modules.settlement.dto.SettlementUndoRedNotificationRequest;
import com.xforceplus.wapp.modules.settlement.service.SettlementItemServiceImpl;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.service.CommSettlementService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    private DeductViewService deductViewService;

    @Autowired
    private SettlementItemServiceImpl settlementItemService;

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private InvoiceServiceImpl invoiceService;

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
    public R undoRedNotification(@RequestBody SettlementUndoRedNotificationRequest request) {
        commSettlementService.applyDestroySettlementPreInvoice(request.getSettlementId());
        return R.ok("撤销红字信息表申请已提交成功");
    }


    @GetMapping("/matched-invoice")
    @ApiOperation("获取指定协议单已匹配的发票")
    public R invoiceList(Long settlementId, @RequestParam @ApiParam("1 协议单，2 EPD") int type) {
        XFDeductionBusinessTypeEnum typeEnum;
        switch (type) {
            case 1:
                typeEnum = XFDeductionBusinessTypeEnum.AGREEMENT_BILL;
                break;
            case 2:
                typeEnum = XFDeductionBusinessTypeEnum.EPD_BILL;
                break;
            default:
                throw new EnhanceRuntimeException("单据类型不正确，应为(协议单:1；EPD:2)");
        }
        final List<MatchedInvoiceListResponse> matchedInvoice = deductViewService.getMatchedInvoice(settlementId, typeEnum);
        return R.ok(matchedInvoice);
    }

    @PostMapping("/matched-invoice")
    @ApiOperation("保存手动调整的票单匹配关系")
    public R saveInvoice( Long settlementId, @RequestBody InvoiceMatchedRequest request) {
        //移除的发票要解除关系释放可用金额，添加的发票要建立关系减去占用金额
        try {
            invoiceService.saveSettlementMatchedInvoice(settlementId, request);
        }catch (Exception e){
            return R.fail(e.getMessage());
        }
        return R.ok();
    }

    @PostMapping("details/tax-no")
    @ApiOperation("修改明细税编")
    public R saveInvoiceDetails(@RequestBody SettlementItemTaxNoUpdatedRequest request) {

        this.settlementItemService.batchUpdateItemTaxNo(request);

        return R.ok();
    }


    @GetMapping("{settlementId}/details")
    @ApiOperation("结算单号获取明细")
    public R details(@PathVariable long settlementId, @RequestParam String settlementNo, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int size) {
        final Page<TXfSettlementItemEntity> itemsBySettlementNo = this.settlementItemService.getItemsBySettlementNo(settlementNo, page, size);
        final PageResult<TXfSettlementItemEntity> result = PageResult.of(itemsBySettlementNo.getRecords(), itemsBySettlementNo.getTotal(), itemsBySettlementNo.getPages(), itemsBySettlementNo.getSize());
        return R.ok(result);
    }

    @GetMapping("/details")
    @ApiOperation("结算单号获取明细（推荐）")
    public R detailsBySettlementNo( @RequestParam String settlementNo, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int size) {
        final Page<TXfSettlementItemEntity> itemsBySettlementNo = this.settlementItemService.getItemsBySettlementNo(settlementNo, page, size);
        final PageResult<TXfSettlementItemEntity> result = PageResult.of(itemsBySettlementNo.getRecords(), itemsBySettlementNo.getTotal(), itemsBySettlementNo.getPages(), itemsBySettlementNo.getSize());
        return R.ok(result);
    }


    @ApiOperation(value = "推荐发票列表", notes = "", response = Response.class)
    @GetMapping(value = "/recommended")
    public R recommend(Long settlementId, @Valid InvoiceRecommendListRequest request) {
        final PageResult<InvoiceDto> recommend = settlementService.recommend(settlementId, request);
        return R.ok( recommend);
    }


}
