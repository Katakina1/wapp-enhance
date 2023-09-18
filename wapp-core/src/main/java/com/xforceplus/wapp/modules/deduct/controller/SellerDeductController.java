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
import com.xforceplus.wapp.modules.deduct.service.BillQueryService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.deduct.service.SellerBillExportService;
import com.xforceplus.wapp.modules.deduct.service.SellerBillQueryService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * Describe: 供应商侧-业务单处理器
 *
 * @Author xiezhongyong
 * @Date 2022/9/18
 */
@Api(tags = "deduct")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +"/seller-deduct")
public class SellerDeductController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SellerBillQueryService sellerBillQueryService;
    @Autowired
    private SellerBillExportService sellerBillExportService;

    @ApiOperation(value = "供应商侧-业务单列表")
    @PostMapping(value = "/list")
    public R<PageResult<QuerySellerDeductListResponse>> queryPageSellerList(@ApiParam(value = "业务单列表请求",required = true) @RequestBody @Validated QuerySellerDeductListRequest request){
        logger.info("供应商侧-查询业务单列表--请求参数{}", JSONObject.toJSONString(request));
        request.setSellerNo(UserUtil.getUser().getUsercode());
        return R.ok(sellerBillQueryService.queryPageList(request));
    }

    @ApiOperation(value = "供应商侧-业务单导出")
    @PostMapping("/export")
    public R<Object> export(@ApiParam(value = "供应商侧-业务单导出请求" ,required=true ) @RequestBody @Validated SellerDeductExportRequest request) {
        request.setSellerNo(UserUtil.getUser().getUsercode());
        return sellerBillExportService.export(request);
    }

}
