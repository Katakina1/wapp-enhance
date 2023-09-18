package com.xforceplus.wapp.modules.noneBusiness.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backfill.model.UploadFileResult;
import com.xforceplus.wapp.modules.backfill.model.UploadFileResultData;
import com.xforceplus.wapp.modules.backfill.service.FileService;
import com.xforceplus.wapp.modules.backfill.service.VerificationService;
import com.xforceplus.wapp.modules.noneBusiness.convert.MtrServiceConverter;
import com.xforceplus.wapp.modules.noneBusiness.dto.MtrIcInvoiceDetailDto;
import com.xforceplus.wapp.modules.noneBusiness.dto.MtrIcSaveDto;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Mtr保存业务到非商
 */
@Service
@Slf4j
public class MtrSaveService {

    @Autowired
    private MtrServiceConverter mtrServiceConverter;
    @Autowired
    private NoneBusinessService noneBusinessService;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private TDxRecordInvoiceDetailDao tDxRecordInvoiceDetailDao;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private FileService fileService;
    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;
    /**
     * 1:保存MTR和IC的代码
     *
     * @param mtrIcSaveDto
     * @return
     */
    public R<Object> saveMtrInfo(MtrIcSaveDto mtrIcSaveDto) {
        log.info("saveMtrInfo:{}", JSON.toJSONString(mtrIcSaveDto));
        if (Objects.isNull(mtrIcSaveDto)) {
            return R.fail("请求参数不能为空");
        }
        if (Objects.isNull(mtrIcSaveDto.getMtrIcInvoiceMainDto())) {
            return R.fail("请求参数发票头信息不能为空");
        }
        if (CollectionUtils.isEmpty(mtrIcSaveDto.getMtrIcInvoiceDetailDto())) {
            return R.fail("请求参数发票明细信息不能为空");
        }
        TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity = mtrServiceConverter.converMtr(mtrIcSaveDto);
        tXfNoneBusinessUploadDetailEntity.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
        String invoiceDate = mtrIcSaveDto.getMtrIcInvoiceMainDto().getPaperDate();
		if (invoiceDate != null && invoiceDate.replaceAll("-", "").length() > 8) {
			invoiceDate = invoiceDate.replaceAll("-", "").substring(0, 8);
		}
		TDxRecordInvoiceEntity tDxRecordInvoiceEntity = mtrServiceConverter.converMtrInvoiceMain(mtrIcSaveDto.getMtrIcInvoiceMainDto());
		if(StringUtils.isBlank(tDxRecordInvoiceEntity.getInvoiceType())) {
			return R.fail("请求参数发票头信息错误，发票类型不能为空");
		}
		tDxRecordInvoiceEntity.setInvoiceDate(DateUtils.convertStringToDate(invoiceDate));//开票时间转换
		if (tDxRecordInvoiceEntity.getInvoiceCode() == null) {
			tDxRecordInvoiceEntity.setInvoiceCode("");
		}
		tDxRecordInvoiceEntity.setUuid(tDxRecordInvoiceEntity.getInvoiceCode() + tDxRecordInvoiceEntity.getInvoiceNo());
		tDxRecordInvoiceEntity.setDetailYesorno("1");
        tDxRecordInvoiceEntity.setFlowType("7");
        tDxRecordInvoiceEntity.setConfirmStatus("0");
        tDxRecordInvoiceEntity.setInvoiceStatus("0");
        tDxRecordInvoiceEntity.setSourceSystem("1");
        tDxRecordInvoiceEntity.setRemark(mtrIcSaveDto.getInvoiceRemark());
        tDxRecordInvoiceEntity.setScanMatchStatus("1");
        tDxRecordInvoiceEntity.setDxhyMatchStatus("0");
        tDxRecordInvoiceEntity.setTpStatus("0");
        tDxRecordInvoiceEntity.setIsDel(IsDealEnum.NO.getValue());
        tDxRecordInvoiceEntity.setQsDate(new Date());
        tDxRecordInvoiceEntity.setQsType("5");
        tDxRecordInvoiceEntity.setQsStatus("1");
        tDxRecordInvoiceEntity.setLastUpdateDate(new Date());
        tDxRecordInvoiceEntity.setNewGfTaxno(tDxRecordInvoiceEntity.getGfTaxNo());
		if (StringUtils.isEmpty(tDxRecordInvoiceEntity.getInvoiceNo())) {
			return R.fail("请求参数发票号码不能为空");
		}
		if (StringUtils.isEmpty(tDxRecordInvoiceEntity.getInvoiceCode()) && 
				!(StringUtils.equals(tDxRecordInvoiceEntity.getInvoiceType(), "16") || StringUtils.equals(tDxRecordInvoiceEntity.getInvoiceType(), "18"))) {
			return R.fail("请求参数发票代码不能为空");
		}
        tXfNoneBusinessUploadDetailEntity.setInvoiceNo(tDxRecordInvoiceEntity.getInvoiceNo());
        tXfNoneBusinessUploadDetailEntity.setInvoiceCode(tDxRecordInvoiceEntity.getInvoiceCode());
        tXfNoneBusinessUploadDetailEntity.setUpdateTime(new Date());
        tXfNoneBusinessUploadDetailEntity.setInvoiceDate(invoiceDate);
//        tXfNoneBusinessUploadDetailEntity.setCreateUser(mtrIcSaveDto.getCreateUser());
        if (StringUtils.isNotBlank(mtrIcSaveDto.getXmlUrl())) {
            tXfNoneBusinessUploadDetailEntity.setFileType(String.valueOf(Constants.FILE_TYPE_XML));
            String base64 = verificationService.getBase64ByRealUrl(Base64.encode(mtrIcSaveDto.getXmlUrl().getBytes()));
            if (StringUtils.isNotBlank(base64)) {
                byte[] buffer = Base64.decode(base64);
                StringBuffer fileName = new StringBuffer();
                fileName.append(UUID.randomUUID().toString());
                fileName.append(".xml");
                try {
                    String uploadResult=fileService.uploadFile(buffer,fileName.toString() , "noneBusiness");
                    UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
                    UploadFileResultData data = uploadFileResult.getData();
                    tXfNoneBusinessUploadDetailEntity.setSourceUploadPath(data.getUploadPath());
                    tXfNoneBusinessUploadDetailEntity.setSourceUploadId(data.getUploadId());
                } catch (IOException e) {
                    log.error("saveMtrInfo uploadFile error:{}",e);
                }
            }
            if(StringUtils.isNotBlank(mtrIcSaveDto.getPdfUrl())){
                String baseOfd64 = verificationService.getBase64ByRealUrl(Base64.encode(mtrIcSaveDto.getPdfUrl().getBytes()));
                if (StringUtils.isNotBlank(baseOfd64)) {
                    byte[] buffer = Base64.decode(baseOfd64);
                    StringBuffer fileName = new StringBuffer();
                    fileName.append(UUID.randomUUID().toString());
                    fileName.append(".pdf");
                    try {
                        String uploadResult=fileService.uploadFile(buffer,fileName.toString() , "noneBusiness");
                        UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
                        UploadFileResultData data = uploadFileResult.getData();
                        tXfNoneBusinessUploadDetailEntity.setUploadPath(data.getUploadPath());
                        tXfNoneBusinessUploadDetailEntity.setUploadId(data.getUploadId());
                    } catch (IOException e) {
                        log.error("saveMtrInfo uploadFile error:{}",e);
                    }
                }
            }
            mtrIcSaveDto.setOfdUrl(null);
            mtrIcSaveDto.setPdfUrl(null);

        }

        if (StringUtils.isNotBlank(mtrIcSaveDto.getOfdUrl())) {
            tXfNoneBusinessUploadDetailEntity.setFileType(String.valueOf(Constants.FILE_TYPE_OFD));
            String base64 = verificationService.getBase64ByRealUrl(Base64.encode(mtrIcSaveDto.getOfdUrl().getBytes()));
            if (StringUtils.isNotBlank(base64)) {
                byte[] buffer = Base64.decode(base64);
                StringBuffer fileName = new StringBuffer();
                fileName.append(UUID.randomUUID().toString());
                fileName.append(".ofd");
                try {
                    String uploadResult=fileService.uploadFile(buffer,fileName.toString() , "noneBusiness");
                    UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
                    UploadFileResultData data = uploadFileResult.getData();
                    tXfNoneBusinessUploadDetailEntity.setSourceUploadPath(data.getUploadPath());
                    tXfNoneBusinessUploadDetailEntity.setSourceUploadId(data.getUploadId());
                } catch (IOException e) {
                    log.error("saveMtrInfo uploadFile error:{}",e);
                }
            }
            if(StringUtils.isNotBlank(mtrIcSaveDto.getImageUrl())){
                String baseOfd64 = verificationService.getBase64ByRealUrl(Base64.encode(mtrIcSaveDto.getImageUrl().getBytes()));
                if (StringUtils.isNotBlank(baseOfd64)) {
                    byte[] buffer = Base64.decode(baseOfd64);
                    StringBuffer fileName = new StringBuffer();
                    fileName.append(UUID.randomUUID().toString());
                    fileName.append(".jpeg");
                    try {
                        String uploadResult=fileService.uploadFile(buffer,fileName.toString() , "noneBusiness");
                        UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
                        UploadFileResultData data = uploadFileResult.getData();
                        tXfNoneBusinessUploadDetailEntity.setUploadPath(data.getUploadPath());
                        tXfNoneBusinessUploadDetailEntity.setUploadId(data.getUploadId());
                    } catch (IOException e) {
                        log.error("saveMtrInfo uploadFile error:{}",e);
                    }
                }
            }
            mtrIcSaveDto.setPdfUrl(null);

        }

        if (StringUtils.isNotBlank(mtrIcSaveDto.getPdfUrl())) {
            tXfNoneBusinessUploadDetailEntity.setFileType(String.valueOf(Constants.FILE_TYPE_PDF));
            String base64 = verificationService.getBase64ByRealUrl(Base64.encode(mtrIcSaveDto.getPdfUrl().getBytes()));
            if (StringUtils.isNotBlank(base64)) {
                byte[] buffer = Base64.decode(base64);
                StringBuffer fileName = new StringBuffer();
                fileName.append(UUID.randomUUID().toString());
                fileName.append(".pdf");
                try {
                    String uploadResult=fileService.uploadFile(buffer,fileName.toString() , "noneBusiness");
                    UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
                    UploadFileResultData data = uploadFileResult.getData();
                    tXfNoneBusinessUploadDetailEntity.setSourceUploadPath(data.getUploadPath());
                    tXfNoneBusinessUploadDetailEntity.setSourceUploadId(data.getUploadId());
                    tXfNoneBusinessUploadDetailEntity.setUploadPath(data.getUploadPath());
                    tXfNoneBusinessUploadDetailEntity.setUploadId(data.getUploadId());
                } catch (IOException e) {
                    log.error("saveMtrInfo uploadFile error:{}",e);
                }
            }

        }
        //更新非商表
		QueryWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new QueryWrapper<>();
		wrapper.eq(TXfNoneBusinessUploadDetailEntity.INVOICE_NO, tDxRecordInvoiceEntity.getInvoiceNo());
		wrapper.eq(TXfNoneBusinessUploadDetailEntity.INVOICE_CODE, tDxRecordInvoiceEntity.getInvoiceCode());
		TXfNoneBusinessUploadDetailEntity resultNoneEntity = noneBusinessService.getOne(wrapper);
		if (Objects.nonNull(resultNoneEntity)) {
			tXfNoneBusinessUploadDetailEntity.setId(resultNoneEntity.getId());
		}
        if (CollectionUtils.isNotEmpty(mtrIcSaveDto.getMtrIcInvoiceDetailDto())) {
            MtrIcInvoiceDetailDto mtrIcInvoiceDetailDto = mtrIcSaveDto.getMtrIcInvoiceDetailDto().get(0);
            tXfNoneBusinessUploadDetailEntity.setGoodsName(mtrIcInvoiceDetailDto.getGoodsName());
        }
        noneBusinessService.saveOrUpdate(tXfNoneBusinessUploadDetailEntity);
        //更新底账表
        QueryWrapper<TDxRecordInvoiceEntity> wrapper1 = new QueryWrapper<>();
        wrapper1.eq(TDxRecordInvoiceEntity.INVOICE_NO, tDxRecordInvoiceEntity.getInvoiceNo());
        wrapper1.eq(TDxRecordInvoiceEntity.INVOICE_CODE, tDxRecordInvoiceEntity.getInvoiceCode());
        TDxRecordInvoiceEntity resultInvoiceEntity = tDxRecordInvoiceDao.selectOne(wrapper1);
        saveOrUpdateDxInvoice(tDxRecordInvoiceEntity);
        if (Objects.nonNull(resultInvoiceEntity)) {
            tDxRecordInvoiceEntity.setId(resultInvoiceEntity.getId());
            tDxRecordInvoiceEntity.setLastUpdateDate(new Date());
            tDxRecordInvoiceDao.updateById(tDxRecordInvoiceEntity);
        } else {
            tDxRecordInvoiceEntity.setCreateDate(new Date());
            tDxRecordInvoiceEntity.setLastUpdateDate(new Date());
            tDxRecordInvoiceDao.insert(tDxRecordInvoiceEntity);
            List<TDxRecordInvoiceDetailEntity> detailEntityList = mtrServiceConverter.converMtrInvoiceDetail(mtrIcSaveDto.getMtrIcInvoiceDetailDto());
            detailEntityList.stream().forEach(e -> {
                e.setUuid(tDxRecordInvoiceEntity.getInvoiceNo() + tDxRecordInvoiceEntity.getInvoiceCode());
                tDxRecordInvoiceDetailDao.insert(e);
            });
        }

        return R.ok();
    }

