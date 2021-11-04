package com.xforceplus.wapp.modules.deduct.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.deduct.dto.*;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @Autowired
    private RecordInvoiceService recordInvoiceService;


    @ApiOperation(value = "修改业务单状态")
    @PostMapping(value = "/updateBillStatus")
    public R updateBillStatus(@ApiParam(value = "修改业务单状态请求" ,required=true )@RequestBody UpdateBillStatusRequest request) {
        logger.info("修改业务单状态--请求参数{}", JSONObject.toJSONString(request));
        TXfDeductionBusinessTypeEnum deductionEnum = ValueEnum.getEnumByValue(TXfDeductionBusinessTypeEnum.class, request.getDeductType()).get();
        TXfBillDeductStatusEnum status = TXfBillDeductStatusEnum.getEnumByCode(request.getDeductStatus());
        for (Long id : request.getIds()) {
            TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
            tXfBillDeductEntity.setId(id);
            TXfBillDeductEntity deductById = deductService.getDeductById(id);
            if (deductById == null) {
                return R.fail("根据id未找到业务单");
            }
            tXfBillDeductEntity.setStatus(deductById.getStatus());
            if (!deductService.updateBillStatus(deductionEnum, tXfBillDeductEntity, status)) {
                return R.fail("修改失败");
            }
        }
        return R.ok("修改成功");
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

    @ApiOperation(value = "索赔单发票列表详情")
    @GetMapping(value = "invoice/{settlementNo}")
    public R<List<InvoiceDetailResponse>> queryInvoiceList(@ApiParam(value = "结算单号",required = true) @PathVariable String settlementNo,@ApiParam(value = "红蓝标识 -1蓝字发票 0-红字发票") @RequestParam String invoiceColor){
        logger.info("索赔单发票列表详情--请求参数{}", settlementNo);
        return R.ok(recordInvoiceService.queryInvoiceList(settlementNo, TXfInvoiceStatusEnum.NORMAL.getCode(),invoiceColor,null));
    }

    @ApiOperation(value = "业务单导出")
    @PostMapping("/export")
    public R export(@ApiParam(value = "业务单导出请求" ,required=true )@RequestBody @Valid DeductExportRequest request) {
        if(deductService.export(request)){
            return R.ok("单据导出正在处理，请在消息中心");
        }else{
            return R.fail("导出任务添加失败");
        }
    }

}
