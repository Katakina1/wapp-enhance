package com.xforceplus.wapp.modules.preinvoice.controller;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.preinvoice.dto.ApplyOperationRequest;
import com.xforceplus.wapp.modules.preinvoice.dto.PreInvoiceItem;
import com.xforceplus.wapp.modules.preinvoice.dto.SplitAgainRequest;
import com.xforceplus.wapp.modules.preinvoice.dto.UndoRedNotificationRequest;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceDaoService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.service.CommClaimService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * 预制发票
 */
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH + "/pre-invoice")
@Slf4j
public class PreInvoiceController {

    @Autowired
    PreInvoiceDaoService preInvoiceDaoService;
    @Autowired
    PreinvoiceService preinvoiceService;
    @Autowired
    CommClaimService commClaimService;
    @Autowired
    LockClient lockClient;

    @ApiOperation(value = "预制发票申请红字信息", notes = "", response = Response.class, tags = {"预制发票"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply-operation")
    public R<List<PreInvoiceItem>> applyOperation(@RequestBody ApplyOperationRequest request) {
    	ApplyOperationRequest newRequest = new ApplyOperationRequest();
    	BeanUtils.copyProperties(request, newRequest);
    	log.info("applyOperation request：{}，newRequest：{}", JSON.toJSONString(request), JSON.toJSONString(newRequest));
        final R<List<PreInvoiceItem>> listR = lockClient.tryLock("apply-operation" + request.getSettlementId(), () -> preInvoiceDaoService.applyOperation(newRequest), -1, 1);
        if (listR == null) {
            return R.fail("正在处理中，请勿重复操作！");
        }
        return listR;
    }


    @ApiOperation(value = "重新发起拆票", notes = "", response = Response.class, tags = {"预制发票"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/split-again")
    public Response<String> splitAgain(@RequestBody SplitAgainRequest request){

        return preInvoiceDaoService.splitAgain(request);
    }

//    @ApiOperation(value = "判断是否有已匹配的红票", notes = "", response = Response.class, tags = {"预制发票"})
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "response", response = Response.class)})
//    @PostMapping(value = "/exist-red-invoice")
//    public Response<String> existRedInvoice(@RequestBody ApplyOperationRequest request){
//
////        return preInvoiceDaoService.splitAgain(request);
//        return  null ;
//    }

    @ApiOperation(value = "重新发起拆票（索赔结算单)", notes = "", response = R.class, tags = {"预制发票"})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "response", response = R.class)})
    @PostMapping(value = "/split-again/claim/{id}")
    public R<String> splitAgainClaim(@PathVariable("id") Long id) {
        log.info("splitAgainClaim params:{}", id);
        if (!CommonUtil.isEdit(id)) {
            return R.fail("参数异常");
        }
        final String key = "splitAgainClaim:" + id;
        Callable<R<String>> callable = () -> commClaimService.againSplitSettlementPreInvoice(id);
        return lockClient.tryLock(key, callable, -1, 1);
    }



    @PostMapping(value = "/undo-notification")
    public Response undoRedNotificationByInvoice(@RequestBody UndoRedNotificationRequest request){
    	UndoRedNotificationRequest newRequest = new UndoRedNotificationRequest();
    	BeanUtils.copyProperties(request, newRequest);
    	log.info("undoRedNotificationByInvoice request：{}，newRequest：{}", JSON.toJSONString(request), JSON.toJSONString(newRequest));
        final boolean lock = lockClient.tryLock("undo-notification" + request.getInvoiceCode() + request.getInvoiceNo(), () ->
                        this.preinvoiceService.applyDestroyPreInvoiceAndRedNotification(newRequest.getInvoiceNo(), newRequest.getInvoiceCode(), newRequest.getRemark())
                , -1, 1);

        if (lock) {
            return Response.ok("申请成功！请等待购方审核或操作！");
        }
        return Response.failed("正在处理中，请勿重复操作！");

    }

    @ApiOperation(value = "预制发票生成红字预览", notes = "", response = R.class, tags = {"预制发票"})
    @GetMapping(value = "/view-red-pdf/{preInvoiceId}")
    public R preViewRedPdf(@PathVariable("preInvoiceId") Long preInvoiceId){
        log.info("根据预制发票生成红字预览请求 preInvoiceId:{} ",preInvoiceId);
        return preinvoiceService.preViewRedPdf(preInvoiceId);
    }

    /*@ApiOperation(value = "预制发票生成红字预览", tags = {"预制发票"})
    @GetMapping(value = "/view-red-pdf/{preInvoiceId}")
    public ResponseEntity<byte[]> viewRedPdf(@PathVariable("preInvoiceId") Long preInvoiceId,
                      @ApiParam("预览模式 0-下载 1-预览(默认)") @RequestParam(required = false) String viewType){
        log.info("根据预制发票生成红字预览请求: {} ",preInvoiceId);
        R<Map<String,String>> pdfRes = preinvoiceService.preViewRedPdf(preInvoiceId);
        log.info("根据预制发票生成红字预览回复: {} ", JSON.toJSONString(pdfRes));
        if (pdfRes == null || !R.OK.equals(pdfRes.getCode())
                || StringUtils.isBlank(pdfRes.getResult().get("pdfUrl"))){
            return null;
        }
        String pdfUrl = pdfRes.getResult().get("pdfUrl");
        HttpResponse pdfResponse = HttpDownloadUtil.getFileResponse(pdfUrl);
        String pdfFileName = HttpDownloadUtil.getFileName(pdfResponse,pdfUrl);
        //设置响应头
        HttpHeaders headers = new HttpHeaders();
        if ("0".equals(viewType)) {
            //通知浏览器以下载的方式打开文件
            headers.setContentDispositionFormData("attachment", pdfFileName);
        }
        //定义以流的形式下载返回文件数据
        headers.setContentType(MediaType.APPLICATION_PDF);
        //使用springmvc框架的ResponseEntity对象封装返回数据
        return new ResponseEntity<>(HttpDownloadUtil.getFileBytes(pdfResponse), headers, HttpStatus.OK);
    }*/

    @ApiOperation(value = "查询结算单下已申请红字信息表预制发票数量", tags = {"预制发票"})
    @GetMapping("/red-no-applied/count/{settlementId}")
    public R<Integer> getAppliedRedNotificationPreInvoice(@PathVariable("settlementId") Long settlementId) {
        log.info("getAppliedRedNoNum params:{}", settlementId);
        Asserts.isFalse(CommonUtil.isEdit(settlementId), "结算单id不能为空");

        return preInvoiceDaoService.getAppliedRedNotificationPreInvoiceCount(settlementId);
    }

    @ApiOperation(value = "待申请红字信息表状态预制发票发起申请", tags = {"预制发票"})
    @PostMapping("/red-no-apply/{id}")
    public R applyRedNotification(@PathVariable("id") Long id) {
        log.info("applyRedNotification params:{}", id);
        Asserts.isFalse(CommonUtil.isEdit(id), "预制发票id不能为空");

        return preinvoiceService.applyRedNotification(id);
    }

}
