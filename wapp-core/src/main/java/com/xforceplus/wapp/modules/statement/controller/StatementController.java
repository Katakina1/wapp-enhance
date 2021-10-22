package com.xforceplus.wapp.modules.statement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.statement.models.*;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author mashaopeng@xforceplus.com
 */
@Api(tags = "结算单管理")
@Slf4j
@RestController
@RequestMapping(EnhanceApiV1.BASE_PATH)
public class StatementController {
    private final StatementServiceImpl statementService;

    public StatementController(StatementServiceImpl statementService) {
        this.statementService = statementService;
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
        val page = statementService.page(current, size, type,
                settlementStatus, settlementNo, purchaserNo, invoiceType, businessNo, taxRate);
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

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
        val count = statementService.count(type, settlementNo, purchaserNo,
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

    @ApiOperation("结算单确认")
    @GetMapping("/settlement/confirm/{settlementNo}")
    public R<List<? extends BaseConfirm>> settlementConfirm(@ApiParam(value = "结算单号", required = true)
                                                            @PathVariable String settlementNo,
                                                            @ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                            @RequestParam Integer type) {
        long start = System.currentTimeMillis();
        if (Objects.isNull(type) || !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        val typeMap = ImmutableMap
                .<Integer, Function<String, List<? extends BaseConfirm>>>builder()
                .put(ServiceTypeEnum.CLAIM.getValue(), statementService::claimConfirmItem)
                .put(ServiceTypeEnum.AGREEMENT.getValue(), statementService::confirmItem)
                .put(ServiceTypeEnum.EPD.getValue(), statementService::confirmItem)
                .build();
        val confirm = typeMap.getOrDefault(type, (n) -> Lists.newArrayList()).apply(settlementNo);
        log.info("待开票详情查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(confirm);
    }

}
