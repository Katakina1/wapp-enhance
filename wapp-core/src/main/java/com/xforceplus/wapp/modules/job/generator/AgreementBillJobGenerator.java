package com.xforceplus.wapp.modules.job.generator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.xforceplus.wapp.enums.BillJobTypeEnum.AGREEMENT_BILL_JOB;

/**
 * @program: wapp-enhance
 * @description: 创建协议单任务
 * @author: Kenny Wong
 * @create: 2021-10-12 17:27
 **/
@Slf4j
@Component
public class AgreementBillJobGenerator extends AbstractBillJobGenerator {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;

    @Autowired
    private TXfBillJobDao tXfBillJobDao;

    @Value("agreementBill.remote.path")
    private String remotePath;

    @Value("agreementBill.remote.fileNameKeyWords")
    private String fileNameKeyWords;

    // TODO 添加定时任务
    // TODO 添加异步处理
    @Override
    public void generate() {
        List<String> fileNames = scanFiles(remotePath, fileNameKeyWords);
        createJob(AGREEMENT_BILL_JOB.getJobType(), fileNames);
    }

    @Override
    public List<String> scanFiles(String remotePath, String fileNameKeyWords) {
        try {
            sftpRemoteManager.openChannel();
            return sftpRemoteManager.getFileNames(remotePath, fileNameKeyWords);
        } catch (JSchException | SftpException e) {
            log.error("获取远程协议单文件列表故障", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void createJob(int jobType, List<String> fileNames) {
        log.info("待创建的协议单任务数={}", fileNames.size());
        fileNames.forEach(
                fileName -> {
                    Date now = new Date();
                    TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
                    tXfBillJobEntity.setJobName(fileName);
                    tXfBillJobEntity.setJobType(jobType);
                    tXfBillJobEntity.setCreateTime(now);
                    tXfBillJobEntity.setUpdateTime(now);
                    // 如果数据不存在则插入
                    if (tXfBillJobDao.selectCount(new QueryWrapper<TXfBillJobEntity>().lambda().eq(TXfBillJobEntity::getJobName, fileName)) == 0) {
                        log.info("创建新协议单任务={}", fileName);
                        tXfBillJobDao.insert(tXfBillJobEntity);
                    } else {
                        log.info("跳过已存在的协议单任务={}", fileName);
                    }
                }
        );
    }
}
