package com.xforceplus.wapp.modules.exceptionreport.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportDto;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ReMatchRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ReportCodeResponse;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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

    @Autowired
    private ExceptionReportMapper exceptionReportMapper;

    @Autowired
    private ClaimBillService claimBillService;

    @Value("${wapp.bill.export.limit:99999}")
    Integer billExportLimit;

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
    public R getCode(@RequestParam(required = false) String type ) {
        final List<ReportCodeResponse> reportCodeResponses ;
        final List<ExceptionReportCodeEnum> values ;
        if (Objects.equals(type,"c")){
            values = ExceptionReportCodeEnum.getClaimCodes();
        }else {
            values = ExceptionReportCodeEnum.getAgreementOrEpdCodes();
        }

        reportCodeResponses = exceptionReportMapper.toReportCode(values);
        return R.ok(reportCodeResponses);
    }


    @PostMapping("re-match")
    @ApiOperation(value = "重新匹配")
    public R reMatch(@RequestBody ReMatchRequest request) {
        final List<Long> ids = request.getIds();
        if (CollectionUtils.isEmpty(ids)) {
            throw new EnhanceRuntimeException("请选择需要重新匹配的例外报告");
        }

        exceptionReportService.reMatchTaxCode(request);
        return R.ok();
    }

    @GetMapping("agreement/export")
    @ApiOperation(value = "协议单导出")
    public R agreementExport(ExceptionReportRequest request) {
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.AGREEMENT);
        if (page.getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        exceptionReportService.export(request, ExceptionReportTypeEnum.AGREEMENT);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @GetMapping("claim/export")
    @ApiOperation(value = "索赔单导出")
    public R claimExport(ExceptionReportRequest request) {
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.CLAIM);
        if (page.getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        exceptionReportService.export(request, ExceptionReportTypeEnum.CLAIM);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @GetMapping("epd/export")
    @ApiOperation(value = "例外报告EPD导出")
    public R epdExport(ExceptionReportRequest request) {
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.EPD);
        if (page.getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        exceptionReportService.export(request, ExceptionReportTypeEnum.EPD);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    private PageResult<ExceptionReportDto> toPageResult(Page<TXfExceptionReportEntity> page) {
        final List<ExceptionReportDto> exceptionReportDtos = exceptionReportMapper.toDto(page.getRecords());
        return PageResult.of(exceptionReportDtos, page.getTotal(), page.getPages(), page.getSize());
    }

    @PostMapping("/import")
    @ApiOperation(value = "例外报告导入")
    public R exceptionReportImport(@RequestParam("file") MultipartFile file) {
    	return exceptionReportService.exceptionReportImport(file);
    }
    @PostMapping("/update")
    @ApiOperation(value = "修改例外报告")
    public R update(@RequestBody ExceptionReportDto exceptionReportDto) {
    	return exceptionReportService.update(exceptionReportDto);
    }
    
}
