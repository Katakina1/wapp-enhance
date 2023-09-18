package com.xforceplus.wapp.modules.noneBusiness.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backfill.service.FileService;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportSizeDto;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.noneBusiness.convert.NoneBusinessConverter;
import com.xforceplus.wapp.modules.noneBusiness.dto.*;
import com.xforceplus.wapp.modules.noneBusiness.service.MtrSaveService;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * 非商业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "非商管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/noneBusiness")
public class NoneBusinessController {

    ExecutorService commonThreadPool = new ThreadPoolExecutor(10, 20, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2000));

    @Autowired
    private NoneBusinessService noneBusinessService;
    @Autowired
    private RecordInvoiceService recordInvoiceService;

    @Autowired
    private FileService fileService;
    @Autowired
    private NoneBusinessConverter noneBusinessConverter;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private MtrSaveService mtrSaveService;

    @ApiOperation(value = "上传电子发票")
    @PutMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public R upload(@ApiParam("文件") @RequestParam("files") MultipartFile[] file,
                    @ApiParam("业务类型") @RequestParam() String bussinessType,
                    @ApiParam("门店号") @RequestParam(required = false) String storeNo,
                    @ApiParam("发票上传门店") @RequestParam(required = false) String invoiceStoreNo,
                    @ApiParam("发票类型") @RequestParam() String invoiceType,
                    @ApiParam("货物发生期间开始") @RequestParam(required = false) String storeStart,
                    @ApiParam("货物发生期间结束") @RequestParam(required = false) String storeEnd,
                    @ApiParam("备注") @RequestParam(required = false) String remark,
                    @ApiParam("业务单号") @RequestParam(required = false) String businessNo) {

        // 去除为空的文件
        List<MultipartFile> fileList = Arrays.stream(file).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fileList)) {
            return R.fail("请选择您要上传的电票文件(pdf/ofd/xml)");
        }
        // 校验 - 文件上限控制
        if (fileList.size() > 10) {
            return R.fail("最多一次性上传10个文件");
        }
        // 校验 - 同批次中重复文件名称校验
        Set<String> uniqueElements = new HashSet<>();
        String duplicateFileName = fileList.stream().map(MultipartFile::getOriginalFilename)
                .filter(n -> !uniqueElements.add(n)).collect(Collectors.joining(","));
        if (StringUtils.isNotBlank(duplicateFileName)) {
            return R.fail("文件[" + duplicateFileName + "]重复上传！");
        }
        // 校验 - 文件类型校验
        List<String> supportFileType = Arrays.asList(Constants.SUFFIX_OF_OFD, Constants.SUFFIX_OF_PDF, Constants.SUFFIX_OF_XML);
        String noSuppTypeFileName = fileList.stream().map(MultipartFile::getOriginalFilename)
                .filter(s -> !supportFileType.contains(FilenameUtils.getExtension(s))).collect(Collectors.joining(","));
        if (StringUtils.isNotBlank(noSuppTypeFileName)) {
            return R.fail("文件[" + noSuppTypeFileName + "]类型不正确,应为:[ofd/pdf/xml]！");
        }

        try {
            StringBuilder batchNo = new StringBuilder();
            batchNo.append(UserUtil.getLoginName()).append("_").append(DateUtils.getNo(5));
            List<TXfNoneBusinessUploadDetailEntity> list = new ArrayList<>();

            ExecutorCompletionService<TXfNoneBusinessUploadDetailEntity> completionService = new ExecutorCompletionService(commonThreadPool);

            for (MultipartFile multipartFile : fileList) {
                TXfNoneBusinessUploadDetailEntity entity = new TXfNoneBusinessUploadDetailEntity();
                entity.setBussinessType(bussinessType);
                entity.setStoreNo(storeNo);
                entity.setStoreStart(storeStart);
                entity.setStoreEnd(storeEnd);
                entity.setBatchNo(batchNo.toString());
                entity.setBussinessNo(businessNo);
                entity.setInvoiceType(invoiceType);
                entity.setInvoiceStoreNo(invoiceStoreNo);
                entity.setRemark(remark);
                entity.setCreateUser(UserUtil.getLoginName());
                entity.setUpdateUser(UserUtil.getLoginName());
                entity.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG);
                entity.setFileName(multipartFile.getOriginalFilename());
                completionService.submit(() -> noneBusinessService.parseFile(multipartFile, entity));
            }
            for (MultipartFile ignored : fileList) {
                try {
                    TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity = completionService.take().get();
                    log.info("处理结果={}", JSON.toJSONString(tXfNoneBusinessUploadDetailEntity));
                    list.add(tXfNoneBusinessUploadDetailEntity);
                } catch (Exception e) {
                    log.info("error=", e);
                }
            }
            return R.ok(list);
        } catch (Exception e) {
            log.error("上传过程中出现异常:" + e.getMessage(), e);
            return R.fail("上传过程中出现错误，请重试" + e.getMessage());
        }
    }

    @ApiOperation("非商上传信息分页查询")
    @GetMapping("/list/paged")
    public R<PageResult<TXfNoneBusinessUploadDetailDto>> paged(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                               @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                               TXfNoneBusinessUploadQueryDto dto) {
        long start = System.currentTimeMillis();
        Page<TXfNoneBusinessUploadDetailDto> page = noneBusinessService.page(current, size, dto);
        log.info("非商上传信息分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getPages(), page.getSize()));
    }

    @ApiOperation("查询页签总数")
    @GetMapping("/summery")
    public R<SummerySubmitResponse> summery(
            TXfNoneBusinessUploadQueryDto dto) {
        SummerySubmitResponse response = new SummerySubmitResponse();
        List<TXfNoneBusinessUploadDetailDto> list = noneBusinessService.noPaged(dto);
        List<TXfNoneBusinessUploadDetailDto> submitList = list.stream().filter(x -> Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG.equals(x.getSubmitFlag())).collect(Collectors.toList());
        response.setSubmitCount(list.size());
        response.setWaitSubmit(submitList.size());
        response.setSubmitedCount(list.size() - submitList.size());
        log.info("查询总数成功");
        return R.ok(response);
    }

    /**
     * 发票预览
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
        if (StringUtils.isBlank(tXfNoneBusinessUploadDetailEntity.getUploadPath())) {
            return R.fail("文件异常，请删除此发票，重新上传");
        }
        String result;
        try {
            result = fileService.downLoadFile(tXfNoneBusinessUploadDetailEntity.getUploadPath());
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
     * 下载源文件
     *
     * @return
     */
    @ApiOperation("下载源文件")
    @PostMapping(value = "/down")
    public R<String> down(@RequestBody FileDownRequest requestBody) {
        try {
            //安全校验处理
            FileDownRequest request = new FileDownRequest();
            BeanUtils.copyProperties(requestBody, request);
            if (CollectionUtils.isEmpty(request.getIds())) {
                throw new RRException("请选中数据后进行下载");
            }
            List<TXfNoneBusinessUploadDetailEntity> list = noneBusinessService.listByIds(request.getIds());
            if (CollectionUtils.isEmpty(list)) {
                throw new RRException("您所选发票不包含任何附件文件");
            }
            noneBusinessService.down(list, request);
            return R.ok("下载成功，请往消息中心查看下载结果");
        } catch (Exception e) {
            log.error("非商下载源文件异常:", e);
            return R.fail("下载源文件异常" + e.getMessage());
        }

    }

    @ApiOperation("上传记录批量批量删除")
    @DeleteMapping("/del")
    public R<String> delOverdue(@RequestBody @ApiParam("id集合") Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("请选中记录后删除");
        }
        R r = R.ok(null, "删除成功");
        long start = System.currentTimeMillis();
        for (Long id : ids) {
            r = noneBusinessService.deleteUploadDetail(id);
        }
        log.info("上传记录批量批量删除,耗时:{}ms", System.currentTimeMillis() - start);
        return r;
    }

    @ApiOperation(value = "非商编辑")
    @PutMapping(value = "/edit")
    public R edit(@RequestBody TXfNoneBusinessUploadQueryDto dto) {
        if (dto == null) {
            return R.fail("参数错误，请确认后重试。");
        }
        TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity = this.noneBusinessService.getById(dto.getId());
        if (null == tXfNoneBusinessUploadDetailEntity) {
            return R.fail("没有找到对应的电票信息");
        }
        if (StringUtils.isNotBlank(dto.getStoreNo())) {
            tXfNoneBusinessUploadDetailEntity.setStoreNo(dto.getStoreNo());
            this.noneBusinessService.updateById(tXfNoneBusinessUploadDetailEntity);
        }

        if (StringUtils.isNotBlank(dto.getCompanyCode())) {
            String invoiceCode = StringUtils.defaultIfBlank(tXfNoneBusinessUploadDetailEntity.getInvoiceCode(), "");
            Optional<TDxRecordInvoiceEntity> tDxRecordInvoiceEntity = new LambdaQueryChainWrapper<>(recordInvoiceService.getBaseMapper())
                    //.eq(TDxRecordInvoiceEntity::getUuid, invoiceCode + tXfNoneBusinessUploadDetailEntity.getInvoiceNo())
                    .eq(StringUtils.isNotBlank(invoiceCode), TDxRecordInvoiceEntity::getInvoiceCode, invoiceCode)
                    .eq(TDxRecordInvoiceEntity::getInvoiceNo, tXfNoneBusinessUploadDetailEntity.getInvoiceNo()).oneOpt();
            if (tDxRecordInvoiceEntity.isPresent()) {
                QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
                wrapper.eq(TAcOrgEntity.TAX_NO, tDxRecordInvoiceEntity.get().getGfTaxNo());
                wrapper.eq(TAcOrgEntity.ORG_TYPE, "5");
                List<TAcOrgEntity> tAcOrgEntityList = companyService.list(wrapper);
                boolean anyMatchFlag = tAcOrgEntityList.stream().map(TAcOrgEntity::getOrgCode).anyMatch(s -> s.contains(dto.getCompanyCode()));
                if (!anyMatchFlag) {
                    return R.fail("填写的JV与税号" + tDxRecordInvoiceEntity.get().getGfTaxNo() + "不匹配，请确认后重试。");
                }
                LambdaUpdateChainWrapper<TDxRecordInvoiceEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(recordInvoiceService.getBaseMapper());
                updateChainWrapper.eq(TDxRecordInvoiceEntity::getUuid, invoiceCode + tXfNoneBusinessUploadDetailEntity.getInvoiceNo());
                updateChainWrapper.set(TDxRecordInvoiceEntity::getJvcode, dto.getCompanyCode());
                updateChainWrapper.update();
            }
        }
        return R.ok("编辑成功", "编辑成功");
    }

    @ApiOperation("校验可以提交数量")
    @PostMapping("/validSumitInfo")
    public R<ValidSubmitResponse> validSumitInfo(@RequestBody ValidSubmitRequest request) {
        ValidSubmitResponse response = new ValidSubmitResponse();
        if ("0".equals(request.getIsAllSelected())) {
            List<TXfNoneBusinessUploadDetailEntity> resultList = noneBusinessService.listByIds(request.getIncludes());
            response.setSubmitCount(resultList.size());
            List<TXfNoneBusinessUploadDetailEntity> submitList = resultList.stream().filter(x -> Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG.equals(x.getSubmitFlag()
            ) && Constants.SIGN_NONE_BUSINESS_SUCCESS.equals(x.getOfdStatus()) && Constants.VERIFY_NONE_BUSINESS_SUCCESSE.equals(x.getVerifyStatus())).collect(Collectors.toList());
            response.setInSubmit(submitList.size());
            response.setExSubmit(resultList.size() - submitList.size());
            return R.ok(response);
        } else {
            List<TXfNoneBusinessUploadDetailDto> list = noneBusinessService.noPaged(request.getExcludes());
            response.setSubmitCount(list.size());
            List<TXfNoneBusinessUploadDetailDto> submitList = list.stream().filter(x -> Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG.equals(x.getSubmitFlag())
                    && Constants.SIGN_NONE_BUSINESS_SUCCESS.equals(x.getOfdStatus()) && Constants.VERIFY_NONE_BUSINESS_SUCCESSE.equals(x.getVerifyStatus())).collect(Collectors.toList());
            response.setInSubmit(submitList.size());
            response.setExSubmit(list.size() - submitList.size());
            return R.ok(response);

        }

    }

    @ApiOperation("校验上传的类型")
    @PostMapping("/validSubmitFile")
    public R<FileDownResponse> validSubmitFile(@RequestBody @ApiParam("id集合") Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("请选中记录后提交");
        }

        List<TXfNoneBusinessUploadDetailEntity> resultList = noneBusinessService.listByIds(Arrays.asList(ids));
        FileDownResponse response = new FileDownResponse();
        List<TXfNoneBusinessUploadDetailEntity> ofdList = resultList.stream().filter(x -> String.valueOf(Constants.FILE_TYPE_OFD).equals(x.getFileType()
        )).collect(Collectors.toList());

        List<TXfNoneBusinessUploadDetailEntity> pdfList = resultList.stream().filter(x -> String.valueOf(Constants.FILE_TYPE_PDF).equals(x.getFileType()
        )).collect(Collectors.toList());

        List<TXfNoneBusinessUploadDetailEntity> xmlList = resultList.stream().filter(x -> String.valueOf(Constants.FILE_TYPE_XML).equals(x.getFileType()
        )).collect(Collectors.toList());
        response.setSubmitCount(resultList.size());
        response.setOfdSubmit(ofdList.size());
        response.setPdfSubmit(pdfList.size());
        response.setXmlSubmit(xmlList.size());
        return R.ok(response);
    }

    @ApiOperation("提交发票到进项发票")
    @PostMapping("/submit")
    public R<ValidSubmitResponse> submit(@RequestBody ValidSubmitRequest request) {
        ValidSubmitResponse response = new ValidSubmitResponse();

        if ("0".equals(request.getIsAllSelected())) {
            List<TXfNoneBusinessUploadDetailEntity> resultList = noneBusinessService.listByIds(request.getIncludes());
            response.setSubmitCount(resultList.size());
            List<TXfNoneBusinessUploadDetailEntity> submitList = resultList.stream().filter(x -> Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG.equals(x.getSubmitFlag()
            ) && Constants.SIGN_NONE_BUSINESS_SUCCESS.equals(x.getOfdStatus()) && Constants.VERIFY_NONE_BUSINESS_SUCCESSE.equals(x.getVerifyStatus())).collect(Collectors.toList());
            List<String> list = new ArrayList<>();
            submitList.stream().forEach(e -> {
                e.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
                Optional<TXfNoneBusinessUploadDetailEntity> entityOpt = noneBusinessService.getSubmitInvoice(e.getInvoiceNo(), e.getInvoiceCode());
                entityOpt.ifPresent(it -> {
                    e.setEntryDate(it.getEntryDate());
                    e.setVoucherNo(it.getVoucherNo());
                });
                noneBusinessService.deleteSubmitInvoice(e.getInvoiceNo(), e.getInvoiceCode());
                if (StringUtils.isNotEmpty(e.getInvoiceNo())) {
                    list.add(e.getInvoiceCode() + e.getInvoiceNo());
                }
            });
            response.setInSubmit(submitList.size());
            response.setExSubmit(resultList.size() - submitList.size());
            noneBusinessService.saveOrUpdateBatch(submitList);
//            noneBusinessService.updateInvoiceInfo(list);
            return R.ok(response, "提交成功");
        } else {
            List<TXfNoneBusinessUploadDetailDto> list = noneBusinessService.noPaged(request.getExcludes());
            response.setSubmitCount(list.size());
            List<TXfNoneBusinessUploadDetailDto> submitList = list.stream().filter(x -> Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG.equals(x.getSubmitFlag())
                    && Constants.SIGN_NONE_BUSINESS_SUCCESS.equals(x.getOfdStatus()) && Constants.VERIFY_NONE_BUSINESS_SUCCESSE.equals(x.getVerifyStatus())).collect(Collectors.toList());
            List<String> list1 = new ArrayList<>();
            submitList.stream().forEach(e -> {
                Optional<TXfNoneBusinessUploadDetailEntity> entityOpt = noneBusinessService.getSubmitInvoice(e.getInvoiceNo(), e.getInvoiceCode());
                entityOpt.ifPresent(it -> {
                    e.setEntryDate(it.getEntryDate());
                    e.setVoucherNo(it.getVoucherNo());
                });
                e.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
                noneBusinessService.deleteSubmitInvoice(e.getInvoiceNo(), e.getInvoiceCode());
                if (StringUtils.isNotEmpty(e.getInvoiceNo())) {
                    list1.add(e.getInvoiceCode() + e.getInvoiceNo());
                }
            });
//            noneBusinessService.updateInvoiceInfo(list1);
            noneBusinessService.saveOrUpdateBatch(noneBusinessConverter.map(submitList));
            response.setInSubmit(submitList.size());
            response.setExSubmit(list.size() - submitList.size());
            return R.ok(response, "提交成功");

        }

    }

