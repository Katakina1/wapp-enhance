package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.einvoice.util.SFTPHandler;
import com.xforceplus.wapp.modules.einvoice.util.ZipUtilRead;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.dao.PhoneAppSignInDao;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.PhoneAppSignInService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/14.
 **/
@Service
@Transactional
public class PhoneAppSignInServiceImpl implements PhoneAppSignInService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PhoneAppSignInServiceImpl.class);
    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteImageRootPath}")
    private String remoteImageRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.signInImgPath}")
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

    private PhoneAppSignInDao phoneAppSignInDao;

    @Autowired
    public PhoneAppSignInServiceImpl(PhoneAppSignInDao phoneAppSignInDao) {
        this.phoneAppSignInDao = phoneAppSignInDao;
    }

    @Override
    public List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query) {
        return phoneAppSignInDao.getRecordIncoiceList(schemaLabel,query);
    }

    @Override
    public int queryTotal(String schemaLabel, Query query) {
        return phoneAppSignInDao.getTotal(schemaLabel,query);
    }

    @Override
    public Map<String, BigDecimal> getSumAmount(String schemaLabel, Query query) {
        return phoneAppSignInDao.getSumAmount(schemaLabel,query);
    }

    @Override
    public List<OptionEntity> searchGf(String schemaLabel, Long userId) {
        return phoneAppSignInDao.searchGf(schemaLabel,userId);
    }

    @Override
    public List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params) {
        return phoneAppSignInDao.queryAllList(schemaLabel,params);
    }

    @Override
    public String getUrlById(String schemaLabel, String uuid) {
         String invoiceImage = phoneAppSignInDao.getUrlById(schemaLabel,uuid);
        SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        try {
            if (!StringUtils.isEmpty(invoiceImage)) {
                imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                //默认文件名
                final String userAccount = "invoiceImg";
                imageHandler.download(invoiceImage, userAccount + ".zip");

                final byte[] zipFile = ZipUtilRead.readZipFile(imageHandler.getLocalImageRootPath() + userAccount + ".zip");

                return org.apache.commons.codec.binary.Base64.encodeBase64String(zipFile);
            }
        } catch (Exception e) {
            LOGGER.info("获取图片失败:{}", e);
        }finally {
            imageHandler.closeChannel();
        }
       return null;

    }
}
