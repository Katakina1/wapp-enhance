package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class ScanThread extends Thread{
    private ExportEntity exportEntity;
    private  MultipartFile file;
    private Map<String, String> ocrMap;
    private ImportSignService importSignService;
     
    public ScanThread(ExportEntity exportEntity, MultipartFile file, Map<String, String> ocrMap, ImportSignService importSignService){
        this.exportEntity=exportEntity;
        this.file=file;
        this.ocrMap=ocrMap;
        this.importSignService=importSignService;
    }
     
    @Override
    public void run() {
        try {
            getInstance(exportEntity,file,ocrMap,importSignService);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public  void getInstance(ExportEntity exportEntity,MultipartFile file,Map<String, String> ocrMap,ImportSignService importSignService) throws Exception {
        String uuid = importSignService.excuteUpload(exportEntity, file, ocrMap);
    }
}