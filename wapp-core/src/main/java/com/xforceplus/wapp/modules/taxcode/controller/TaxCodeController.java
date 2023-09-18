package com.xforceplus.wapp.modules.taxcode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.dto.IdsDto;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.models.TaxCodeLog;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Api(tags = "进项税编管理")
@Slf4j
@RestController
@RequestMapping(EnhanceApiV1.BASE_PATH)
public class TaxCodeController {
    private final TaxCodeServiceImpl taxCodeService;

    public TaxCodeController(TaxCodeServiceImpl taxCodeService) {
        this.taxCodeService = taxCodeService;
    }

    @ApiOperation("税编分页查询")
    @GetMapping("/tax/code")
    public R<PageResult<TaxCode>> getOverdue(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                             @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                             @ApiParam("税收分类编码") @RequestParam(required = false) String goodsTaxNo,
                                             @ApiParam("商品名称") @RequestParam(required = false) String itemName,
                                             @ApiParam("itemNo") @RequestParam(required = false) String itemNo,
                                             @ApiParam("税编中类编码") @RequestParam(required = false) String medianCategoryCode) {
        long start = System.currentTimeMillis();
        val page = taxCodeService.page(current, size, goodsTaxNo, itemName, itemNo, medianCategoryCode);
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }


    @ApiOperation("税编批量删除")
    @DeleteMapping("/tax/code")
    public R<Boolean> delOverdue(@RequestBody @Validated IdsDto<Long> idsDto) {
        long start = System.currentTimeMillis();
        List<TaxCodeEntity> entities = idsDto.getIds().stream().distinct()
                .map(it -> TaxCodeEntity.builder().id(it).deleteFlag(String.valueOf(System.currentTimeMillis()))
                        .updateUser(UserUtil.getUserId()).build())
                .collect(Collectors.toList());
        boolean update = taxCodeService.updateBatchById(entities);
        log.info("税编批量删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }


    @ApiOperation("同步税编到3.0")
    @GetMapping("/tax/code/sync")
    public R<String> taxCodeSync(@RequestParam Long id) {
        long start = System.currentTimeMillis();
        String taxCodeLog = taxCodeService.taxCodeSync(id);
        log.info("同步税编到3.0,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(taxCodeLog);
    }

    @ApiOperation("税编修改日志/修改记录列表")
    @GetMapping("/tax/code/log")
    public R<PageResult<TaxCodeLog>> taxCodeLog(@RequestParam(required = false) String itemNo,
                                                @RequestParam(required = false) String itemName,
                                                @RequestParam(required = false) String sellerName,
                                                @RequestParam(required = false) String sellerNo,
                                                @RequestParam(required = false) Integer auditStatus,
                                                @RequestParam(required = false) Integer sendStatus,
                                                @RequestParam(required = false) String begin,
                                                @RequestParam(required = false) String end,
                                                @RequestParam(required = false) String auditBegin,
                                                @RequestParam(required = false) String auditEnd,
                                                @ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Integer current,
                                                @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Integer size) {
        long start = System.currentTimeMillis();
        Page<TaxCodeLog> taxCodeLog = taxCodeService.taxCodeLog(itemNo, itemName, sellerName, sellerNo, auditStatus, sendStatus, begin, end, auditBegin, auditEnd, current, size);
        log.info("税编修改日志,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(taxCodeLog.getRecords(), taxCodeLog.getTotal(), taxCodeLog.getPages(), taxCodeLog.getSize()));
    }

    @ApiOperation("税编搜索列表")
    @GetMapping("/tax/code/search")
    public R<List<TaxCodeBean>> taxCodeSearch(@RequestParam(required = false) String taxCode) {
        long start = System.currentTimeMillis();
        Either<String, List<TaxCodeBean>> taxCodeLog = taxCodeService.searchTaxCode(null, taxCode, null);
        log.info("税编搜索列表,耗时:{}ms", System.currentTimeMillis() - start);
        if (taxCodeLog.isRight()) {
            return R.ok(taxCodeLog.get());
        }
        return R.fail(taxCodeLog.getLeft());
    }

    @ApiOperation("税编修改")
    @PutMapping("/tax/code")
    public R<Boolean> updateTaxCode(@RequestBody TaxCode taxCode, @RequestParam Integer isSeller) {
        long start = System.currentTimeMillis();
        boolean update = taxCodeService.updateTaxCode(taxCode, UserUtil.getUser(), isSeller==1);
        log.info("税编修改,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }

    @ApiOperation("税编审核")
    @PatchMapping("/tax/code/{id}")
    public R<Boolean> auditTaxCode(@PathVariable String id, @RequestParam Integer auditStatus,
                                   @RequestParam(required = false) String opinion) {
        long start = System.currentTimeMillis();
        boolean update = taxCodeService.auditTaxCode(id, auditStatus, opinion, UserUtil.getUser());
        log.info("税编审核,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }


    @ApiOperation("中台税编列表")
    @GetMapping("/tax/code/list")
    public R<List<TaxCodeBean>> overdueList(@ApiParam("过滤税率") @RequestParam(required = false) String taxRate,
                                            @ApiParam("税编简称") @RequestParam(required = false) String shortName,
                                            @ApiParam("查询数据") @RequestParam(required = false) String searchText) {
        log.info("中台税编列表查询,请求参数:{}-{}-{}", taxRate, shortName, searchText);
        long start = System.currentTimeMillis();
        Either<String, List<TaxCodeBean>> either;
        if (StringUtils.isNotBlank(shortName)) {
            either = taxCodeService.taxCodeQuery(taxRate, shortName, searchText);
        } else {
            either = taxCodeService.searchTaxCode(taxRate, null, searchText);
        }
        log.info("中台税编列表查询,耗时:{}ms", System.currentTimeMillis() - start);
        return either.isRight() ? R.ok(either.get()) : R.fail(either.getLeft());
    }

}
