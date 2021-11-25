package com.xforceplus.wapp.modules.backFill.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backFill.model.*;
import com.xforceplus.wapp.modules.backFill.service.BackFillService;
import com.xforceplus.wapp.modules.backFill.service.InvoiceImportListener;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by SunShiyong on 2021/10/12.
 */
@Api(tags = "backFill")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH+"/invoice/backFill")
public class BackFillController  extends AbstractController {

    @Autowired
    private BackFillService backFillService;

    @ApiOperation(value = "纸票发票回填")
    @PostMapping(value = "/commitVerify")
    public R comitVerify(@ApiParam(value = "纸票发票回填请求" ,required=true )@RequestBody BackFillCommitVerifyRequest request){
        logger.info("纸票发票回填--入参：{}", JSONObject.toJSONString(request));
        request.setOpUserId(getUserId());
        request.setOpUserName(getUserName());
        request.setVendorId(getUser().getUsercode());
        return backFillService.commitVerify(request);
    }

    @ApiOperation(value = "上传发票校验")
    @GetMapping(value = "/comitVerifyCheck")
    public R comitVerifyCheck(@ApiParam(value = "被蓝冲的发票id") @RequestParam(required = false) Long id,@ApiParam(value = "结算单号") @RequestParam(required = false)  String settlementNo){
        logger.info("上传发票校验--id：{}",id);
        logger.info("上传发票校验--settlementNo：{}",settlementNo);
        return backFillService.commitVerifyCheck(id,settlementNo);
    }

    @ApiOperation(value = "电票发票上传" )
    @PostMapping("/upload")
    public R upload(@RequestParam("files") MultipartFile[] files, @RequestParam("gfName") String gfName, @RequestParam("jvCode") String jvCode, @RequestParam("vendorId") String vendorid,@RequestParam("settlementNo") String settlementNo, @RequestParam("invoiceColor")String invoiceColor) {
        BackFillCommitVerifyRequest request = new BackFillCommitVerifyRequest();
        request.setInvoiceColor(invoiceColor);
        request.setSettlementNo(settlementNo);
        R r = backFillService.checkCommitRequest(request);
        if (R.FAIL.equals(r.getCode())) {
            return r;
        }
        return backFillService.upload(files,gfName,jvCode,vendorid,settlementNo);
    }

    @ApiOperation(value = "循环获取上传结果")
    @GetMapping("/upload/{batchNo}")
    public R pollingUploadResult(@PathVariable String batchNo) {
        logger.info("循环获取上传结果--入参：{}", batchNo);
        return R.ok(this.backFillService.getUploadResult(batchNo));
    }

    @ApiOperation(value = "上传发票匹配")
    @PostMapping("/match")
    public R match(@ApiParam(value = "上传发票匹配请求" ,required=true )@RequestBody BackFillMatchRequest request) {
        logger.info("发票回填后匹配--入参：{}", JSONObject.toJSONString(request));
        request.setVenderId(getUser().getUsercode());
        return backFillService.matchPreInvoice(request);
    }

    @ApiOperation(value = "excel批量上传")
    @PostMapping("/upload/excel")
    public R upload(@RequestParam MultipartFile file, @RequestParam String gfName, @RequestParam String jvCode, @RequestParam("vendorId") String vendorid,
                    @RequestParam String settlementNo,@RequestParam String invoiceColor) {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        try {
            final String filename = file.getOriginalFilename();
            final String suffix = filename.substring(filename.lastIndexOf(   ".") + 1);
            if (StringUtils.isNotBlank(suffix)) {
                InvoiceImportListener listener = new InvoiceImportListener();
                EasyExcel.read(file.getInputStream(), BackfillInvoice.class, listener).sheet().doRead();
                if (CollectionUtils.isEmpty(listener.getValidInvoices())) {
                    return R.fail("未解析到数据");
                }
                if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
                    return R.fail("数据填写错误");
                }
                logger.info("导入数据解析条数:{}", listener.getRows());
                BackFillCommitVerifyRequest request = new BackFillCommitVerifyRequest();
//                request.setOpUserId(getUserId());
//                request.setOpUserName(getUserName());
                request.setSettlementNo(settlementNo);
                request.setInvoiceColor(invoiceColor);
                request.setGfName(gfName);
                request.setJvCode(jvCode);
                request.setVendorId(vendorid);
                List<BackFillVerifyBean> bverifyBeanList = new ArrayList<>();
                BackFillVerifyBean backFillVerifyBean = null;
                for (BackfillInvoice backfillInvoice : listener.getValidInvoices()) {
                    backFillVerifyBean = new BackFillVerifyBean();
                    backFillVerifyBean.setAmount(backfillInvoice.getAmount());
                    backFillVerifyBean.setInvoiceCode(backfillInvoice.getInvoiceCode());
                    backFillVerifyBean.setInvoiceNo(backfillInvoice.getInvoiceNo());
                    backFillVerifyBean.setPaperDrewDate(backfillInvoice.getPaperDrewDate());
                    bverifyBeanList.add(backFillVerifyBean);
                }
                request.setVerifyBeanList(bverifyBeanList);
                request.setOpUserId(UserUtil.getUserId());
                return backFillService.commitVerify(request);
            } else {
                throw new EnhanceRuntimeException("文件:[" + filename + "]后缀名不正确,应为:[xls/xlsx]");
            }
        } catch (Exception e) {
            logger.error("上传过程中出现异常:" + e.getMessage(), e);
            return R.fail("上传过程中出现错误，请重试");
        }

    }

    @PostMapping("re-split")
    public R reSplit(){


        return R.ok();
    }



}
