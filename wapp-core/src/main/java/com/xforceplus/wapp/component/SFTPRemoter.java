package com.xforceplus.wapp.component;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static com.xforceplus.wapp.util.LocalFileSystemManager.createFolderIfNonExist;

/**
 * sftp链接工具类
 *
 * @author yuanlz
 */
@Slf4j
@Component
public class SFTPRemoter {
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
     * 密钥文件路径
     */
    @Value("${pro.sftp.default.privateKey}")
    private String privateKey;

    /**
     * 密钥口令
     */
    @Value("${pro.sftp.default.privateKey}")
    private String passphrase;

    @Value("${pro.sftp.host}")
    private String host;

    @Value("${pro.sftp.default.port}")
    private int port;

    @Value("${pro.sftp.username}")
    private String userName;

    @Value("${pro.sftp.default.timeout}")
    private int timeout;

    /**
     * 判断SFTP channel是否正常连接
     *
     * @return
     */
    public boolean isChannelConnected() {
        return (Objects.nonNull(session) && session.isConnected())
                && (Objects.nonNull(channel) && channel.isConnected())
                && (Objects.nonNull(sftp) && sftp.isConnected());
    }

    /**
     * 以密钥链接方式打开SFTP通道
     */
    public void openChannel() throws JSchException {
        if (isChannelConnected()) {
            return;
        }
        closeChannel();
        // 初始化端口
        if (0 > port) {
            port = 22;
        }

        // 创建JSch对象
        JSch jsch = new JSch();

        if (StringUtils.isNotBlank(privateKey)) {
            //使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
            if ("".equals(passphrase)) {
                jsch.addIdentity(privateKey, passphrase);
            } else {
                jsch.addIdentity(privateKey);
            }
        } else {
            throw new JSchException(String.format("密钥错误privateKey=%s,SFTP连接初始化失败", privateKey));
        }

        // 通过 用户名，主机地址，端口 获取一个Session对象
        session = jsch.getSession(userName, host, port);

        // 为Session对象设置properties
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.setConfig(config);

        // 设置超时时间
        session.setTimeout(timeout);
        // 建立链接
        session.connect();
        // 打开SFTP通道
        channel = session.openChannel("sftp");
        // 建立SFTP通道的连接
        channel.connect();
        sftp = (ChannelSftp) channel;
    }

    /**
     * 关闭SFTP通道
     */
    public void closeChannel() {
        Optional.ofNullable(channel).ifPresent(Channel::disconnect);
        Optional.ofNullable(session).ifPresent(Session::disconnect);
        Optional.ofNullable(sftp).ifPresent(ChannelSftp::disconnect);
    }

    /**
     * 返回SFTP指定目录下所有文件名
     *
     * @param path             文件路径
     * @param fileNameKeyWords 文件名关键字
     * @return
     * @throws JSchException
     * @throws SftpException
     */
    public List<String> getFileNames(String path, String fileNameKeyWords) throws SftpException, JSchException {
        if (!isChannelConnected()) {
            throw new JSchException("SFTP连接故障，请重新连接");
        }
        List<String> fileNames = new ArrayList<>();
        // 进入目录
        try {
            sftp.cd(path);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                //TODO 进入根目录了还未进入税号文件夹+年份文件夹
                sftp.mkdir(path);
                sftp.cd(path);
            } else {
                throw e;
            }
        }

        // 根据名称关键字查询所有文件名
        Vector<LsEntry> vector = sftp.ls(fileNameKeyWords);
        for (LsEntry lsEntry : vector) {
            fileNames.add(lsEntry.getFilename());
        }

        return fileNames;
    }

    /**
     * 下载文件
     *
     * @param path     远程文件路径
     * @param fileName 文件名
     * @throws SftpException
     */
    public void downloadFile(String path, String fileName, String localPath) throws SftpException, IOException {
        createFolderIfNonExist(localPath);
        // 进入并设置为当前目录
        sftp.cd(path);
        // 下载
        File file = new File(localPath, fileName);
        FileOutputStream out = new FileOutputStream(file);
        sftp.get(path, out);
        out.close();
    }
}
