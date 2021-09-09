package com.xforceplus.wapp.modules.ExcelClean.service.impl;


import com.xforceplus.wapp.modules.ExcelClean.dao.ExportExcelCleanDao;
import com.xforceplus.wapp.modules.ExcelClean.service.ExportExcleCleanService;

import com.xforceplus.wapp.modules.einvoice.util.SFTPHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service("exportExcleCleanService")
public class ExportExcelCleanServiceImpl implements ExportExcleCleanService {
    private static final Logger LOGGER= getLogger(ExportExcelCleanServiceImpl.class);


    @Autowired
    ExportExcelCleanDao exportExcelCleanDao;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteExcelFileRootPath}")
    private String remoteImageRootPath;

    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;

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

    @Override
    public void messagecontrolClean() {
        //清除ftp
        List<Map> exportlogList=exportExcelCleanDao.getExportlogByMonth();

        for (Map exportlog:exportlogList){
            if(exportlog.containsKey("filepath")&&null!=exportlog.get("filepath")&&!"".equals(exportlog.get("filepath"))){
// 删除sftp文件服务器上的zip压缩图片
                SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
                try {
                    imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                    imageHandler.deleteRemote(exportlog.get("filepath")+"");
                    LOGGER.debug("--------------删除excel文件成功："+exportlog.get("filepath")+"------------");
                } catch (Exception e) {
                    LOGGER.debug("----删除excel文件时异常："+exportlog.get("filepath")+"---" + e);
                } finally {
                    //关闭远程sftp连接
                    imageHandler.closeChannel();
                    LOGGER.debug("--------------关闭远程sftp连接成功------------");
                }
            }
        }
        //清除
        exportExcelCleanDao.exportExcelClean();
        //清除机器人报文日志
        exportExcelCleanDao.delectRobotMessage();
    }

 }
