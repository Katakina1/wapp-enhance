package com.xforceplus.wapp.modules.blackwhitename.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
                                                                @ApiParam("税号") @RequestParam(required = false) String taxNo) {
        long start = System.currentTimeMillis();
        val page = speacialCompanyService.page(current, size);
        log.info("黑白名单信息分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("黑白名单信息导入")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file,
                         @ApiParam("导入类型 0黑名单 1白名单") @RequestParam(required = true) String type) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        if(StringUtils.isEmpty(type)){
            return R.fail("导入类型不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = speacialCompanyService.importData(file.getInputStream(),type);
        log.info("黑白名单信息导入,耗时:{}ms", System.currentTimeMillis() - start);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据", result.get())) : R.fail(result.getLeft());
    }


}
