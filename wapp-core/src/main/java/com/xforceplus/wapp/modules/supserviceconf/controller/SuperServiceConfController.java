package com.xforceplus.wapp.modules.supserviceconf.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportSizeDto;
import com.xforceplus.wapp.modules.discountRateLog.dto.DiscountRateLogDto;
import com.xforceplus.wapp.modules.discountRateLog.service.DiscountRateLogService;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.modules.supserviceconf.dto.SupSerConfValidSubmitRequest;
import com.xforceplus.wapp.modules.supserviceconf.dto.SuperServiceConfDto;
import com.xforceplus.wapp.modules.supserviceconf.dto.SuperServiceConfQueryDto;
import com.xforceplus.wapp.modules.supserviceconf.service.SuperServiceConfService;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.TAcUserEntity;
import com.xforceplus.wapp.util.BeanUtils;
import com.xforceplus.wapp.util.StaticString;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 供应商服务配置
 */
@Slf4j
@RestController
@Api(tags = "供应商服务配置")
@RequestMapping(EnhanceApi.BASE_PATH + "/superServiceConf")
public class SuperServiceConfController {
    @Autowired
    private SuperServiceConfService superServiceConfService;
    @Autowired
    private DiscountRateLogService discountRateLogService;

    @ApiOperation("供应商服务配置查询")
    @PostMapping("/list")
    public R<PageResult<SuperServiceConfDto>> paged(@RequestBody  SuperServiceConfQueryDto request) throws Exception {
        log.info("供应商服务配置查询--请求参数{}", JSON.toJSON(request));
        PageResult<SuperServiceConfDto> page = superServiceConfService.paged(request);
        return R.ok(page);
    }

    @ApiOperation("修改供应商服务配置")
    @PostMapping("/update")
    public R updateVoucherNo(@RequestBody  SuperServiceConfQueryDto request) {
        log.info("供应商服务配置更新--请求参数{}", JSON.toJSON(request));
        return superServiceConfService.update(request);
    }


    @ApiOperation("供应商服务配置批量导入更新")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws Exception {
        if (!StaticString.EXCEL_TYPE.equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }

        SpecialCompanyImportSizeDto result = superServiceConfService.uploadImportData(file);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(result.getErrorMsg())){
            return R.fail(result.getErrorMsg());
        }

        if(result.getUnValidCount()==0){
            return R.ok("message","导入成功");
        }
        return  R.ok("message", String.format("导入[%d]条数据  导入成功[%d]条数据 导入失败[%d]条数据 导入失败数据请前往消息中心查看", result.getImportCount(),result.getValidCDount(),result.getUnValidCount()));
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出")
    public R checkExport(@RequestBody SupSerConfValidSubmitRequest request) throws Exception {

        if ("false".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null && request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 500) {
                return R.fail("最大导出数量不能超过五百");
            }

            SuperServiceConfQueryDto queryDto = new SuperServiceConfQueryDto();
            //查询条件
            if(Objects.nonNull(request.getExcludes())){
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
                //userCode处理
                superServiceConfService.dealUserCode(queryDto);
            }
            //勾选条件处理
            if(CollectionUtils.isNotEmpty(request.getIncludes())){
                String userId = superServiceConfService.dealUserCodeStr(request.getIncludes());
                queryDto.setUserId(userId);
            }
            //最大500条
            queryDto.setPageNo(0);
            queryDto.setPageSize(500);

            List<TAcUserEntity> resultList = superServiceConfService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                superServiceConfService.export(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心");
        } else {
            Integer count = superServiceConfService.count(request.getExcludes());
            if (count > 500) {
                return R.fail("最大导出数量不能超过五百");
            }
            SuperServiceConfQueryDto queryDto = new SuperServiceConfQueryDto();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
            }
            queryDto.setPageNo(0);
            queryDto.setPageSize(500);
            List<TAcUserEntity> resultList = superServiceConfService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                superServiceConfService.export(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看");
        }
    }

    /**
     * 查询折让率整改日志
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/discountRateLog")
    public R<Page<OrgLogEntity>> getDiscountRateLog(@RequestBody DiscountRateLogDto request) throws Exception {
        Page<OrgLogEntity> page = discountRateLogService.getDiscountRateLog(request);
        return R.ok(page);
    }
}
