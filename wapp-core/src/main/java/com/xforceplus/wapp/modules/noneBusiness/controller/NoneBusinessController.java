package com.xforceplus.wapp.modules.noneBusiness.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.noneBusiness.dto.FileDownRequest;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * 非商业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "非商管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/noneBusiness")
public class NoneBusinessController {

    @Autowired
    private NoneBusinessService noneBusinessService;

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "上传电子发票")
    @PutMapping("/upload")
    public R upload(@ApiParam("文件") @RequestParam("file") MultipartFile file,
                    @ApiParam("业务类型") @RequestParam() String bussinessType,
                    @ApiParam("门店号") @RequestParam() String storeNo,
                    @ApiParam("发票上传门店") @RequestParam(required = false) String invoiceStoreNo,
                    @ApiParam("发票类型") @RequestParam() String invoiceType,
                    @ApiParam("货物发生期间") @RequestParam(required = false) String storeDate,
                    @ApiParam("业务单号") @RequestParam() String businessNo) {
        List<byte[]> ofd = new ArrayList<>();
        List<byte[]> pdf = new ArrayList<>();
        try {
            Set<String> fileNames = new HashSet<>();
            final String filename = file.getOriginalFilename();
            if (!fileNames.add(filename)) {
                return R.fail("文件[" + filename + "]重复上传！");
            }
            final String suffix = filename.substring(filename.lastIndexOf(".") + 1);
            if (StringUtils.isNotBlank(suffix)) {
                switch (suffix.toLowerCase()) {
                    case Constants.SUFFIX_OF_OFD:
                        //OFD处理
                        ofd.add(IOUtils.toByteArray(file.getInputStream()));
                        break;
                    case Constants.SUFFIX_OF_PDF:
                        // PDF 处理
                        pdf.add(IOUtils.toByteArray(file.getInputStream()));
                        break;
                    default:
                        throw new EnhanceRuntimeException("文件:[" + filename + "]类型不正确,应为:[ofd/pdf]");
                }
            } else {
                throw new EnhanceRuntimeException("文件:[" + filename + "]后缀名不正确,应为:[ofd/pdf]");
            }
            String batchNo = UUID.randomUUID().toString().replace("-", "");
            TXfNoneBusinessUploadDetailEntity entity = new TXfNoneBusinessUploadDetailEntity();
            entity.setBussinessType(bussinessType);
            entity.setStoreNo(storeNo);
            entity.setStoreDate(storeDate);
            entity.setBatchNo(batchNo);
            entity.setBussinessNo(businessNo);
            entity.setInvoiceType(invoiceType);
            entity.setInvoiceStoreNo(invoiceStoreNo);
            entity.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG);
            if (CollectionUtils.isNotEmpty(ofd)) {
                noneBusinessService.parseOfdFile(ofd, entity);
            }
            if (CollectionUtils.isNotEmpty(pdf)) {
                noneBusinessService.parsePdfFile(pdf, entity);
            }
            return R.ok(batchNo);
        } catch (Exception e) {
            log.error("上传过程中出现异常:" + e.getMessage(), e);
            return R.fail("上传过程中出现错误，请重试" + e.getMessage());
        }
    }

    @ApiOperation("非商上传信息分页查询")
    @GetMapping("/list/paged")
    public R<PageResult<TXfNoneBusinessUploadDetailEntity>> paged(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                                  @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                                  @ApiParam("业务类型") @RequestParam(required = false) String bussinessType,
                                                                  @ApiParam("门店号") @RequestParam(required = false) String storeNo,
                                                                  @ApiParam("发票上传门店") @RequestParam(required = false) String invoiceStoreNo,
                                                                  @ApiParam("发票类型") @RequestParam(required = false) String invoiceType,
                                                                  @ApiParam("验真状态") @RequestParam(required = false) String verifyStauts,
                                                                  @ApiParam("验签名状态") @RequestParam(required = false) String ofdStatus,
                                                                  @ApiParam("JV") @RequestParam(required = false) String jvCode,
                                                                  @ApiParam("供应商号") @RequestParam(required = false) String supplierId,
                                                                  @ApiParam("是否提交 0未提交 1已提交") @RequestParam(required = true) String type,
                                                                  @ApiParam("业务单号") @RequestParam(required = false) String businessNo) {
        long start = System.currentTimeMillis();
        Page<TXfNoneBusinessUploadDetailEntity> page = noneBusinessService.page(current, size, bussinessType,
                storeNo,
                invoiceStoreNo,
                invoiceType,
                verifyStauts,
                ofdStatus,
                jvCode,
                supplierId,
                type,
                businessNo);
        log.info("非商上传信息分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getPages(), page.getSize()));
    }

    /**
     * 电票的退票=纸票的退票+退单
     *
     * @return
     */
    @ApiOperation("发票预览")
    @RequestMapping(value = "/preview", method = {GET})
    public R preview(@ApiParam("id") @RequestParam() Long id) {
        TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity = this.noneBusinessService.getById(id);
        if (null == tXfNoneBusinessUploadDetailEntity) {
            return R.fail("没有找到对应的电票信息");
        }
        String result;
        try {
            result = fileService.downLoadFile(tXfNoneBusinessUploadDetailEntity.getUploadId());
        } catch (IOException e) {
            log.error("电票预览下载文件异常");
            return R.fail("电票预览下载文件异常");
        }
        if (tXfNoneBusinessUploadDetailEntity.getFileType().equals(String.valueOf(Constants.FILE_TYPE_OFD))) {

            return R.ok("data:image/jpeg;base64," + result);
        } else {
            return R.ok("data:application/pdf;base64," + result);
        }

    }

    /**
     * 电票的退票=纸票的退票+退单
     *
     * @return
     */
    @ApiOperation("下载源文件")
    @PostMapping(value = "/down")
    public void down(FileDownRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            throw new RRException("请选中数据后进行下载");
        }
        List<TXfNoneBusinessUploadDetailEntity> list = noneBusinessService.listByIds(request.getIds());
        if (CollectionUtils.isEmpty(list)) {
            throw new RRException("您所选发票不包含任何附件文件");
        }
        noneBusinessService.down(list,request);
    }


}
