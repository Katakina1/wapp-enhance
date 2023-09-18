package com.xforceplus.wapp.modules.discountRateSetting.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.discountRateLog.dto.OrgDto;
import com.xforceplus.wapp.modules.discountRateLog.service.DiscountRateLogService;
import com.xforceplus.wapp.modules.discountRateSetting.dto.OrgExportRequest;
import com.xforceplus.wapp.modules.discountRateSetting.service.OrgService;
import com.xforceplus.wapp.modules.discountRateSetting.service.impl.OrgServiceImpl;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import com.xforceplus.wapp.util.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Api(tags = "供应商表查询")
@RequestMapping(EnhanceApi.BASE_PATH + "/org")
public class OrgController {

    @Autowired
    private OrgServiceImpl orgService;
    /**
     * 查询供应商信息
     * @return
     */
    @PostMapping("/list")
    public R<Page<TAcOrgEntity>> listPaged(@RequestBody OrgDto vo) {
        Page<TAcOrgEntity> page = orgService.paged(vo);
        return R.ok(page);
    }
    @PostMapping("/export")
    @ApiOperation(value = "供应商限额勾选导出")
    public R checkExport(@RequestBody OrgExportRequest request) throws Exception {
        String type = "抵扣勾选";
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 10000) {
                return R.fail("最大导出数量不能超过一万条数据");
            }

            OrgDto queryDto = new OrgDto();
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TAcOrgEntity> resultList = orgService.getByBatchIds(request.getIncludes());
            orgService.orgExport(resultList, request,type);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }
            OrgDto queryDto = new OrgDto();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
            }
            Integer count = orgService.count(request.getExcludes());
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TAcOrgEntity> resultList = orgService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                orgService.orgExport(resultList, request,type);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }
}
