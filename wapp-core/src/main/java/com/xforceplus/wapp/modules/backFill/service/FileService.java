package com.xforceplus.wapp.modules.backFill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.IOException;

@Service
@Slf4j
public class FileService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${Esb.url.upload}")
    private String uploadUrl;

    @Value("${Esb.url.downLoad}")
    private String downLoadUrl;
    public String uploadFile(MultipartFile file) throws IOException {
        //设置请求头
    	
        return uploadFile(file.getBytes(), file.getOriginalFilename());
    }
    
    public String uploadFile(byte[] fileBytes,String  originalFilename) throws IOException {
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        headers.setContentType(type);

        //设置请求体，注意是LinkedMultiValueMap
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        ByteArrayResource contentsAsResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return originalFilename;
            }
        };
        form.add("uploadFile", contentsAsResource);
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        String s = restTemplate.postForObject(uploadUrl, files, String.class);
        log.info("上传文件返回结果:{}", s);
        return s;
    }

    public byte[] downLoadFile4ByteArray(String uploadId)  {
        final ResponseEntity<byte[]> entity = restTemplate.getForEntity(downLoadUrl + "?uploadId=" + uploadId, byte[].class);
        return entity.getBody();
    }

    public String downLoadFile(String uploadId)throws IOException {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(downLoadUrl + "?uploadId=" + uploadId, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        if (responseEntity.getBody() != null) {
            return new BASE64Encoder().encode(responseEntity.getBody());
        }
        return null;
    }
}
