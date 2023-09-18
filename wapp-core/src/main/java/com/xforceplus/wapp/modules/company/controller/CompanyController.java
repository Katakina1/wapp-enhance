package com.xforceplus.wapp.modules.company.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.enums.TaxDeviceTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.company.dto.CompanyUpdateRequest;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Either;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * 索赔单业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "抬头信息管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private LockClient lockClient;

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
    @PostMapping("/updateByTaxNo")
    public R<Object> updateByTaxNo(@RequestBody CompanyUpdateRequest companyUpdateRequest) {
        if(StringUtils.isEmpty(companyUpdateRequest.getTaxNo())){
            return  R.fail("税号不能为空");
        }
        if (Objects.nonNull(companyUpdateRequest.getTaxDeviceType())
                && Objects.isNull(TaxDeviceTypeEnum.fromValue(companyUpdateRequest.getTaxDeviceType()))) {
            return R.fail("未知税盘类型");
        }
        log.info("updateByTaxNo:{},userId:{}", JSON.toJSONString(companyUpdateRequest), UserUtil.getUserId());
        final String key = "updateByTaxNo:" + companyUpdateRequest.getTaxNo();
        Callable<Boolean> callable = () -> {
            // 查询原开票限额
            R<TAcOrgEntity> rogEntityR = getByTaxNo(companyUpdateRequest.getTaxNo());
            if (!R.OK.equals(rogEntityR.getCode())) {
                log.warn("查询原开票信息失败:{}", JSON.toJSONString(rogEntityR));
                return null;
            }
            // 开票限额是否修改
            boolean quotaUpdate = Objects.nonNull(companyUpdateRequest.getQuota()) && Objects.nonNull(rogEntityR.getResult().getQuota()) && companyUpdateRequest.getQuota().compareTo(BigDecimal.valueOf(rogEntityR.getResult().getQuota())) == 0;
            // 开票设备是否修改
            boolean deviceTypeUpdate = Objects.nonNull(companyUpdateRequest.getTaxDeviceType()) && !companyUpdateRequest.getTaxDeviceType().equals(rogEntityR.getResult().getTaxDeviceType());
            // 更新
            companyService.update(companyUpdateRequest);
            return quotaUpdate || deviceTypeUpdate;
        };
        Boolean flag = lockClient.tryLock(key, callable, -1, 1);
        if (flag == null) {
            return R.fail("修改失败，请稍后重试");
        }
        return R.ok(flag, "修改成功");
    }

    @ApiOperation("根据税号获取抬头信息")
    @GetMapping("/getByTaxNo")
    public R<TAcOrgEntity> getByTaxNo(@ApiParam("税号") @RequestParam(required = true) String taxNo) {
        String userCode = null;
        if(Objects.nonNull(UserUtil.getUser())&&StringUtils.isNotEmpty(UserUtil.getUser().getUsercode())){
            userCode = UserUtil.getUser().getUsercode();
        }
        TAcOrgEntity result = companyService.getByTaxNo(taxNo,userCode);
        if (result == null) { //从用户表补充
        	result = companyService.newGetByTaxNoAndUserCode(taxNo, userCode);
        }
        if (result == null) {
            return R.fail("未查询到对应的抬头信息");
        }
        // 默认航信单盘
        if (Objects.isNull(result.getTaxDeviceType())) {
            result.setTaxDeviceType(TaxDeviceTypeEnum.HX_DEVICE.code());
        }
        return R.ok(result);
    }

    @ApiOperation("抬头信息导入")
    @PutMapping("/import")
    public R<Object> batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = companyService.importData(file);
        log.info("抬头信息导入,耗时:{}ms", System.currentTimeMillis() - start);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据", result.get())) : R.fail(result.getLeft());
    }


    @ApiOperation("购方机构列表")
    @GetMapping("purchasers")
    public R<Object> purchaserOrg() {
        final List<TAcOrgEntity> purchaserOrgs = companyService.getPurchaserOrgs();
        return R.ok(Collections.singletonMap("orgs", purchaserOrgs));
    }
}
