package com.xforceplus.wapp.modules.backfill.service;

import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.backfill.model.UploadFileResult;
import com.xforceplus.wapp.modules.backfill.model.UploadFileResultData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${Esb.url.upload}")
    private String uploadUrl;

    @Value("${Esb.url.downLoad}")
    private String downLoadUrl;
    @Value("${wapp.nas.url}")
    private String nasBaseUrl;

    public String uploadFile(MultipartFile file, String venderId) throws IOException {
        //设置请求头

        return uploadFile(file.getBytes(), file.getOriginalFilename(), venderId);
    }

    public String uploadFile(byte[] fileBytes, String originalFilename, String venderId) throws IOException {
        File file = FileUtils.getFile(nasBaseUrl + "/" + venderId + "/" + DateUtils.getStringDateShort());
        if (!file.exists()) {
            file.mkdirs();
        }
        File uploadFile = FileUtils.getFile(file, originalFilename);
        FileUtils.writeByteArrayToFile(uploadFile,fileBytes);
        UploadFileResult uploadFileResult = new UploadFileResult();
        UploadFileResultData data = new UploadFileResultData();
        data.setUploadPath(uploadFile.getAbsolutePath());
        data.setUploadId(UUID.randomUUID().toString());
        uploadFileResult.setData(data);

        return JsonUtil.toJsonStr(uploadFileResult);
    }

    public Boolean deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        File file = FileUtils.getFile(filePath);
        if (file.exists()) {
            file.delete();
        }
        return true;
    }

    public byte[] downLoadFile4ByteArray(String uploadId) {
        File file = FileUtils.getFile(uploadId);
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error("下载图片异常:{}", e);
        }
        return null;
    }

    public String downLoadFile(String uploadId) throws IOException {
        File file = FileUtils.getFile(uploadId);
        return new BASE64Encoder().encode(FileUtils.readFileToByteArray(file));
    }
}