package com.xforceplus.wapp.modules.backfill.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.dto.IdsDto;
import com.xforceplus.wapp.modules.backfill.model.HostInvoiceModel;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backfill.model.RecordInvoiceResponse;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by SunShiyong on 2021/10/16.
 */
@Api(tags = "recordInvoice")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +"/invoice")
public class RecordInvoiceController extends AbstractController {

    @Autowired
    RecordInvoiceService recordInvoiceService;

    @ApiOperation(value = "结算单发票列表")
    @GetMapping(value = "/list")
    public R<PageResult<RecordInvoiceResponse>> list(
            @ApiParam(value = "页码" ,required = true) @RequestParam long pageNo,
            @ApiParam(value = "页数" ,required = true) @RequestParam long pageSize,
            @ApiParam(value = "结算单号") @RequestParam(required = false) String settlementNo,
            @ApiParam(value = "发票颜色 0红票 1 蓝票") @RequestParam(required = false) String invoiceColor,
            @ApiParam(value = "发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲") @RequestParam(required = false) String invoiceStatus){
        logger.info("结算单详情发票列表--入参：{}", settlementNo +"--"+invoiceStatus);
        return R.ok(recordInvoiceService.queryPageList(pageNo,pageSize,settlementNo,invoiceColor ,invoiceStatus,getUser().getUsercode()));
    }

    @ApiOperation(value = "结算单发票列表")
    @GetMapping(value = "/list/by-settlement")
    public R<PageResult<RecordInvoiceResponse>> listRecordInvoice(
            @ApiParam(value = "页码", required = true) @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam(value = "页数", required = true) @RequestParam(defaultValue = "20") Integer pageSize,
            @ApiParam(value = "结算单号") @RequestParam String settlementNo) {
        logger.info("结算单详情发票列表--入参：{}", settlementNo);
        return R.ok(recordInvoiceService.queryPageRecordInvoiceList(pageNo, pageSize, settlementNo, getUser().getUsercode()));
    }

    @ApiOperation(value = "结算单发票列表tab")
    @GetMapping(value = "/count")
    public R count(@ApiParam(value = "结算单号") @RequestParam(required = false) String settlementNo,
                    @ApiParam(value = "发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲") @RequestParam(required = false) String invoiceStatus){
        logger.info("结算单详情发票列表tab--入参：{}", settlementNo +"--"+invoiceStatus);
        return R.ok(recordInvoiceService.getCountBySettlementNo(settlementNo, invoiceStatus,getUser().getUsercode()));
    }

    @ApiOperation(value = "结算单发票列表详情")
    @GetMapping(value = "/detail/{id}")
    public R<InvoiceDetailResponse> detail(@ApiParam(value = "主键",required = true) @PathVariable Long id){
        logger.info("结算单发票列表详情--入参：{}", id);
        return R.ok(recordInvoiceService.getInvoiceById(id));
    }

    @ApiOperation(value = "发票删除（支持待审核蓝票删除）")
    @DeleteMapping(value = "/delete/{id}")
    public R delete(@ApiParam(value = "发票id", required = true) @PathVariable Long id){
        logger.info("底账发票列表--入参：{}", id);
        return recordInvoiceService.deleteInvoiceV2(id);
    }

    @ApiOperation(value = "根据红票查询蓝票")
    @GetMapping(value = "/queryBlueInvoice")
    public R<List<InvoiceDetailResponse>> queryBlueInvoice(@ApiParam(value = "红票代码", required = true) @RequestParam String invoiceCode,
                                                           @ApiParam(value = "红票号码", required = true) @RequestParam String invoiceNo){
        logger.info("根据红票查询蓝票--红票代码：{}", invoiceCode);
        logger.info("根据红票查询蓝票--红票号码：{}", invoiceNo);
        return R.ok(recordInvoiceService.queryRefInvoice(invoiceCode,invoiceNo, 0));
    }

    @ApiOperation(value = "查询关联发票信息")
    @GetMapping(value = "/query-ref-invoices")
    public R<List<InvoiceDetailResponse>> queryRedInvoice(@ApiParam(value = "发票代码", required = true) @RequestParam String invoiceCode,
                                                          @ApiParam(value = "发票号码", required = true) @RequestParam String invoiceNo,
                                                          @ApiParam(value = "查询类型(0: 通过红票查询蓝票，1：通过蓝票查询红票)", required = true) @RequestParam Integer queryType){
        logger.info("查询关联发票信息--发票代码：{}", invoiceCode);
        logger.info("查询关联发票信息--发票号码：{}", invoiceNo);
        logger.info("查询关联发票信息--查询类型：{}", queryType);
        return R.ok(recordInvoiceService.queryRefInvoice(invoiceCode,invoiceNo, queryType));
    }

    @ApiOperation(value = "HOST发票查询")
    @GetMapping(value = "/host-invoices")
    public R<PageResult<HostInvoiceModel>> queryHostInvoice(@ApiParam(value = "供应商名称") @RequestParam(required = false) String sellerName,
                                                                  @ApiParam(value = "供应商号") @RequestParam(required = false) String sellerNo,
                                                                  @ApiParam(value = "HOST发票号码") @RequestParam(required = false) String hostInv,
                                                                  @ApiParam(value = "全电发票号码") @RequestParam(required = false) String invoiceNo,
                                                                  @ApiParam(value = "开票日期开始时间") @RequestParam(required = false) String invoiceDateStart,
                                                                  @ApiParam(value = "开票日期结束时间") @RequestParam(required = false) String invoiceDateEnd,
                                                                  @ApiParam(value = "页数") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                  @ApiParam(value = "条数") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Tuple2<Long, List<HostInvoiceModel>> invoice = recordInvoiceService.queryHostInvoice(sellerName, sellerNo, hostInv, invoiceNo,
                invoiceDateStart, invoiceDateEnd, pageNum, pageSize);

        return R.ok(PageResult.of(invoice._1, invoice._2));
    }

    @ApiOperation(value = "HOST发票导出")
    @PostMapping(value = "/host-invoices/export")
    public R<Void> exportHostInvoice(@RequestBody IdsDto<Long> idsDto) {
        String s = recordInvoiceService.exportHostInvoice(idsDto.getIds());
        return StringUtils.isBlank(s) ? R.ok() : R.fail(s);
    }

}
