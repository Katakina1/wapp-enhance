package com.xforceplus.wapp.modules.entryaccount.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.AuthIgnore;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.vo.InvoiceSummonsExportVo;
import com.xforceplus.wapp.common.vo.InvoiceSummonsVo;
import com.xforceplus.wapp.enums.AuthStatusEnum;
import com.xforceplus.wapp.modules.entryaccount.dto.TDxSummonsRMSDto;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountResultDTO;
import com.xforceplus.wapp.modules.entryaccount.schedule.CustomersToBMSScheduler;
import com.xforceplus.wapp.modules.entryaccount.schedule.InvoiceReceiptToBMSScheduler;
import com.xforceplus.wapp.modules.entryaccount.service.EntryAccountService;
import com.xforceplus.wapp.modules.entryaccount.service.impl.SummonsRMSService;
import com.xforceplus.wapp.repository.entity.TDxSummonsRMSEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 入账接口
 * 该接口路径放行token校验
 * @Author: ChenHang
 * @Date: 2023/6/29 16:34
 */
@Slf4j
@RestController
@Api(tags = "RMS入账API")
@RequestMapping(EnhanceApi.BASE_PATH + "/entryAccount")
public class EntryAccountController {

    @Autowired
    private EntryAccountService entryAccountService;

    @Autowired
    private CustomersToBMSScheduler customersToBMSScheduler;

    @Autowired
    private InvoiceReceiptToBMSScheduler invoiceReceiptToBMSScheduler;

    @Autowired
    private SummonsRMSService summonsRMSService;

    // 接受&更新发票入账信息 目前该接口对接 RMS海关缴款书 & 非商发票 的入账信息
    @ApiOperation("入账功能")
    @PostMapping("/entryAccount")
    @AuthIgnore
    public R entryAccount(@RequestBody List<EntryAccountDTO> entryAccountDTOList) {
        log.info("RMS入账入参:{}", JSONObject.toJSONString(entryAccountDTOList));
        try {
            Map<String, List<EntryAccountResultDTO>> resultMap = entryAccountService.entryAccount(entryAccountDTOList);
            log.info("RMS入账回参:{}", JSONObject.toJSONString(resultMap));
            return R.ok(resultMap);
        } catch (Exception e) {
            log.error("RMS入账接口错误:", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     *
     * @return
     */
    @ApiOperation("手工触发获取BMS海关票明细及推送明细结果")
    @PostMapping("/customerToBMS")
    public R customerToBMS() {
        log.info("手工触发获取BMS海关票明细及推送明细结果定时任务开始处理======");
        customersToBMSScheduler.customerToBMS();
        log.info("手工触发获取BMS海关票明细及推送明细结果定时任务结束处理======");
        return R.ok();
    }

    /**
     *
     * @return
     */
    @ApiOperation("手工触发推送BMS发票签收结果")
    @PostMapping("/invoiceReceiptToBMS")
    public R invoiceReceiptToBMS() {
        log.info("手工触发推送BMS发票签收结果定时任务开始处理======");
        invoiceReceiptToBMSScheduler.invoiceReceiptToBMS();
        log.info("手工触发推送BMS发票签收结果定时任务结束处理======");
        return R.ok();
    }

    /**
     * RMS非商发票传票清单查询
     */
    @ApiOperation("RMS非商发票传票清单查询")
    @PostMapping("/invoiceSummons/list")
    public R invoiceSummonsList(@RequestBody InvoiceSummonsVo vo) {
        log.info("RMS非商发票传票清单查询入参:{}", JSONObject.toJSONString(vo));
        try {
            Page<TDxSummonsRMSEntity> page = summonsRMSService.invoiceSummonsList(vo);
            List<TDxSummonsRMSDto> response = new ArrayList<>();
            for (TDxSummonsRMSEntity entity : page.getRecords()) {
                TDxSummonsRMSDto tDxSummonsRMSDto = new TDxSummonsRMSDto();
                BeanUtil.copyProperties(entity, tDxSummonsRMSDto);
                response.add(tDxSummonsRMSDto);
            }
            log.info("RMS非商发票传票清单查询回参:{}", JSONObject.toJSONString(page));
            return R.ok(PageResult.of(response, page.getTotal(), vo.getPageNo(), vo.getPageSize()));
        } catch (Exception e) {
            log.error("RMS非商发票传票清单查询错误:", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * RMS非商发票传票清单导出
     */
    @ApiOperation("RMS非商发票传票清单导出")
    @PostMapping("/customsSummonsExport")
    public R customsSummonsExport(@RequestBody InvoiceSummonsExportVo request) {
        //页面存在勾选按照勾选的Id查询，没有勾选按照搜索条件查询
        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null || request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 2000) {
                return R.fail("最大选中导出数量不能超过2000条数据");
            }

            List<TDxSummonsRMSEntity> resultList = summonsRMSService.getByBatchIds(request.getIncludes());
            summonsRMSService.export(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {

            if(Objects.isNull(request.getExcludes())){
                return R.fail("查询条件不能为空");
            }
            InvoiceSummonsVo vo = request.getExcludes();
//            Integer count = summonsRMSService.queryCount(vo);
//            if (count > 10000) {
//                return R.fail("最大导出数量不能超过一万条");
//            }
//            vo.setPageNo(0);
//            vo.setPageSize(10000);
            List<TDxSummonsRMSEntity> resultList = summonsRMSService.invoiceSummonsList(vo).getRecords();
            if (CollectionUtils.isNotEmpty(resultList)) {
                summonsRMSService.export(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }
    }

}
