package com.xforceplus.wapp.modules.deduct.controller;

import com.xforceplus.wapp.annotation.AuthIgnore;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.modules.deduct.dto.DeductPreInvoicePushRequest;
import com.xforceplus.wapp.modules.deduct.service.DeductPreInvoiceService;
import com.xforceplus.wapp.service.CommonMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-19 10:30
 **/
@Api(tags = "deduct手工测试接口")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH + "/deduct/mock")
@Slf4j
public class DeductTestController {
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private DeductPreInvoiceService deductPreInvoiceService;

    @PostMapping("deduct-pre-invoice")
    @AuthIgnore
    @ApiOperation("手工mock推预制发票/送红字信息表事件")
    public R pushDeductPreInvoice(DeductRedNotificationEventEnum event, @RequestBody DeductRedNotificationEvent.DeductRedNotificationModel body) {
        commonMessageService.sendMessage(event, body);
        return R.ok();
    }

    @PostMapping("/deduct-pre-invoice/list")
    @AuthIgnore
    @ApiOperation("手工mock推预制发票/送红字信息表事件")
    public R pushDeductPreInvoiceList(DeductRedNotificationEventEnum event, @RequestBody List<DeductRedNotificationEvent.DeductRedNotificationModel> body) {
        body.forEach(model -> commonMessageService.sendMessage(event, model));
        return R.ok();
    }

    @PostMapping("/deduct-pre-invoice/settlement")
    @AuthIgnore
    @ApiOperation("结算单下预制发票红字信息表中间表数据修复")
    public R pushDeductPreInvoiceBySettlement(@RequestBody DeductPreInvoicePushRequest request) {
        Asserts.isEmpty(request.getSettlementNoList(), "结算单号不能为空");

        request.getSettlementNoList().forEach(settlementNo -> {
            try {
                deductPreInvoiceService.pushBySettlementNo(settlementNo);
            } catch (Exception e) {
                log.error("push error:{}", settlementNo, e);
            }
        });
        return R.ok();
    }
}
