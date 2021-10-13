package com.xforceplus.wapp.modules.taxcode.controller;

import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.modules.overdue.valid.OverdueCreateValidGroup;
import com.xforceplus.wapp.modules.overdue.valid.OverdueUpdateValidGroup;
import com.xforceplus.wapp.modules.taxcode.dto.IdsDto;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@RestController
@Api(tags = "进项税编管理")
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
                                             @ApiParam("itemNo") @RequestParam(required = false) String itemNo) {
        long start = System.currentTimeMillis();
        Tuple2<List<TaxCode>, Long> page = taxCodeService.page(current, size, goodsTaxNo, itemName, itemNo);
        log.info("税编分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._2, page._1));
    }


    @ApiOperation("税编批量删除")
    @DeleteMapping("/tax/code")
    public R<Boolean> delOverdue(@RequestBody IdsDto idsDto) {
        long start = System.currentTimeMillis();
        boolean update = taxCodeService.delete(idsDto.getIds());
        log.info("税编批量删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }
}
