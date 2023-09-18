package com.xforceplus.wapp.modules.audit.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.util.StringUtils;
import com.google.common.collect.Sets;
import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.dto.IdsDto;
import com.xforceplus.wapp.modules.audit.enums.AuditStatusEnum;
import com.xforceplus.wapp.modules.audit.schedule.BlueInvoiceAutoAuthScheduler;
import com.xforceplus.wapp.modules.audit.service.InvoiceAuditService;
import com.xforceplus.wapp.modules.audit.vo.InvoiceAuditVO;
import com.xforceplus.wapp.modules.backfill.service.BackFillService;
import com.xforceplus.wapp.repository.entity.InvoiceAudit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Api(tags = "发票审核")
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = EnhanceApiV1.BASE_PATH + "/invoice/audit")
public class InvoiceAuditController {
    private final InvoiceAuditService invoiceAuditService;

    @Autowired
    private BlueInvoiceAutoAuthScheduler blueInvoiceAutoAuthScheduler;
    @Autowired
    private BackFillService backFillService;

    @ApiOperation("查询发票审核列表")
    @GetMapping("")
    public R<PageResult<InvoiceAudit>> search(@RequestParam(required = false) String invoiceNo,
                                              @RequestParam(required = false) String invoiceCode,
                                              @RequestParam(required = false) String settlementNo,
                                              @RequestParam(required = false) String auditStatus,
                                              @RequestParam(defaultValue = "1") Integer pages,
                                              @RequestParam(defaultValue = "10") Integer size) {
        long start = System.currentTimeMillis();
        Tuple2<Long, List<InvoiceAudit>> r = invoiceAuditService.search(invoiceNo, invoiceCode, settlementNo, auditStatus, pages, size);
        log.info("查询发票审核列表,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(r._2, r._1, pages, size));
    }

    @ApiOperation("发票提交审核")
    @PostMapping("/{settlementNo}")
    public R<Boolean> add(@PathVariable("settlementNo") String settlementNo, @RequestBody InvoiceAuditVO audit) {
        long start = System.currentTimeMillis();
        boolean r = invoiceAuditService.add(settlementNo, audit.getUuids(), audit.getRemark(), NumberUtils.INTEGER_ZERO);
        log.info("发票审核提交,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(r);
    }

    @ApiOperation("发票审核")
    @PatchMapping("")
    public R<Boolean> audit(@RequestBody @Valid InvoiceAuditVO audit) {
        long start = System.currentTimeMillis();
        boolean r = invoiceAuditService.audit(audit.getUuids(), audit.getAuditStatus(), audit.getAuditRemark());
        log.info("发票审核提交,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(r);
    }

    @ApiOperation("发票审核删除")
    @DeleteMapping("")
    public R<Boolean> delete(@RequestBody IdsDto<String> uuids) {
        long start = System.currentTimeMillis();
        boolean r = invoiceAuditService.delete(uuids.getIds());
        log.info("发票审核删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(r);
    }

    @ApiOperation("发票审核状态")
    @GetMapping("/status")
    public R<Boolean> status(@RequestParam String uuids) {
        long start = System.currentTimeMillis();
        boolean r = invoiceAuditService.passAudit(uuids);
        log.info("发票审核状态,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(r);
    }

    @ApiOperation("自动审核job触发（蓝票）")
    @GetMapping("/job/execute")
    public R<Boolean> jobExecute() {
        blueInvoiceAutoAuthScheduler.execute();
        return R.ok(true);
    }

    @ApiOperation("自动审核根据蓝票信息触发")
    @PostMapping("/job/execute/{invoiceCode}/{invoiceNo}")
    public R<Boolean> jobExecute(@PathVariable("invoiceCode") String invoiceCode, @PathVariable("invoiceNo") String invoiceNo) {
        if (StringUtils.isEmpty(invoiceNo)) {
            return R.fail("参数有误");
        }
        List<InvoiceAudit> invoiceAuditList = invoiceAuditService.search(Sets.newHashSet(invoiceCode + invoiceNo));
        if (CollectionUtil.isEmpty(invoiceAuditList)) {
            return R.fail("未查询到发票审核记录");
        }
        InvoiceAudit invoiceAudit = invoiceAuditList.get(0);
        if (!AuditStatusEnum.NOT_AUDIT.getValue().equalsIgnoreCase(invoiceAudit.getAuditStatus())) {
            return R.fail("发票已经审核完成");
        }
        try {
            backFillService.autoAuthBlueFlush(invoiceAudit);
            return R.ok(true);
        } catch (EnhanceRuntimeException ee) {
            log.warn("蓝票自动审核异常:{},{}", invoiceAudit.getInvoiceUuid(), ee.getMessage());
        } catch (Exception e) {
            log.error("蓝票自动审核异常:{}", invoiceAudit.getInvoiceUuid(), e);
        }
        return R.fail("手动审核异常");
    }
}
