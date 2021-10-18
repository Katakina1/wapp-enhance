package com.xforceplus.wapp.modules.exceptionreport.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ReMatchRequest;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 16:19
 **/
@Api(tags = "例外报告接口")
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/exception-report")
public class ExceptionReportController {

    @Autowired
    private ExceptionReportService exceptionReportService;

    @GetMapping("claim")
    @ApiOperation("列外报告-索赔单")
    public R getClaim(ExceptionReportRequest request) {
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.CLAIM);
        return R.ok(toPageResult(page));
    }

    @GetMapping("epd")
    @ApiOperation("列外报告-EPD单")
    public R getEPD(ExceptionReportRequest request) {
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.EPD);
        return R.ok(toPageResult(page));
    }

    @GetMapping("agreement")
    @ApiOperation("列外报告-协议单")
    public R getAgreement(ExceptionReportRequest request) {
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.AGREEMENT);
        return R.ok(toPageResult(page));
    }

    @GetMapping("codes")
    @ApiOperation(value = "例外报告说明列表")
    public R getCode() {
        final ExceptionReportCodeEnum[] values = ExceptionReportCodeEnum.values();
        return R.ok(values);
    }

    @PostMapping("re-match")
    @ApiOperation(value = "重新匹配")
    public R reMatch(@RequestBody ReMatchRequest request) {
        final List<Long> ids = request.getIds();
        if (CollectionUtils.isEmpty(ids)) {
            throw new EnhanceRuntimeException("请选择需要重新匹配的例外报告");
        }
        return R.ok();
    }

    @GetMapping("agreement/export")
    @ApiOperation(value = "协议单导出")
    public R agreementExport(ExceptionReportRequest request) {
        exceptionReportService.export(request, ExceptionReportTypeEnum.AGREEMENT);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @GetMapping("claim/export")
    @ApiOperation(value = "索赔单导出")
    public R claimExport(ExceptionReportRequest request) {
        exceptionReportService.export(request, ExceptionReportTypeEnum.CLAIM);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @GetMapping("epd/export")
    @ApiOperation(value = "EPD单导出")
    public R epdExport(ExceptionReportRequest request) {
        exceptionReportService.export(request, ExceptionReportTypeEnum.EPD);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    private static <T> PageResult<T> toPageResult(Page<T> page) {
        return PageResult.of(page.getRecords(), page.getTotal(), page.getPages(), page.getSize());
    }


}