//    @PostMapping("claim/export")
//    @ApiOperation(value = "数据导出")
//    public R export(TXfNoneBusinessUploadQueryDto dto) {
////        noneBusinessService.export(dto);
//        return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
//    }

    @PostMapping("check/export")
    @ApiOperation(value = "导出")
    public R checkExport(@RequestBody ValidSubmitRequest request) throws IOException {

        if ("0".equals(request.getIsAllSelected())) {
            if (request.getIncludes() == null && request.getIncludes().size() == 0) {
                return R.fail("请勾选数据后进行导出");
            }
            if (request.getIncludes().size() > 10000) {
                return R.fail("最大导出数量不能超过一万");
            }
            List<TXfNoneBusinessUploadDetailDto> resultList = noneBusinessService.getByIds(request.getIncludes());
            noneBusinessService.export(resultList, request);

            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        } else {
            List<TXfNoneBusinessUploadDetailDto> resultList = noneBusinessService.noPaged(request.getExcludes());
            if (resultList.size() > 10000) {
                return R.fail("最大导出数量不能超过一万");
            }
            noneBusinessService.export(resultList, request);
            return R.ok("单据导出正在处理，请在消息中心查看导出结果。");
        }


    }

    @ApiOperation("凭证号 入账日期信息导入")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        SpecialCompanyImportSizeDto result = noneBusinessService.queryImportData(file);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(result.getErrorMsg())) {
            return R.fail(result.getErrorMsg());
        }

        if (result.getUnValidCount() == 0) {
            return R.ok("message", "导入成功");
        }
        return R.ok("message", String.format("导入[%d]条数据  导入成功[%d]条数据 导入失败[%d]条数据 导入失败数据请前往消息中心查看", result.getImportCount(), result.getValidCDount(), result.getUnValidCount()));
    }

    @ApiOperation("非商电票上传导入")
    @PutMapping("/upload/import")
    public R uploadBatchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        SpecialCompanyImportSizeDto result = noneBusinessService.uploadImportData(file);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(result.getErrorMsg())) {
            return R.fail(result.getErrorMsg());
        }

        if (result.getUnValidCount() == 0) {
            return R.ok("message", "导入成功");
        }
        return R.ok("message", String.format("导入[%d]条数据  导入成功[%d]条数据 导入失败[%d]条数据 导入失败数据请前往消息中心查看", result.getImportCount(), result.getValidCDount(), result.getUnValidCount()));
    }

    @ApiOperation("保存3.0同步的MTR发票信息")
    @PostMapping("/saveMtrInfo")
    public R<Object> saveMtrInfo(@RequestBody MtrIcSaveDto mtrIcSaveDto) throws IOException {
        return mtrSaveService.saveMtrInfo(mtrIcSaveDto);
    }


}