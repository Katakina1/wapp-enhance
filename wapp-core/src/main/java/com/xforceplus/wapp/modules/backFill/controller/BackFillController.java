package com.xforceplus.wapp.modules.backFill.controller;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backFill.model.BackFillCommitVerifyRequest;
import com.xforceplus.wapp.modules.backFill.model.BackFillMatchRequest;
import com.xforceplus.wapp.modules.backFill.model.SpecialElecUploadDto;
import com.xforceplus.wapp.modules.backFill.service.BackFillService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.system.controller.AbstractController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@RestController
@RequestMapping(value = "/invoice/backFill")
public class BackFillController  extends AbstractController {

    @Autowired
    private BackFillService backFillService;

    @ApiOperation(value = "纸票发票回填", notes = "", response = Response.class, tags = {"backFill"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/commitVerify")
    public R comitVerify(@ApiParam(value = "BackFillCommitVerifyRequest" ,required=true )@RequestBody BackFillCommitVerifyRequest request){
        logger.info("纸票发票回填--入参：{}", JSONObject.toJSONString(request));
        request.setOpUserId(getUserId());
        request.setOpUserName(getUserName());
        return backFillService.commitVerify(request);
    }

    @ApiOperation(value = "电票发票上传", notes = "", response = Response.class, tags = {"backFill"})
    @ApiResponses(value = {
    @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping("/upload")
    public R upload(@RequestParam("files") MultipartFile[] files, @RequestParam("gfName") String gfName, @RequestParam("jvCode") String jvcode, @RequestParam("vendorId") String vendorid) {
        if (files.length == 0) {
            return R.fail("请选择您要上传的电票文件(pdf/ofd)");
        }
        if (files.length > 10) {
            return R.fail("最多一次性上传10个文件");
        }
        List<byte[]> ofd = new ArrayList<>();
        List<byte[]> pdf = new ArrayList<>();
        try {
            Set<String> fileNames=new HashSet<>();
            for (int i = 0; i < files.length; i++) {
                final MultipartFile file = files[i];
                final String filename = file.getOriginalFilename();
                if(!fileNames.add(filename)){
                    return R.fail("文件["+filename+"]重复上传！");
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
            }

            SpecialElecUploadDto dto = new SpecialElecUploadDto();
            dto.setOfds(ofd);
            dto.setJvCode(jvcode);
            dto.setUserId(getUserId());
            dto.setGfName(gfName);
            dto.setPdfs(pdf);
            dto.setVendorId(vendorid);

            final String batchNo = backFillService.uploadAndVerify(dto);

            return R.ok(batchNo);
        } catch (Exception e) {
            logger.error("上传过程中出现异常:" + e.getMessage(), e);
            return R.fail("上传过程中出现错误，请重试");
        }
    }

    @ApiOperation(value = "循环获取上传结果", notes = "", response = Response.class, tags = {"backFill"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping("/upload/{batchNo}")
    public R pollingUploadResult(@PathVariable String batchNo) {
        logger.info("循环获取上传结果--入参：{}", batchNo);
        return R.ok(this.backFillService.getUploadResult(batchNo));
    }

    @ApiOperation(value = "上传发票匹配", notes = "", response = Response.class, tags = {"backFill"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping("/match")
    public R match(@ApiParam(value = "BackFillMatchRequest" ,required=true )@RequestBody BackFillMatchRequest request) {
        logger.info("发票回填后匹配--入参：{}", JSONObject.toJSONString(request));
        return backFillService.matchPreInvoice(request);
    }

}
