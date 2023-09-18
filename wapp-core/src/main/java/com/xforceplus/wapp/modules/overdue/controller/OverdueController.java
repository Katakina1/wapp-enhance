package com.xforceplus.wapp.modules.overdue.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.dto.IdsDto;
import com.xforceplus.wapp.enums.DefaultSettingEnum;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.modules.overdue.converters.OverdueConverter;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.dto.RedSwitchDto;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.modules.overdue.service.DefaultSettingServiceImpl;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.overdue.valid.OverdueCreateValidGroup;
import com.xforceplus.wapp.modules.overdue.valid.OverdueUpdateValidGroup;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import com.xforceplus.wapp.util.StaticString;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Either;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Api(tags = "超期配置管理")
@Slf4j
@RestController
@RequestMapping(EnhanceApiV1.BASE_PATH)
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
                                             @ApiParam(value = "配置类型 1.索赔、2.协议、3.EPD", required = true)
                                             @RequestParam Integer type,
                                             @ApiParam("供应商名称") @RequestParam(required = false) String sellerName,
                                             @ApiParam("供应商编号") @RequestParam(required = false) String sellerNo,
                                             @ApiParam("供应商税号") @RequestParam(required = false) String sellerTaxNo) {
        long start = System.currentTimeMillis();
        R<PageResult<Overdue>> r = ValueEnum.getEnumByValue(ServiceTypeEnum.class, type).map(it -> {
            val page = overdueService.page(current, size,
                    it, sellerName, sellerNo, sellerTaxNo);
            return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
        }).orElse(R.fail("配置类型不正确"));
        log.info("超期配置查询,耗时:{}ms", System.currentTimeMillis() - start);
        return r;
    }

    @ApiOperation("更新超期配置")
    @PatchMapping("/overdue")
    public R<Boolean> updateOverdue(@RequestBody @Validated(OverdueUpdateValidGroup.class) OverdueDto overdue) {
        long start = System.currentTimeMillis();
        OverdueEntity map = overdueConverter.map(overdue);
        map.setUpdateUser(UserUtil.getUserName());
        boolean update = overdueService.updateById(map);
        log.info("超期配置更新,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }

    @ApiOperation("删除超期配置")
    @DeleteMapping("/overdue/{id}")
    public R<Boolean> delOverdue(@ApiParam(value = "规则ID", required = true) @PathVariable Long id) {
        long start = System.currentTimeMillis();
        boolean update = overdueService.updateById(OverdueEntity.builder().id(id).updateUser(UserUtil.getUserName())
                .deleteFlag(String.valueOf(System.currentTimeMillis())).build());
        log.info("超期配置删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }

    @ApiOperation("新增超期配置")
    @PostMapping("/overdue")
    public R<Boolean> addOverdue(@RequestBody @Validated(OverdueCreateValidGroup.class) OverdueDto overdue) {
        long start = System.currentTimeMillis();
        val hasExist = new LambdaQueryChainWrapper<>(overdueService.getBaseMapper())
                .isNull(OverdueEntity::getDeleteFlag)
                .eq(OverdueEntity::getType, overdue.getType())
                .eq(OverdueEntity::getSellerTaxNo, overdue.getSellerTaxNo())
                .or()
                .isNull(OverdueEntity::getDeleteFlag)
                .eq(OverdueEntity::getType, overdue.getType())
                .eq(OverdueEntity::getSellerNo, overdue.getSellerNo())
                .count();
        if (hasExist > 0) {
            return R.fail("配置已存在");
        }
        OverdueEntity map = overdueConverter.map(overdue, UserUtil.getUserName());
        boolean update = overdueService.save(map);
        log.info("超期配置新增,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(update);
    }

    @SneakyThrows
    @ApiOperation("导入超期配置")
    @PutMapping("/overdue/{type}")
    public R<Integer> exportOverdue(@ApiParam(value = "超期配置类型", required = true) @PathVariable Integer type,
                                    @ApiParam(value = "文件", required = true) @RequestParam("file") MultipartFile file) {
        ServiceTypeEnum typeEnum = ValueEnum.getEnumByValue(ServiceTypeEnum.class, type).orElseThrow(() -> new RuntimeException("超期配置类型不正确"));
        if (!StaticString.EXCEL_TYPE.equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = overdueService.export(typeEnum, file.getInputStream());
        log.info("超期配置导入,耗时:{}ms", System.currentTimeMillis() - start);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据", result.get())) : R.fail(result.getLeft());
    }

    @ApiOperation("查询默认超期时间")
    @GetMapping("/overdue/{type}/day")
    public R<Integer> getOverdueDay(@ApiParam(value = "超期配置类型 1.索赔、2.协议、3.EPD", required = true)
                                    @PathVariable Integer type) {
        long start = System.currentTimeMillis();
        R<Integer> r = ValueEnum.getEnumByValue(DefaultSettingEnum.class, type).map(it -> {
            Integer result = defaultSettingService.getOverdueDay(it);
            return R.ok(result);
        }).orElse(R.fail("超期配置类型不正确"));
        log.info("默认超期时间查询,耗时:{}ms", System.currentTimeMillis() - start);
        return r;
    }

    @ApiOperation("修改默认超期时间")
    @PatchMapping("/overdue/{type}/day/{day}")
    public R<Boolean> updateOverdueDay(@ApiParam(value = "超期配置类型 1.索赔、2.协议、3.EPD", required = true)
                                       @PathVariable Integer type,
                                       @ApiParam(value = "超期时间（天）", required = true) @PathVariable Integer day) {
        long start = System.currentTimeMillis();
        if (day <= 0) {
            return R.fail("超期时间必须大于等于0");
        }
        R<Boolean> r = ValueEnum.getEnumByValue(DefaultSettingEnum.class, type).map(it -> {
            Boolean result = defaultSettingService.updateOverdueDay(it, day, UserUtil.getUserName());
            return R.ok(result);
        }).orElse(R.fail("超期配置类型不正确"));
        log.info("默认超期时间修改,耗时:{}ms", System.currentTimeMillis() - start);
        return r;
    }

    @ApiOperation("修改红字信息开关时间")
    @PostMapping("/red/switch")
    public R<Boolean> updateRedSwitch(@RequestBody IdsDto<RedSwitchDto> value) {
        long start = System.currentTimeMillis();
        Optional<RedSwitchDto> valid = value.getIds().stream().filter(dto -> dto.getEnd().compareTo(dto.getStart()) < 0).findAny();
        if (valid.isPresent()) {
            return R.fail("开始时间不能在结束时间之后");
        }
        Boolean r = defaultSettingService.updateOrSaveRedSwitch(value.getIds(), UserUtil.getUserName());
        log.info("修改红字信息开关时间,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(r);
    }

    @ApiOperation("删除红字信息开关时间")
    @DeleteMapping("/red/switch")
    public R<Boolean> deleteRedSwitch(@RequestBody IdsDto<Long> ids) {
        long start = System.currentTimeMillis();
        Boolean r = defaultSettingService.deleteRedSwitch(ids.getIds());
        log.info("删除红字信息开关时间,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(r);
    }

    @ApiOperation("查询红字信息开关时间")
    @GetMapping("/red/switch")
    public R<PageResult<RedSwitchDto>> getRedSwitch(@RequestParam(defaultValue = "1") Integer pages,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        long start = System.currentTimeMillis();
        List<RedSwitchDto> r = defaultSettingService.getRedSwitch().stream().map(it -> {
            RedSwitchDto dto = new RedSwitchDto();
            dto.setId(it._1);
            dto.setStart(it._2);
            dto.setEnd(it._3);
            return dto;
        }).collect(Collectors.toList());
        if (r.size() < pages * size && pages > 1) {
            return R.ok(PageResult.of(Lists.newArrayList(), r.size(), pages, size));
        }
        //一年就十几条没有使用数据库分页
        List<List<RedSwitchDto>> partition = Lists.partition(r, size);
        log.info("查询红字信息开关时间,耗时:{}ms", System.currentTimeMillis() - start);
        if (CollectionUtils.isEmpty(partition)) {
            return R.ok(PageResult.of(new ArrayList<>(), r.size(), pages, size));
        }
        return R.ok(PageResult.of(partition.get(pages -1), r.size(), pages, size));
    }
}
