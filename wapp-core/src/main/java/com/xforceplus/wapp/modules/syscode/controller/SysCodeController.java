package com.xforceplus.wapp.modules.syscode.controller;

import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.syscode.dto.SysCodeDTO;
import com.xforceplus.wapp.modules.syscode.service.SysCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 系统小代码
 * @date : 2022/10/25 10:41
 **/
@Api(tags = "小代码管理")
@Slf4j
@RestController
@RequestMapping(EnhanceApiV1.BASE_PATH + "/syscode")
public class SysCodeController {

    @Resource
    private SysCodeService sysCodeService;

    @ApiOperation(value = "根据sysId和sysCode查询", tags = {"小代码"})
    @GetMapping("/{sysId}/{sysCode}")
    public R<SysCodeDTO> getSysCode(@PathVariable("sysId") String sysId, @PathVariable("sysCode") String sysCode) {
        log.info("getSysCode params:[{}]-[{}]", sysId, sysCode);
        return R.ok(sysCodeService.getOneBy(sysId, sysCode));
    }

    @ApiOperation(value = "新增", tags = {"小代码"})
    @PostMapping("/")
    public R add(@RequestBody @Valid SysCodeDTO dto) {
        log.info("addSysCode params:{}", dto);
        return R.ok(sysCodeService.add(dto));
    }

    @ApiOperation(value = "更新", tags = {"小代码"})
    @PutMapping("/")
    public R update(@RequestBody @Valid SysCodeDTO dto) {
        log.info("updateSysCode params:{}", dto);
        return R.ok(sysCodeService.update(dto));
    }

    @ApiOperation(value = "获取供应商侧结算单撤销按钮显示配置 true-显示", tags = {"小代码"})
    @GetMapping("/seller/destroy/settlement/{type}")
    public R<Boolean> getDestroySettlementFlag(@ApiParam(value = "结算单类型 1-索赔单 2-协议单") @PathVariable("type") String type) {
        return R.ok(sysCodeService.getDestroySettlementFlag(type));
    }
}
