package com.xforceplus.wapp.modules.deduct.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.deduct.dto.*;
import com.xforceplus.wapp.modules.deduct.service.*;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:22
 **/
@Api(tags = "deduct")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH + "/deduct")
public class DeductController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeductService deductService;
    @Autowired
    private BillQueryService billQueryService;
    @Autowired
    private BillExportService billExportService;
    @Autowired
    private BillHistoryDataOpsService billHistoryDataOpsService;

    @ApiOperation(value = "修改业务单状态")
    @PostMapping(value = "/updateBillStatus")
    public R updateBillStatus(@ApiParam(value = "修改业务单状态请求", required = true) @RequestBody UpdateBillStatusRequest request) {
        logger.info("修改业务单状态--请求参数{}", JSONObject.toJSONString(request));
        TXfDeductionBusinessTypeEnum deductionEnum = ValueEnum.getEnumByValue(TXfDeductionBusinessTypeEnum.class, request.getDeductType()).get();
        TXfDeductStatusEnum status = TXfDeductStatusEnum.getEnumByCode(request.getDeductStatus());
        int success = 0;
        int fail = 0;
        R response = new R();
        for (Long id : request.getIds()) {
            TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
            tXfBillDeductEntity.setId(id);
            TXfBillDeductEntity deductById = deductService.getDeductById(id);
            if (deductById == null) {
                return R.fail("根据id未找到业务单");
            }
            tXfBillDeductEntity.setUpdateTime(new Date());
            tXfBillDeductEntity.setStatus(deductById.getStatus());
            tXfBillDeductEntity.setLockFlag(deductById.getLockFlag());
            if (deductService.updateBillStatus(deductionEnum, tXfBillDeductEntity, status, response)) {
                success++;
            } else {
                fail++;
            }
        }
        String message = StringUtils.isNotEmpty(response.getMessage()) ? String.format("修改结果：成功【%s】条，失败【%s】条, 失败原因：【%s】", success, fail, response.getMessage()) : String.format("修改结果：成功【%s】条，失败【%s】条", success, fail);
        return R.ok(message);
    }

    @ApiOperation(value = "业务单列表")
    @GetMapping(value = "list")
    public R<PageResult<QueryDeductListResponse>> queryPageList(@ApiParam(value = "业务单列表请求", required = true) QueryDeductListRequest request) {
        logger.info("查询业务单列表--请求参数{}", JSONObject.toJSONString(request));
        return R.ok(deductService.queryPageList(request));
    }

    @ApiOperation(value = "业务单详情")
    @GetMapping(value = "detail/{id}")
    public R<DeductDetailResponse> getDeductDetail(@ApiParam(value = "主键", required = true) @PathVariable Long id) {
        logger.info("查询业务单列表--请求参数{}", id);
        return R.ok(deductService.getDeductDetailById(id));
    }

    @ApiOperation(value = "业务单详情(分页)")
    @GetMapping(value = "details/{id}")
    public R<DeductDetailResponse> getDeductDetailByPage(@ApiParam(value = "主键", required = true) @PathVariable Long id,
                                                         @ApiParam(value = "页码", required = true) @RequestParam(defaultValue = "1") Integer pageNo,
                                                         @ApiParam(value = "页数", required = true) @RequestParam(defaultValue = "20") Integer pageSize) {
        logger.info("查询业务单分页列表--请求参数{}", id);
        return R.ok(deductService.getDeductDetailPageById(id, pageNo, pageSize));
    }

    @ApiOperation(value = "业务单列表tab")
    @GetMapping(value = "tab")
    public R<List<JSONObject>> queryPageTab(@ApiParam(value = "业务单列表tab请求", required = true) QueryDeductListRequest request) {
        logger.info("查询业务单列表--请求参数{}", JSONObject.toJSONString(request));
        return R.ok(deductService.queryPageTab(request));
    }

    @ApiOperation(value = "索赔单发票列表详情")
    @GetMapping(value = "invoice/{businessNo}")
    public R<List<InvoiceDetailResponse>> queryInvoiceList(@ApiParam(value = "业务单号", required = true) @PathVariable String businessNo,
                                                           @ApiParam(value = "红蓝标识 -1蓝字发票 0-红字发票") @RequestParam String invoiceColor) {
        logger.info("索赔单发票列表详情--请求参数{}", businessNo);
        return R.ok(deductService.queryDeductInvoiceList(businessNo, invoiceColor));
    }

    @ApiOperation(value = "业务单发票列表详情")
    @GetMapping(value = "invoices/{deductId}")
    public R<List<InvoiceDetailResponse>> queryInvoiceListByDeductId(@ApiParam(value = "业务单ID", required = true) @PathVariable Long deductId) {
        logger.info("业务单发票列表详情--请求参数{}", deductId);
        return R.ok(deductService.queryDeductInvoiceList(deductId));
    }

    @ApiOperation(value = "业务单导出")
    @PostMapping("/export")
    public R export(@ApiParam(value = "业务单导出请求", required = true) @RequestBody @Valid DeductExportRequest request) {
        return billExportService.export(request);
    }

    @ApiOperation(value = "沃尔玛侧-业务单列表tab(新版post请求，目前查询参数较为复杂，后续可能会更多参数，改用post)")
    @PostMapping(value = "tab")
    public R<List<QueryDeductTabResponse>> queryPageTabNew(@ApiParam(value = "业务单列表tab请求", required = true) @RequestBody QueryDeductListNewRequest request) {
        logger.info("沃尔玛侧-查询业务单列表-NEW-POST--请求参数{}", JSONObject.toJSONString(request));
        return R.ok(billQueryService.queryPageTab(request));
    }

    @ApiOperation(value = "沃尔玛侧-业务单列表(新版post请求，目前查询参数较为复杂，后续可能会更多参数，改用post)")
    @PostMapping(value = "list")
    public R<PageResult<QueryDeductListResponse>> queryPageListNew(@ApiParam(value = "业务单列表请求", required = true) @RequestBody @Validated QueryDeductListNewRequest request) {
        logger.info("沃尔玛侧-查询业务单列表NWE-POST--请求参数{}", JSONObject.toJSONString(request));
        return R.ok(billQueryService.queryPageList(request));
    }

    @ApiOperation(value = "业务单历史数据开票状态同步")
    @PostMapping(value = "syn-history-bill-make-invoice-status")
    public R syncHistoryBillMakeInvoiceStatus(@ApiParam(value = "同步请求", required = true) @RequestBody @Validated SyncHistoryBillMakeInvoiceStatusRequest request) {
        logger.info("业务单历史数据开票状态同步请求参数{}", JSONObject.toJSONString(request));

        if (StringUtils.isBlank(request.getSettlementNo()) &&
                (StringUtils.isBlank(request.getCreateTimeBegin()) || StringUtils.isBlank(request.getCreateTimeEnd()))) {
            throw new RuntimeException("结算单编号为空时，时间参数不能为空");
        }

        billHistoryDataOpsService.syncHistoryBillMakeInvoiceStatus(request);
        return R.ok(request.getOpId());

    }

}
