package com.xforceplus.wapp.modules.job.timer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.repository.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;


/**
 * @author Xforce
 */
@Component
@Slf4j
public class DelOriginDataTimer {
    @Autowired
    private LockClient lockClient;
    @Autowired
    private TXfOriginClaimBillDao originClaimBillDao;
    @Autowired
    private TXfOriginClaimItemHyperDao originClaimItemHyperDao;
    @Autowired
    private TXfOriginClaimItemSamsDao originClaimItemSamsDao;
    @Autowired
    private TXfOriginSapFbl5nDao originSapFbl5nDao;
    @Autowired
    private TXfOriginSapZarrDao originSapZarrDao;
    @Autowired
    private TXfOriginAgreementMergeDao originAgreementMergeDao;
    @Autowired
    private TXfOriginEpdBillDao originEpdBillDao;
    @Autowired
    private TXfOriginEpdLogItemDao originEpdLogItemDao;
    @Value("${del-origin-data-timer.beforeDays:15}")
    private Integer beforeDays;
    @Value("${del-origin-data-timer.open:false}")
    private Boolean open;

//    @Async("taskThreadPoolExecutor")
//    @Scheduled(cron = "${del-origin-data-timer.handle-cron:0 0 12 * * ?}")
    public void handle() {
//        if (!open) {
//            return;
//        }
//        lockClient.tryLock("delOriginDataTimer", () -> {
//            log.info("开始删除Excel原始数据");
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, -beforeDays);
//
//            QueryWrapper queryWrapper = new QueryWrapper();
//            queryWrapper.le("create_time", calendar.getTime());
//
//            originClaimBillDao.delete(queryWrapper);
//            originClaimItemHyperDao.delete(queryWrapper);
//            originClaimItemSamsDao.delete(queryWrapper);
//
//            originSapFbl5nDao.delete(queryWrapper);
//            originSapZarrDao.delete(queryWrapper);
//            originAgreementMergeDao.delete(queryWrapper);
//
//            originEpdBillDao.delete(queryWrapper);
//            originEpdLogItemDao.delete(queryWrapper);
//
//            log.info("结束删除Excel原始数据");
//        }, -1, 1);
    }
}

