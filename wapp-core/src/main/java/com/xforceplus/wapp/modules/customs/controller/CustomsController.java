package com.xforceplus.wapp.modules.customs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.customs.InvoiceCheckEnum;
import com.xforceplus.wapp.modules.customs.convert.ManageStatusExportEnum;
import com.xforceplus.wapp.modules.customs.dto.*;
import com.xforceplus.wapp.modules.customs.service.CustomsDetailService;
import com.xforceplus.wapp.modules.customs.service.CustomsService;
import com.xforceplus.wapp.modules.customs.service.TDxCustomsSummonsService;
import com.xforceplus.wapp.modules.entryaccount.dto.CustomsSummonsDto;
import com.xforceplus.wapp.common.vo.CustomsSummonsExportVo;
import com.xforceplus.wapp.common.vo.CustomsSummonsVo;
import com.xforceplus.wapp.modules.entryaccount.service.EntryAccountService;
import com.xforceplus.wapp.repository.entity.TDxCustomsSummonsEntity;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import com.xforceplus.wapp.util.BeanUtils;
import com.xforceplus.wapp.util.StaticString;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 海关缴款书管理
 */
@Slf4j
@RestController
@Api(tags = "海关缴款书管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/customs")
public class CustomsController {
    @Autowired
    private CustomsService customsService;

    @Autowired
    private CustomsDetailService customsDetailService;

    @Autowired
    private EntryAccountService entryAccountService;

    @Autowired
    private TDxCustomsSummonsService customsSummonsService;

    @ApiOperation("海关缴款书勾选查询")
    @PostMapping("/list")
    public R<PageResult<CustomsDto>> paged(@RequestBody CustomsQueryDto request) {
        log.info("海关缴款书查询--请求参数{}", JSON.toJSON(request));
        //默认查询正常状态的数据
        request.setManageStatus(ManageStatusExportEnum.STATUS_1.getCode());

        PageResult<CustomsDto> page = customsService.paged(request);
        return R.ok(page);
    }

    @ApiOperation("海关缴款书入账查询")
    @PostMapping("/entryList")
    public R<PageResult<CustomsDto>> entryList(@RequestBody CustomsQueryDto request) {
        log.info("海关缴款书查询--请求参数{}", JSON.toJSON(request));

        PageResult<CustomsDto> page = customsService.entryPaged(request);
        return R.ok(page);
    }


    @ApiOperation("海关缴款书明细查询")
    @PostMapping("/detailList")
    public R<PageResult<CustomsDetailDto>> detailPaged(@RequestBody CustomsQueryDto request) {
        log.info("海关缴款书明细查询--请求参数{}", request);
        PageResult<CustomsDetailDto> page = customsDetailService.paged(request);
        return R.ok(page);
    }

    @ApiOperation("异常海关缴款书查询")
    @PostMapping("/abnormalList")
    public R<PageResult<CustomsDto>> abnormalListPaged(@RequestBody CustomsQueryDto request) {
        log.info("海关缴款书查询--请求参数{}", JSON.toJSON(request));
        //默认查询正常状态的数据
        request.setManageStatus(ManageStatusExportEnum.STATUS_0.getCode());

        PageResult<CustomsDto> page = customsService.paged(request);
        return R.ok(page);
    }


    @ApiOperation("海关缴款书全选金额统计")
    @PostMapping("/getCheckAmount")
    public R<CustomsAmountDto> getCheckAmount(@RequestBody CustomsQueryDto request) throws Exception {
        log.info("海关缴款书全选金额统计查询--请求参数{}", JSON.toJSON(request));
        CustomsAmountDto customsAmountDto = customsService.getCheckAmount(request);
        return R.ok(customsAmountDto);
    }

    @ApiOperation("首页认证状态数量统计")
    @PostMapping("/count")
    public R<List<QueryCustomsTabResponse>> count() throws Exception {
        log.info("首页认证状态数量统计查询");
        return R.ok(customsService.queryAuthCount());
    }

    @ApiOperation("根据Id获取详情")
    @GetMapping("/getById")
    public R<TDxCustomsEntity> getById(@ApiParam("Id") @RequestParam(required = true) String id){
        TDxCustomsEntity result = customsService.getById(id);
        return R.ok(result);
    }


