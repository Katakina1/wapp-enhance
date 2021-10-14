package com.xforceplus.wapp.modules.overdue.controller;

import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.overdue.converters.OverdueConverter;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.modules.overdue.service.DefaultSettingServiceImpl;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.overdue.valid.OverdueCreateValidGroup;
import com.xforceplus.wapp.modules.overdue.valid.OverdueUpdateValidGroup;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@RestController
@Api(tags = "超期配置")
public class OverdueController {
    private final DefaultSettingServiceImpl defaultSettingService;
    private final OverdueServiceImpl overdueService;
    private final OverdueConverter overdueConverter;

    public OverdueController(OverdueServiceImpl overdueService, DefaultSettingServiceImpl defaultSettingService, OverdueConverter overdueConverter) {
        this.defaultSettingService = defaultSettingService;
        this.overdueService = overdueService;
        this.overdueConverter = overdueConverter;
    }

    @ApiOperation("超期配置分页查询")
    @GetMapping("/overdue")
    public R<PageResult<Overdue>> getOverdue(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                             @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                             @ApiParam("供应商名称") @RequestParam(required = false) String sellerName,
                                             @ApiParam("供应商税号") @RequestParam(required = false) String sellerTaxNo) {
        long start = System.currentTimeMillis();
        val page = overdueService.page(current, size, sellerName, sellerTaxNo);
        log.info("超期配置查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("更新超期配置")
    @PatchMapping("/overdue")
    public R<Boolean> updateOverdue(@RequestBody @Validated(OverdueUpdateValidGroup.class) OverdueDto overdue) {
        long start = System.currentTimeMillis();
        OverdueEntity map = overdueConverter.map(overdue);
        map.setUpdateUser(111L);
        boolean update = overdueService.updateById(map);
        log.info("超期配置更新,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }

    @ApiOperation("删除超期配置")
    @DeleteMapping("/overdue/{id}")
    public R<Boolean> delOverdue(@ApiParam("规则ID") @PathVariable Long id) {
        long start = System.currentTimeMillis();
        boolean update = overdueService.updateById(OverdueEntity.builder().id(id)
                .updateUser(111L)
                .deleteFlag(String.valueOf(System.currentTimeMillis())).build());
        log.info("超期配置删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }

    @ApiOperation("新增超期配置")
    @PostMapping("/overdue")
    public R<Boolean> addOverdue(@RequestBody @Validated(OverdueCreateValidGroup.class) OverdueDto overdue) {
        long start = System.currentTimeMillis();
        boolean update = overdueService.save(OverdueEntity.builder().sellerName(overdue.getSellerName())
                .sellerTaxNo(overdue.getSellerTaxNo()).overdueDay(overdue.getOverdueDay())
                .updateUser(111L).createUser(111L).build());
        log.info("超期配置新增,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }

    @SneakyThrows
    @ApiOperation("导入超期配置")
    @PutMapping("/overdue")
    public R<Integer> exportOverdue(@RequestParam("file") MultipartFile file) {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = overdueService.export(file.getInputStream());
        log.info("超期配置导入,耗时:{}ms", System.currentTimeMillis() - start);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据", result.get())) : R.fail(result.getLeft());
    }

    @ApiOperation("查询默认超期时间")
    @GetMapping("/overdue/default/day")
    public R<String> getOverdueDay() {
        long start = System.currentTimeMillis();
        String result = defaultSettingService.getOverdueDay();
        log.info("默认超期时间查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(result);
    }

    @ApiOperation("修改默认超期时间")
    @PatchMapping("/overdue/default/day/{day}")
    public R<Boolean> updateOverdueDay(@ApiParam("超期时间（天）") @PathVariable String day) {
        long start = System.currentTimeMillis();
        Boolean result = defaultSettingService.updateOverdueDay(day);
        log.info("默认超期时间修改,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(result);
    }
}
