package com.xforceplus.wapp.modules.taxcode.controller;

import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.client.TaxCodeRsp;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.dto.IdsDto;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.models.TaxCodeTree;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
    public R<Boolean> delOverdue(@RequestBody @Validated IdsDto idsDto) {
        long start = System.currentTimeMillis();
        List<TaxCodeEntity> entities = idsDto.getIds().stream().distinct()
                .map(it -> TaxCodeEntity.builder().id(it).deleteFlag(String.valueOf(System.currentTimeMillis()))
                        .updateUser(UserUtil.getUserId()).build())
                .collect(Collectors.toList());
        boolean update = taxCodeService.updateBatchById(entities);
        log.info("税编批量删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }


    @ApiOperation("中台税编列表")
    @GetMapping("/tax/code/list")
    public R<List<TaxCodeBean>> overdueList(@RequestParam("查询数据") String searchText) {
        long start = System.currentTimeMillis();
        val either = taxCodeService.searchTaxCode(null, searchText);
        log.info("中台税编列表查询,耗时:{}ms", System.currentTimeMillis() - start);
        return either.isRight() ? R.ok(either.get()) : R.fail(either.getLeft());
    }
}
