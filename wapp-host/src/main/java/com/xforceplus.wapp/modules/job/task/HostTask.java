package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.modules.job.service.HostCountService;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${task.host.po.date}")
    private String podate;
    
    @Value("${task.host.claim.date}")
    private String claimdate;
    
    @Value("${task.host.invoice.date}")
    private String invoicedate;
    
    @Value("${task.host.claimdetail.date}")
    private String claimdetaildate;
    @Autowired
    private HostCountService hostCountService;


    //day

    /**
     *  导入ｈｏｓｔ状态为1的订单数据
     */
    @Async
    @Scheduled(cron = "${task.host.po1}")
    public void  connHostPo1(){
        logger.info("connHostPo1");
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(podate));
        currentTime = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = formatter.format(currentTime);
        String date1=date2+" 00:00:00";

        matchService.connHostPo1(date1,0);
    }




    /**
     *  导入ｈｏｓｔ状态为２的claim
     */
    @Scheduled(cron = "${task.host.claim2}")
    @Async
    public void  connHostClaimType2(){
        logger.info("connHostClaimType2");
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(claimdate));
        currentTime = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = formatter.format(currentTime);
        String date1=date2+" 00:00:00";
        matchService.connHostClaimType2(date1,0);
    }

    /**
     	*  导入ｈｏｓｔ状态为３的索赔数据
     */
//    @Async
//    @Scheduled(cron = "${task.host.claim3}")
    public void  connHostClaimType3(){
        logger.info("connHostClaimType3");
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(claimdate));
        currentTime = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = formatter.format(currentTime);
        String date1=date2+" 00:00:00";

        matchService.connHostClaimType3(date1,0);
    }

    /**
     * update invoice hoststatus
     */
    @Scheduled(cron = "${task.host.invoice}")
    @Async
    public void connHostAgain(){
        logger.info("connHostAgain");
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(invoicedate));
        currentTime = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = formatter.format(currentTime);
        String date1=date2+" 00:00:00";
        matchService.connHostAgain(date1);
    }

    /**
     * update invoice hoststatus
     */
    @Scheduled(cron = "${task.host.claimdetail}")
    @Async
    public void getHostClaimDetail(){
        logger.info("getHostClaimDetail");
        matchService.getHostClaimDetail(Integer.valueOf(claimdetaildate));
    }

//
//    //week
//
//    /**
//     *  导入ｈｏｓｔ状态为1的订单数据
//     */
//    @Scheduled(cron = "${task.host.week.po1}")
//    @Async
//    public void  connWeekHostPo1(){
//        logger.info("connHostPo1");
//
//        matchService.connHostPo1(-7);
//    }
//
//    /**
//     *  导入ｈｏｓｔ状态为２的claim
//     */
//    @Scheduled(cron = "${task.host.week.claim2}")
//    @Async
//    public void  connWeekHostClaimType2(){
//        logger.info("connHostClaimType2");
//
//        matchService.connHostClaimType2(-7);
//    }
//
//    /**
//     *  导入ｈｏｓｔ状态为３的索赔数据
//     */
//    @Scheduled(cron = "${task.host.week.claim3}")
//    @Async
//    public void  connWeekHostClaimType3(){
//        logger.info("connHostClaimType3");
//
//        matchService.connHostClaimType3(-7);
//    }
//
//    /**
//     * update invoice hoststatus
//     */
//    @Scheduled(cron = "${task.host.week.invoice}")
//    @Async
//    public void connWeekHostAgain(){
//        logger.info("connHostAgain");
//        matchService.connHostAgain(-7);
//    }
//
//
    //writeScreen

    /**
     * writeScreen
     */
    @Scheduled(cron = "${task.host.writescreen}")
    @Async
    public void writeScreen(){
        logger.info("writeScreen");
        Long[] ids={};
        matchService.runWritrScreen(ids);
    }

    @Scheduled(cron = "${task.host.writescreenAm}")
    @Async
    public void writeScreenAM(){
        logger.info("writeScreen");
        Long[] ids={};
        matchService.runWritrScreen1(ids);
    }

//    @Scheduled(cron = "${host.count}")
//    @Async
    public void hostCount(){
        logger.info("hostCount job start !");
        hostCountService.hostCount();
    }
    


}
