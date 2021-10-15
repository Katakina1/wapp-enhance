package com.xforceplus.wapp.modules.job.command;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Objects;

/**
 * @program: wapp-generator
 * @description: 原始索赔单下载步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 13:45
 **/
@Slf4j
public class ClaimBillDownloadCommand implements Command {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;
    @Autowired
    private BillJobService billJobService;
    @Value("claimBill.remote.path")
    private String remotePath;
    @Value("claimBill.local.path")
    private String localPath;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            downloadFile(remotePath, fileName, localPath);
            context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DOWNLOAD_COMPLETE.getJobStatus());
            saveContext(context);
        } else {
            log.info("跳过文件下载步骤，当前任务={}, 状态={}", fileName, jobStatus);
        }
        return false;
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
        sftpRemoteManager.openChannel();
        sftpRemoteManager.downloadFile(remotePath, fileName, localPath);
    }

    /**
     * 保存context瞬时状态入库
     *
     * @param context
     * @return
     */
    private int saveContext(Context context) {
        TXfBillJobEntity tXfBillJobEntity = BeanUtils.mapToBean(context, TXfBillJobEntity.class);
        return billJobService.updateById(tXfBillJobEntity);
    }
}
