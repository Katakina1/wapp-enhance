package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.modules.job.service.ScanMatchService;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
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
@Component("hostTask")
public class HostTask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MatchService matchService;


//    /**
//     *  导入ｈｏｓｔ状态为1的订单数据
//     */
//    @Scheduled(cron = "${task.host.po1}")
//    @Async
//    public void  connHostPo1(){
//        logger.info("connHostPo1");
//
//        matchService.connHostPo1();
//    }
//
//
//    /**
//     *  导入ｈｏｓｔ状态为２的订单数据
//     */
//    @Scheduled(cron = "${task.host.po2}")
//    @Async
//    public void  connHostPo2(){
//        logger.info("connHostPo2");
//        matchService.connHostPo2();
//    }
//
//    /**
//     * 导入ｈｏｓｔ状态为４的订单数据
//     */
//    @Scheduled(cron = "${task.host.po4}")
//    @Async
//    public void  connHostPo4(){
//        logger.info("connHostPo4");
//
//        matchService.connHostPo4();
//    }
//
//
//    /**
//     *  导入ｈｏｓｔ状态为２的claim
//     */
//    @Scheduled(cron = "${task.host.claim2}")
//    @Async
//    public void  connHostClaimType2(){
//        logger.info("connHostClaimType2");
//
//        matchService.connHostClaimType2();
//    }
//
//    /**
//     *  导入ｈｏｓｔ状态为３的索赔数据
//     */
//    @Scheduled(cron = "${task.host.claim3}")
//    @Async
//    public void  connHostClaimType3(){
//        logger.info("connHostClaimType3");
//
//        matchService.connHostClaimType3();
//    }
//
//    /**
//     * update invoice hoststatus
//     */
//    @Scheduled(cron = "${task.host.invoice}")
//    @Async
//    public void connHostAgain(){
//        logger.info("connHostAgain");
//        matchService.connHostAgain();
//    }
//
//    /**
//     * update invoice hoststatus
//     */
//    @Scheduled(cron = "${task.host.claimdetail}")
//    @Async
//    public void getHostClaimDetail(){
//        logger.info("getHostClaimDetail");
//        matchService.getHostClaimDetail();
//    }
//    /**
//     * update invoice hoststatus
//     */
//    @Scheduled(cron = "${task.host.writescreen}")
//    @Async
//    public void writeScreen(){
//        logger.info("writeScreen");
//        matchService.runWritrScreen();
//    }

}
