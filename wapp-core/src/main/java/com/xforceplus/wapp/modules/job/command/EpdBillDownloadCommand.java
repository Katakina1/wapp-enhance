package com.xforceplus.wapp.modules.job.command;

import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.util.LocalFileSystemManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @program: wapp-generator
 * @description: 原始EPD单下载步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 13:45
 **/
@Slf4j
@Component
public class EpdBillDownloadCommand implements Command {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;
    @Value("${epdBill.remote.path}")
    private String remotePath;
    @Value("${epdBill.local.path}")
    private String localPath;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            try {
                process(remotePath, fileName, localPath, context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                context.put(TXfBillJobEntity.REMARK, e.getMessage());
            }
        } else {
            log.info("跳过文件下载步骤，当前任务={}, 状态={}", fileName, jobStatus);
        }
        return false;
    }


    /**
     * @param remotePath
     * @param fileName
     * @param localPath
     * @param context
     * @throws Exception
     */
    private void process(String remotePath, String fileName, String localPath, Context context) throws Exception {
        downloadFile(remotePath, fileName, localPath);
        context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DOWNLOAD_COMPLETE.getJobStatus());
    }

    /**
     * 是否是当前步骤的前置状态
     *
     * @param jobStatus
     * @return
     */
    private boolean isValidJobStatus(int jobStatus) {
        return Objects.equals(BillJobStatusEnum.INIT.getJobStatus(), jobStatus);
    }

    /**
     * 下载远程文件
     *
     * @param remotePath
     * @param fileName
     * @param localPath
     * @throws IOException
     * @throws SftpException
     */
    private void downloadFile(String remotePath, String fileName, String localPath) throws Exception {
        LocalFileSystemManager.deleteFile(localPath, fileName);
        sftpRemoteManager.openChannel();
        sftpRemoteManager.downloadFile(remotePath, fileName, localPath);
    }

}
