package com.xforceplus.wapp.modules.company.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.company.dto.CompanyUpdateRequest;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 索赔单业务逻辑
 */
@Slf4j
@RestController("/company")
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


}
