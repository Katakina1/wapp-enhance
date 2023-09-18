package com.xforceplus.wapp.modules.taxcode.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeReportRequest;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeReportService;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeReportEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "例外报告接口")
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/taxcode-exception-report")
public class TaxCodeReportController {

    @Autowired
    private TaxCodeReportService taxCodeReportService;


    @GetMapping("query")
    @ApiOperation("列外报告-riversand-税编")
    public R get(TaxCodeReportRequest request) {
        final PageResult<TXfTaxCodeReportEntity> page = taxCodeReportService.getPage(request);
        return R.ok(page);
    }

    @PatchMapping("/{status}")
    @ApiOperation("列外报告-riversand-税编-处理")
    public R get(@ApiParam("1处理、0撤销处理") @PathVariable("status") String status, @RequestBody TaxCodeReportRequest request) {
        final boolean page = taxCodeReportService.update(status, request);
        return page ? R.ok(page) : R.fail("处理失败");
    }

    @GetMapping("export")
    @ApiOperation(value = "税编例外报告导出")
    public R export(TaxCodeReportRequest request) {
        taxCodeReportService.export(request, ExceptionReportTypeEnum.TAXCODEREPORT);
        return R.ok("单据导出正在处理，请在消息中心");
    }


}
