package com.xforceplus.wapp.modules.blackwhitename.controller;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.modules.blackwhitename.util.ExcelUtil;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 索赔单业务逻辑
 */
@Slf4j
@Api(tags = "黑白名单信息管理")
public class SpecialCompanyController {

    @Autowired
    private SpeacialCompanyService speacialCompanyService;

    @ApiOperation("黑白名单信息分页查询")
    @GetMapping("/specialCompany/list/paged")
    public R<PageResult<TXfBlackWhiteCompanyEntity>> getOverdue(@ApiParam("页数") @RequestParam(required = true, defaultValue = "1") Long current,
                                                                @ApiParam("条数") @RequestParam(required = true, defaultValue = "10") Long size,
                                                                @ApiParam("税号") @RequestParam(required = false) String taxNo) {
        long start = System.currentTimeMillis();
        val page = speacialCompanyService.page(current, size);
        log.info("黑白名单信息分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("黑白名单信息导入")
    @PostMapping("/specialCompany/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true, defaultValue = "1") MultipartFile file,
                         @ApiParam("导入类型 0黑名单 1白名单") @RequestParam(required = true, defaultValue = "0") String type) {
        Workbook workbook = ExcelUtil.getWorkBook(file);
        List<TXfBlackWhiteCompanyEntity> resultList = speacialCompanyService.parseExcel(workbook);
        resultList.stream().forEach(entity -> {
            if (StringUtils.isNotEmpty(entity.getSupplier6d())) {
                entity.setSupplierType(type);
                TXfBlackWhiteCompanyEntity result = speacialCompanyService.getBlackListBy6D(entity.getSupplier6d());
                if (null != result) {
                    entity.setId(result.getId());
                }

            }
        });
        speacialCompanyService.saveOrUpdateBatch(resultList);
        return R.ok();
    }


}