    /**
     * 插入扫描表记录
     * @param recordInvoiceEntity
     * @return
     */
    public int saveOrUpdateDxInvoice(TDxRecordInvoiceEntity recordInvoiceEntity) {
        TDxInvoiceEntity entity = new TDxInvoiceEntity();
        BeanUtil.copyProperties(recordInvoiceEntity, entity);
        QueryWrapper<TDxInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxInvoiceEntity.UUID, entity.getUuid());
        TDxInvoiceEntity tDxInvoiceEntity = tDxInvoiceDao.selectOne(wrapper);
        boolean flag = false;
        int result = 0;
        try {
            //判断uuid是否存在
            if (tDxInvoiceEntity == null) {
                //不存在
                //录入
                entity.setBindyesorno("0");
                entity.setPackyesorno("0");
                flag = tDxInvoiceDao.insert(entity) > 0;
            } else {
                result = 1;
                //存在数据
                entity.setId(tDxInvoiceEntity.getId());
                tDxInvoiceDao.updateById(entity);
            }
        } catch (Exception e) {
            log.error("录入发票:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("录入发票失败:" + e.getMessage());
        } finally {
            log.info("更新/插入结果:{},{}-{}", flag, recordInvoiceEntity.getInvoiceCode(), recordInvoiceEntity.getInvoiceNo());
        }
        return result;
    }
}
