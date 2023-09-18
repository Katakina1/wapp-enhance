package com.xforceplus.wapp.modules.syslog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.syslog.callable.SendSysLogCallable;
import com.xforceplus.wapp.repository.dao.TXfSysLogConfigDao;
import com.xforceplus.wapp.repository.dao.TXfSysLogDao;
import com.xforceplus.wapp.repository.entity.TXfSysLogConfigEntity;
import com.xforceplus.wapp.repository.entity.TXfSysLogEntity;
import com.xforceplus.wapp.threadpool.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class SysLogBatchService extends ServiceImpl<TXfSysLogDao, TXfSysLogEntity>
        implements ApplicationListener<SpringApplicationEvent> {

    @Autowired
    private TXfSysLogConfigDao tXfSysLogConfigDao;
    @Autowired
    private TXfSysLogDao tXfSysLogDao;

    @Override
    public void onApplicationEvent(SpringApplicationEvent event) {
        System.out.println("#####:"+event.getClass().getName());
        if(event instanceof ApplicationReadyEvent) {
            //启动报送线程
            ThreadPoolManager.submitCustomL1(new SendSysLogCallable());
        }
    }

    /**
     * 清除已过期数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearExpire(){
        //1.获取超期天数
        Integer expireDayNum = 3;//默认3天
        QueryWrapper<TXfSysLogConfigEntity> configEntityQ = new QueryWrapper<>();
        configEntityQ.in(TXfSysLogConfigEntity.CONFIG_KEY,"expire_day_num");
        TXfSysLogConfigEntity expireConfig = tXfSysLogConfigDao.selectOne(configEntityQ);
        if (expireConfig != null){
            expireDayNum = Integer.valueOf(expireConfig.getConfigValue());
        }
        log.info("系统日志超期清除--超期天数：{}",expireDayNum);
        //2.清除已超期日志数据
        QueryWrapper<TXfSysLogEntity> logEntityD = new QueryWrapper<>();
        logEntityD.lt(TXfSysLogEntity.CREATE_TIME,DateUtils.addDate(new Date(),-expireDayNum));
        tXfSysLogDao.delete(logEntityD);
    }
}
