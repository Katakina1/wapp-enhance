package com.xforceplus.wapp.modules.exchange.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backFill.model.BackFillCommitVerifyRequest;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backFill.model.UploadFileResult;
import com.xforceplus.wapp.modules.backFill.model.UploadFileResultData;
import com.xforceplus.wapp.modules.backFill.service.BackFillService;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.backFill.service.InvoiceFileService;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.exchange.model.*;
import com.xforceplus.wapp.modules.exchange.service.InvoiceExchangeService;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import com.xforceplus.wapp.repository.entity.TXfInvoiceFileEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.xforceplus.wapp.modules.sys.util.UserUtil.getUserId;

/**
 * Created by SunShiyong on 2021/11/18.
 */

@Api(tags = "exchange")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH+"/invoice/exchange")
public class InvoiceExchangeController extends AbstractController {

    @Autowired
    private InvoiceExchangeService invoiceExchangeService;

    @Autowired
    private BackFillService backFillService;

    @Autowired
    private FileService fileService;

    @Autowired
    private InvoiceFileService invoiceFileService;

    @Autowired
    private RecordInvoiceService recordInvoiceService;

    @ApiOperation(value = "换票列表查询")
    @GetMapping(value = "/list")
    public R<PageResult<InvoiceExchangeResponse>> list(@ApiParam(value = "换票列表请求",required = true)QueryInvoiceExchangeRequest request){
        logger.info("换票列表查询--请求参数{}", JSONObject.toJSONString(request));
        request.setVenderid(getUser().getUsercode());
        return R.ok(invoiceExchangeService.queryPageList(request));
    }
    @ApiOperation(value = "换票详情查询")
    @GetMapping(value = "/detail/{id}")
    public R<List<InvoiceDetailResponse>> detatil(@ApiParam(value = "主键",required = true) @PathVariable Long id){
        logger.info("换票详情查询--请求参数{}", id);
        return R.ok(invoiceExchangeService.getNewInvoiceById(id));
    }

    @ApiOperation(value = "保存上传的新票")
    @PostMapping("/match")
    public R match(@ApiParam(value = "保存上传的新票请求" ,required=true )@RequestBody BackFillExchangeRequest request) {
        logger.info("发票回填后匹配--入参：{}", JSONObject.toJSONString(request));
        request.setVenderId(getUser().getUsercode());
        return invoiceExchangeService.match(request);
    }

    @ApiOperation(value = "纸票发票回填")
    @PostMapping(value = "/commitVerify")
    public R comitVerify(@ApiParam(value = "纸票发票回填请求" ,required=true )@RequestBody BackFillCommitVerifyRequest request){
        logger.info("纸票发票回填--入参：{}", JSONObject.toJSONString(request));
        request.setOpUserId(getUserId());
        request.setOpUserName(getUserName());
        request.setVendorId(getUser().getUsercode());
        return backFillService.commitInvoiceVerify(request);
    }

    @ApiOperation(value = "电票发票回填（需要验真）" )
    @PostMapping("/comitVerifyElec")
    public R comitVerifyElec(@RequestParam("files") MultipartFile[] files, @RequestParam("gfName") String gfName, @RequestParam("jvCode") String jvCode, @RequestParam("vendorId") String vendorid) {
        return backFillService.upload(files,gfName,jvCode,vendorid,null);
    }

    @ApiOperation(value = "电票发票上传（无需验真）" )
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file, @RequestParam("newInvoiceId") String newInvoiceId,@RequestParam("vendorId") String vendorid) {
        return invoiceExchangeService.upload(file, newInvoiceId, vendorid);
    }

    @ApiOperation(value = "电票发票下载" )
    @PostMapping("/download")
    public R download(@RequestParam("newInvoiceId")  String newInvoiceId) {
        return invoiceExchangeService.download(newInvoiceId);
    }



    @ApiOperation(value = "手工确认换票" )
    @PostMapping("/confirm")
    public R confirm(@ApiParam(value = "换票请求",required = true) @RequestBody ExchangeSaveRequest request) {
        return invoiceExchangeService.confirm(request);
    }

    @ApiOperation(value = "换票完成" )
    @PostMapping("/finish")
    public R finish(@ApiParam(value = "换票请求",required = true) @RequestBody ExchangeFinishRequest request) {
        return invoiceExchangeService.finish(request);
    }


}
