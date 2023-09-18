package com.xforceplus.wapp.modules.settlement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.SettlementApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendResponse;
import com.xforceplus.wapp.modules.deduct.dto.SettlementCancelRequest;
import com.xforceplus.wapp.modules.deduct.dto.SettmentRedListRequest;
import com.xforceplus.wapp.modules.deduct.dto.SettmentRedListResponse;
import com.xforceplus.wapp.modules.deduct.service.BillRefQueryService;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationRollbackFailResult;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.settlement.dto.SettlementApproveInfo;
import com.xforceplus.wapp.modules.settlement.dto.SettlementApproveRequest;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemResponse;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemTaxNoUpdatedRequest;
import com.xforceplus.wapp.modules.settlement.dto.SettlementUndoRedNotificationRequest;
import com.xforceplus.wapp.modules.settlement.service.SettlementItemServiceImpl;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.service.CommSettlementService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 19:51
 **/
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/settlement")
@Slf4j
public class SettlementController {

    @Autowired
    private ClaimService claimService;
    @Autowired
    private CommSettlementService commSettlementService;
    @Autowired
    private SettlementItemServiceImpl settlementItemService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private LockClient lockClient;
    @Autowired
    private BillRefQueryService billRefQueryService;
    @Autowired
    protected TaxCodeServiceImpl taxCodeService;
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;

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
    	SettlementUndoRedNotificationRequest newRequest = new SettlementUndoRedNotificationRequest();
    	BeanUtils.copyProperties(request, newRequest);
    	log.info("undoRedNotification request：{}，newRequest：{}", JSON.toJSONString(request), JSON.toJSONString(newRequest));
        final boolean lock = lockClient.tryLock("undo-red-notification:" + newRequest.getSettlementId(),
                () -> commSettlementService.applyDestroySettlementPreInvoice(newRequest.getSettlementId(), newRequest.getRevertRemark()),
                -1, 1);
        if (lock) {
            return R.ok("撤销红字信息表申请已提交成功");
        }
        return R.fail("正在处理中，请勿重复操作！");
    }

