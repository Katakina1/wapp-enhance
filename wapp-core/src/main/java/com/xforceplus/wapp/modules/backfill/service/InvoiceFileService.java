package com.xforceplus.wapp.modules.backfill.service;

import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.modules.backfill.model.InvoiceFileEntity;
import com.xforceplus.wapp.modules.backfill.model.InvoiceMain;
import com.xforceplus.wapp.modules.backfill.model.UploadFileResult;
import com.xforceplus.wapp.repository.daoExt.InvoiceFileDao;
import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceFileEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.*;

import static com.xforceplus.wapp.constants.Constants.*;


/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-23 19:50
 **/
@Service
@Slf4j
public class InvoiceFileService{
    @Autowired
    private InvoiceFileDao invoiceFileDao;

    @Autowired
    private IDSequence idSequence;

    @Autowired
    private FileService fileService;


    @Autowired
    @Lazy
    private VerificationService verificationService;

    public void save(TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity, InvoiceMain invoiceMain) {
        TXfInvoiceFileEntity file;
        List<Integer> types = new ArrayList<>();

        boolean isOfd = false;
        boolean isXml = false;
        if (Objects.equals(FILE_TYPE_OFD, electronicUploadRecordDetailEntity.getFileType())) {
            types.add(InvoiceFileEntity.TYPE_OF_JPEG);
            types.add(InvoiceFileEntity.TYPE_OF_OFD);
            isOfd = true;
        }else if (Objects.equals(FILE_TYPE_XML, electronicUploadRecordDetailEntity.getFileType())) {
            types.add(InvoiceFileEntity.TYPE_OF_XML);
            isXml = true;
        } else {
            types.add(InvoiceFileEntity.TYPE_OF_PDF);
        }
        final List<TXfInvoiceFileEntity> invoiceAndTypes = invoiceFileDao.getByInvoiceAndTypes(invoiceMain.getInvoiceNo(), invoiceMain.getInvoiceCode(), types);
        Map<Integer, TXfInvoiceFileEntity> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(invoiceAndTypes)) {
            invoiceAndTypes.forEach(x -> map.put(x.getType(), x));
        }

        if (isOfd) {
            file = map.get(InvoiceFileEntity.TYPE_OF_OFD);

        } else if (isXml) {
            file = map.get(InvoiceFileEntity.TYPE_OF_XML);
        } else {
            file = map.get(InvoiceFileEntity.TYPE_OF_PDF);
        }

