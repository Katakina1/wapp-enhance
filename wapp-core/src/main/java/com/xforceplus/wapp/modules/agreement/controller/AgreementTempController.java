package com.xforceplus.wapp.modules.agreement.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.agreement.service.AgreementTempService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/agreement-temp")
@Api(tags = "deduct手工测试接口")
@Slf4j
public class AgreementTempController {
    @Autowired
    private AgreementTempService agreementTempService;


    @ApiOperation(value = "修复协议单蓝票明细占用-结算单明细ID为空")
    @PostMapping("/repairSettlementItemId")
    public R export() {
        agreementTempService.repairSettlementItemId();
        return R.ok();
    }



}
