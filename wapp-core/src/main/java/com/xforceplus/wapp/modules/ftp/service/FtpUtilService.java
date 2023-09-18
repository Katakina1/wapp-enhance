package com.xforceplus.wapp.modules.ftp.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Service
public class FtpUtilService {

    private static Logger log = LoggerFactory.getLogger(FtpUtilService.class);

    /**
     * sftp操作对象
     */
    private ChannelSftp sftp = null;

    /**
     * ssh 会话
     */
    private Session session = null;

    /**
     * sftp 通道
     */
    private Channel channel = null;
    /**
     * ftp服务器地址
     */
    @Value("${pro.sftp.host}")
    public String hostname;

    /**
     * ftp服务器端口
     */
    @Value("${pro.sftp.default.port}")
    public int port;

    /**
     * ftp登录账号
     */
    @Value("${pro.sftp.username}")
    public String username;

    /**
     * ftp登录密码
     */
    @Value("${pro.sftp.password}")
    public String password;

    @Value("${pro.sftp.default.timeout}")
    public String timeout;

    /**
     * ftp上传的根路径
     */
    @Value("${filePathConstan.remoteExcelFileRootPath}")
    public String pathprefix;

    /**
     * 下载文件的本地默认存储根路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    public String localPathDefault;
    @Value("${pro.sftp.default.privateKey}")
    private String privateKey; //"/home/svcwapptomcat/.ssh/id_rsa";//密钥文件路径
    @Value("${pro.sftp.default.passphrase}")
    private String passphrase="";//密钥口令

    @Value("${pro.sftp.auth-method:private}")
    private String authMethod;

    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径 = basePath+directory
     *
     * @param pathname ftp服务保存地址，完整路径
     * @param fileName 上传到ftp的文件名
     * @param
     *
     * @throws Exception
     */
    public void uploadFile(String pathname, String fileName,InputStream inputStream) throws Exception{
        log.debug("开始上传文件");
        this.initFtpClient();
        try {
            try {
                this.sftp.cd(pathname);
            } catch (SftpException e) {
                //目录不存在，则创建文件夹
                String[] dirs = pathname.split("/");
                String tempPath = "";
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {
                        continue;
                    }
                    tempPath += "/" + dir;
                    try {
                        this.sftp.cd(tempPath);
                    } catch (SftpException ex) {
                        this.sftp.mkdir(tempPath);
                        this.sftp.cd(tempPath);
                    }
                }
            }

//    	File orgFile = new File(originfilename);
//        inputStream = new FileInputStream(orgFile);
            //上传文件
            this.sftp.put(inputStream, fileName);
            inputStream.close();
            //删除本地文件，防止服务器空间不足

            log.debug("上传文件成功");
        }finally {
            closeChannel();
        }
    }


    /**
     * 下载文件。
     * @param directory 下载目录
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     *
     * @throws Exception
     */
    public boolean downloadFile(String directory, String downloadFile, String saveFile) throws Exception{
        try {
            boolean succ = false;
            log.debug("开始下载文件!");
            //文件存储路径是否存在
            File filePath = new File(saveFile);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            this.initFtpClient();
            if (directory != null && !"".equals(directory)) {
                this.sftp.cd(directory);
            }
            String file = saveFile + downloadFile;
            File fileLocal = new File(file);
            if (fileLocal.exists()) {
                fileLocal.delete();
            }
            fileLocal.createNewFile();

            this.sftp.get(downloadFile, file);
            succ = true;
            log.debug("下载文件成功!");
            return succ;
        } finally {
            this.closeChannel();
        }

    }

    /**
     * 关闭ftp客户端连接
     *
     * @since           1.0
     */
    @PreDestroy
    public void destroy() {
        this.closeChannel();
//    	if (sftp != null) {
//            if (sftp.isConnected()) {
//                sftp.disconnect();
//            }
//        }
//        if (session != null) {
//            if (session.isConnected()) {
//                session.disconnect();
//            }
//        }
    }

    /**
     * <p>Description: 关闭SFTP通道</p>
     *
     * @author yuanlz
     * @date 2016年12月21日 下午7:05:21
     */
    public void closeChannel() {
        if (null != this.channel) {
            this.channel.disconnect();
        }
        if (null != this.session) {
            this.session.disconnect();
        }
        if (null != this.sftp) {
            this.sftp.disconnect();
        }
    }


    /**
     * 连接sftp服务器
     * @throws JSchException
     */
    //密码连接方式
//    private void initFtpClient() throws JSchException{
//    	if (null != this.session && this.session.isConnected()
//                || null != this.channel && this.channel.isConnected()
//                || null != this.sftp && this.sftp.isConnected()) {
//            this.closeChannel();
//        }
//        // 初始化端口
//        if (0 > port) {
//            port = 22;
//        }
//        // 创建JSch对象
//        JSch jsch = new JSch();
//        // 通过 用户名，主机地址，端口 获取一个Session对象
//        this.session = jsch.getSession(username, hostname, port);
//        // 设置密码
//        if (password != null) {
//            this.session.setPassword(password);
//        }
//        // 为Session对象设置properties
//        Properties config = new Properties();
//        config.put("StrictHostKeyChecking", "no");
//        this.session.setConfig(config);
//        // 设置超时时间
//        this.session.setTimeout(Integer.parseInt(timeout));
//        // 建立链接
//        this.session.connect();
//        // 打开SFTP通道
//        this.channel = this.session.openChannel("sftp");
//        // 建立SFTP通道的连接
//        this.channel.connect();
//        this.sftp = (ChannelSftp) this.channel;
//    }
    //密钥连接方式
    private void initFtpClient() throws JSchException{
        if (null != this.session && this.session.isConnected()
                || null != this.channel && this.channel.isConnected()
                || null != this.sftp && this.sftp.isConnected()) {
            this.closeChannel();
        }
        // 初始化端口
        if (0 > port) {
            port = 22;
        }
        // 创建JSch对象
        JSch jsch = new JSch();
        if (Objects.equals(authMethod,"private")&&privateKey != null && !"".equals(privateKey)) {
            //使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
            if (passphrase != null && "".equals(passphrase)) {
                jsch.addIdentity(privateKey, passphrase);
            } else {
                jsch.addIdentity(privateKey);
            }
        }else{
            //logger.info("密钥路径初始化失败");
        }
        // 通过 用户名，主机地址，端口 获取一个Session对象
        this.session = jsch.getSession(username, hostname, port);
        // 设置密码
        if (Objects.equals(authMethod,"pwd") && password != null) {
            this.session.setPassword(password);
        }
        // 为Session对象设置properties
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications","publickey,keyboard-interactive,password");
        this.session.setConfig(config);
        // 设置超时时间
        this.session.setTimeout(Integer.parseInt(timeout));
        // 建立链接
        this.session.connect();
        // 打开SFTP通道
        this.channel = this.session.openChannel("sftp");
        // 建立SFTP通道的连接
        this.channel.connect();
        this.sftp = (ChannelSftp) this.channel;
    }

}