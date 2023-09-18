package com.xforceplus.wapp.modules.deduct.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.dto.IdsDto;
import com.xforceplus.wapp.modules.claim.dto.ManagerSellerRequest;
import com.xforceplus.wapp.modules.deduct.dto.ImportResponse;
import com.xforceplus.wapp.modules.deduct.export.ImportListener;
import com.xforceplus.wapp.modules.deduct.service.ManagerSellerService;
import com.xforceplus.wapp.modules.deduct.vo.ManagerSellerVO;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.ManagerSellerSettingEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:22
 **/
@Slf4j
@Api(tags = "managerSeller")
@AllArgsConstructor
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH + "/manager/seller")
public class ManagerSellerController {
    private ManagerSellerService managerSellerService;

    @ApiOperation(value = "分页查询")
    @GetMapping(value = "")
    public R<PageResult<ManagerSellerSettingEntity>> page(@ApiParam(value = "分页查询", required = true) ManagerSellerRequest request) {
        Page<ManagerSellerSettingEntity> page = managerSellerService.getPage(request);
        return R.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "")
    public R<Void> delete(@ApiParam(value = "id集合", required = true) @RequestBody IdsDto<Long> ids) {
        if (CollectionUtils.isEmpty(ids.getIds())) {
            return R.fail("参数不能为空");
        }
        managerSellerService.deleteAndUnlock(ids.getIds(), UserUtil.getUserName());
        return R.ok(null, "处理成功");
    }

    @ApiOperation(value = "加锁解锁")
    @PatchMapping(value = "/{lockFlag}")
    public R<Void> lockAndUnlock(@ApiParam(value = "id集合", required = true) @RequestBody IdsDto<Long> ids, @PathVariable("lockFlag") Integer lockFlag) {
        managerSellerService.batchLock(ids.getIds(), lockFlag, UserUtil.getUserName());
        return R.ok(null, "处理成功");
    }

    @ApiOperation(value = "新增")
    @PostMapping(value = "")
    public R<Void> add(@ApiParam(value = "新增数据", required = true) @RequestBody ManagerSellerVO vo) {
        managerSellerService.bachInsertAndLock(Lists.newArrayList(vo), UserUtil.getUserName());
        return R.ok(null, "处理成功");
    }

    @ApiOperation("导入")
    @PutMapping("")
    public R<ImportResponse> batchImport(@ApiParam("导入的文件") @RequestParam MultipartFile file) {
        try {
            ImportListener<ManagerSellerVO> listener = new ImportListener<>(managerSellerService::importData);
            listener.setFailConsumer(managerSellerService::importFail);
            listener.addCheck(managerSellerService::checkImport);
            EasyExcel.read(file.getInputStream(), ManagerSellerVO.class, listener).sheet().doRead();
            ImportResponse response = new ImportResponse();
            if (listener.getRows() == 0) {
                response.setErrorMsg("未解析到数据");
                return R.ok(response);
            }
            response.setImportCount(listener.getRows());
            response.setPassCount(listener.getValidInvoices().size());
            response.setFailCount(listener.getInvalidInvoices().size());
            return R.ok(response, "处理成功");
        } catch (IOException e) {
            log.info("供应商锁定导入异常: {}", e.getMessage(), e);
            return R.fail("导入失败！系统异常");
        }
    }
}
