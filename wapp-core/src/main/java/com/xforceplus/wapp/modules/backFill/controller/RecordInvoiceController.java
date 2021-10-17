package com.xforceplus.wapp.modules.backFill.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.backFill.model.BackFillCommitVerifyRequest;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by SunShiyong on 2021/10/16.
 */
@RestController
@RequestMapping(value = "/invoice")
public class RecordInvoiceController extends AbstractController {

    @Autowired
    RecordInvoiceService recordInvoiceService;

    @ApiOperation(value = "底账发票列表", notes = "", response = Response.class, authorizations = {
            @Authorization(value = "X-Access-Token")},tags = {"recordInvoice"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/list")
    public R list(@ApiParam(value = "结算单号", required = true) @RequestParam String settlementNo,
                  @ApiParam(value = "发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲", required = true) @RequestParam String invoiceStatus){
        logger.info("底账发票列表--入参：{}", settlementNo +"--"+invoiceStatus);
        return R.ok(recordInvoiceService.getInvocieBySettlementNo(settlementNo, invoiceStatus));
    }

    @ApiOperation(value = "红票删除", notes = "", response = Response.class, authorizations = {
            @Authorization(value = "X-Access-Token")},tags = {"recordInvoice"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @DeleteMapping(value = "/delete/{id}")
    public R delete(@ApiParam(value = "发票id", required = true) @PathVariable Long id){
        logger.info("底账发票列表--入参：{}", id);
        return R.ok(recordInvoiceService.deleteInvoice(id));
    }





}
