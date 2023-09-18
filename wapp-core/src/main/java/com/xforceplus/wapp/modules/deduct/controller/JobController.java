package com.xforceplus.wapp.modules.deduct.controller;

import com.xforceplus.wapp.modules.syslog.schedule.SysLogClearExpireScheduler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.deduct.schedule.AgreementDeductScheduler;
import com.xforceplus.wapp.modules.deduct.schedule.ClaimBlueInvoiceScheduler;
import com.xforceplus.wapp.modules.deduct.schedule.ClaimDeductScheduler;
import com.xforceplus.wapp.modules.deduct.schedule.ClaimDeductTaxCodeScheduler;
import com.xforceplus.wapp.modules.deduct.schedule.ClaimSettlementScheduler;
import com.xforceplus.wapp.modules.deduct.schedule.EPDDeductScheduler;
import com.xforceplus.wapp.modules.settlement.schedule.SettlementOverDueScheduler;
import com.xforceplus.wapp.modules.settlement.schedule.SettlementScheduler;
import com.xforceplus.wapp.modules.settlement.schedule.SettlementTaxCodeScheduler;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:22
 **/
@Api(tags = "deduct")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +"/job")
@Slf4j
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
    @Autowired
    private SysLogClearExpireScheduler sysLogClearExpireScheduler;


    @Autowired
    private ClaimBlueInvoiceScheduler claimBlueInvoiceScheduler;
    @ApiOperation(value = "手动执行job")
    @GetMapping(value = "/run/{name}")
    public R<Object> doJob(@ApiParam(value = "执行job" ,required=true )@PathVariable("name") String name) {
    	log.info("执行job ==================================== {}",name);
        if (StringUtils.equals("agreementDeductScheduler", name)) {
            agreementDeductScheduler.AgreementDeductDeal();
            return R.ok("agreementDeductScheduler成功"); 
        }
        if (StringUtils.equals("claimDeductScheduler", name)) {
            claimDeductScheduler.claimDeductDeal();
            return R.ok("claimDeductScheduler成功"); 
        }
        if (StringUtils.equals("claimDeductTaxCodeScheduler", name)) {
            claimDeductTaxCodeScheduler.matchTaxCode();
            return R.ok("claimDeductTaxCodeScheduler成功"); 
        }
        if (StringUtils.equals("claimSettlementScheduler", name)) {
            claimSettlementScheduler.claimMergeSettlementDeductDeal();
            return R.ok("claimSettlementScheduler成功"); 
        }
        if (StringUtils.equals("epdDeductScheduler", name)) {
            epdDeductScheduler.EPDDeductDeal();
            return R.ok("epdDeductScheduler成功"); 
        }
        if (StringUtils.equals("settlementOverDueScheduler", name)) {
            settlementOverDueScheduler.settlementAutoConfirm();
            return R.ok("settlementOverDueScheduler成功"); 
        }
        if (StringUtils.equals("settlementScheduler", name)) {
            settlementScheduler.settlementSplit();
            return R.ok("settlementScheduler成功"); 
        }
        if (StringUtils.equals("settlementTaxCodeScheduler", name)) {
            settlementTaxCodeScheduler.settlementFixTaxCode();
            return R.ok("settlementTaxCodeScheduler成功"); 
        }
        if(StringUtils.equals("claimBlueInvoiceScheduler", name)){
            claimBlueInvoiceScheduler.claimBlueInfoDeal();
            return R.ok("claimBlueInvoiceScheduler成功"); 
        }
        if(StringUtils.equals("sysLogClearExpireScheduler", name)){
            sysLogClearExpireScheduler.sysLogClearExpire();
            return R.ok("sysLogClearExpireScheduler成功");
        }
        return R.fail(name+":不存在");
    }


}
