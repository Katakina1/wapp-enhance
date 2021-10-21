package com.xforceplus.wapp.modules.preinvoice.controller;

import com.xforceplus.wapp.modules.preinvoice.dto.ApplyOperationRequest;
import com.xforceplus.wapp.modules.preinvoice.dto.PreInvoiceItem;
import com.xforceplus.wapp.modules.preinvoice.dto.SplitAgainRequest;
import com.xforceplus.wapp.modules.preinvoice.dto.UndoRedNotificationRequest;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceDaoService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预制发票
 */
@RestController
@RequestMapping(value = "/api/pre-invoice")
public class PreInvoiceController {

    @Autowired
    PreInvoiceDaoService preInvoiceDaoService;

    @ApiOperation(value = "预制发票申请红字信息", notes = "", response = Response.class, tags = {"预制发票"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply-operation")
    public Response<PreInvoiceItem> applyOperation(@RequestBody ApplyOperationRequest request){

        return preInvoiceDaoService.applyOperation(request);
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



    @PostMapping(value = "/undo-notification")
    public Response undoRedNotificationByInvoice(@RequestBody UndoRedNotificationRequest request){


        return Response.ok("申请成功！请等待购方审核或操作！");

    }

}
