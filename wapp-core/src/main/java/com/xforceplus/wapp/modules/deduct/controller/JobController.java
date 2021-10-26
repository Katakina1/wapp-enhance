package com.xforceplus.wapp.modules.deduct.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.deduct.schedule.*;
import com.xforceplus.wapp.modules.settlement.schedule.SettlementOverDueScheduler;
import com.xforceplus.wapp.modules.settlement.schedule.SettlementScheduler;
import com.xforceplus.wapp.modules.settlement.schedule.SettlementTaxCodeScheduler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:22
 **/
@Api(tags = "deduct")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +"/job")
public class JobController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AgreementDeductScheduler agreementDeductScheduler;
    @Autowired
    private ClaimDeductScheduler claimDeductScheduler;
    @Autowired
    private ClaimDeductTaxCodeScheduler claimDeductTaxCodeScheduler;
    @Autowired
    private ClaimSettlementScheduler claimSettlementScheduler;
    @Autowired
    private  EPDDeductScheduler epdDeductScheduler;
    @Autowired
    private SettlementOverDueScheduler settlementOverDueScheduler;
    @Autowired
    private SettlementScheduler settlementScheduler;
    @Autowired
    private  SettlementTaxCodeScheduler settlementTaxCodeScheduler;
    @ApiOperation(value = "手动执行job")
    @GetMapping(value = "/run/{name}")
    public R doJob(@ApiParam(value = "执行job" ,required=true )@PathVariable("name") String name) {
        if (StringUtils.isEmpty(name)) {
            return    R.ok("");
        }
        if (StringUtils.equals("agreementDeductScheduler", name)) {
            agreementDeductScheduler.AgreementDeductDeal();
        }
        if (StringUtils.equals("claimDeductScheduler", name)) {
            claimDeductScheduler.claimDeductDeal();
        }
        if (StringUtils.equals("agreementDeductScheduler", name)) {
            agreementDeductScheduler.AgreementDeductDeal();
        }
        if (StringUtils.equals("claimDeductTaxCodeScheduler", name)) {
            claimDeductTaxCodeScheduler.matchTaxCode();
        }
        if (StringUtils.equals("claimSettlementScheduler", name)) {
            claimSettlementScheduler.claimMergeSettlementDeductDeal();
        }
        if (StringUtils.equals("epdDeductScheduler", name)) {
            epdDeductScheduler.EPDDeductDeal();

        }
        if (StringUtils.equals("settlementOverDueScheduler", name)) {
            settlementOverDueScheduler.settlementAutoConfirm();
        }
        if (StringUtils.equals("settlementScheduler", name)) {
            settlementScheduler.settlementSplit();
        }
        if (StringUtils.equals("settlementTaxCodeScheduler", name)) {
            settlementTaxCodeScheduler.settlementFixTaxCode();
        }
        return R.ok("");

    }


}
