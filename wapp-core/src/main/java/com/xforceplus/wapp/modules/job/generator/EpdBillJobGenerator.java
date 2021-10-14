package com.xforceplus.wapp.modules.job.generator;

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
import java.util.List;

import static com.xforceplus.wapp.component.BillJobTypeEnum.AGREEMENT_BILL_JOB;

/**
 * @program: wapp-enhance
 * @description: 创建EPD单任务
 * @author: Kenny Wong
 * @create: 2021-10-12 17:35
 **/
@Slf4j
@Component
public class EpdBillJobGenerator extends AbstractBillJobGenerator {

    @Autowired
    private SFTPRemoteManager sftpRemoteManager;

    @Autowired
    private TXfBillJobDao tXfBillJobDao;

    @Value("path.agreementBill.remote")
    private String remotePath;

    @Override
    public void generate() {
        List<String> fileNames = scanFiles(remotePath);
        createJob(AGREEMENT_BILL_JOB.getJobType(), fileNames);
    }

    @Override
    public List<String> scanFiles(String remotePath) {
        try {
            sftpRemoteManager.openChannel();
            return sftpRemoteManager.getFileNames(remotePath, "");
        } catch (JSchException | SftpException e) {
            log.error("获取远程协议单文件列表故障", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void createJob(int jobType, List<String> fileNames) {
        TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
        // fileNames.forEach(
        // fileName -> tXfBillJobDao.insert(tXfBillJobEntity)
        // );
    }
}
