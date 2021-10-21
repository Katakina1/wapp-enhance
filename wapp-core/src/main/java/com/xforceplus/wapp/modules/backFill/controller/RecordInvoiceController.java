package com.xforceplus.wapp.modules.backFill.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.backFill.model.RecordInvoiceResponse;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by SunShiyong on 2021/10/16.
 */
@Api(tags = "recordInvoice")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +"/invoice")
public class RecordInvoiceController extends AbstractController {

    @Autowired
    RecordInvoiceService recordInvoiceService;

    @ApiOperation(value = "结算单详情发票列表")
    @GetMapping(value = "/list")
    public R<PageResult<RecordInvoiceResponse>> list(
            @ApiParam(value = "页码") @RequestParam long pageNo,
            @ApiParam(value = "页数") @RequestParam long pageSize,
            @ApiParam(value = "结算单号") @RequestParam(required = false) String settlementNo,
            @ApiParam(value = "发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲") @RequestParam(required = false) String invoiceStatus){
        logger.info("结算单详情发票列表--入参：{}", settlementNo +"--"+invoiceStatus);
        return R.ok(recordInvoiceService.queryPageList(pageNo,pageSize,settlementNo, invoiceStatus,getUser().getUsercode()));
    }

    @ApiOperation(value = "结算单详情发票列表tab")
    @GetMapping(value = "/count")
    public R count(@ApiParam(value = "结算单号") @RequestParam(required = false) String settlementNo,
                    @ApiParam(value = "发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲") @RequestParam(required = false) String invoiceStatus){
        logger.info("结算单详情发票列表tab--入参：{}", settlementNo +"--"+invoiceStatus);
        return R.ok(recordInvoiceService.getCountBySettlementNo(settlementNo, invoiceStatus,getUser().getUsercode()));
    }

    @ApiOperation(value = "红票删除")
    @DeleteMapping(value = "/delete/{id}")
    public R delete(@ApiParam(value = "发票id", required = true) @PathVariable Long id){
        logger.info("底账发票列表--入参：{}", id);
        return R.ok(recordInvoiceService.deleteInvoice(id));
    }

}
