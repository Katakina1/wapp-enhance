package com.xforceplus.wapp.modules.company.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.blackwhitename.util.ExcelUtil;
import com.xforceplus.wapp.modules.company.dto.CompanyUpdateRequest;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import io.swagger.annotations.*;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 索赔单业务逻辑
 */
@Slf4j
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/company")
@Api(tags = "抬头信息管理")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @ApiOperation("抬头信息分页查询")
    @GetMapping("/list/paged")
    public R<PageResult<TAcOrgEntity>> getOverdue(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                  @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                  @ApiParam("税号") @RequestParam(required = false) String taxNo) {
        long start = System.currentTimeMillis();
        val page = companyService.page(current, size, taxNo);
        log.info("抬头信息分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("根据税号抬头信息修改")
    @GetMapping("/updateByTaxNo")
    public R updateByTaxNo(@RequestBody CompanyUpdateRequest companyUpdateRequest) {
        companyService.update(companyUpdateRequest);
        return R.ok();
    }

    @ApiOperation("抬头信息导入")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file
                         ) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = companyService.importData(file.getInputStream());
        log.info("抬头信息导入,耗时:{}ms", System.currentTimeMillis() - start);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据", result.get())) : R.fail(result.getLeft());
    }


}