        if (file == null) {
            file = new TXfInvoiceFileEntity();
            file.setInvoiceNo(invoiceMain.getInvoiceNo());
            file.setInvoiceCode(invoiceMain.getInvoiceCode());
            file.setStatus(1);
            file.setId(idSequence.nextId());
            file.setStorage(0);
            file.setOrigin(0);
            file.setCreateUser(electronicUploadRecordDetailEntity.getCreateUser());
            file.setPath(electronicUploadRecordDetailEntity.getUploadPath());
            if (isOfd) {
                file.setType(InvoiceFileEntity.TYPE_OF_OFD);
                if(StringUtils.isNotEmpty(invoiceMain.getOfdImageUrl())){
                    final TXfInvoiceFileEntity jpeg = map.get(InvoiceFileEntity.TYPE_OF_JPEG);
                    if (jpeg != null) {
                        updateImg(jpeg, invoiceMain.getOfdImageUrl(),invoiceMain);
                    } else {
                        saveImg(file, invoiceMain.getOfdImageUrl(),invoiceMain);
                    }
                }
            } else if (isXml) {
                file.setType(InvoiceFileEntity.TYPE_OF_XML);
            } else {
                file.setType(InvoiceFileEntity.TYPE_OF_PDF);
            }
            this.invoiceFileDao.save(file);
        } else {
            if (isOfd) {
                if(StringUtils.isNotEmpty(invoiceMain.getOfdImageUrl())){
                    final TXfInvoiceFileEntity jpeg = map.get(InvoiceFileEntity.TYPE_OF_JPEG);
                    if (jpeg != null) {
                        updateImg(jpeg, invoiceMain.getOfdImageUrl(),invoiceMain);
                    } else {
                        saveImg(file, invoiceMain.getOfdImageUrl(),invoiceMain);
                    }
                }
            }
            file.setPath(electronicUploadRecordDetailEntity.getUploadPath());
            this.invoiceFileDao.update(file);
        }
    }


    public void save(String invoiceCode,String invoiceNo,String path, int type,Long userId) {
        TXfInvoiceFileEntity file = new TXfInvoiceFileEntity();
        file.setInvoiceNo(invoiceNo);
        file.setInvoiceCode(invoiceCode);
        file.setStatus(1);
        file.setId(idSequence.nextId());
        file.setStorage(0);
        file.setOrigin(0);
        file.setCreateUser(String.valueOf(userId));
        file.setPath(path);
        file.setType(type);
        this.invoiceFileDao.save(file);
    }

    private void saveImg(TXfInvoiceFileEntity ofd, String imgPreviewUrl,InvoiceMain invoiceMain) {
        String result = verificationService.getBase64ByRealUrl(imgPreviewUrl);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(result)){
            try {
                final String uploadFile = fileService.uploadFile(Base64.getDecoder().decode(result), UUID.randomUUID().toString().replace("-", "") + ".jpeg",invoiceMain.getSellerTaxNo());
                UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
                final String uploadId = uploadFileResult.getData().getUploadId();
                TXfInvoiceFileEntity img = new TXfInvoiceFileEntity();
                img.setPath(uploadId);
                img.setType(InvoiceFileEntity.TYPE_OF_JPEG);
                img.setInvoiceCode(ofd.getInvoiceCode());
                img.setInvoiceNo(ofd.getInvoiceNo());
                img.setStorage(ofd.getStorage());
                img.setOrigin(ofd.getOrigin());
                img.setStatus(ofd.getStatus());
                img.setCreateUser(ofd.getCreateUser());
                img.setCreateTime(new Date());
                img.setId(idSequence.nextId());
                this.invoiceFileDao.save(img);
            } catch (IOException e) {
                log.error("发票:no:["+ofd.getInvoiceNo()+"],code:["+ofd.getInvoiceCode()+"]上传文件失败:" + e.getMessage(), e);
            }
        }

    }

    private void updateImg(TXfInvoiceFileEntity img, String imgPreviewUrl,InvoiceMain invoiceMain) {
        String result = verificationService.getBase64ByRealUrl(imgPreviewUrl);
        try {
            final String uploadFile = fileService.uploadFile(Base64.getDecoder().decode(result), UUID.randomUUID().toString().replace("-", "") + ".jpeg",invoiceMain.getSellerTaxNo());
            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
            final String uploadId = uploadFileResult.getData().getUploadId();
            img.setPath(uploadId);
            img.setType(InvoiceFileEntity.TYPE_OF_JPEG);
            this.invoiceFileDao.update(img);
        } catch (IOException e) {
            log.error("发票:no:["+img.getInvoiceNo()+"],code:["+img.getInvoiceCode()+"]上传文件失败:" + e.getMessage(), e);
        }catch (RestClientException e){
            log.error("发票:no:["+img.getInvoiceNo()+"],code:["+img.getInvoiceCode()+"]获取国税文件失败"+e.getMessage(),e);
        }
    }

    public List<TXfInvoiceFileEntity> getByInvoice(String invoiceNo, String invoiceCode){
        return invoiceFileDao.getByInvoice(invoiceNo,invoiceCode);
    }

    public TXfInvoiceFileEntity getInvoiceFileUrl(String invoiceNo, String invoiceCode) {
        List<TXfInvoiceFileEntity> invoiceFileEntityList = getByInvoice(invoiceNo, invoiceCode);
        if (null == invoiceFileEntityList || invoiceFileEntityList.isEmpty()) {
            return null;
        }
        TXfInvoiceFileEntity resultEntity = new TXfInvoiceFileEntity();
        //优先使用图片，其次使用pdf
        for (TXfInvoiceFileEntity invoiceFileEntity : invoiceFileEntityList) {
            if (invoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_JPEG) || invoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_JPG)
                    || StringUtils.equals(invoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_JPG.replace(".", ""))
                    || StringUtils.equals(invoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_JPEG.replace(".", ""))) {
                return invoiceFileEntity;
            }
            if (invoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_PDF)
                    || StringUtils.equals(invoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_PDF.replace(".", ""))) {
                resultEntity = invoiceFileEntity;
                continue;
            }
        }

        return resultEntity;
    }

    public TXfInvoiceFileEntity getSourceInvoiceFileUrl(String invoiceNo, String invoiceCode) {
        List<TXfInvoiceFileEntity> invoiceFileEntityList = getByInvoice(invoiceNo, invoiceCode);
        if (null == invoiceFileEntityList || invoiceFileEntityList.isEmpty()) {
            return null;
        }
        //使用pdf
        for (TXfInvoiceFileEntity invoiceFileEntity : invoiceFileEntityList) {
            if (invoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_PDF)) {
                return invoiceFileEntity;
            }
        }

        return null;
    }

    public TXfInvoiceFileEntity getSourceInvoiceFileUrl(String invoiceNo, String invoiceCode, String invoiceType) {
        List<TXfInvoiceFileEntity> invoiceFileEntityList = getByInvoice(invoiceNo, invoiceCode);
        if (null == invoiceFileEntityList || invoiceFileEntityList.isEmpty()) {
            return null;
        }
        TXfInvoiceFileEntity resultEntity = new TXfInvoiceFileEntity();
        //优先使用图片，其次使用pdf
        for (TXfInvoiceFileEntity invoiceFileEntity : invoiceFileEntityList) {
            if (InvoiceTypeEnum.QC_INVOICE.getValue().equalsIgnoreCase(invoiceType) || InvoiceTypeEnum.QS_INVOICE.getValue().equalsIgnoreCase(invoiceType)) {
                if (invoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_XML)
                        || StringUtils.equals(invoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_XML.replace(".", ""))) {
                    return invoiceFileEntity;
                }
            }
            if (invoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_OFD)
                    || StringUtils.equals(invoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_OFD.replace(".", ""))) {

                return invoiceFileEntity;
            }
            if (invoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_PDF)
                    || StringUtils.equals(invoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_PDF.replace(".", ""))) {
                resultEntity = invoiceFileEntity;
                continue;
            }
        }

        return resultEntity;
    }
}
