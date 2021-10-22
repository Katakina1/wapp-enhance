package com.xforceplus.wapp.modules.noneBusiness.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backFill.model.*;
import com.xforceplus.wapp.modules.backFill.service.BackFillService;
import com.xforceplus.wapp.modules.backFill.service.DiscernService;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.backFill.service.VerificationService;
import com.xforceplus.wapp.repository.dao.TXfNoneBusinessUploadDetailDao;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 非商业务逻辑
 */
@Service
@Slf4j
public class NoneBusinessService extends ServiceImpl<TXfNoneBusinessUploadDetailDao, TXfNoneBusinessUploadDetailEntity> {
    @Autowired
    private BackFillService backFillService;

    @Autowired
    private FileService fileService;
    @Autowired
    private DiscernService discernService;
    @Value("${wapp.integration.customer-no}")
    private String customerNo;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private RestTemplate restTemplate;


    public void parseOfdFile(List<byte[]> ofd, TXfNoneBusinessUploadDetailEntity entity) {
        StringBuffer fileName = new StringBuffer();
        fileName.append(UUID.randomUUID().toString());
        fileName.append(".");
        fileName.append(Constants.SUFFIX_OF_OFD);
        String uploadResult = null;
        try {
            //上传源文件到ftp服务器
            uploadResult = fileService.uploadFile(ofd.get(0), fileName.toString());
        } catch (IOException ex) {
            log.error("非商上传电票到文件服务器失败:{}", ex);
        }
        UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
        UploadFileResultData data = uploadFileResult.getData();
        entity.setFileType(String.valueOf(Constants.FILE_TYPE_OFD));
        entity.setSourceUploadId(data.getUploadId());
        entity.setCreateUser("awat");
        entity.setSourceUploadPath(data.getUploadPath());
        //发送验签
        OfdResponse response = backFillService.signOfd(ofd.get(0));
        //验签成功
        if (response.isOk()) {
            entity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
            final InvoiceMain invoiceMain = response.getResult().getInvoiceMain();
            VerificationRequest verificationRequest = new VerificationRequest();
            verificationRequest.setAmount(invoiceMain.getAmountWithoutTax());
            verificationRequest.setCheckCode(invoiceMain.getCheckCode());
            verificationRequest.setCustomerNo(customerNo);
            verificationRequest.setInvoiceCode(invoiceMain.getInvoiceCode());
            verificationRequest.setInvoiceNo(invoiceMain.getInvoiceNo());
            verificationRequest.setPaperDrewDate(invoiceMain.getPaperDrewDate());
            VerificationResponse verificationResponse = verificationService.verify(verificationRequest);
            if (verificationResponse.isOK()) {
                entity.setXfVerifyTaskId(verificationResponse.getResult());
                entity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
            } else {
                entity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
            }
            if (StringUtils.isNotEmpty(response.getResult().getImageUrl())) {

                try {
                    String base64=verificationService.getBase64ByUrl(response.getResult().getImageUrl());

                    String uploadFile = fileService.uploadFile(Base64.decode(base64), UUID.randomUUID().toString().replace("-", "") + ".jpeg");
                    UploadFileResult uploadFileImageResult = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
                    if (null != uploadFileImageResult) {
                        entity.setUploadId(uploadFileImageResult.getData().getUploadId());
                        entity.setUploadPath(uploadFileImageResult.getData().getUploadPath());
                    }
                } catch (IOException e) {
                    log.error("非商下载税局OFD图片失败:{}", e);
                }

            }

        } else {
            entity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
            entity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
        }

        this.save(entity);
    }

    public void parsePdfFile(List<byte[]> pdf, TXfNoneBusinessUploadDetailEntity entity) {
        StringBuffer fileName = new StringBuffer();
        fileName.append(UUID.randomUUID().toString());
        fileName.append(".");
        fileName.append(Constants.SUFFIX_OF_PDF);
        String uploadResult = null;
        try {
            //上传源文件到ftp服务器
            uploadResult = fileService.uploadFile(pdf.get(0), fileName.toString());
        } catch (IOException ex) {
            log.error("非商上传电票到文件服务器失败:{}", ex);
        }
        UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
        UploadFileResultData data = uploadFileResult.getData();
        entity.setFileType(String.valueOf(Constants.FILE_TYPE_PDF));
        entity.setSourceUploadId(data.getUploadId());
        entity.setCreateUser("awat");
        entity.setSourceUploadPath(data.getUploadPath());
        entity.setUploadId(data.getUploadId());
        entity.setUploadPath(data.getUploadPath());
        //发送识别
        String taskId = discernService.discern(pdf.get(0));
        //发送识别成功
        if (StringUtils.isNotEmpty(taskId)) {
            entity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_DOING);
            entity.setXfDiscernTaskId(taskId);
            entity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
        } else {
            entity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
            entity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
        }

        this.save(entity);
    }

    public TXfNoneBusinessUploadDetailEntity getObjByVerifyTaskId(String verifyTaskId) {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq(TXfNoneBusinessUploadDetailEntity.XF_VERIFY_TASK_ID, verifyTaskId);
        return getOne(wrapper);
    }

    public TXfNoneBusinessUploadDetailEntity getObjByDiscernTaskId(String taskId) {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq(TXfNoneBusinessUploadDetailEntity.XF_DISCERN_TASK_ID, taskId);
        return getOne(wrapper);
    }

    public boolean updateById(TXfNoneBusinessUploadDetailEntity entity) {
        if (null == entity.getId()) {
            return false;
        }
        LambdaUpdateChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());

        if (null != entity.getId()) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getId, entity.getId());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceCode())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceCode, entity.getInvoiceCode());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceNo())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, entity.getInvoiceNo());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceDate())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceDate, entity.getInvoiceDate());
        }
        if (StringUtils.isNotBlank(entity.getVerifyStatus())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getVerifyStatus, entity.getVerifyStatus());
        }
        if (StringUtils.isNotBlank(entity.getXfVerifyTaskId())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getXfVerifyTaskId, entity.getXfVerifyTaskId());
        }
        if (StringUtils.isNotBlank(entity.getReason())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getReason, entity.getReason());
        }
        return wrapper.update();
    }

    public Page<TXfNoneBusinessUploadDetailEntity> page(Long current, Long size, String bussinessType,
                                                        String storeNo,
                                                        String invoiceStoreNo,
                                                        String invoiceType,
                                                        String verifyStauts,
                                                        String ofdStatus,
                                                        String jvCode,
                                                        String supplierId,
                                                        String type,
                                                        String businessNo) {
        LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity>(baseMapper);
        if (StringUtils.isNotBlank(bussinessType)) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getBussinessType, bussinessType);
        }
        if (StringUtils.isNotBlank(storeNo)) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getStoreNo, storeNo);
        }
        if (StringUtils.isNotBlank(invoiceStoreNo)) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getInvoiceStoreNo, invoiceStoreNo);
        }
        if (StringUtils.isNotBlank(verifyStauts)) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getVerifyStatus, verifyStauts);
        }
        if (StringUtils.isNotBlank(ofdStatus)) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getOfdStatus, ofdStatus);
        }

        Page<TXfNoneBusinessUploadDetailEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("抬头信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return page;
    }
}
