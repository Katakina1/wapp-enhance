package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.redTicket.dao.QueryOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.dao.UploadOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import com.xforceplus.wapp.modules.redTicket.service.UploadOpenRedTicketDataService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.apache.log4j.LogManager.getLogger;

/**
 * Created by 1 on 2018/11/12 11:49
 */
@Service
public class UploadOpenRedTicketDataServiceImpl implements UploadOpenRedTicketDataService {
    private static final Logger LOGGER = getLogger(UploadOpenRedTicketDataServiceImpl.class);
    /**
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempPath}")
    private String tempPath;
    /**
     * 上传时本地文件存放路径
     */
    @Value("${filePathConstan.remoteCostFileRootPath}")
    private String depositPath;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteImageRootPath}")
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

    @Autowired
    private UploadOpenRedTicketDataDao uploadOpenRedTicketDataDao;

    @Autowired
    private QueryOpenRedTicketDataDao queryOpenRedTicketDataDao;

    @Override
    public Integer getRedTicketMatchListCount(Map<String, Object> map) {
        return uploadOpenRedTicketDataDao.getRedTicketMatchListCount(map);
    }

    @Override
    public List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map) {
        List<RedTicketMatch> redTicketMatches=uploadOpenRedTicketDataDao.queryOpenRedTicket(map);
        for (int i = 0; i < redTicketMatches.size(); i++){
            if(redTicketMatches.get(i).getBusinessType().equals("2")){
                BigDecimal taxRate =(redTicketMatches.get(i).getTaxRate()).divide(new BigDecimal(100));
                taxRate = taxRate.add(new BigDecimal(1));
                redTicketMatches.get(i).setRedTotalAmount(redTicketMatches.get(i).getRedTotalAmount().multiply(taxRate));
            }
        }
        return redTicketMatches;
    }

    @Override
    public List<FileEntity> queryRedDatalist(Map<String, Object> para) {
        List<FileEntity> fileEntities = uploadOpenRedTicketDataDao.queryRedDatalist(para);

        for(int i = 0 ; i < fileEntities.size();i++){
            String fileName=  fileEntities.get(i).getFileName() +'.'+fileEntities.get(i).getFileType();
            fileEntities.get(i).setFileName(fileName);
        }
        return fileEntities;
    }

    @Override
    public int deleteRedData(Map<String, Object> para)  {
        com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);

        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            handler.deleteRemote(para.get("filePath").toString());
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            LOGGER.debug("远程删除失败！");
        }


        return uploadOpenRedTicketDataDao.deleteRedData(para);
    }
}
