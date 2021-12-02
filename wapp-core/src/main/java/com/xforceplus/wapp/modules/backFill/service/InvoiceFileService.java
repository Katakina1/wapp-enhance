package com.xforceplus.wapp.modules.backFill.service;

import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.backFill.model.InvoiceFileEntity;
import com.xforceplus.wapp.modules.backFill.model.InvoiceMain;
import com.xforceplus.wapp.modules.backFill.model.UploadFileResult;
import com.xforceplus.wapp.repository.daoExt.InvoiceFileDao;
import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceFileEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


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
    private RestTemplate restTemplate;

    public void save(TXfElecUploadRecordDetailEntity electronicUploadRecordDetailEntity, InvoiceMain invoiceMain) {
        TXfInvoiceFileEntity file;
        List<Integer> types = new ArrayList<>();

        boolean isOfd = false;
        if (!electronicUploadRecordDetailEntity.getFileType()) {
            types.add(InvoiceFileEntity.TYPE_OF_JPEG);
            types.add(InvoiceFileEntity.TYPE_OF_OFD);
            isOfd = true;
        }else {
            types.add(InvoiceFileEntity.TYPE_OF_PDF);
        }
        final List<TXfInvoiceFileEntity> invoiceAndTypes = invoiceFileDao.getByInvoiceAndTypes(invoiceMain.getInvoiceNo(), invoiceMain.getInvoiceCode(), types);
        Map<Integer, TXfInvoiceFileEntity> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(invoiceAndTypes)) {
            invoiceAndTypes.forEach(x -> map.put(x.getType(), x));
        }

        if (isOfd) {
            file = map.get(InvoiceFileEntity.TYPE_OF_OFD);

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

        final ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(imgPreviewUrl, byte[].class);
        try {
            final String uploadFile = fileService.uploadFile(forEntity.getBody(), UUID.randomUUID().toString().replace("-", "") + ".jpeg",invoiceMain.getSellerTaxNo());
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

    private void updateImg(TXfInvoiceFileEntity img, String imgPreviewUrl,InvoiceMain invoiceMain) {
        try {
            final ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(imgPreviewUrl, byte[].class);
            final String uploadFile = fileService.uploadFile(forEntity.getBody(), UUID.randomUUID().toString().replace("-", "") + ".jpeg",invoiceMain.getSellerTaxNo());
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

}
