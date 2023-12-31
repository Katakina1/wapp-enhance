package com.xforceplus.wapp.modules.job.generator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.component.SFTPRemoteManager;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.xforceplus.wapp.enums.BillJobStatusEnum.INIT;
import static com.xforceplus.wapp.enums.BillJobTypeEnum.CLAIM_BILL_JOB;

/**
 * @program: wapp-enhance
 * @description: 创建索赔单任务
 * @author: Kenny Wong
 * @create: 2021-10-12 17:34
 **/
@Slf4j
@Component
public class ClaimBillJobGenerator extends AbstractBillJobGenerator {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;

    @Autowired
    private BillJobService billJobService;

    @Autowired
    private LockClient lockClient;

    @Value("${claimBill.remote.path}")
    private String remotePath;

    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${claimBill.scan-cron}")
    @Override
    public void generate() {
        try {
            log.info("启动原始索赔单任务生成器");
            lockClient.tryLock("claimBillJobGenerator", () -> {
                log.info("启动原始索赔单任务生成器成功");
                List<String> fileNames = scanFiles(remotePath);
                createJob(CLAIM_BILL_JOB.getJobType(), fileNames);
            }, -1, 1);
        }finally {
            this.sftpRemoteManager.closeChannel();
        }
    }

    @Override
    public List<String> scanFiles(String remotePath) {
        try {
            sftpRemoteManager.openChannel();
            return sftpRemoteManager.listFiles(remotePath);
        } catch (JSchException | SftpException e) {
            log.error("获取远程索赔单文件列表故障", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void createJob(int jobType, List<String> fileNames) {
        log.info("待创建的索赔单任务数={}", fileNames.size());
        fileNames.forEach(
                fileName -> {
                    Date now = new Date();
                    TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
                    tXfBillJobEntity.setJobName(fileName);
                    tXfBillJobEntity.setJobType(jobType);
                    tXfBillJobEntity.setJobStatus(INIT.getJobStatus());
                    tXfBillJobEntity.setCreateTime(now);
                    tXfBillJobEntity.setUpdateTime(now);
                    // 如果数据不存在则插入
                    if (billJobService.count(new QueryWrapper<TXfBillJobEntity>().lambda().eq(TXfBillJobEntity::getJobName, fileName)) == 0) {
                        log.info("创建新索赔单任务={}", fileName);
                        billJobService.save(tXfBillJobEntity);
                    } else {
                        log.info("跳过已存在的索赔单任务={}", fileName);
                    }
                }
        );
    }
}

