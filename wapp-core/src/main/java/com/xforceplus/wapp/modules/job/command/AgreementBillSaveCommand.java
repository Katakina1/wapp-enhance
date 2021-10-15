package com.xforceplus.wapp.modules.job.command;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.enums.BillJobAcquisitionObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.listener.OriginAgreementBillDataListener;
import com.xforceplus.wapp.modules.job.service.OriginAgreementBillService;
import com.xforceplus.wapp.modules.job.service.impl.BillJobServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfOriginAgreementItemDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.util.LocalFileSystemManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.xforceplus.wapp.enums.BillJobAcquisitionObjectEnum.*;

/**
 * @program: wapp-generator
 * @description: 原始协议单入库步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 13:54
 **/
@Slf4j
public class AgreementBillSaveCommand implements Command {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;
    @Autowired
    private BillJobServiceImpl billJobServiceImpl;
    @Autowired
    private OriginAgreementBillService originAgreementBillService;
    @Autowired
    private TXfOriginAgreementItemDao tXfOriginAgreementItemDao;
    @Value("agreementBill.remote.path")
    private String remotePath;
    @Value("agreementBill.local.path")
    private String localPath;
    @Value("agreementBill.sheetName")
    private String sheetName;
    @Value("agreementBill.item.hyperSheetName")
    private String hyperItemSheetName;
    @Value("agreementBill.item.samsSheetName")
    private String samsItemSheetName;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            if (!isLocalFileExists(localPath, fileName)) {
                log.info("未找到本地文件，需重新下载，当前任务={}, 目录={}", fileName, localPath);
                downloadFile(remotePath, fileName, localPath);
            }
            try {
                process(localPath, fileName, context);
                context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.SAVE_COMPLETE.getJobStatus());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                saveContext(context);
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
        Object jobAcquisitionObject = context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT);
        if (Objects.isNull(jobAcquisitionObject)) {
            jobAcquisitionObject = BILL_OBJECT;
            context.put(TXfBillJobEntity.JOB_ACQUISITION_OBJECT, BILL_OBJECT);
            context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, 0);
        }
        BillJobAcquisitionObjectEnum jao = Optional
                .ofNullable(fromCode(Integer.parseInt(String.valueOf(jobAcquisitionObject))))
                .orElse(BILL_OBJECT);
        switch (jao) {
            case BILL_OBJECT:
                // 如果是主信息
                processBillObject(localPath, fileName, context);
                processBillItemObject(localPath, fileName, context);
                break;
            case BILL_ITEM:
                // 如果是Hyper明细信息
                processBillItemObject(localPath, fileName, context);
                processBillItemSamsObject(localPath, fileName, context);
                break;
            case BILL_ITEM_SAMS:
                // 如果是Sams明细信息
                processBillItemSamsObject(localPath, fileName, context);
                break;
            default:
                log.warn("未知的被处理对象, 当前任务={}, 清空当前值jobAcquisitionObject={}, 等待下次重新执行", fileName, jao);
                context.put(TXfBillJobEntity.JOB_ACQUISITION_OBJECT, null);
                context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, null);
        }
    }

    /**
     * 处理单据主信息
     *
     * @param localPath
     * @param fileName
     * @param context
     */
    private void processBillObject(String localPath, String fileName, Context context) {
        int cursor = Optional
                .ofNullable(context.get(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS))
                .map(v -> Integer.parseInt(String.valueOf(v)))
                .orElse(1);
        int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
        File file = new File(localPath, fileName);
        OriginAgreementBillDataListener readListener = new OriginAgreementBillDataListener(jobId, cursor, originAgreementBillService);
        EasyExcel.read(file, readListener)
                .sheet(sheetName)
                .headRowNumber(cursor)
                .doRead();
        context.put(TXfBillJobEntity.JOB_ACQUISITION_OBJECT, BILL_ITEM);
        context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, readListener.getCursor());
    }

    /**
     * 处理Hyper明细信息
     *
     * @param localPath
     * @param fileName
     * @param context
     */
    private void processBillItemObject(String localPath, String fileName, Context context) {
        int cursor = Optional
                .ofNullable(context.get(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS))
                .map(v -> Integer.parseInt(String.valueOf(v)))
                .orElse(1);

        context.put(TXfBillJobEntity.JOB_ACQUISITION_OBJECT, BILL_ITEM_SAMS);
        context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, 0);
    }

    /**
     * 处理Sames明细信息
     *
     * @param localPath
     * @param fileName
     * @param context
     */
    private void processBillItemSamsObject(String localPath, String fileName, Context context) {
        int cursor = Optional
                .ofNullable(context.get(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS))
                .map(v -> Integer.parseInt(String.valueOf(v)))
                .orElse(1);

        context.put(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS, 0);
    }

    /**
     * 保存context瞬时状态入库
     *
     * @param context
     * @return
     */
    private int saveContext(Context context) {
        TXfBillJobEntity tXfBillJobEntity = BeanUtils.mapToBean(context, TXfBillJobEntity.class);
        return billJobServiceImpl.updateById(tXfBillJobEntity);
    }

}
