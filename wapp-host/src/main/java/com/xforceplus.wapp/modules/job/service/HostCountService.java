package com.xforceplus.wapp.modules.job.service;

/**
 * @author raymond.yan
 */
public interface HostCountService {
    /**
     * 用于记录host数据库固定时段内订单和索赔数量增加情况
     * @return
     */
    Integer hostCount();
}
