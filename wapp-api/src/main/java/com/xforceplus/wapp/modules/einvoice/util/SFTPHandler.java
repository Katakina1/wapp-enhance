package com.xforceplus.wapp.modules.einvoice.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * sftp链接工具类
 *
 * @author yuanlz
 */
public class SFTPHandler {
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
     * 本地图片路径
     */
    private String localImageRootPath;
    /**
     * 远程主机报文备份路径
     */
    private String remoteImageRootPath;
    private String privateKey="/home/svcwapptomcat/.ssh/id_rsa";//密钥文件路径
    private String passphrase="";//密钥口令

    private static final Logger LOGGER = Logger.getLogger(SFTPHandler.class);

    /**
     * <p>Description: 私有构造函数，通过 本地报文路径 远程主机报文路径 本地报文备份路径 远程主机报文备份路径 创建对象</p>
     *
     * @param remoteImageRootPath 远程主机根路径
     */
    private SFTPHandler(String remoteImageRootPath, String localImageRootPath) {
        this.remoteImageRootPath = remoteImageRootPath;
        this.localImageRootPath = localImageRootPath;
    }

    /**
     * <p>Description: 对外暴露的方法，可以通过 本地报文路径 远程主机报文路径 本地报文备份路径 远程主机报文备份路径 获取handler对象</p>
     *
     * @return SFTPHandler SFTP操作对象handler
     * @author yuanlz
     * @date 2016年12月21日 下午10:43:40
     */
    public static SFTPHandler getHandler(String remoteImageRootPath, String localImageRootPath) {
        return new SFTPHandler(remoteImageRootPath, localImageRootPath);
    }

    /**
     * <p>Description: 打开SFTP通道</p>
     *
     * @param host    主机地址
     * @param user    用户名
     * @param pwd     密码
     * @param port    端口号
     * @param timeout 超时时间
     * @author yuanlz
     * @date 2016年12月21日 下午7:03:40
     */
//    public void openChannel(String host, String user, String pwd, int port, int timeout) throws JSchException {
//
//        if (null != this.session && this.session.isConnected()
//                || null != this.channel && this.channel.isConnected()
//                || null != this.sftp && this.sftp.isConnected()) {
//            this.closeChannel();
//        }
//
//        // 初始化端口
//        if (0 > port) {
//            port = 22;
//        }
//
//        // 创建JSch对象
//        JSch jsch = new JSch();
//
//        // 通过 用户名，主机地址，端口 获取一个Session对象
//        this.session = jsch.getSession(user, host, port);
//
//        // 设置密码
//        if (pwd != null) {
//            this.session.setPassword(pwd);
//        }
//
//        // 为Session对象设置properties
//        Properties config = new Properties();
//        config.put("StrictHostKeyChecking", "no");
//        this.session.setConfig(config);
//
//        // 设置超时时间
//        this.session.setTimeout(timeout);
//
//        // 建立链接
//        this.session.connect();
//
//        // 打开SFTP通道
//        this.channel = this.session.openChannel("sftp");
//
//        // 建立SFTP通道的连接
//        this.channel.connect();
//        this.sftp = (ChannelSftp) this.channel;
//    }
    //密钥链接方式
    public void openChannel(String host, String user, String pwd, int port, int timeout) throws JSchException{

        if(null != this.session && this.session.isConnected()
                || null != this.channel && this.channel.isConnected()
                || null != this.sftp && this.sftp.isConnected()){
            this.closeChannel();
        }
        // 初始化端口
        if(0 > port){
            port = 22;
        }

        // 创建JSch对象
        JSch jsch = new JSch();

        if (privateKey != null && !"".equals(privateKey)) {
            //使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
            if (passphrase != null && "".equals(passphrase)) {
                jsch.addIdentity(privateKey, passphrase);
            } else {
                jsch.addIdentity(privateKey);
            }
        }else{
            //logger.info("密钥路径初始化失败");
        }
//        jsch.addIdentity("vn088jh","Fth123456");
        // 通过 用户名，主机地址，端口 获取一个Session对象
        this.session = jsch.getSession(user, host, port);
        //SFTPHandler.logger.info("SFTPHandler : Session Created");

        // 设置密码
//        if (pwd != null) {
//	        	this.session.setPassword(pwd);
//        }
        // 为Session对象设置properties
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications","publickey,keyboard-interactive,password");
        this.session.setConfig(config);

        // 设置超时时间
        this.session.setTimeout(timeout);

        // 建立链接
        this.session.connect();
        //SFTPHandler.logger.info("SFTPHandler : Session Connected.");

        // 打开SFTP通道
        this.channel = this.session.openChannel("sftp");
        //SFTPHandler.logger.info("SFTPHandler : Channel Opened.");

        // 建立SFTP通道的连接
        this.channel.connect();
        //SFTPHandler.logger.info("SFTPHandler : Channel Connected.");
        this.sftp = (ChannelSftp) this.channel;

        //SFTPHandler.logger.info("SFTPHandler : Handler Created.");
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
     * <p>Description: 返回SFTP指定目录下所有文件名</p>
     *
     * @param keyName 文件名关键字
     * @throws JSchException
     * @throws IOException
     * @throws SftpException
     * @author yuanlz
     * @date 2016年12月21日 下午7:12:28
     */
    public List<String> getFileNameList(String keyName) throws JSchException, IOException, SftpException {

        List<String> fileNamelist = new ArrayList<String>();

        if (null == this.sftp || this.sftp.isClosed()) {
            throw new JSchException("链接丢失");
        }

        // 进入目录
        try {
            this.sftp.cd(this.remoteImageRootPath);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) { //TODO 进入根目录了还未进入税号文件夹+年份文件夹
                this.sftp.mkdir(this.remoteImageRootPath);
                this.sftp.cd(this.remoteImageRootPath);
            } else {
                throw e;
            }
        }

        // 根据名称关键字查询所有文件名
        @SuppressWarnings("unchecked")
        Vector<ChannelSftp.LsEntry> vector = this.sftp.ls(keyName);
        for (LsEntry lsEntry : vector) {
            fileNamelist.add(lsEntry.getFilename());
        }

        return fileNamelist;
    }

