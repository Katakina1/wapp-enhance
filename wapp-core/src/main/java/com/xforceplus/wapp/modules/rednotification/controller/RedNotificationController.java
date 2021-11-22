package com.xforceplus.wapp.modules.rednotification.controller;

import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedRevokeMessageResult;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "/api/red-notification")
@Slf4j
public class RedNotificationController {
    @Autowired
    RedNotificationMainService rednotificationService;
    @Autowired
    TaxWareService taxWareService;
    @Autowired
    ExportCommonService exportCommonService;

    @ApiOperation(value = "红字信息申请(页面申请)", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply")
    public Response applyByPage(@RequestBody RedNotificationApplyReverseRequest request){
        return rednotificationService.applyByPage(request,false);
    }

    @ApiOperation(value = "红字信息申请表新增待申请", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/add")
    public Response<String> add(@RequestBody AddRedNotificationRequest request){
        return rednotificationService.add(request);
    }

    @ApiOperation(value = "红字信息表列表统计", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/summary")
    public Response<SummaryResult> summary(@RequestBody QueryModel queryModel){
        return rednotificationService.summary(queryModel);
    }


    @ApiOperation(value = "红字信息表列表", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/list")
    public Response<PageResult<RedNotificationMain>> list(@RequestBody QueryModel queryModel){
        return rednotificationService.listData(queryModel);
    }


    @ApiOperation(value = "红字信息表详情", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/detail")
    public Response<RedNotificationInfo> detail(@RequestParam(value="id") Long id){
        return rednotificationService.detail(id);
    }


    @ApiOperation(value = "红字信息表下载pdf", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/download-pdf")
    public Response download(@RequestBody RedNotificationExportPdfRequest request){
        return rednotificationService.downloadPdf(request);
    }

    @ApiOperation(value = "红字信息表导出", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/export")
    public Response export(@RequestBody RedNotificationExportPdfRequest request){

        return rednotificationService.export(request);
    }


    @ApiOperation(value = "红字信息表撤销", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/roll-back")
    public Response rollback(@RequestBody RedNotificationApplyReverseRequest request){
        //ssrf是漏洞
        RedNotificationApplyModel model = new RedNotificationApplyModel();
        BeanUtil.copyProperties(request,model);
        return  rednotificationService.rollback(model);
    }

    /**
     *
     * @return
     */
    @ApiOperation(value = "红字信息表（确认)或(驳回)", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/confirm-reject")
    public Response<String> operation(@RequestBody RedNotificationConfirmRejectRequest request){
        //response splitting漏洞
        RedNotificationConfirmRejectModel model = new RedNotificationConfirmRejectModel();
        BeanUtil.copyProperties(request,model);
        return rednotificationService.operation(model);
    }

    @ApiOperation(value = "红字信息表导入", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/import")
    public Response importNotification(@RequestParam("file") MultipartFile file){
        rednotificationService.importNotification(file);
        return Response.ok("成功");
    }



    @ApiOperation(value = "获取红字信息申请终端", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/getTerminals")
    public Response<GetTerminalResult> getTerminals(@RequestBody QueryModel queryModel){
        return rednotificationService.getTerminals(queryModel);
//        Response.ok("成功",getTerminalResult);
    }


//    @ApiOperation(value = "红字信息申请(服务申请)", notes = "", response = Response.class, tags = {"red-notification",})
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "response", response = Response.class)})
//    @PostMapping(value = "/apply-by-service")
//    public Response applyByService(){
//
//        return Response.ok("成功");
//    }

    @ApiOperation(value = "mock撤销结果返回", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/mock-roll-back-result")
    public Response<String> mockRollBackResult(@RequestBody RedRevokeMessageResult redRevokeMessageResult){
         taxWareService.handleRollBack(redRevokeMessageResult);
         return Response.ok("处理成功");
    }

}
