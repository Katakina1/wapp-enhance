package com.xforceplus.wapp.modules.job.command;

import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
        String jobStatus = String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS));
        if (Objects.equals(String.valueOf(BillJobStatusEnum.INIT.getJobStatus()), jobStatus)) {
            sftpRemoteManager.openChannel();
            sftpRemoteManager.downloadFile(remotePath, fileName, localPath);
            context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DOWNLOAD_COMPLETE.getJobStatus());
            saveContext(context);
        } else {
            log.info("跳过文件下载步骤，当前任务={}, 状态={}", fileName, jobStatus);
        }
        return false;
    }

    private int saveContext(Context context) {
        Integer id = Integer.valueOf(String.valueOf(context.get(TXfBillJobEntity.ID)));
        int status = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        return billJobService.updateStatus(id, status);
    }
}
