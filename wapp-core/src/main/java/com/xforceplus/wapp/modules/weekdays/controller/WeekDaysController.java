package com.xforceplus.wapp.modules.weekdays.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.weekdays.dto.TXfMatchWeekdaysDto;
import com.xforceplus.wapp.modules.weekdays.service.WeekDaysService;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailDto;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadQueryDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * 工作日业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "工作日管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/weekDays")
public class WeekDaysController {

    @Autowired
    private WeekDaysService weekDaysService;


    @ApiOperation("工作日管理分页查询")
    @GetMapping("/list/paged")
    public R<PageResult<TXfMatchWeekdaysDto>> paged(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                    @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                    @ApiParam("开始时间") @RequestParam(required = false) String weekDayStart,
                                                    @ApiParam("结束时间") @RequestParam(required = false) String weekDayEnd) {
        long start = System.currentTimeMillis();
        val page = weekDaysService.page(current, size, weekDayStart, weekDayEnd);
        log.info("工作日管理分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }

    @ApiOperation("上传记录批量批量删除")
    @DeleteMapping("/del")
    public R<String> delete(@RequestBody @ApiParam("id集合") Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("请选中记录后删除");
        }
        long start = System.currentTimeMillis();
        weekDaysService.removeByIds(Arrays.asList(ids));
        log.info("上传记录批量批量删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok("删除成功");
    }

    @ApiOperation("工作日信息导入")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = weekDaysService.importData(file);
        log.info("工作日管理,耗时:{}ms", System.currentTimeMillis() - start);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据 导入失败数据请前往消息中心查看", result.get())) : R.fail(result.getLeft());
    }


}
