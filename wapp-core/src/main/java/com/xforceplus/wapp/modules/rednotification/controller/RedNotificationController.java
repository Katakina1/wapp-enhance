package com.xforceplus.wapp.modules.rednotification.controller;

import com.xforceplus.wapp.modules.rednotification.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

@RestController
@RequestMapping(value = "/red-notification")
public class RedNotificationController {

    @ApiOperation(value = "红字信息申请(页面申请)", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply")
    public Response applyByPage(@RequestBody RedNotificationApplyReverseRequest request){

        return Response.ok("成功","");
    }

    @ApiOperation(value = "红字信息申请表新增待申请", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/add")
    public Response<String> add(@RequestBody AddRedNotificationRequest request){

        return Response.ok("成功","");
    }

    @ApiOperation(value = "红字信息表列表统计", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/summary")
    public Response<SummaryResult> summary(@RequestBody QueryModel queryModel){

        return Response.ok("成功",null);
    }


    @ApiOperation(value = "红字信息表列表", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/list")
    public Response<RedNotificationMain> list(@RequestBody QueryModel queryModel){

        return Response.ok("成功",null);
    }


    @ApiOperation(value = "红字信息表详情", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/detail")
    public Response<List<RedNotificationInfo>> detail(@RequestParam(value="id") Long id){

        return Response.ok("成功");
    }


    @ApiOperation(value = "红字信息表下载", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/download")
    public Response download(@RequestBody RedNotificationExportPdfRequest request){
        return Response.ok("成功");
    }


    @ApiOperation(value = "红字信息表撤销", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/roll-back")
    public Response rollback(@RequestBody RedNotificationApplyReverseRequest request){

        return Response.ok("成功");
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
        return Response.ok("成功");
    }

    @ApiOperation(value = "获取红字信息表模板", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/template")
    public Response template(){
        return Response.ok("成功");
    }

    @ApiOperation(value = "红字信息表导入", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/import")
    public Response importNotification(MultipartHttpServletRequest multipartRequest){

        return Response.ok("成功");
    }

    @ApiOperation(value = "红字信息导出", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/export")
    public Response export(@RequestBody QueryModel queryModel){

        return Response.ok("成功");
    }


    @ApiOperation(value = "获取红字信息申请终端", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/getTerminals")
    public Response<GetTerminalResult> getTerminals(@RequestBody QueryModel queryModel){
        return Response.ok("成功");
    }


//    @ApiOperation(value = "红字信息申请(服务申请)", notes = "", response = Response.class, tags = {"red-notification",})
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "response", response = Response.class)})
//    @PostMapping(value = "/apply-by-service")
//    public Response applyByService(){
//
//        return Response.ok("成功");
//    }

}
