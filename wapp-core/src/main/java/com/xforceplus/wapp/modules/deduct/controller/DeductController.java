package com.xforceplus.wapp.modules.deduct.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.dto.DeductDetailResponse;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import com.xforceplus.wapp.modules.deduct.dto.UpdateBillStatusRequest;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:22
 **/
@Api(tags = "deduct")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +"/deduct")
public class DeductController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeductService deductService;


    @ApiOperation(value = "修改业务单状态")
    @PostMapping(value = "/updateBillStatus")
    public R updateBillStatus(@ApiParam(value = "UpdateBillStatusRequest" ,required=true )@RequestBody UpdateBillStatusRequest request) {
        logger.info("修改业务单状态--请求参数{}", JSONObject.toJSONString(request));
        XFDeductionBusinessTypeEnum deductionEnum = ValueEnum.getEnumByValue(XFDeductionBusinessTypeEnum.class, request.getDeductType()).get();
        TXfBillDeductStatusEnum status = TXfBillDeductStatusEnum.getEnumByCode(request.getDeductStatus());
        TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
        tXfBillDeductEntity.setId(request.getId());
        TXfBillDeductEntity deductById = deductService.getDeductById(request.getId());
        if (deductById == null) {
            return R.fail("根据id未找到业务单");
        }
        tXfBillDeductEntity.setStatus(deductById.getStatus());
        if (deductService.updateBillStatus(deductionEnum, tXfBillDeductEntity, status)) {
            return R.ok("修改成功");
        } else {
            return R.fail("修改失败");
        }
    }

    @ApiOperation(value = "业务单列表")
    @GetMapping(value = "list")
    public R<PageResult<QueryDeductListResponse>> queryPageList(@ApiParam(value = "业务单列表请求",required = true) QueryDeductListRequest request){
        logger.info("查询业务单列表--请求参数{}", JSONObject.toJSONString(request));
        return R.ok(deductService.queryPageList(request));
    }

    @ApiOperation(value = "业务单详情")
    @GetMapping(value = "detail/{id}")
    public R<DeductDetailResponse> getDeductDetail(@ApiParam(value = "主键",required = true) @PathVariable Long id){
        logger.info("查询业务单列表--请求参数{}", id);
        return R.ok(deductService.getDeductDetailById(id));
    }

    @ApiOperation(value = "业务单列表tab")
    @GetMapping(value = "tab")
    public R<List<JSONObject>> queryPageTab(@ApiParam(value = "业务单列表tab请求",required = true) QueryDeductListRequest request){
        logger.info("查询业务单列表--请求参数{}", JSONObject.toJSONString(request));
        return R.ok(deductService.queryPageTab(request));
    }




}
