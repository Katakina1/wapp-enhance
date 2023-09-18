package com.xforceplus.wapp.modules.statement.controller;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.validation.Valid;

import com.xforceplus.wapp.modules.statement.dto.*;
import com.xforceplus.wapp.modules.statement.vo.SettlementExportVo;
import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.enums.TXfAmountSplitRuleEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.statement.service.SettlementQueryService;
import com.xforceplus.wapp.modules.statement.models.BaseConfirm;
import com.xforceplus.wapp.modules.statement.models.BaseInformation;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.modules.statement.models.Settlement;
import com.xforceplus.wapp.modules.statement.models.SettlementCount;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import com.xforceplus.wapp.modules.sys.util.UserUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mashaopeng@xforceplus.com
 */
@Api(tags = "结算单管理")
@Slf4j
@RestController
@RequestMapping(EnhanceApiV1.BASE_PATH)
public class StatementController {
    private final StatementServiceImpl statementService;
    private final SettlementQueryService settlementQueryService;

    public StatementController(StatementServiceImpl statementService, SettlementQueryService settlementQueryService) {
        this.statementService = statementService;
        this.settlementQueryService = settlementQueryService;
    }

    @ApiOperation("结算单分页查询")
    @GetMapping("/settlement")
    public R<PageResult<Settlement>> getStatement(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                  @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                  @ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                  @RequestParam Integer type,
                                                  @ApiParam("结算单状态") @RequestParam(required = false) Integer settlementStatus,
                                                  @ApiParam("结算单号") @RequestParam(required = false) String settlementNo,
                                                  @ApiParam("购方编号") @RequestParam(required = false) String purchaserNo,
                                                  @ApiParam("发票类型（枚举值与结果实体枚举值相同）")
                                                  @RequestParam(required = false) String invoiceType,
                                                  @ApiParam("单据号（索赔、协议、EPD）")
                                                  @RequestParam(required = false) String businessNo,
                                                  @ApiParam("税率") @RequestParam(required = false) String taxRate) {
        long start = System.currentTimeMillis();
        if (StringUtils.isNotBlank(invoiceType) && !ValueEnum.isValid(InvoiceTypeEnum.class, invoiceType)) {
            return R.fail("发票类型不正确");
        }
        if (Objects.isNull(type) || !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        if (Objects.nonNull(settlementStatus) && !ValueEnum.isValid(TXfSettlementStatusEnum.class, settlementStatus)) {
            return R.fail("结算单状态不正确");
        }
        val page = statementService.page(UserUtil.getUser(), current, size, type,
                settlementStatus, settlementNo, purchaserNo, invoiceType, businessNo, taxRate);
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }
    
    @ApiOperation(value = "结算单导出")
    @PostMapping("/settlement/export")
	public R<Object> export(@Valid @RequestBody StatementRequest request) {
    	request.setSize(99999L);
        if (StringUtils.isNotBlank(request.getInvoiceType()) && !ValueEnum.isValid(InvoiceTypeEnum.class, request.getInvoiceType())) {
            return R.fail("发票类型不正确");
        }
        if (Objects.isNull(request.getType()) || !ValueEnum.isValid(ServiceTypeEnum.class, request.getType())) {
            return R.fail("查询类型不正确");
        }
        if (Objects.nonNull(request.getSettlementStatus()) && !ValueEnum.isValid(TXfSettlementStatusEnum.class, request.getSettlementStatus())) {
            return R.fail("结算单状态不正确");
        }
        if(statementService.export(request)){
            return R.ok("单据导出正在处理，请在消息中心");
        }else{
            return R.fail("导出任务添加失败");
        }
    }

//    @ApiOperation(value = "结算单导出")
//    @PostMapping("/export")
//    public R<Object> export(@ApiParam(value = "结算单导出请求" ,required=true )@RequestBody DeductListRequest request) {
//    	request.setSize(99999);
//    	request.setBusinessType(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue());
//        if(deductViewService.export(request)){
//            return R.ok("单据导出正在处理，请在消息中心");
//        }else{
//            return R.fail("导出任务添加失败");
//        }
//    }

    @ApiOperation("结算单分页查询")
    @GetMapping("/settlement/count")
    public R<Collection<SettlementCount>> getStatementCount(@ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                            @RequestParam Integer type,
                                                            @ApiParam("结算单号") @RequestParam(required = false) String settlementNo,
                                                            @ApiParam("结算单号") @RequestParam(required = false) String purchaserNo,
                                                            @ApiParam("发票类型") @RequestParam(required = false) String invoiceType,
                                                            @ApiParam("单据号（索赔、协议、EPD）")
                                                            @RequestParam(required = false) String businessNo,
                                                            @ApiParam("税率") @RequestParam(required = false) String taxRate) {
        long start = System.currentTimeMillis();
        if (StringUtils.isNotBlank(invoiceType) && !ValueEnum.isValid(InvoiceTypeEnum.class, invoiceType)) {
            return R.fail("发票类型不正确");
        }
        if (Objects.isNull(type) || !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        val count = statementService.count(UserUtil.getUser(), type, settlementNo, purchaserNo,
                invoiceType, businessNo, taxRate);
        //全部统计
        count.add(new SettlementCount("0", count.stream().mapToInt(SettlementCount::getTotal).sum()));
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(count);
    }

    @ApiOperation("结算单分页查询")
    @GetMapping("/walmart/settlement")
    public R<PageResult<Settlement>> getWalmartStatement(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                         @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                         @ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                         @RequestParam Integer type,
                                                         @ApiParam("结算单状态") @RequestParam(required = false) Integer settlementStatus,
                                                         @ApiParam("结算单号") @RequestParam(required = false) String settlementNo,
                                                         @ApiParam("购方编号") @RequestParam(required = false) String purchaserNo,
                                                         @ApiParam("发票类型（枚举值与结果实体枚举值相同）")
                                                         @RequestParam(required = false) String invoiceType,
                                                         @ApiParam("单据号（索赔、协议、EPD）")
                                                         @RequestParam(required = false) String businessNo,
                                                         @ApiParam("税率") @RequestParam(required = false) String taxRate) {
        long start = System.currentTimeMillis();
        if (StringUtils.isNotBlank(invoiceType) && !ValueEnum.isValid(InvoiceTypeEnum.class, invoiceType)) {
            return R.fail("发票类型不正确");
        }
        if (Objects.isNull(type) || !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        if (Objects.nonNull(settlementStatus) && !ValueEnum.isValid(TXfSettlementStatusEnum.class, settlementStatus)) {
            return R.fail("结算单状态不正确");
        }
        val page = statementService.page(null, current, size, type,
                settlementStatus, settlementNo, purchaserNo, invoiceType, businessNo, taxRate);
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("结算单分页查询")
    @GetMapping("/walmart/settlement/count")
    public R<Collection<SettlementCount>> getWalmartStatementCount(@ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                                   @RequestParam Integer type,
                                                                   @ApiParam("结算单号") @RequestParam(required = false) String settlementNo,
                                                                   @ApiParam("结算单号") @RequestParam(required = false) String purchaserNo,
                                                                   @ApiParam("发票类型") @RequestParam(required = false) String invoiceType,
                                                                   @ApiParam("单据号（索赔、协议、EPD）")
                                                                   @RequestParam(required = false) String businessNo,
                                                                   @ApiParam("税率") @RequestParam(required = false) String taxRate) {
        long start = System.currentTimeMillis();
        if (StringUtils.isNotBlank(invoiceType) && !ValueEnum.isValid(InvoiceTypeEnum.class, invoiceType)) {
            return R.fail("发票类型不正确");
        }
        if (Objects.isNull(type) || !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        val count = statementService.count(null, type, settlementNo, purchaserNo,
                invoiceType, businessNo, taxRate);
        //全部统计
        count.add(new SettlementCount("0", count.stream().mapToInt(SettlementCount::getTotal).sum()));
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(count);
    }

    @ApiOperation("结算单详情-开票预览-待开票列表")
    @GetMapping("/settlement/invoice/{settlementNo}")
    public R<PageResult<PreInvoice>> awaitingInvoices(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                      @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                      @ApiParam(value = "结算单号", required = true) @PathVariable String settlementNo) {
        long start = System.currentTimeMillis();
        val page = statementService.awaitingInvoicePage(current, size, settlementNo);
        log.info("待开票列表查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("结算单主信息-公司信息")
    @GetMapping("/settlement/company/{settlementNo}")
    public R<Settlement> companyMessage(@ApiParam(value = "结算单号", required = true) @PathVariable String settlementNo) {
        long start = System.currentTimeMillis();
        val company = statementService.companyMessage(settlementNo);
        settlementQueryService.setQueryTab(company);
        log.info("结算单主信息-公司信息查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(company);
    }

    @ApiOperation("结算单详情-开票预览-待开票详情")
    @GetMapping("/settlement/invoice/detail/{invoiceId}")
    public R<PreInvoice> preInvoiceDetail(@ApiParam(value = "发票ID", required = true) @PathVariable Long invoiceId) {
        long start = System.currentTimeMillis();
        val invoice = statementService.preInvoice(invoiceId);
        log.info("待开票详情查询,耗时:{}ms", System.currentTimeMillis() - start);
        return invoice.map(R::ok).orElseGet(() -> R.fail("未查询到数据"));
    }

    @ApiOperation("结算单详情-基本信息")
    @GetMapping("/settlement/base/information/{settlementNo}")
    public R<PageResult<? extends BaseInformation>> baseInformation(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                                    @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                                    @ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                                    @RequestParam Integer type,
                                                                    @ApiParam(value = "结算单号", required = true) @PathVariable String settlementNo) {
        long start = System.currentTimeMillis();
        if (Objects.isNull(type) || !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        val typeMap = ImmutableMap
                .<Integer, Function3<Long, Long, String, Tuple2<List<? extends BaseInformation>, Page<?>>>>builder()
                .put(ServiceTypeEnum.CLAIM.getValue(), statementService::baseInformationClaimPage)
                .put(ServiceTypeEnum.AGREEMENT.getValue(), statementService::baseInformationAgreementPage)
                .put(ServiceTypeEnum.EPD.getValue(), statementService::baseInformationAgreementPage)
                .build();
        val page = typeMap
                .getOrDefault(type, (s, p, n) -> Tuple.of(Lists.newArrayList(), new Page<>(current, size)))
                .apply(current, size, settlementNo);
        log.info("基本信息列表查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("结算单确认列表")
    @GetMapping("/settlement/confirm/{type}/{settlementNo}")
    public R<List<? extends BaseConfirm>> settlementConfirmList(@ApiParam(value = "结算单号", required = true)
                                                                @PathVariable String settlementNo,
                                                                @ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                                @PathVariable Integer type) {
        long start = System.currentTimeMillis();
        if (Objects.isNull(type) || !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        val typeMap = ImmutableMap
                .<Integer, Function<String, List<? extends BaseConfirm>>>builder()
                .put(ServiceTypeEnum.CLAIM.getValue(), statementService::claimConfirmItem)
                .put(ServiceTypeEnum.AGREEMENT.getValue(), statementService::confirmItemList)
                .put(ServiceTypeEnum.EPD.getValue(), statementService::confirmItemList)
                .build();
        val confirm = typeMap.getOrDefault(type, (n) -> Lists.newArrayList()).apply(settlementNo);
        log.info("待开票详情查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(confirm);
    }

    @ApiOperation("结算单列表确认")
    @PostMapping("/settlement/confirm/{confirmType}")
    public R<Boolean> settlementConfirm(@ApiParam(value = "确认类型 1.单价不变、3.数量不变", required = true)
                                        @PathVariable Integer confirmType,
                                        @Validated @RequestBody ConfirmDto dto) {
        if (!Integer.valueOf(2).equals(dto.getStatementType())){
            //非协议单，需校验明细ID列表不为空
            if (CollectionUtils.isEmpty(dto.getIds())){
                return R.fail("明细ID列表不能为空");
            }
        }
        long start = System.currentTimeMillis();
        val r = ValueEnum.getEnumByValue(TXfAmountSplitRuleEnum.class, confirmType)
                .map(it -> R.ok(statementService.confirmItem(dto.getSettlementNo(), dto.getSellerNo(), dto.getIds(), it)))
                .orElseGet(() -> R.fail("确认类型不正确"));
        log.info("待开票详情查询,耗时:{}ms", System.currentTimeMillis() - start);
        return r;
    }


    /**
     * 以下post方法由原来的get 改为了post, 考虑查询参数比较多，后期可能会更加多，就没必要遵循 restful了
     */
    @ApiOperation("结算单分页查询(供应商侧)")
    @PostMapping("/settlement/list")
    public R<PageResult<Settlement>> listStatement(@RequestBody @Validated QuerySettlementListRequest request) {
        long start = System.currentTimeMillis();

        PageResult<Settlement> settlementPageResult = settlementQueryService.listSettlement(UserUtil.getUser(), request);
        log.info("结算单分页查询(供应商侧),耗时: {} ms", System.currentTimeMillis() - start);
        return R.ok(settlementPageResult);
    }

    /**
     *
     * 红字结算单 索赔/协议导出
     */
    @ApiOperation("结算单分页查询(供应商侧)")
    @PostMapping("/settlement/exportList")
    public R exportList(@RequestBody SettlementExportVo request) {
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 2000) {
                return R.fail("最大选中导出数量不能超过2000条数据");
            }

            List<Settlement> resultList = settlementQueryService.getByBatchIds(request.getIncludes());
            settlementQueryService.export(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {
            UserEntity user = UserUtil.getUser();
            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }
            QuerySettlementListRequest vo = request.getExcludes();
            Long count = settlementQueryService.queryCount(user, vo);
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }
            vo.setPageNo(0);
            vo.setPageSize(10000);
            List<Settlement> resultList = settlementQueryService.listSettlement(user, vo).getRows();
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(resultList)) {
                settlementQueryService.export(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }

    @ApiOperation("结算单tab 列表(供应商侧)")
    @PostMapping("/settlement/tabs")
    public R<List<QuerySettlementTabResponse>> statementTabs(@RequestBody @Validated QuerySettlementListRequest request) {
        long start = System.currentTimeMillis();

        List<QuerySettlementTabResponse> tabResponses = settlementQueryService.tabCount(UserUtil.getUser(), request);
        log.info("结算单tab 列表(供应商侧),耗时: {} ms", System.currentTimeMillis() - start);
        return R.ok(tabResponses);
    }

    @ApiOperation("结算单分页查询(沃尔玛侧)")
    @PostMapping("/walmart/settlement/list")
    public R<PageResult<Settlement>> walmartListStatement(@RequestBody @Validated QuerySettlementListRequest request) {
        long start = System.currentTimeMillis();

        PageResult<Settlement> settlementPageResult = settlementQueryService.listSettlement(null, request);
        log.info("结算单分页查询(沃尔玛侧),耗时: {} ms", System.currentTimeMillis() - start);
        return R.ok(settlementPageResult);
    }

    @ApiOperation("结算单tab 列表(沃尔玛侧)")
    @PostMapping("/walmart/settlement/tabs")
    public R<List<QuerySettlementTabResponse>> walmartStatementTabs(@RequestBody @Validated QuerySettlementListRequest request) {
        long start = System.currentTimeMillis();

        List<QuerySettlementTabResponse> tabResponses = settlementQueryService.tabCount(null, request);
        log.info("结算单tab 列表(沃尔玛侧),耗时: {} ms", System.currentTimeMillis() - start);
        return R.ok(tabResponses);
    }
}
