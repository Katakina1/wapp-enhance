package com.xforceplus.wapp.modules.rednotification.controller;

import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@RestController
@RequestMapping(value = "/api/red-notification")
public class RedNotificationController {
    @Autowired
    RedNotificationMainService rednotificationService;

    @ApiOperation(value = "红字信息申请(页面申请)", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply")
    public Response applyByPage(@RequestBody RedNotificationApplyReverseRequest request){
        return rednotificationService.applyByPage(request);
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
        return  rednotificationService.rollback(request);
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
        return rednotificationService.operation(request);
    }

    @ApiOperation(value = "获取红字信息表模板", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/template")
    public void template(HttpServletResponse res, HttpServletRequest req){
        try {
            String name = "红字信息表导入模板";
            String fileName = name+".xlsx";
            ServletOutputStream out;
            res.setContentType("multipart/form-data");
            res.setCharacterEncoding("UTF-8");
            res.setContentType("text/html");
            String filePath = getClass().getResource("/excl/" + fileName).getPath();
            String userAgent = req.getHeader("User-Agent");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
            // 非IE浏览器的处理：
            fileName = new String((fileName).getBytes("UTF-8"), "ISO-8859-1");
            }
            filePath = URLDecoder.decode(filePath, "UTF-8");
            res.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            FileInputStream inputStream = null;

                inputStream = new FileInputStream(filePath);

            out = res.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
            // 4.写到输出流(out)中
            out.write(buffer, 0, b);
            }
            inputStream.close();

            if (out != null) {
            out.flush();
            out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
