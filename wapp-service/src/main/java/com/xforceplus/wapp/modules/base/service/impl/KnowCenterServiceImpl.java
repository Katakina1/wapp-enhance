package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.dao.KnowCenterDao;
import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
import com.xforceplus.wapp.modules.base.entity.KnowledgeFileExcelEntity;
import com.xforceplus.wapp.modules.base.service.KnowCenterService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class KnowCenterServiceImpl implements KnowCenterService {

    private final static Logger LOGGER = getLogger(KnowCenterServiceImpl.class);

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
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempPath}")
    private String tempPath;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteKnowFileRootPath}")
    private String remoteKnowFileRootPath;

    @Autowired
    private KnowCenterDao knowCenterDao;

    @Override
    public List<KnowledgeFileEntity> queryList(Map<String, Object> map) {
        return knowCenterDao.queryList(map);
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return knowCenterDao.queryCount(map);
    }

    @Override
    public void uploadKnow(MultipartFile file,String venderType) {
        LOGGER.debug("----------------上传知识中心文件开始--------------------");
        //文件名称
        String fileName = this.getOriginalFilename(file.getOriginalFilename());
        //获取文件类型
        String fileType = fileName.substring(fileName.indexOf(".")+1);
        SFTPHandler handler = SFTPHandler.getHandler(remoteKnowFileRootPath);
        String path="";
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            path =  handler.upload(file,tempPath);
            LOGGER.debug("{}" , path);
        } catch (Exception e){
            LOGGER.debug("----------------上传知识中心文件异常--------------------:{}" , e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
        if(StringUtils.isNotBlank(path)){
            KnowledgeFileEntity entity = new KnowledgeFileEntity();
            entity.setFileExtension(fileType);
            entity.setFileName(fileName);
            entity.setFilePath(path);
            entity.setVenderType(venderType);
            if(file.getSize()!=0) {
                entity.setFileSize(file.getSize() / 1024);
            } else{
                entity.setFileSize(0L);
            }
            //保存上传文件的信息
            knowCenterDao.saveKnowFile(entity);
        }
        LOGGER.debug("----------------上传知识中心文件完成--------------------");

    }

    @Override
    public void getDownLoadFile(String path,String fileName, HttpServletResponse response) {
        SFTPHandler handler = SFTPHandler.getHandler(remoteKnowFileRootPath,tempPath);
        try {
            String name =fileName.split("\\.")[0];
            String fileType= fileName.substring(fileName.indexOf("."));
            String pathName =  path.substring(path.lastIndexOf('/') + 1);
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            response.reset();
            //设置响应头
            response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(name,"UTF-8")+fileType);
            OutputStream output = response.getOutputStream();
            handler.download(path, pathName);
            File file = new File(tempPath+pathName);
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
            LOGGER.debug("----下载文件异常---" + e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }
    @Override
    public List<KnowledgeFileExcelEntity> toExcel(List<KnowledgeFileEntity> list){
        List<KnowledgeFileExcelEntity> list2=new ArrayList<>();
        for (KnowledgeFileEntity kf:list) {
            KnowledgeFileExcelEntity kfe=new KnowledgeFileExcelEntity();
            kfe.setRownum(kf.getRownum());
            kfe.setVenderType(formateVenderType(kf.getVenderType()));
            kfe.setFileExtension(kf.getFileExtension());
            kfe.setFileName(kf.getFileName());
            kfe.setFileSize(kf.getFileSize().toString());
            kfe.setUploadDate(formatDate(kf.getUploadDate()));
            list2.add(kfe);
        }
        return list2;
    }

    /**
     * 获取上传文件的名称
     *
     * @param filename 文件的原始名称，有可能包含路径
     * @return
     */
    private String getOriginalFilename(String filename) {

        int unixSep = filename.lastIndexOf("/");
        int winSep = filename.lastIndexOf(SUBSTR_REGEX_FOR_FILE);
        int pos = winSep > unixSep ? winSep : unixSep;
        return pos != -1 ? filename.substring(pos + 1) : filename;

    }

    /**
     * 删除上传的文件信息
     * @param entity 文件
     * */
    public void deleteOne(KnowledgeFileEntity entity) throws JSchException, SftpException{
        knowCenterDao.deleteOne(entity.getFileId());
        deleteFromFTP(entity.getFilePath());

    }

    private String deleteFromFTP(String filePath) throws JSchException, SftpException {
        SFTPHandler handler = SFTPHandler.getHandler(remoteKnowFileRootPath);
        handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
        handler.deleteRemote(filePath);

        return "删除成功";
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateVenderType(String venderType){
        String value="";
        if("0".equals(venderType)){
            value="商品";
        }else if("1".equals(venderType)){
            value="费用";
        }
        return value;
    }
}
