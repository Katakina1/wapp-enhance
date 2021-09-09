package com.xforceplus.wapp.modules.fixed.service.impl;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.fixed.dao.FixedMatchDao;
import com.xforceplus.wapp.modules.fixed.dao.FixedQuestionnaireDao;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.service.QuestionnaireService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service("FixedQuestionnaireService")
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final static Logger LOGGER = getLogger(QuestionnaireServiceImpl.class);

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
    @Value("${filePathConstan.remoteFixedRootPath}")
    private String remoteRootPath;
    /**
     * 远程文件临时存放路径
     */
    @Value("${filePathConstan.remoteFixedTempRootPath}")
    private String remoteTempRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localFixedRootPath}")
    private String localRootPath;

    @Autowired
    private FixedQuestionnaireDao questionnaireDao;

    @Autowired
    private FixedMatchDao matchDao;

    @Override
    public List<MatchQueryEntity> queryList(Map<String, Object> map) {
        return questionnaireDao.queryList(map);
    }

    @Override
    public int queryCount(Map<String, Object> map) {
        return questionnaireDao.queryCount(map);
    }

    @Override
    public List<RecordInvoiceEntity> getInvoiceDetail(Long matchId) {
        return questionnaireDao.getInvoiceDetail(matchId);
    }

    @Override
    public List<OrderEntity> getOrderDetail(Long matchId) {
        return questionnaireDao.getOrderDetail(matchId);
    }

    @Override
    public List<FileEntity> getFileDetail(Long matchId) {
        return questionnaireDao.getFileDetail(matchId);
    }

    @Override
    public void viewImg(Long id, HttpServletResponse response) {
        FileEntity entity = questionnaireDao.getFileInfo(id);
        String filePath = entity.getFilePath();
        String fileName = entity.getFileName();
        SFTPHandler handler = SFTPHandler.getHandler(remoteTempRootPath,localRootPath );
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            //文件后缀
            String suffix = filePath.substring(filePath.lastIndexOf('.')+1);
            response.reset();
            //设置相应图片类型的响应头
            if("png".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","image/png");
            }
            if("pdf".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","application/pdf");
            }
            if("jpeg".equalsIgnoreCase(suffix) || "jpe".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","image/jpeg");
            }
            if("gif".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","image/gif");
            }
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
            LOGGER.debug("----获取图片异常---" + e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response) {
        FileEntity entity = questionnaireDao.getFileInfo(id);
        String filePath = entity.getFilePath();
        String fileName = entity.getFileName();
        SFTPHandler handler = SFTPHandler.getHandler(remoteTempRootPath,localRootPath);
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
            LOGGER.debug("----下载文件异常---" + e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }

    @Override
    public void fileConfirm(MatchQueryEntity match) {
        //问题单文件
        List<FileEntity> fileList = match.getFileList();
        //原问题单文件
        List<FileEntity> fileListOriginal = questionnaireDao.getFileDetail(match.getId());
        if(fileList!=null && !fileList.isEmpty()){
            for(FileEntity file : fileList){
                //新上传的文件
                if(file.getMatchId()==null) {
                    //移动文件(临时->正式),返回正式路径
                    String path = moveFile(file.getFilePath());
                    //更新路径,关联匹配号
                    matchDao.updateFile(path, match.getId(), file.getId());
                }else {
                    for (FileEntity fe : fileListOriginal) {
                        //没有删除的文件
                        if (file.getId() .equals( fe.getId())) {
                            fileListOriginal.remove(fe);
                            break;
                        }
                    }
                }
            }
        }
        //剩下的为删除的文件
        for (FileEntity deFile : fileListOriginal) {
            questionnaireDao.deleteFile(deFile.getId());
        }
    }

    /**
     * 将文件从临时目录移动至正式目录
     * @param filePath 临时目录路径
     * @return
     */
    private String moveFile(String filePath){
        LOGGER.debug("----------------移动问题单文件开始--------------------");
        String path = "";
        SFTPHandler handler = SFTPHandler.getHandler(remoteRootPath, remoteTempRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            path = handler.move(filePath);
        } catch (Exception e){
            LOGGER.debug("----------------移动问题单文件异常--------------------:{}" , e);
        } finally {
            handler.closeChannel();
        }
        LOGGER.debug("----------------移动问题单文件完成--------------------");
        return path;
    }
}
