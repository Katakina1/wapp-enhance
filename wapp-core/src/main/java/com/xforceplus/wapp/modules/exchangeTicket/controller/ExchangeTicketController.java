package com.xforceplus.wapp.modules.exchangeTicket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeGenerateRefundDto;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketQueryDto;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeTicketResultDto;
import com.xforceplus.wapp.modules.exchangeTicket.dto.ExchangeValidSubmitRequest;
import com.xforceplus.wapp.modules.exchangeTicket.service.ExchangeTicketService;
import com.xforceplus.wapp.repository.entity.TXfExchangeTicketEntity;
import com.xforceplus.wapp.util.BeanUtils;
import com.xforceplus.wapp.util.StaticString;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 换票业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "换票管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/exchangeTicket")
public class ExchangeTicketController {
    @Autowired
    private ExchangeTicketService exchangeTicketService;

    @ApiOperation("换票信息导入")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws IOException {
        if (!StaticString.EXCEL_TYPE.equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }

        return exchangeTicketService.importFile(file);
    }

    @PostMapping("check/export")
    @ApiOperation(value = "导出")
    public R checkExport(@RequestBody ExchangeValidSubmitRequest request) {

        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null && request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 500) {
                return R.fail("最大导出数量不能超过五百");
            }
            List<TXfExchangeTicketEntity> resultList = exchangeTicketService.getByIds(request.getIncludes());
            if (CollectionUtils.isNotEmpty(resultList)) {
                exchangeTicketService.export(resultList, request);
            }

            return R.ok("单据导出正在处理，请在消息中心");
        } else {
            Integer count = exchangeTicketService.count(request.getExcludes());
            if (count > 500) {
                return R.fail("最大导出数量不能超过五百");
            }
            TXfExchangeTicketEntity entity = new TXfExchangeTicketEntity();
            if (Objects.nonNull(request.getExcludes())) {
                BeanUtils.copyProperties(request.getExcludes(),entity);
            }
            Page<TXfExchangeTicketEntity> page = exchangeTicketService.pageAmount(0L, 500L, entity);
            List<TXfExchangeTicketEntity> resultList = page.getRecords();

            if (CollectionUtils.isNotEmpty(resultList)) {
                exchangeTicketService.export(resultList, request);
            }
            return R.ok("单据导出正在处理，请在消息中心查看");
        }


    }

    @ApiOperation("换票信息分页查询")
    @GetMapping("/list/paged")
    public R<PageResult<TXfExchangeTicketEntity>> paged(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                        @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                        ExchangeTicketQueryDto dto) {
        long start = System.currentTimeMillis();
        PageResult<TXfExchangeTicketEntity> page = exchangeTicketService.paged(current, size, dto);
        return R.ok(page);
    }

    @ApiOperation("换票信息分页查询")
    @GetMapping("/list/pagedAmount")
    public R<ExchangeTicketResultDto> pagedAmount(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                  @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                  TXfExchangeTicketEntity dto) {
        long start = System.currentTimeMillis();
        Page<TXfExchangeTicketEntity> page = exchangeTicketService.pageAmount(current, size, dto);
        BigDecimal taxAmount = BigDecimal.valueOf(0);
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        BigDecimal totalTax = BigDecimal.valueOf(0);
        List<TXfExchangeTicketEntity> list = page.getRecords();
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                TXfExchangeTicketEntity tXfExchangeTicketEntity = list.get(i);
                tXfExchangeTicketEntity.setExchangePaperDate(StaticString.formatDate(tXfExchangeTicketEntity.getExchangePaperDate()));
                if (null != list.get(i).getTaxAmount()) {
                    taxAmount = taxAmount.add(list.get(i).getTaxAmount());
                }
                if (null != list.get(i).getAmountWithTax()) {
                    totalAmount = totalAmount.add(list.get(i).getAmountWithTax());
                }

                if (null != list.get(i).getAmountWithoutTax()) {
                    totalTax = totalTax.add(list.get(i).getAmountWithoutTax());
                }
                if(StringUtils.isNotEmpty(list.get(i).getExchangeInvoiceNo())&&StringUtils.isNotEmpty(list.get(i).getExchangeInvoiceCode())){

                }
            }
        }
        ExchangeTicketResultDto dto1 = new ExchangeTicketResultDto();
        dto1.setResult(page);
        dto1.setTaxAmount(taxAmount);
        dto1.setTotalAmount(totalAmount);
        dto1.setTotalTax(totalTax);
        return R.ok(dto1);
    }

    @ApiOperation("审核换票信息")
    @PostMapping("/updateExchangeStatus")
    public R updateExchangeStatus(@RequestBody ExchangeValidSubmitRequest request) {
        if (CollectionUtils.isEmpty(request.getIncludes())) {
            return R.fail("请勾选信息后进行审核");
        }
        return exchangeTicketService.updateExchangeStatus(request);
    }
    
    @ApiOperation("修改凭证号信息")
    @PatchMapping("/update/voucherNo/{id}")
    public R updateVoucherNo(@PathVariable Long id, @RequestParam String voucherNo) {

        return exchangeTicketService.updateVoucherNo(id, voucherNo);
    }
    
    @ApiOperation("修改凭证号信息")
    @PostMapping("/update/voucherNo/import")
    public R updateVoucherNoImport(MultipartFile file) {
        if (!StaticString.EXCEL_TYPE.equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        return exchangeTicketService.updateVoucherNoImport(file);
    }

    @ApiOperation("生成退单号")
    @PostMapping("/generateRefund")
    public R generateRefund(@RequestBody ExchangeGenerateRefundDto request) {
        if (CollectionUtils.isEmpty(request.getIdList())) {
            return R.fail("请勾选信息后进行退票");
        }
        return exchangeTicketService.generateRefund(request);
    }

    @ApiOperation("获取换票信息")
    @GetMapping("/getExchangeInfoByuuid")
    public R<InvoiceDetailResponse> getExchangeInfoByuuid(@RequestParam(required = false) String  invoiceCode, @RequestParam(required = false) String invoiceNo) {
        return exchangeTicketService.getExchangeInfoByuuid(invoiceCode, invoiceNo);
    }

    @ApiOperation("上传记录批量批量删除")
    @DeleteMapping("/del")
    public R<String> delOverdue(@RequestBody @ApiParam("id集合") Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("请选中记录后删除");
        }
        long start = System.currentTimeMillis();
        exchangeTicketService.removeByIds(Arrays.asList(ids));
        log.info("上传记录批量批量删除,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok("删除成功");
    }
}
