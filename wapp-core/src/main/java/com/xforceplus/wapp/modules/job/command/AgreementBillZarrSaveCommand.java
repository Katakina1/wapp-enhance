package com.xforceplus.wapp.modules.job.command;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.dto.OriginAgreementBillZarrDto;
import com.xforceplus.wapp.modules.job.listener.OriginAgreementBillZarrDataListener;
import com.xforceplus.wapp.modules.job.service.OriginSapZarrService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapFbl5nEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapZarrEntity;
import com.xforceplus.wapp.util.LocalFileSystemManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.xforceplus.wapp.enums.BillJobAcquisitionObjectEnum.ITEM;

/**
 * @program: wapp-generator
 * @description: 原始协议单SAP-ZARR0355入库步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 13:54
 **/
@Slf4j
@Component
public class AgreementBillZarrSaveCommand implements Command {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;
    @Autowired
    private OriginSapZarrService service;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;
    @Autowired
    private Validator validator;
    @Value("${agreementBill.remote.path}")
    private String remotePath;
    @Value("${agreementBill.local.path}")
    private String localPath;
    @Value("${agreementBill.item.sheetName}")
    private String sheetName;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus) && isValidJobAcquisitionObject(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT))) {
            log.info("开始保存原始协议单SAP-ZARR0355数据入库fileName={}, sheetName={}", fileName, sheetName);
            if (!isLocalFileExists(localPath, fileName)) {
                log.info("未找到本地文件，需重新下载，当前任务={}, 目录={}", fileName, localPath);
                downloadFile(remotePath, fileName, localPath);
            }
            try {
                //处理某个job的excel前先删除这个job的原始数据（以前可能处理一半需要重新处理excel）
                int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
                deleteOriginZarr(jobId);
                process(localPath, fileName, context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.info("跳过原始协议单SAP-ZARR0355数据入库步骤, 当前任务={}, 状态={}", fileName, jobStatus);
        }
        return false;
    }

    private void deleteOriginZarr(Integer jobId) {
        QueryWrapper<TXfOriginSapZarrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapZarrEntity.JOB_ID, jobId);
        service.remove(queryWrapper);
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
        return Objects.equals(ITEM.getCode(), jobAcquisitionObject);
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
        int cursor = 1;
        int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
        File file = FileUtils.getFile(localPath, fileName);
        OriginAgreementBillZarrDataListener readListener = new OriginAgreementBillZarrDataListener(jobId, cursor, service, validator);
        long start  = System.currentTimeMillis();
        try {
            EasyExcel.read(file, OriginAgreementBillZarrDto.class, readListener)
                    .sheet(sheetName)
                    .headRowNumber(cursor)
                    .doRead();
            context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.SAVE_COMPLETE.getJobStatus());
            //更新
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobStatus(BillJobStatusEnum.SAVE_COMPLETE.getJobStatus());
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 处理出现异常
            context.put(TXfBillJobEntity.REMARK, e.getMessage());
        }
        log.info("协议单:{} zarr原始数据入库花费{}ms", jobId, System.currentTimeMillis() - start);
    }
}
