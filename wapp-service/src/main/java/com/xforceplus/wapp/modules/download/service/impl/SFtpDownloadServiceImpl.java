package com.xforceplus.wapp.modules.download.service.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.download.service.SFtpDownloadService;
import com.xforceplus.wapp.modules.enterprise.service.ExportService;
import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;

@Service
public class SFtpDownloadServiceImpl implements SFtpDownloadService {

    private final static Logger LOGGER = getLogger(SFtpDownloadServiceImpl.class);

    //sftp IP底账
    @Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
    @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
    @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
    @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteCostFileRootPath}")
    private String remoteCostFileRootPath;
    /**
     * 远程文件临时存放路径
     */
    @Value("${filePathConstan.remoteCostFileTempRootPath}")
    private String remoteCostFileTempRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;
    
    @Autowired
    private ExportService exportService;
    
    public void download(String schemaLabel, Map<String, Object> parmMap, HttpServletResponse response) {
    	int serviceType = Integer.parseInt((String)parmMap.get("serviceType"));
    	
    	if (serviceType == 2) {
    		ExportLogEntity exportLog = exportService.getExportLog(schemaLabel, parmMap);
    		if (exportLog != null && !StringUtils.isEmpty(exportLog.getFilepath())) {
    			Map<String, String> pathMap = this.getFtpFileMap(exportLog.getFilepath());
    			String filePath = pathMap.get("filePath");
    			String fileName = pathMap.get("fileName");
    			this.downloadFile((filePath + fileName), fileName, response);
    		}
    	}
    }
    
    public void downloadFile(String filePath, String fileName, HttpServletResponse response) {
        SFTPHandler handler = SFTPHandler.getHandler(localImageRootPath, localImageRootPath);
        try {
            fileName = new String(fileName.getBytes("utf-8"),"iso-8859-1");
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            response.reset();
            //设置响应头
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            OutputStream output = response.getOutputStream();
            handler.download(filePath, fileName);
            File file = new File(handler.getLocalImageRootPath()+fileName);
            FileInputStream in = new FileInputStream(file);// 获取实体类对应Byte
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            output.flush();
            in.close();
            output.close();
        } catch (Exception e) {
        	LOGGER.debug(e.getMessage(), e);
            LOGGER.debug("----下载文件异常---" + e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }
	
	
	 private Map<String, String> getFtpFileMap(String imagePath) {
		  Map<String, String> ftpFileMap = new HashMap<String, String>();
		  StringBuilder filePath = new StringBuilder();
		  //linux服务器
		  if(imagePath.indexOf(":")<0) {
		   filePath.append("/");
		  }
		  String[] filePaths = imagePath.split("/");
		  
		  for(int i=0;i<filePaths.length-1;i++) {
		   if(!StringUtils.isEmpty(filePaths[i])) {
		    filePath.append(filePaths[i]);
		    filePath.append("/");
		   }
		  }
		  ftpFileMap.put("filePath", filePath.toString());
		  ftpFileMap.put("fileName", filePaths[filePaths.length-1]);
		  return ftpFileMap;
	 }
}
