package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.modules.job.service.ScanMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 *
 * 定时查询匹配信息，查询未扫描匹配和扫描匹配失败
 * 四个匹配数据类型    po  费用   外部红票    内部红票
 * @author fth
 * @date 2018年10月30日 下午15:57:22
 */
@PropertySource(value = {"classpath:config.properties"})
@Component("matchTask")
public class MatchTask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ScanMatchService scanMatchService;


    /**
     *  内部红票 定时任务查询未扫描匹配的数据
     */
    public void  innerRedInvoice(){
         //查询出未扫描匹配和扫描匹配失败的发票
         scanMatchService.innerRedInvoice();
    }


    /**
     *  外部红票 定时任务查询未扫描匹配的数据
     */
    public void  outerRedInvoice(){
        //查询出未扫描匹配和扫描匹配失败的发票
        scanMatchService.outerRedInvoice();
    }

    /**
     *  PO索赔与发票  查询未扫描匹配的数据
     */
    public void  posuopeiInvoice(){
        //查询出未扫描匹配和扫描匹配失败的发票
        scanMatchService.posuopeiInvoice();
    }


    /**
     *  费用匹配  查询未扫描匹配的数据
     */
    public void  costInvoice(){
        //查询出未扫描匹配和扫描匹配失败的发票
        scanMatchService.costInvoice();
    }


}
