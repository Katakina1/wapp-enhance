package com.xforceplus.wapp.modules.job.command;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.dto.OriginEpdLogItemDto;
import com.xforceplus.wapp.modules.job.listener.OriginEpdLogItemDataListener;
import com.xforceplus.wapp.modules.job.service.OriginEpdLogItemService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginEpdLogItemEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapFbl5nEntity;
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

import static com.xforceplus.wapp.enums.BillJobAcquisitionObjectEnum.BILL;
import static com.xforceplus.wapp.enums.BillJobAcquisitionObjectEnum.ITEM;
import static java.lang.Enum.valueOf;

/**
 * @program: wapp-generator
 * @description: 原始EPD单入库步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 13:54
 **/
@Slf4j
@Component
public class EpdLogItemSaveCommand implements Command {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;
    @Autowired
    private OriginEpdLogItemService service;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;
    @Autowired
    private Validator validator;
    @Value("${epdBill.remote.path}")
    private String remotePath;
    @Value("${epdBill.local.path}")
    private String localPath;
    @Value("${epdBill.item.sheetName}")
    private String sheetName;

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus) && isValidJobAcquisitionObject(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT))) {
            log.info("开始保存原始EPD单LOG明细数据入库fileName={}, sheetName={}", fileName, sheetName);
            if (!isLocalFileExists(localPath, fileName)) {
                log.info("未找到本地文件，需重新下载，当前任务={}, 目录={}", fileName, localPath);
                downloadFile(remotePath, fileName, localPath);
            }
            try {
                //处理某个job的excel前先删除这个job的原始数据（以前可能处理一半需要重新处理excel）
                int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
                deleteOriginEpdItem(jobId);
                process(localPath, fileName, context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                context.put(TXfBillJobEntity.REMARK, e.getMessage());
            }
        } else {
            log.info("跳过原始EPD单LOG明细数据入库步骤, 当前任务={}, 状态={}", fileName, jobStatus);
        }
        return false;
    }

    private void deleteOriginEpdItem(Integer jobId){
        QueryWrapper<TXfOriginEpdLogItemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginEpdLogItemEntity.JOB_ID,jobId);
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
        return Objects.isNull(jobAcquisitionObject) || Objects.equals(ITEM.getCode(), jobAcquisitionObject);
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
            context.put(TXfBillJobEntity.JOB_ACQUISITION_OBJECT, ITEM.getCode());
        }
        int cursor = 1;
        int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
        File file = FileUtils.getFile(localPath, fileName);
        OriginEpdLogItemDataListener readListener = new OriginEpdLogItemDataListener(jobId, cursor, service, validator);
        long start  = System.currentTimeMillis();
        try {
            EasyExcel.read(file, OriginEpdLogItemDto.class, readListener)
                    .sheet(sheetName)
                    .headRowNumber(cursor)
                    .doRead();
            // 正常处理结束，清空游标
            context.put(TXfBillJobEntity.JOB_ACQUISITION_OBJECT, BILL.getCode());
            //更新
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobAcquisitionObject(BILL.getCode());
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 处理出现异常
            context.put(TXfBillJobEntity.REMARK, e.getMessage());
        }
        log.info("EPD单:{} log原始数据入库花费{}ms", jobId, System.currentTimeMillis() - start);
    }
}
