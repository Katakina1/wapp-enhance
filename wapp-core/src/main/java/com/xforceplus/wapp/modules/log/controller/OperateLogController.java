package com.xforceplus.wapp.modules.log.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.backFill.model.BackFillCommitVerifyRequest;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by SunShiyong on 2021/10/25.
 * 操作日志记录
 */

@Api(tags = "operateLog")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH+"/operateLog")
public class OperateLogController extends AbstractController {

    @Autowired
    private OperateLogService operateLogService;

    @ApiOperation(value = "查询操作日志")
    @GetMapping(value = "/list")
    public R list(@ApiParam(value = "业务id" ,required=true )@RequestParam Long businessId,@ApiParam(value = "用户id" ,required=true )@RequestParam Long userId){
        logger.info("查询操作日志--入参：{}", businessId);
        return R.ok(operateLogService.query(businessId,userId));
    }


}
