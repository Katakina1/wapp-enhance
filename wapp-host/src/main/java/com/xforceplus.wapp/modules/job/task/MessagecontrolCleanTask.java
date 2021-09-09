package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.modules.ExcelClean.service.ExportExcleCleanService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



/**
 *
 * 定时查询host数据库信息，更新本地数据库
 * @raymond
 * @date 2018年11月15日 下午15:57:22
 */
@PropertySource(value = {"classpath:config.properties"})
@Component("messagecontrolCleanTask")
public class MessagecontrolCleanTask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ExportExcleCleanService exportExcleCleanService;


    /**
     *  清除messagecontrol表一个月以前的数据的数据
     */
    @Scheduled(cron = "${task.export.clean.messagecontrol}")
    @Async
    public void  messagecontrolClean(){
        logger.info("messagecontrolClean开始执行");

        exportExcleCleanService.messagecontrolClean();
        logger.info("messagecontrolClean结束执行");
    }




}
