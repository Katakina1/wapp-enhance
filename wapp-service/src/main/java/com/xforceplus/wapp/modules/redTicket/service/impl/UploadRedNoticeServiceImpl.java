package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.einvoice.util.DateTimeHelper;
import com.xforceplus.wapp.modules.einvoice.util.SFTPHandler;
import com.xforceplus.wapp.modules.einvoice.util.ZipUtil;
import com.xforceplus.wapp.modules.redTicket.dao.ExamineAndUploadRedNoticeDao;
import com.xforceplus.wapp.modules.redTicket.dao.QueryOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.dao.UploadOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.dao.UploadRedNoticeDao;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.service.UploadRedNoticeService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.FILE_DATE_FORMAT;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by 1 on 2018/10/22 13:56
 */
@Service
public class UploadRedNoticeServiceImpl implements UploadRedNoticeService {

    private final static Logger LOGGER = getLogger(QueryOpenRedTicketDataServiceImpl.class);
    @Autowired
    private UploadOpenRedTicketDataDao uploadOpenRedTicketDataDao;
    /**
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempPath}")
    private String tempPath;
    /**
     * 上传时本地文件存放路径
     */
    @Value("${filePathConstan.depositPath}")
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

    private UploadRedNoticeDao uploadRedNoticeDao;

    private QueryOpenRedTicketDataDao queryOpenRedTicketDataDao;


    @Autowired
    public UploadRedNoticeServiceImpl(UploadRedNoticeDao uploadRedNoticeDao,QueryOpenRedTicketDataDao queryOpenRedTicketDataDao) {
        this.uploadRedNoticeDao = uploadRedNoticeDao;
        this.queryOpenRedTicketDataDao=queryOpenRedTicketDataDao;
    }

    @Override
    public List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map) {
        List<RedTicketMatch> redTicketMatches=uploadRedNoticeDao.queryOpenRedTicket(map);
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
    public Integer getRedTicketMatchListCount(Map<String, Object> params) {
        return uploadRedNoticeDao.getRedTicketMatchListCount(params);
    }

    @Override
    public List<FileEntity> queryRedNoticelist(Map<String, Object> para) {
        return uploadRedNoticeDao.queryRedNoticelist(para);
    }

    @Override @Transactional
    public int deleteRedData(Map<String, Object> para) {
        com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);

        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            handler.deleteRemote(para.get("filePath").toString());
            //修改匹配状态
            this.updateMatchStatusDelectNotice(para);
            //修改不同类型的状态 索赔
            if(para.get("businessType").toString().equals("1")){
                this.updateStatusDelectNotice(para);
            }
            //协议
           /* if(para.get("businessType").toString().equals("2")){
                this.updateProcolStatusDelectNotice(para);
            }*/


        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            LOGGER.debug("远程删除失败！");
        }


        return uploadOpenRedTicketDataDao.deleteRedData(para);
    }

    private void updateStatusDelectNotice(Map<String,Object> para) {
        uploadRedNoticeDao.updateStatusDelectNotice(para);

    }

    private void updateMatchStatusDelectNotice(Map<String,Object> para) {
        uploadRedNoticeDao.updateMatchStatusDelectNotice(para);
    }

    private void updateProcolStatusDelectNotice(Map<String,Object> para) {
        uploadRedNoticeDao.updateProcolStatusDelectNotice(para);
    }

}
