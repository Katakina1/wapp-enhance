package com.xforceplus.wapp.modules.agreement.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.agreement.dto.AgreementOperateStepRequest;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.agreement.service.AgreementService;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.dto.NegativeAndOverDueSummary;
import com.xforceplus.wapp.modules.deduct.dto.*;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.service.CommAgreementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 17:17
 **/
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/agreement")
@Api(tags = "协议单API")
@Slf4j
public class AgreementController {
    @Autowired
    private DeductViewService deductService;
    @Autowired
    private AgreementService agreementService;
    @Autowired
    private AgreementBillService agreementBillService;
    @Autowired
    private CommAgreementService commAgreementService;
    @Autowired
    private LockClient lockClient;
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;
    @Autowired
    private StatementServiceImpl statementService;
    @Autowired
    private PreinvoiceService preinvoiceService;

    // 使用全局配置 PropertyEditorRegistrar
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//    }

    @GetMapping("summary")
    @ApiOperation(value = "页头统计")
    public R summary(DeductListRequest request){
        request.setTaxRate(null);
        //只显示未超期的数据列表
//        request.setOverdue(0);
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
        final List<SummaryResponse> summary = deductService.summary(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        return R.ok(summary);
    }

    @GetMapping
    @ApiOperation(value = "协议列表")
    public R agreements(DeductListRequest request) {
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
//        request.setOverdue(0);
        final PageResult<DeductListResponse> page = deductService.deductByPage(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);

        if (Objects.nonNull(request.getTaxRate())&& BigDecimal.ZERO.compareTo( request.getTaxRate())<=0) {
            final BigDecimal sum = deductService.sumDueAndNegative(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
            page.setExt(NegativeAndOverDueSummary.builder().negativeOverDueAmount(sum.toPlainString()).build());
        }

        return R.ok(page);
    }


    @PostMapping("settlement")
    @ApiOperation("生成结算单")
    public R makeSettlement(@RequestBody MakeSettlementRequest request){
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
//        final TXfSettlementEntity tXfSettlementEntity = deductService.makeSettlement(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        log.info("生成结算单请求参数:{}", JSON.toJSONString(request));
        //防并发锁
        String lockKey = request.getSellerNo()+request.getPurchaserNo()+request.getTaxRate().toPlainString();
        return lockClient.tryLock(lockKey, () ->{
            //生成结算单
            TXfSettlementEntity tXfSettlementEntity = agreementBillService.makeSettlementByManual(request);
            Map<String, Object> map=new HashMap<>();
            map.put("settlementId",tXfSettlementEntity.getId());
            map.put("settlementNo",tXfSettlementEntity.getSettlementNo());
            return R.ok(map, "结算单生成完毕");
        }, -1, 1);
    }

/*    @PostMapping("pre-settlement")
    @ApiOperation("结算单预匹配发票")
    public R preSettlement(@RequestBody PreMakeSettlementRequest request) {
        final String usercode = UserUtil.getUser().getUsercode();
        request.setSellerNo(usercode);
//        final List<MatchedInvoiceListResponse> matchedInvoice = deductService.getMatchedInvoice(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        log.info("结算单预匹配发票请求参数:{}", JSON.toJSONString(request));
        List<MatchedInvoiceListResponse> matchedInvoice = agreementBillService.preSettlementByManual(request);
        return R.ok(matchedInvoice);
    }*/

    @PostMapping("pre-settlement-new")
    @ApiOperation("结算单预匹配发票明细")
    public R<MatchedInvoiceDetailListResponse> preSettlementNew(@RequestBody PreMakeSettlementRequest request) {
        request.setSellerNo(UserUtil.getUser().getUsercode());
        log.info("结算单预匹配发票明细请求参数:{}", JSON.toJSONString(request));
        MatchedInvoiceDetailListResponse response = agreementBillService.preSettlementNewByManual(request);
        return R.ok(response);
    }

    @GetMapping(value = "invoice-detail/recommend")
    @ApiOperation(value = "推荐发票明细列表")
    public R<InvoiceRecommendDetailListResponse> recommend(@Valid InvoiceRecommendDetailListRequest request) {
      request.setSellerNo(UserUtil.getUser().getUsercode());
      log.info("推荐发票明细列表请求参数:{}", JSON.toJSONString(request));
      InvoiceRecommendDetailListResponse recommendResponse = agreementService.recommend(request);
      return R.ok(recommendResponse);
    }


    @PostMapping("cancel-settlement")
    public R cancelSettlement(@RequestBody SettlementCancelRequest request){
        log.info("结算单发起撤销:{}", JSON.toJSONString(request));
        Asserts.isFalse(CommonUtil.isEdit(request.getSettlementId()), "参数异常");

        String key = "cancelSettlement" + request.getSettlementId();
        lockClient.tryLock(key, () -> {
            if (deductBlueInvoiceService.checkSettlementSource(request.getSettlementId())) {
                log.info("新版组合结算单撤销:{}", request.getSettlementId());
                commAgreementService.destroyAgreementSettlementV2(request);
            } else {
                log.info("旧版组合结算单撤销:{}", request.getSettlementId());
                commAgreementService.destroyAgreementSettlement(request.getSettlementId());
            }
        }, -1, 1);
        return R.ok("结算单取消成功");
    }

    @ApiOperation(value = "协议业务单导出")
    @PostMapping("/export")
    public R<Object> export(@ApiParam(value = "协议业务单导出请求" ,required=true )@RequestBody DeductListRequest request) {
    	request.setSize(99999);
        request.setSellerNo(UserUtil.getUser().getUsercode());
    	request.setBusinessType(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
        if(deductService.export(request)){
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }else{
            return R.fail("导出任务添加失败");
        }
    }

    @ApiOperation("协议结算单组合生成流程处理")
    @PostMapping("/step")
    public R agreementSteps(@RequestBody @Validated AgreementOperateStepRequest request) {
        log.info("agreementSteps params:{}", JSON.toJSONString(request));

        LambdaQueryWrapper<TXfSettlementEntity> queryWrapper = Wrappers.lambdaQuery(TXfSettlementEntity.class).eq(TXfSettlementEntity::getSettlementNo, request.getSettlementNo());
        TXfSettlementEntity tXfSettlementEntity = statementService.getOne(queryWrapper);
        Asserts.isNull(tXfSettlementEntity, "结算单不存在");

        // 第二步
        final Integer theSecondStep = 2;
        R response;
        if (NumberUtils.INTEGER_ZERO.equals(request.getType())) {
            // 取消结算单
            response = commAgreementService.deleteAgreementSettlement(tXfSettlementEntity);
        } else if (NumberUtils.INTEGER_ONE.equals(request.getType())) {
            // 上一步
            if (theSecondStep.equals(request.getStep())) {
                // 取消结算单
                Asserts.isFalse(agreementService.checkSettlementWaitConfirm(tXfSettlementEntity),
                        "存在协议单已生成结算单，请刷新关闭当前页并刷新列表重试");
                response = commAgreementService.deleteAgreementSettlement(tXfSettlementEntity);
            } else {
                // 删除预制发票，回到待确认状态
                Asserts.isFalse(agreementService.checkSettlementWaitUploadInvoiceAndNoApplyRedNo(tXfSettlementEntity),
                        "存在协议单已生成结算单，请刷新关闭当前页并刷新列表重试");
                response = commAgreementService.backToWaitConfirm(tXfSettlementEntity);
            }
        } else {
            // 下一步
            if (theSecondStep.equals(request.getStep())) {
                // 拆票
                Asserts.isFalse(agreementService.checkSettlementWaitConfirm(tXfSettlementEntity),
                        "存在协议单已生成结算单，请刷新关闭当前页并刷新列表重试");
                response = preinvoiceService.splitPreInvoice(tXfSettlementEntity,false);
            } else {
                // 申请红字信息表
                Asserts.isFalse(agreementService.checkSettlementWaitUploadInvoiceAndNoApplyRedNo(tXfSettlementEntity),
                        "存在协议单已生成结算单，请刷新关闭当前页并刷新列表重试");
                response = preinvoiceService.redNotificationApply(tXfSettlementEntity);
            }
        }
        log.info("agreementSteps response:{}", JSON.toJSONString(response));
        return response;
    }

    @ApiOperation("协议结算单重新拆票（不自动申请红字）")
    @PostMapping("/splitAgain/{id}")
    public R splitAgain(@PathVariable("id") Long settlementId) {
        log.info("splitAgain params:{}", settlementId);
        Asserts.isFalse(CommonUtil.isEdit(settlementId), "参数有误");

        return commAgreementService.splitAgain(settlementId);
    }
}
