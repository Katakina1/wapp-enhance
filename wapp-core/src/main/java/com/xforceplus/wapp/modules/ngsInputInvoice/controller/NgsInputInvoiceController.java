package com.xforceplus.wapp.modules.ngsInputInvoice.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.evat.common.domain.ngs.NgsInputInvoiceQuery;
import com.xforceplus.evat.common.entity.TDxNgsInputInvoiceEntity;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.ngsInputInvoice.dto.NgsInputInvoiceRequest;
import com.xforceplus.wapp.modules.ngsInputInvoice.service.impl.NgsInputInvoiceServiceImpl;
import com.xforceplus.wapp.util.BeanUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * 非商业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "ngx进项发票")
@RequestMapping(EnhanceApi.BASE_PATH + "/ngsInputInvoice")
public class NgsInputInvoiceController {

    @Autowired
    private NgsInputInvoiceServiceImpl ngsInputInvoiceService;

    @PostMapping("/list")
    public R<Page<TDxNgsInputInvoiceEntity>> abnormalListPaged(@RequestBody NgsInputInvoiceQuery ngdInputInvoiceQuery) {
        Page<TDxNgsInputInvoiceEntity> page = ngsInputInvoiceService.paged(ngdInputInvoiceQuery);
        return R.ok(page);
    }

    @PostMapping("/export")
    public R abnormalExport(@RequestBody NgsInputInvoiceRequest request) throws Exception {
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 10000) {
                return R.fail("最大导出数量不能超过一万条数据");
            }

            NgsInputInvoiceQuery queryDto = new NgsInputInvoiceQuery();
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TDxNgsInputInvoiceEntity> resultList = ngsInputInvoiceService.getByBatchIds(request.getIncludes());
            ngsInputInvoiceService.inputInvoiceExport(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }

            Integer count = ngsInputInvoiceService.count(request.getExcludes());
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }
            NgsInputInvoiceQuery queryDto = new NgsInputInvoiceQuery();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
            }
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TDxNgsInputInvoiceEntity> resultList = ngsInputInvoiceService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                ngsInputInvoiceService.inputInvoiceExport(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }

}