    /**
     * <p>Description: 获取本地文件名列表</p>
     *
     * @param keyName 文件名关键字
     * @throws FileNotFoundException
     * @author yuanlz
     * @date 2016年12月22日 下午3:52:55
     */
    public List<String> getLocalFileNameList(final String keyName) throws FileNotFoundException {
        List<String> fileNamelist = new ArrayList<String>();
        File directory = new File(this.localImageRootPath);
        // 文件夹是否存在
        if (false == directory.exists()) {
            // 创建文件夹
            if (false == directory.mkdir()) {
                throw new FileNotFoundException();
            }

            // 可写
            if (false == directory.canWrite()) {
                if (false == directory.setWritable(true)) {
                    throw new FileNotFoundException();
                }
            }
        }
        String[] fileNames = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(keyName)) {
                    return true;
                }
                return false;
            }
        });
        fileNamelist.addAll(Arrays.asList(fileNames));
        return fileNamelist;
    }

    /**
     * 下载文件
     *
     * @param downloadPath 远程文件路径
     * @param fileName     下载文件名
     * @throws FileNotFoundException
     * @throws SftpException
     */
    public void download(String downloadPath, String fileName)
            throws FileNotFoundException, SftpException {
        try {
            String pathArry[] = downloadPath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (int i = 0; i < pathArry.length - 1; i++) {
                if (pathArry[i].equals("")) {
                    continue;
                }
                filePath.append(pathArry[i] + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }
            File directory = new File(this.localImageRootPath);
            // 文件夹是否存在
            if (false == directory.exists()) {
                // 创建文件夹
                if (false == directory.mkdirs()) {
                    throw new FileNotFoundException();
                }

                // 可写
                if (false == directory.canWrite()) {
                    if (false == directory.setWritable(true)) {
                        throw new FileNotFoundException();
                    }
                }
            }
            // 下载
            File file = new File(this.localImageRootPath + fileName);
            FileOutputStream out = new FileOutputStream(file);
            this.sftp.get(downloadPath, out);
            if (null != out)
                out.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

    }

    /**
     * <p>Description: 上传文件</p>
     *
     * @param uploadFile 上传的文件名
     * @throws SftpException
     * @throws FileNotFoundException
     * @author yuanlz
     * @date 2016年12月21日 下午10:01:26
     */
    public void upload(String uploadFile) throws SftpException, FileNotFoundException {
        try {
            this.sftp.cd(this.remoteImageRootPath);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                this.sftp.mkdir(this.remoteImageRootPath);
                this.sftp.cd(this.remoteImageRootPath);
            } else {
                throw e;
            }
        }
        File file = new File(this.remoteImageRootPath + uploadFile);
        FileInputStream in = new FileInputStream(file);
        this.sftp.put(in, uploadFile);
        if (null != in)
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
    }

    /**
     * 上传文件 （图片）
     *
     * @param gfTaxNo    购方税号
     * @param pathDate   当前年份
     * @param uploadFile 文件名
     * @return
     * @throws SftpException
     * @throws FileNotFoundException
     */
    public String uploadImg(String gfTaxNo, String pathDate, String uploadFile) throws SftpException, FileNotFoundException {
        String path = null;
        try {
            path = this.remoteImageRootPath + gfTaxNo + "/" + pathDate + "/";
            this.sftp.cd(path);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                try {
                    createDir(path, sftp);
                } catch (Exception e1) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                throw e;
            }
        }
        File file = new File(this.localImageRootPath + uploadFile);
        FileInputStream in = new FileInputStream(file);
        this.sftp.put(in, uploadFile);
        if (null != in)
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        return path;
    }
    
    /**
     * 单纯的上传文件 （图片）
     *
     * @param gfTaxNo    购方税号
     * @param pathDate   当前年份
     * @param uploadFile 文件名
     * @return
     * @throws SftpException
     * @throws FileNotFoundException
     */
    public String onlyUploadImg(String gfTaxNo, String pathDate, String uploadFile) throws SftpException, FileNotFoundException {
        String path = null;
        try {
            path = this.remoteImageRootPath + gfTaxNo + "/" + pathDate + "/";
            this.sftp.cd(path);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                try {
                    createDir(path, sftp);
                } catch (Exception e1) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                throw e;
            }
        }
        File file = new File(this.localImageRootPath + uploadFile);
        FileInputStream in = new FileInputStream(file);
        this.sftp.put(in, uploadFile);
        if (null != in)
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        
        return path;
    }

    public String uploadScanImage(String pathDate, String uploadFile) throws SftpException, FileNotFoundException {
        String path = null;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        String monthPath = String.valueOf(c.get(Calendar.MONTH) + 1);
        String datePath = String.valueOf(c.get(Calendar.DATE));
        path = this.remoteImageRootPath + pathDate + "/" + monthPath + "/" + datePath + "/";
        try {
            this.sftp.cd(path);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                try {
                    createDir(path, sftp);
                } catch (Exception e1) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                throw e;
            }
        }
        File file = new File(this.localImageRootPath + uploadFile);
        FileInputStream in = new FileInputStream(file);
        this.sftp.put(in, uploadFile);
        if (null != in)
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        return path;
    }

    public String getRemoteImageRootPath() {
        return remoteImageRootPath;
    }

    public void setRemoteImageRootPath(String remoteImageRootPath) {
        this.remoteImageRootPath = remoteImageRootPath;
    }


    /**
     * <p>Description: 删除远程文件</p>
     *
     * @param bakFile 删除远程文件的文件名
     * @throws SftpException
     * @author yuanlz
     * @date 2016年12月21日 下午10:02:22
     */
    public void deleteRemote(String deletePath) throws SftpException, SftpException, IOException {
        try {
            String pathArry[] = deletePath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (int i = 0; i < pathArry.length - 1; i++) {
                if (pathArry[i].equals("")) {
                    continue;
                }
                filePath.append(pathArry[i] + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }
            this.sftp.rm(deletePath);
        } catch (SftpException e) {
            throw e;
        }
    }

    public String getLocalImageRootPath() {
        return localImageRootPath;
    }

    public void setLocalImageRootPath(String localImageRootPath) {
        this.localImageRootPath = localImageRootPath;
    }

    /**
     * 判断目录是否存在
     */
    public boolean isDirExist(String directory) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    /**
     * 创建一个文件目录
     *
     * @throws Exception
     */
    public void createDir(String createpath, ChannelSftp sftp) throws Exception {
        try {
            if (isDirExist(createpath)) {
                this.sftp.cd(createpath);
            }
            String pathArry[] = createpath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }
            this.sftp.cd(createpath);
        } catch (SftpException e) {
            throw new Exception("创建路径错误：" + createpath);
        }
    }
	
	
	
}