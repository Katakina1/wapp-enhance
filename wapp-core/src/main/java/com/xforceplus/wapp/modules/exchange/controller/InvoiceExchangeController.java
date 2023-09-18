package com.xforceplus.wapp.modules.exchange.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.modules.backfill.model.BackFillCommitVerifyRequest;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backfill.service.BackFillService;
import com.xforceplus.wapp.modules.exchange.model.*;
import com.xforceplus.wapp.modules.exchange.service.InvoiceExchangeService;
import com.xforceplus.wapp.modules.job.executor.InvoiceExchangeJobExecutor;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by SunShiyong on 2021/11/18.
 */

@Api(tags = "exchange")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH + "/invoice/exchange")
public class InvoiceExchangeController extends AbstractController {

    @Autowired
    private InvoiceExchangeService invoiceExchangeService;
    @Autowired
    private BackFillService backFillService;
    @Autowired
    InvoiceExchangeJobExecutor executor;


    @ApiOperation(value = "换票列表查询")
    @GetMapping(value = "/list")
    public R<PageResult<InvoiceExchangeResponse>> list(@ApiParam(value = "换票列表请求", required = true) QueryInvoiceExchangeRequest request) {
        logger.info("换票列表查询--请求参数{}", JSONObject.toJSONString(request));
        final String usercode = getUser().getUsercode();
        request.setVenderid(usercode);
        return R.ok(invoiceExchangeService.queryPageList(request));
    }

    @ApiOperation(value = "换票详情查询")
    @GetMapping(value = "/detail/{id}")
    public R<List<InvoiceDetailResponse>> detatil(@ApiParam(value = "发票id", required = true) @PathVariable Long id) {
        logger.info("换票详情查询--请求参数{}", id);
        return R.ok(invoiceExchangeService.getNewInvoiceById(id));
    }

    @ApiOperation(value = "保存上传的新票")
    @PostMapping("/match")
    public R match(@ApiParam(value = "保存上传的新票请求", required = true) @RequestBody @Valid BackFillExchangeRequest request) {
        logger.info("发票回填后匹配--入参：{}", JSONObject.toJSONString(request));
        final String usercode = getUser().getUsercode();
        request.setVenderId(usercode);
        return invoiceExchangeService.match(request);
    }

    @ApiOperation(value = "纸票发票回填")
    @PostMapping(value = "/commitVerify")
    public R comitVerify(@ApiParam(value = "纸票发票回填请求", required = true) @RequestBody BackFillCommitVerifyRequest request) {
        logger.info("纸票发票回填--入参：{}", JSONObject.toJSONString(request));
        final String usercode = getUser().getUsercode();
        request.setVendorId(usercode);
        return backFillService.commitInvoiceVerify(request);
    }

    @ApiOperation(value = "电票发票回填（需要验真）")
    @PostMapping("/comitVerifyElec")
    public R comitVerifyElec(@RequestParam("files") MultipartFile[] files, @RequestParam("gfName") String gfName, @RequestParam("jvCode") String jvCode) {
        String vendorid = getUser().getUsercode();
        return backFillService.upload(files, gfName, jvCode, vendorid, null, 1);
    }
    @ApiOperation(value = "电票发票上传（无需验真）")
    @PostMapping("/upload")
    public R upload(@RequestParam("files") MultipartFile[] files, @RequestParam("invoiceId") Long invoiceId) {
        return invoiceExchangeService.upload(files, invoiceId);
    }

    @ApiOperation(value = "电票发票下载")
    @PostMapping("/download")
    public R download(@ApiParam(value = "发票id", required = true) @RequestParam("invoiceId") Long invoiceId) {
        ConvertInvoiceModel model = new ConvertInvoiceModel();
        model.setInvoiceId(invoiceId);
        ConvertInvoiceModel newModel = new ConvertInvoiceModel();
        BeanUtil.copyProperties(model, newModel);
        return invoiceExchangeService.download(newModel.getInvoiceId());
    }


    @ApiOperation(value = "手工确认换票")
    @PostMapping("/confirm")
    public R confirm(@ApiParam(value = "换票请求", required = true) @RequestBody ExchangeSaveRequest request) {
        return invoiceExchangeService.confirm(request);
    }

    @ApiOperation(value = "换票完成")
    @PostMapping("/finish")
    public R finish(@ApiParam(value = "换票请求", required = true) @RequestBody ExchangeFinishRequest request) {
        return invoiceExchangeService.finish(request);
    }

    @ApiOperation(value = "换票定时任务立即执行")
    @PostMapping("/execute")
    public R execute() {
        executor.execute();
        return R.ok(null, "执行完毕");
    }

    @ApiOperation(value = "供应商换票导出")
    @PostMapping("/export")
    public R export(@RequestBody ExchangeExportRequest request) {

        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null && request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 500) {
                return R.fail("最大导出数量不能超过五百");
            }
            List<InvoiceExchangeResponse> resultList = invoiceExchangeService.getByIds(request.getIncludes());
            invoiceExchangeService.export(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心");
        } else {
            List<InvoiceExchangeResponse> resultList = invoiceExchangeService.noPaged(request.getExcludes());
            if (resultList.size() > 500) {
                return R.fail("最大导出数量不能超过五百");
            }
            invoiceExchangeService.export(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心");
        }


    }




}
