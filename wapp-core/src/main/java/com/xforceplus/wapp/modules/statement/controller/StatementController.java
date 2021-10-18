package com.xforceplus.wapp.modules.statement.controller;

import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.statement.models.Statement;
import com.xforceplus.wapp.modules.statement.models.StatementCount;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
                                                 @ApiParam(value = "tab 1.索赔、2.协议、3.EPD", required = true)
                                                 @RequestParam Integer type,
                                                 @ApiParam("结算单状态") @RequestParam(required = false) List<String> settlementStatus,
                                                 @ApiParam("结算单号") @RequestParam(required = false) String settlementNo,
                                                 @ApiParam("结算单号") @RequestParam(required = false) String purchaserNo,
                                                 @ApiParam("发票类型（枚举值与结果实体枚举值相同）")
                                                 @RequestParam(required = false) String invoiceType,
                                                 @ApiParam("单据号（索赔、协议、EPD）")
                                                 @RequestParam(required = false) String businessNo,
                                                 @ApiParam("税率") @RequestParam(required = false) String taxRate) {
        long start = System.currentTimeMillis();

        val page = statementService.page(current, size, type,
                settlementStatus, settlementNo, purchaserNo, invoiceType, businessNo, taxRate);
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(null);
    }

    @ApiOperation("结算单分页查询")
    @GetMapping("/statement/count")
    public R<List<StatementCount>> getStatementCount(@ApiParam(value = "tab 1.索赔、2.协议、3.EPD", required = true)
                                                     @RequestParam Integer type,
                                                     @ApiParam("结算单号") @RequestParam(required = false) String settlementNo,
                                                     @ApiParam("结算单号") @RequestParam(required = false) String purchaserNo,
                                                     @ApiParam("发票类型") @RequestParam(required = false) String invoiceType,
                                                     @ApiParam("单据号（索赔、协议、EPD）")
                                                     @RequestParam(required = false) String businessNo,
                                                     @ApiParam("税率") @RequestParam(required = false) String taxRate) {
        long start = System.currentTimeMillis();
        val count = statementService.count(type, settlementNo, purchaserNo,
                invoiceType, businessNo, taxRate);
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(count);
    }
}
