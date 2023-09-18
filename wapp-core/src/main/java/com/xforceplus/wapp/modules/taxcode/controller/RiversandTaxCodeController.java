package com.xforceplus.wapp.modules.taxcode.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.dto.IdsDto;
import com.xforceplus.wapp.modules.taxcode.dto.RiversandTcQueryDto;
import com.xforceplus.wapp.modules.taxcode.dto.RiversandTcValidSubmitRequest;
import com.xforceplus.wapp.modules.taxcode.dto.RiversandtTcDto;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfTaxCodeRiversandExtDao;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;
import com.xforceplus.wapp.util.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pengtao@xforceplus.com
 */
@Slf4j
@RestController
@Api(tags = "Riversand税编同步管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/riversandtaxcode")
public class RiversandTaxCodeController {

    @Autowired
    private TaxCodeServiceImpl taxCodeService;
    @Autowired
    private TXfTaxCodeRiversandExtDao tXfTaxCodeRiversandExtDao;

    @ApiOperation("税编同步查询")
    @PostMapping("/list")
    public R<PageResult<RiversandtTcDto>> paged(@RequestBody RiversandTcQueryDto request) {
        log.info("Riversand税编同步查询--请求参数{}", JSON.toJSON(request));
        PageResult<RiversandtTcDto> page = taxCodeService.paged(request);
        return R.ok(page);
    }

    @PostMapping("/export")
    @ApiOperation(value = "勾选的税编同步信息导出")
    public R checkExport(@RequestBody RiversandTcValidSubmitRequest request) throws Exception {
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 10000) {
                return R.fail("最大导出数量不能超过一万条数据");
            }

            RiversandTcQueryDto queryDto = new RiversandTcQueryDto();
            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);
            LambdaQueryWrapper<TXfTaxCodeRiversandEntity> query = new LambdaQueryWrapper<>();
            query.in(TXfTaxCodeRiversandEntity::getId, request.getIncludes());
            List<TXfTaxCodeRiversandEntity> resultList = tXfTaxCodeRiversandExtDao.selectList(query);
            taxCodeService.export(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }
            //同步状态，为空查询全部，=1查询已同步包含-1,1,2,3,4,未同步包含0,5
            //日期处理
            taxCodeService.dealDate(request.getExcludes());
            //状态处理
            taxCodeService.dealSyncStatus(request.getExcludes());
            RiversandTcQueryDto queryDto = new RiversandTcQueryDto();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),queryDto);
            }

            Integer count = taxCodeService.queryCount(queryDto);
            if (count > 10000) {
                return R.fail("最大导出数量不能超过一万条");
            }

            queryDto.setPageNo(0);
            queryDto.setPageSize(10000);

            List<TXfTaxCodeRiversandEntity> resultList =taxCodeService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                taxCodeService.export(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }

/*    @ApiOperation("Riversand同步税编到3.0")
    @PostMapping("/taxcodesync")
    public R taxCodeSync(@RequestBody @Validated IdsDto<Long> ids) {
        long start = System.currentTimeMillis();
        String successMsg = "同步成功";
        String failMsg = "同步失败";
        AtomicInteger successNum = new AtomicInteger();
        AtomicInteger failNum = new AtomicInteger();
        ids.getIds().forEach( x -> {
           R r= taxCodeService.reTaxCodeSync(x);
           if(StringUtils.equals("1",r.getCode())||StringUtils.contains(r.getMessage(),"存在")){
               successNum.getAndIncrement();
           }else {
               failNum.getAndIncrement();
           }
        });

        log.info("Riversand同步税编到3.0,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(true,successMsg+successNum+"个,"+failMsg+failNum+"个");
    }*/

    @ApiOperation("Riversand同步税编到3.0")
    @PostMapping("/taxcodesync")
    public R taxCodeSync(@RequestBody RiversandTcValidSubmitRequest request) {
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行同步");
            }
            if (request.getIncludes().size() > 1000) {
                return R.fail("最大同步数量不能超过一千条数据");
            }

            RiversandTcQueryDto queryDto = new RiversandTcQueryDto();
            queryDto.setPageNo(0);
            queryDto.setPageSize(1000);
            LambdaQueryWrapper<TXfTaxCodeRiversandEntity> query = new LambdaQueryWrapper<>();
            query.in(TXfTaxCodeRiversandEntity::getId, request.getIncludes());
            List<TXfTaxCodeRiversandEntity> resultList = tXfTaxCodeRiversandExtDao.selectList(query);
            return taxCodeService.reTaxCodeSyncList(resultList);
        } else {
            if (Objects.isNull(request.getExcludes())) {
                return R.fail("查询条件不能为空");
            }
            //同步状态，为空查询全部，=1查询已同步包含-1,1,2,3,4,未同步包含0,5
            //日期处理
            taxCodeService.dealDate(request.getExcludes());
            //状态处理
            taxCodeService.dealSyncStatus(request.getExcludes());
            RiversandTcQueryDto queryDto = new RiversandTcQueryDto();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(), queryDto);
            }

            Integer count = taxCodeService.queryCount(queryDto);
            if (count > 1000) {
                return R.fail("最大同步数量不能超过一千条");
            }

            queryDto.setPageNo(0);
            queryDto.setPageSize(1000);

            List<TXfTaxCodeRiversandEntity> resultList = taxCodeService.queryByPage(queryDto);
            if (CollectionUtils.isNotEmpty(resultList)) {
                return taxCodeService.reTaxCodeSyncList(resultList);
            }
        }
        return R.fail("查询条件异常请检查后重试");
    }
}
