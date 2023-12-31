package com.xforceplus.wapp.modules.blackwhitename.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportSizeDto;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 索赔单业务逻辑
 */
@Slf4j
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/specialCompany")
@Api(tags = "黑白名单信息管理")
public class SpecialCompanyController {

    @Autowired
    private SpeacialCompanyService speacialCompanyService;

    @ApiOperation("黑白名单信息分页查询")
    @GetMapping("/list/paged")
    public R<PageResult<TXfBlackWhiteCompanyEntity>> getOverdue(@ApiParam("页数") @RequestParam(required = true, defaultValue = "1") Long current,
                                                                @ApiParam("条数") @RequestParam(required = true, defaultValue = "10") Long size,
                                                                @ApiParam("税号") @RequestParam(required = false) String supplierTaxNo,
                                                                @ApiParam("供应商编号6D") @RequestParam(required = false) String supplier6d,
                                                                @ApiParam("供应商编号") @RequestParam(required = false) String sapNo,
                                                                @ApiParam("类型类型 0黑名单 1白名单") @RequestParam(required = false) String type,
                                                                @ApiParam("创建时间开始") @RequestParam(required = false) String createTimeStart,
                                                                @ApiParam("创建时间结束") @RequestParam(required = false) String createTimeEnd,
                                                                @ApiParam("公司名称") @RequestParam(required = false) String companyName) {
        long start = System.currentTimeMillis();
        Page<TXfBlackWhiteCompanyEntity> page = speacialCompanyService.page(current, size, supplierTaxNo, companyName, type, createTimeStart, createTimeEnd, supplier6d, sapNo);
        log.info("黑白名单信息分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getPages(), page.getSize()));
    }

    @ApiOperation("黑白名单信息导入")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file,
                         @ApiParam("导入类型 0黑名单 1白名单") @RequestParam(required = true) String type) throws IOException {
    	String contentType = file.getContentType();
    	InputStream excelInputStream = file.getInputStream();
    	String originalFilename = file.getOriginalFilename().replaceAll("../", "").replaceAll("..\\\\", "").replaceAll("\\*", "");
        if (!StringUtils.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", contentType)) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        if (StringUtils.isEmpty(type)) {
            return R.fail("导入类型不能为空");
        }
        SpecialCompanyImportSizeDto result = null;
        if (Constants.COMPANY_TYPE_WHITE.equals(type)) {
            result = speacialCompanyService.importWhiteData(excelInputStream, type, originalFilename);
        } else {
            result = speacialCompanyService.importBlackData(excelInputStream, type, originalFilename);
        }
        if(StringUtils.isNotEmpty(result.getErrorMsg())){
            return R.fail(result.getErrorMsg());
        }
        if(result.getUnValidCount()==0){
            return R.ok("message","导入成功");
        }
        return  R.ok("message", String.format("导入[%d]条数据  导入成功[%d]条数据 导入失败[%d]条数据 导入失败数据请前往消息中心查看", result.getImportCount(),result.getValidCDount(),result.getUnValidCount()));
    }

    @ApiOperation("黑白名单批量删除")
    @DeleteMapping("/tax/code")
    public R<Boolean> delOverdue(@RequestBody @ApiParam("id集合") Long[] ids) {
        long start = System.currentTimeMillis();
        speacialCompanyService.deleteById(ids);
        log.info("黑白名单批量删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok();
    }


}
