package com.xforceplus.wapp.modules.job.command;

import com.alibaba.excel.EasyExcel;
import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.listener.OriginAgreementBillDataListener;
import com.xforceplus.wapp.modules.job.service.OriginAgreementBillService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.util.LocalFileSystemManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.xforceplus.wapp.enums.BillJobAcquisitionObjectEnum.BILL;

/**
 * @program: wapp-generator
 * @description: 原始协议单入库步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 13:54
 **/
@Slf4j
@Component
public class AgreementBillSaveCommand implements Command {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;
    @Autowired
    private OriginAgreementBillService service;
    @Value("${agreementBill.remote.path}")
    private String remotePath;
    @Value("${agreementBill.local.path}")
    private String localPath;
    @Value("${agreementBill.sheetName}")
    private String sheetName;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus) && isValidJobAcquisitionObject(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT))) {
            if (!isLocalFileExists(localPath, fileName)) {
                log.info("未找到本地文件，需重新下载，当前任务={}, 目录={}", fileName, localPath);
                downloadFile(remotePath, fileName, localPath);
            }
            try {
                process(localPath, fileName, context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.info("跳过文件入库步骤, 当前任务={}, 状态={}", fileName, jobStatus);
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
        return Objects.equals(BillJobStatusEnum.DOWNLOAD_COMPLETE.getJobStatus(), jobStatus);
    }

    /**
     * 是否是当前步骤的处理对象
     *
     * @param jobAcquisitionObject
     * @return
     */
    private boolean isValidJobAcquisitionObject(Object jobAcquisitionObject) {
        return Objects.isNull(jobAcquisitionObject) || Objects.equals(BILL.getCode(), jobAcquisitionObject);
    }

    /**
     * 本地文件是否存在
     *
     * @param localPath
     * @param fileName
     * @return
     */
    private boolean isLocalFileExists(String localPath, String fileName) {
        return LocalFileSystemManager.isFileExists(localPath, fileName);
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
     * 处理文件入库的过程
     *
     * @param localPath
     * @param fileName
     * @param context
     */
    private void process(String localPath, String fileName, Context context) {
        // 获取当前进度
        if (Objects.isNull(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT))) {
            context.put(TXfBillJobEntity.JOB_ACQUISITION_OBJECT, BILL.getCode());
            context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, 1);
        }
        int cursor = Optional
                .ofNullable(context.get(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS))
                .map(v -> Integer.parseInt(String.valueOf(v)))
                .orElse(1);
        int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
        File file = new File(localPath, fileName);
        OriginAgreementBillDataListener readListener = new OriginAgreementBillDataListener(jobId, cursor, service);
        try {
            EasyExcel.read(file, readListener)
                    .sheet(sheetName)
                    .headRowNumber(cursor)
                    .doRead();
            // 正常处理结束，清空游标
            context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, readListener.getCursor());
            context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.SAVE_COMPLETE.getJobStatus());
            // deleteFile(localPath, fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 处理出现异常，记录游标
            context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, readListener.getCursor());
            context.put(TXfBillJobEntity.REMARK, e.getMessage());
        }
    }
}