//    @PostMapping("/matched-invoice")
//    @ApiOperation("获取指定协议单已匹配的发票")
//    public R invoiceList(@RequestBody GetMatchInvoiceRequest request) {
//        final String usercode = UserUtil.getUser().getUsercode();
//        request.setSellerNo(usercode);
//        XFDeductionBusinessTypeEnum typeEnum;
//        switch (request.getType()) {
//            case 1:
//                typeEnum = XFDeductionBusinessTypeEnum.AGREEMENT_BILL;
//                break;
//            case 2:
//                typeEnum = XFDeductionBusinessTypeEnum.EPD_BILL;
//                break;
//            default:
//                throw new EnhanceRuntimeException("单据类型不正确，应为(协议单:1；EPD:2)");
//        }
//        final List<MatchedInvoiceListResponse> matchedInvoice = deductViewService.getMatchedInvoice(request, typeEnum);
//        return R.ok(matchedInvoice);
//    }
//
//    //TODO  可能去掉
//    @PostMapping("{settlementId}/matched-invoice")
//    @ApiOperation("保存手动调整的票单匹配关系")
//    public R saveInvoice(@PathVariable Long settlementId, @RequestBody InvoiceMatchedRequest request) {
//        //移除的发票要解除关系释放可用金额，添加的发票要建立关系减去占用金额
//        try {
//            invoiceService.saveSettlementMatchedInvoice(settlementId, request);
//        }catch (Exception e){
//            return R.fail(e.getMessage());
//        }
//        return R.ok();
//    }

    @PostMapping("details/tax-no")
    @ApiOperation("修改明细税编")
    public R saveInvoiceDetails(@RequestBody SettlementItemTaxNoUpdatedRequest request) {
        log.info("修改结算单明细税编入参：{}", JSON.toJSONString(request));
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
        List<SettlementItemResponse> respList = new ArrayList<>();
        SettlementItemResponse itemResponse;
//        BeanUtil.copyList(itemsBySettlementNo.getRecords(), respList, SettlementItemResponse.class);
        for (TXfSettlementItemEntity itemEntity : itemsBySettlementNo.getRecords()){
            itemResponse = new SettlementItemResponse();
            BeanUtil.copyProperties(itemEntity,itemResponse);
            //补充税编名称
            if (StringUtils.isNotBlank(itemEntity.getGoodsTaxNo())){
                log.info("结算单详情按税编补充税编名称请求：{}",itemEntity.getGoodsTaxNo());
                Either<String, List<TaxCodeBean>> taxCodeResult =  taxCodeService.searchTaxCode(itemEntity.getGoodsTaxNo(),null);
                log.info("结算单详情按税编补充税编名称回复：{}",JSON.toJSONString(taxCodeResult.get()));
                if(taxCodeResult.isRight()){
                    List<TaxCodeBean> taxCodeBeans = taxCodeResult.get();
                    if (CollectionUtils.isNotEmpty(taxCodeBeans)){
                        itemResponse.setTaxName(taxCodeBeans.get(0).getTaxName());
                    }
                }
            }
            respList.add(itemResponse);
        }

        // 红字信息填充
        billRefQueryService.fullSettlementItem(respList);
        final PageResult<SettlementItemResponse> result = PageResult.of(respList, itemsBySettlementNo.getTotal(), itemsBySettlementNo.getPages(), itemsBySettlementNo.getSize());
        return R.ok(result);
    }


    @ApiOperation(value = "推荐发票列表", notes = "", response = Response.class)
    @GetMapping(value = "invoice/recommended")
    public R recommend( @Valid InvoiceRecommendListRequest request) {
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final PageResult<InvoiceRecommendResponse> recommend = settlementService.recommend(request);
        return R.ok( recommend);
    }

    @ApiOperation(value = "红字结算单-子菜单", notes = "", response = Response.class)
    @GetMapping(value = "red/list")
    public R list(SettmentRedListRequest request) {
        final String userCode = UserUtil.getUser().getUsercode();
        request.setSellerNo(userCode);
        final PageResult<SettmentRedListResponse> recommend = settlementService.redList(request);
        return R.ok( recommend);
    }

    @ApiOperation("判断结算单是否历史，还是新流程生成 true-新")
    @GetMapping("/check/source/{id}")
    public R<Boolean> checkSettlementSource(@PathVariable("id") Long settlementId) {
        Asserts.isFalse(CommonUtil.isEdit(settlementId), "参数有误");
        return R.ok(deductBlueInvoiceService.checkSettlementSource(settlementId));
    }

    @ApiOperation("结算单撤销")
    @PostMapping("/destroy")
    public R<String> destroySettlement(@RequestBody SettlementCancelRequest request){
        log.info("结算单发起撤销新流程:{}", JSON.toJSONString(request));
        Asserts.isFalse(CommonUtil.isEdit(request.getSettlementId()), "参数异常");
        SettlementCancelRequest newRequest = new SettlementCancelRequest();
    	BeanUtils.copyProperties(request, newRequest);
    	log.info("undoRedNotification request：{}，newRequest：{}", JSON.toJSONString(request), JSON.toJSONString(newRequest));
        String key = "destroySettlement:" + newRequest.getSettlementId();
        return lockClient.tryLock(key, () -> commSettlementService.destroySettlementV2(newRequest), -1, 1);
    }

    @ApiOperation("结算单撤销轮训（红字信息表撤销中）")
    @GetMapping("/destroy/polling/{id}")
    public R<List<RedNotificationRollbackFailResult>> pollingDestroy(@PathVariable("id") Long id) {
        log.info("pollingDestroy params:{}", id);

        return commSettlementService.pollingDestroy(id);
    }

    @ApiOperation("结算单审核信息")
    @GetMapping("/approve/info/{id}")
    public R<SettlementApproveInfo> approveSettlementBefore(@PathVariable("id") Long settlementId) {
        log.info("approveSettlementBefore params:{}", settlementId);
        Asserts.isFalse(CommonUtil.isEdit(settlementId), "参数有误");

        return commSettlementService.approveSettlementBefore(settlementId);
    }

    @ApiOperation("结算单审核结果")
    @PostMapping("/approve")
    public R<String> approveSettlement(@RequestBody SettlementApproveRequest request) {
        log.info("approveSettlement params:[{}]", JSON.toJSONString(request));
        Long settlementId = request.getSettlementId();
        Asserts.isFalse(CommonUtil.isEdit(settlementId), "参数异常");

        TXfSettlementEntity settlementEntity = settlementService.getById(settlementId);
        Asserts.isNull(settlementEntity, "结算单不存在");
        Asserts.isFalse(Objects.equals(TXfSettlementStatusEnum.WAIT_CHECK.getCode(), settlementEntity.getSettlementStatus()), "结算单非待审核状态");

        String key = "approveSettlement:" + settlementId;
        Callable<R<String>> callable = () -> {
            long start = System.currentTimeMillis();
            if (SettlementApproveTypeEnum.BLUE_FLUSH.getCode().equals(settlementEntity.getApproveType())) {
                R<Boolean> r = commSettlementService.blueFlushApprove(settlementEntity, request);
                log.info("蓝冲审核结果:{},cost[{}]ms", r, System.currentTimeMillis() - start);
                return R.ok(null, NumberUtils.INTEGER_ONE == 1 ? "审核完成" : "驳回成功");
            }
            R<String> result;
            switch (request.getType()) {
                case 1:
                    // 审核通过，撤销结算单/重新拆票
                    result = commSettlementService.approveSuccessSettlement(settlementEntity);
                    break;
                case 0:
                    // 审核驳回，还原结算单到待开票状态，清空撤销原因
                    result = commSettlementService.approveRejectSettlement(settlementEntity, request.getRemark());
                    break;
                default:
                    throw new EnhanceRuntimeException("未知操作类型");
            }
            log.info("撤销/重拆审核结果:{}, cost[{}]ms", result, System.currentTimeMillis() - start);
            return result;
        };
        return lockClient.tryLock(key, callable, -1, 1);
    }
}
