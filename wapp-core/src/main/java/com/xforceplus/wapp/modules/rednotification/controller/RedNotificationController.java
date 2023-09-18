package com.xforceplus.wapp.modules.rednotification.controller;

import com.xforceplus.wapp.modules.rednotification.model.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedRevokeMessageResult;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;


@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +  "/red-notification")
@Slf4j
public class RedNotificationController {
    @Autowired
    RedNotificationMainService rednotificationService;
    @Autowired
    TaxWareService taxWareService;
    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private RedisTemplate redisTemplate;
    private static String KEY = "AUTO_APPLY_RED-NOTIFICATION_SWITCH";

    @ApiOperation(value = "红字信息申请(页面申请)", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/apply")
    public Response applyByPage(@RequestBody RedNotificationApplyReverseRequest request){
        return rednotificationService.applyByPage(request,false);
    }

    @ApiOperation(value = "红字信息申请表新增待申请", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/add")
    public Response<String> add(@RequestBody AddRedNotificationRequest request){
        return rednotificationService.add(request);
    }

    @ApiOperation(value = "红字信息表列表统计", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/summary")
    public Response<SummaryResult> summary(@RequestBody QueryModel queryModel){
        return rednotificationService.summary(queryModel);
    }


    @ApiOperation(value = "红字信息表列表", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/list")
    public Response<PageResult<RedNotificationMain>> list(@RequestBody QueryModel queryModel){
        //红字信息表 jira号 WALMART-64
        if(queryModel.getApplyingStatus() !=null && queryModel.getApplyingStatus()==5){
            queryModel.setApplyingStatus(3);
            queryModel.setApproveStatus(4);
        }
        return rednotificationService.listData(queryModel);
    }

    @ApiOperation(value = "红字信息表详情", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/detail")
    public Response<RedNotificationInfo> detail(@RequestParam(value="id") Long id){
        return rednotificationService.detail(id);
    }


    @ApiOperation(value = "红字信息表下载pdf", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/download-pdf")
    public Response download(@RequestBody RedNotificationExportPdfRequest request){
        return rednotificationService.downloadPdf(request);
    }

    @ApiOperation(value = "红字信息表导出", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/export")
    public Response export(@RequestBody RedNotificationExportPdfRequest request){
        //红字信息表 jira号 WALMART-64
        if(request.getQueryModel().getApplyingStatus() !=null && request.getQueryModel().getApplyingStatus()==5){
            request.getQueryModel().setApplyingStatus(3);
            request.getQueryModel().setApproveStatus(4);
        }
        return rednotificationService.export(request);
    }


    @ApiOperation(value = "红字信息表撤销", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
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
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/confirm-reject")
    public Response<String> operation(@RequestBody RedNotificationConfirmRejectRequest request){
        //response splitting漏洞
        RedNotificationConfirmRejectModel model = new RedNotificationConfirmRejectModel();
        BeanUtil.copyProperties(request,model);
        return rednotificationService.operation(model);
    }

    @ApiOperation(value = "红字信息表导入", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/import")
    public Response importNotification(@RequestParam("file") MultipartFile file){
       return rednotificationService.importNotification(file);
//        return Response.ok("成功");
    }



    @ApiOperation(value = "获取红字信息申请终端", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = { @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/getTerminals")
    public Response<GetTerminalResult> getTerminals(@RequestBody QueryModel queryModel){
        return rednotificationService.getTerminals(queryModel);
//        Response.ok("成功",getTerminalResult);
    }


    @ApiOperation(value = "获取红字信息申请终端", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = { @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/getTerminals/{taxNo}")
    public Response<List<TerminalDTO>> getTerminals(@ApiParam(value = "购方税号") @PathVariable("taxNo") String taxNo){
        log.info("getTerminals params:{}", taxNo);
        if (StringUtils.isBlank(taxNo)) {
            return Response.failed("税号不能为空");
        }
        return Response.ok("", rednotificationService.getTerminalList(taxNo));
    }


//    @ApiOperation(value = "红字信息申请(服务申请)", notes = "", response = Response.class, tags = {"red-notification",})
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "response", response = Response.class)})
//    @PostMapping(value = "/apply-by-service")
//    public Response applyByService(){
//
//        return Response.ok("成功");
//    }
    
	@ApiOperation(value = "删除待申请的红字信息表", notes = "", response = Response.class, tags = { "red-notification", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "response", response = Response.class) })
	@PostMapping(value = "/delete")
	public Response deleteById(@RequestBody RedNotificationDeleteRequest request) {
		String userName = UserUtil.getUserName(), loginname = UserUtil.getLoginName();
		log.info("red deleteById userName:{} loginname:{} param:{}", userName, loginname, JSON.toJSONString(request));
		return rednotificationService.deleteById(request, userName, loginname);
	}

	@ApiOperation(value = "红字信息表撤销", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/roll-back/redIds")
    public Response rollbackByIds(@RequestBody RedNotificationApplyReverseRequest request){
		String userName = UserUtil.getUserName(), loginname = UserUtil.getLoginName();
		// ssrf是漏洞
		RedNotificationApplyModel model = new RedNotificationApplyModel();
		BeanUtil.copyProperties(request, model);
		return rednotificationService.rollbackByIds(model, userName, loginname);
    }

	@ApiOperation(value = "红字信息表撤销（结算单撤销需先撤销红字）", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/roll-back/v2/redIds")
    public Response rollbackByIdsV2(@RequestBody RedNotificationApplyReverseRequest request){
        log.info("rollbackByIdsV2 params:[{}]-[{}]-[{}]", JSON.toJSONString(request), UserUtil.getUserName(), UserUtil.getLoginName());
		// ssrf是漏洞
		RedNotificationApplyModel model = new RedNotificationApplyModel();
		BeanUtil.copyProperties(request, model);
		return rednotificationService.rollbackByIdsV2(model);
    }


    @ApiOperation(value = "mock撤销结果返回", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/mock-roll-back-result")
    public Response<String> mockRollBackResult(@RequestBody RedRevokeMessageResult redRevokeMessageResult){
         taxWareService.handleRollBack(redRevokeMessageResult);
         return Response.ok("处理成功");
    }

    @ApiOperation(value = "更新红字信息表pdf链接", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/update/pdf/url/{id}")
    public Response<Boolean> updatePdfUrl(@PathVariable Long id) {
        Boolean r = rednotificationService.updatePdfUrl(id);
        return Response.ok("处理成功", r);
    }

    @ApiOperation(value = "查询自动红字信息表申请开关", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = { @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/getRedNotificationSwitch")
    public Response<Map<String,String>> getRedNotificationSwitch(){
        Map<String,String> result= new HashMap<>();;
        try {
            if(!redisTemplate.hasKey(KEY)){
                redisTemplate.opsForValue().set(KEY, "off");
            }
            result.put("switchFlag",redisTemplate.opsForValue().get(KEY).toString());
            result.put("startDate",redisTemplate.opsForValue().get(KEY+"-startDate") !=null ? redisTemplate.opsForValue().get(KEY+"-startDate").toString() : "");
            result.put("endDate",redisTemplate.opsForValue().get(KEY+"-endDate") !=null ? redisTemplate.opsForValue().get(KEY+"-endDate").toString() : "");
            result.put("msg","开关状态获取成功");
            return Response.ok("结果查询成功",result);
        } catch (Exception e) {
            log.info(e.getMessage());
            result.put("msg","开关状态获取失败");
            return Response.failed("结果查询失败");
        }
    }

    @ApiOperation(value = "设置自动红字信息表申请开关", notes = "", response = Response.class, tags = {"red-notification",})
    @ApiResponses(value = { @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/setRedNotificationSwitch")
    public Response<Map<String,String>> setRedNotificationSwitch(@RequestBody AutoApplyRedNotificationRequest autoApplyRedNotificationRequest){
        if(org.apache.commons.lang.StringUtils.isNotBlank(autoApplyRedNotificationRequest.getSwitchFlag()) && "off".equals(autoApplyRedNotificationRequest.getSwitchFlag())){
            redisTemplate.opsForValue().set(KEY,"off");
            redisTemplate.opsForValue().set(KEY+"-startDate", "");
            redisTemplate.opsForValue().set(KEY+"-endDate", "");
        }else if(org.apache.commons.lang.StringUtils.isNotBlank(autoApplyRedNotificationRequest.getSwitchFlag()) && "on".equals(autoApplyRedNotificationRequest.getSwitchFlag())){
            if( (checkDate(autoApplyRedNotificationRequest.getStartDate()) && checkDate(autoApplyRedNotificationRequest.getEndDate()) &&
                    autoApplyRedNotificationRequest.getEndDate().compareTo(autoApplyRedNotificationRequest.getStartDate())>=0
            )){
                redisTemplate.opsForValue().set(KEY+"-startDate", autoApplyRedNotificationRequest.getStartDate());
                redisTemplate.opsForValue().set(KEY+"-endDate", autoApplyRedNotificationRequest.getEndDate());
            }else{
                redisTemplate.opsForValue().set(KEY+"-startDate", "");
                redisTemplate.opsForValue().set(KEY+"-endDate", "");
            }
            redisTemplate.opsForValue().set(KEY,"on");
        }else{
            return Response.failed("设置失败！");
        }
        return Response.ok("设置成功！");
    }

    public boolean checkDate(String datevalue){
        boolean result=false;
        if(org.apache.commons.lang.StringUtils.isBlank(datevalue)){
            return result;
        }
        Date d = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try
        {
            d = dateFormat.parse(datevalue);
        }
        catch(Exception e)
        {
            return result;
        }
        String eL= "((\\d{3}[1-9]|\\d{2}[1-9]\\d|\\d[1-9]\\d{2}|[1-9]\\d{3})(((0[13578]|1[02])(0[1-9]|[12]\\d|3[01]))|((0[469]|11)(0[1-9]|[12]\\d|30))|(02(0[1-9]|[1]\\d|2[0-8]))))|(((\\d{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(datevalue);
        boolean b = m.matches();
        if(b) {
            result=true;
        } else {
            result=false;
        }
        return result;
    }
}
