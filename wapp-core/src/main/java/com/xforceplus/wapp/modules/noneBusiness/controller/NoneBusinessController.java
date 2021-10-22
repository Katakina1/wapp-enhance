package com.xforceplus.wapp.modules.noneBusiness.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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

    @ApiOperation(value = "上传电子发票", consumes = "multipart/form-data")
    @PutMapping ("/upload")
    public R getOverdue(@ApiParam("文件") @RequestParam("file") MultipartFile file,
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
           if (CollectionUtils.isNotEmpty(ofd)) {
               noneBusinessService.parseOfdFile(ofd, entity);
           }
            if (CollectionUtils.isNotEmpty(pdf)) {
                noneBusinessService.parseOfdFile(ofd, entity);
            }
            return R.ok(batchNo);
       } catch (Exception e) {
           log.error("上传过程中出现异常:" + e.getMessage(), e);
           return R.fail("上传过程中出现错误，请重试" + e.getMessage());
     }
    }


}
