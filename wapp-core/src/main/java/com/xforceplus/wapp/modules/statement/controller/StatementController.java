package com.xforceplus.wapp.modules.statement.controller;

import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.statement.models.Statement;
import com.xforceplus.wapp.modules.statement.models.StatementCount;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Objects;

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
    @GetMapping("/statement")
    public R<PageResult<Statement>> getStatement(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                 @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                 @ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
                                                 @RequestParam Integer type,
                                                 @ApiParam("结算单状态") @RequestParam(required = false) Integer settlementStatus,
                                                 @ApiParam("结算单号") @RequestParam(required = false) String settlementNo,
                                                 @ApiParam("结算单号") @RequestParam(required = false) String purchaserNo,
                                                 @ApiParam("发票类型（枚举值与结果实体枚举值相同）")
                                                 @RequestParam(required = false) String invoiceType,
                                                 @ApiParam("单据号（索赔、协议、EPD）")
                                                 @RequestParam(required = false) String businessNo,
                                                 @ApiParam("税率") @RequestParam(required = false) String taxRate) {
        long start = System.currentTimeMillis();
        if (StringUtils.isNotBlank(invoiceType) && !ValueEnum.isValid(InvoiceTypeEnum.class, invoiceType)) {
            return R.fail("发票类型不正确");
        }
        if (Objects.nonNull(type) && !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
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
    @GetMapping("/statement/count")
    public R<Collection<StatementCount>> getStatementCount(@ApiParam(value = "查询类型 1.索赔、2.协议、3.EPD", required = true)
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
        if (Objects.nonNull(type) && !ValueEnum.isValid(ServiceTypeEnum.class, type)) {
            return R.fail("查询类型不正确");
        }
        val count = statementService.count(type, settlementNo, purchaserNo,
                invoiceType, businessNo, taxRate);
        //全部统计
        count.add(new StatementCount("0", count.stream().mapToInt(StatementCount::getTotal).sum()));
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(count);
    }
}
