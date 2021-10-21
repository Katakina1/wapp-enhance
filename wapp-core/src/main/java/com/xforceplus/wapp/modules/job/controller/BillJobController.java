package com.xforceplus.wapp.modules.job.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.job.executor.AgreementBillJobExecutor;
import com.xforceplus.wapp.modules.job.executor.ClaimBillJobExecutor;
import com.xforceplus.wapp.modules.job.executor.EpdBillJobExecutor;
import com.xforceplus.wapp.modules.job.generator.AgreementBillJobGenerator;
import com.xforceplus.wapp.modules.job.generator.ClaimBillJobGenerator;
import com.xforceplus.wapp.modules.job.generator.EpdBillJobGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: wapp-generator
 * @description: bill job controler
 * @author: Kenny Wong
 * @create: 2021-10-21 14:19
 **/
@Slf4j
@RestController
@Api(tags = "单据任务管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/bill/jobs")
public class BillJobController {

    @Autowired
    private AgreementBillJobGenerator agreementBillJobGenerator;
    @Autowired
    private ClaimBillJobGenerator claimBillJobGenerator;
    @Autowired
    private EpdBillJobGenerator epdBillJobGenerator;
    @Autowired
    private AgreementBillJobExecutor agreementBillJobExecutor;
    @Autowired
    private ClaimBillJobExecutor claimBillJobExecutor;
    @Autowired
    private EpdBillJobExecutor epdBillJobExecutor;

    private static final String DEFAULT_RESPONSE = "已安排执行，请关注日志";

    @ApiOperation("立即触发协议单文件扫描任务")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @PostMapping("/agreement/scan")
    public R<String> scanAgreementFiles(@RequestBody String anyting) {
        agreementBillJobGenerator.generate();
        return R.ok(DEFAULT_RESPONSE);
    }

    @ApiOperation("立即触发索赔单文件扫描任务")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @PostMapping("/claim/scan")
    public R<String> scanClaimFiles(@RequestBody String anyting) {
        claimBillJobGenerator.generate();
        return R.ok(DEFAULT_RESPONSE);
    }

    @ApiOperation("立即触发EPD单文件扫描任务")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @PostMapping("/epd/scan")
    public R<String> scanEpdFiles(@RequestBody String anyting) {
        epdBillJobGenerator.generate();
        return R.ok(DEFAULT_RESPONSE);
    }

    @ApiOperation("立即触发协议单执行任务")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @PostMapping("/agreement/execute")
    public R<String> executeAgreementJobs(@RequestBody String anyting) {
        agreementBillJobExecutor.execute();
        return R.ok(DEFAULT_RESPONSE);
    }

    @ApiOperation("立即触发索赔单执行任务")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @PostMapping("/claim/execute")
    public R<String> executeClaimJobs(@RequestBody String anyting) {
        claimBillJobExecutor.execute();
        return R.ok(DEFAULT_RESPONSE);
    }

    @ApiOperation("立即触发EPD单执行任务")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @PostMapping("/epd/execute")
    public R<String> executeEpdJobs(@RequestBody String anyting) {
        epdBillJobExecutor.execute();
        return R.ok(DEFAULT_RESPONSE);
    }
}