    @ApiOperation("修改有效税款金额所属期凭证号")
    @PostMapping("/updateCustoms")
    public R updateCustoms(@Validated @RequestBody CustomsUpdateRequest request) {
        log.info("海关缴款书更新--请求参数{}", JSON.toJSON(request));
        return customsService.updateCustoms(request);
    }

    @ApiOperation("手工录入海关票")
    @PostMapping("/saveCustoms")
    public R saveCustoms(@Validated @RequestBody CustomsSaveRequest request) {
        log.info("海关缴款书手工录入--请求参数{}", JSON.toJSON(request));
        return customsService.saveCustoms(request);
    }

    @ApiOperation("海关缴款书批量导入更新")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws Exception {
        if (!StaticString.EXCEL_TYPE.equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }

        CustomsImportSizeDto result = customsService.uploadImportData(file);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(result.getErrorMsg())){
            return R.fail(result.getErrorMsg());
        }

        if(result.getUnValidCount()==0){
            return R.ok("message","导入成功");
        }
        return  R.ok("message", String.format("导入[%d]条数据  导入成功[%d]条数据 导入失败[%d]条数据 导入失败数据请前往消息中心查看", result.getImportCount(),result.getValidCDount(),result.getUnValidCount()));
    }

    @PostMapping("/export")
    @ApiOperation(value = "海关票勾选导出")
    public R checkExport(@RequestBody CustomsValidSubmitRequest request) throws Exception {
        String type = "抵扣勾选";
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 10000) {
                return R.fail("最大导出数量不能超过一万条数据");
            }

            CustomsQueryDto queryDto = new CustomsQueryDto();
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TDxCustomsEntity> resultList = customsService.getByBatchIds(request.getIncludes());
            customsService.export(resultList, request,type);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                    return R.fail("查询条件不能为空");
            }
            //默认查询正常状态的数据
            request.getExcludes().setManageStatus(ManageStatusExportEnum.STATUS_1.getCode());
            CustomsQueryDto queryDto = new CustomsQueryDto();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
            }

            //WALMART-3277 4,6,8状态联查
            customsService.dealIsCheck(queryDto);
            //日期处理
            customsService.dealDate(queryDto);

            Integer count = customsService.queryCount(queryDto);
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }

            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TDxCustomsEntity> resultList = customsService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                customsService.export(resultList, request,type);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }


    @PostMapping("/abnormalExport")
    @ApiOperation(value = "异常状态海关票导出")
    public R abnormalExport(@RequestBody CustomsValidSubmitRequest request) throws Exception {
        String type = "异常状态";
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 10000) {
                return R.fail("最大导出数量不能超过一万条数据");
            }

            CustomsQueryDto queryDto = new CustomsQueryDto();
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TDxCustomsEntity> resultList = customsService.getByBatchIds(request.getIncludes());
            customsService.abnormalExport(resultList, request,type);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }

            //默认查询正常状态的数据
            request.getExcludes().setManageStatus(ManageStatusExportEnum.STATUS_0.getCode());
            Integer count = customsService.count(request.getExcludes());
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }
            CustomsQueryDto queryDto = new CustomsQueryDto();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
            }
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            //日期处理
            customsService.dealDate(queryDto);

            List<TDxCustomsEntity> resultList = customsService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                customsService.abnormalExport(resultList, request,type);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }


    @PostMapping("/entryExport")
    @ApiOperation(value = "海关票入账导出")
    public R entryExport(@RequestBody CustomsValidSubmitRequest request) throws Exception {
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 10000) {
                return R.fail("最大导出数量不能超过一万条数据");
            }

            CustomsQueryDto queryDto = new CustomsQueryDto();
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TDxCustomsEntity> resultList = customsService.getByBatchIds(request.getIncludes());
            customsService.entryExport(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }

            Integer count = customsService.count(request.getExcludes());
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }
            CustomsQueryDto queryDto = new CustomsQueryDto();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
            }
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            //日期处理
            customsService.dealDate(queryDto);

            customsService.dealCustomsNo(queryDto);

            //入账中的勾选状态全选处理
            if(StringUtils.equals("all",queryDto.getIsCheck())){
                queryDto.setIsCheck("");
            } else if(StringUtils.equals(InvoiceCheckEnum.CHECK_4.toString(),queryDto.getIsCheck())){
                //勾选状态处理
                List<String> isChecks = Arrays.asList(InvoiceCheckEnum.CHECK_4.getValue().toString(),InvoiceCheckEnum.CHECK_6.getValue().toString(),
                        InvoiceCheckEnum.CHECK_8.getValue().toString());

                queryDto.setIsCheck(isChecks.stream().collect(Collectors.joining(",")));
            }
            List<TDxCustomsEntity> resultList = customsService.queryEntryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                customsService.entryExport(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }

    @ApiOperation("海关缴款书勾选认证")
    @PostMapping("/check")
    public R check(@RequestBody CustomsCheckRequest request) throws Exception {
        log.info("海关缴款书查询--请求参数{}", JSON.toJSON(request));
        return customsService.check(request);
    }

    @ApiOperation("首页入账状态数量统计")
    @PostMapping("/entry/count")
    public R<List<QueryCustomsTabResponse>> entryCount() throws Exception {
        log.info("首页入账状态数量统计查询");
        return R.ok(customsService.queryEntryCount());
    }

    @ApiOperation("海关缴款书入账")
    @PostMapping("/entry/account")
    public R entry(@RequestBody CustomsEntryRequest request) throws Exception {
        log.info("海关缴款书入账--请求参数{}", JSON.toJSON(request));
        return customsService.entry(request);
    }

    @ApiOperation("海关缴款书批量导入更新凭证号")
    @PutMapping("/entry/import")
    public R entryBatchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws Exception {
        if (!StaticString.EXCEL_TYPE.equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }

        CustomsImportSizeDto result = customsService.uploadImportData(file);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(result.getErrorMsg())){
            return R.fail(result.getErrorMsg());
        }

        if(result.getUnValidCount()==0){
            return R.ok("message","导入成功");
        }
        return  R.ok("message", String.format("导入[%d]条数据  导入成功[%d]条数据 导入失败[%d]条数据 导入失败数据请前往消息中心查看", result.getImportCount(),result.getValidCDount(),result.getUnValidCount()));
    }

    /**
     * 海关缴款书单条主动获取明细并比对结果
     */
    @GetMapping("/activeCustomerToBMS/{id}")
    public R activeCustomerToBMS(@PathVariable String id) {
        try {
            entryAccountService.activeCustomerToBMS(id);
            return R.ok(null, "手工比对成功!");
        } catch (Exception e) {
            log.error("主动触发海关缴款书明细比对失败");
            return R.fail(e.getMessage());
        }
    }

    /**
     * 海关缴款书传票清单查询
     */
    @PostMapping("/queryCustomsSummonsList")
    public R queryCustomsSummonsList(@RequestBody CustomsSummonsVo request) {
        log.info("海关缴款书传票清单查询--请求参数{}", JSONObject.toJSONString(request));
        PageResult<CustomsSummonsDto> pageResult = customsSummonsService.queryCustomsSummonsList(request);
        return R.ok(pageResult);
    }

    /**
     * 海关缴款书传票清单导出
     */
    @PostMapping("/customsSummonsExport")
    public R customsSummonsExport(@RequestBody CustomsSummonsExportVo request) {
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 2000) {
                return R.fail("最大选中导出数量不能超过2000条数据");
            }

            List<TDxCustomsSummonsEntity> resultList = customsSummonsService.getByBatchIds(request.getIncludes());
            customsSummonsService.export(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }
            CustomsSummonsVo vo = request.getExcludes();
            Integer count = customsSummonsService.queryCount(vo);
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }
            vo.setPageNo(0);
            vo.setPageSize(10000);
            List<TDxCustomsSummonsEntity> resultList = customsSummonsService.queryByPage(vo);
            if (CollectionUtils.isNotEmpty(resultList)) {
                customsSummonsService.export(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }

}
