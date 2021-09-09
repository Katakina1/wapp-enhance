package com.xforceplus.wapp.modules.collect.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;

import java.util.Map;

/**
 * 异常发票采集业务层接口
 * @author Colin.hu
 * @date 4/11/2018
 */
public interface AbnormalInvoiceCollectionService {

    /**
     * 获取异常发票采集页面数据
     * @param map 查询条件 (gfName-购方名称,createStartDate-采集开始时间,createEndDate-采集结束时间)
     * @return 异常发票采集数据集
     */
    PagedQueryResult<CollectListStatistic> selectAbnormalInvoiceCollection(Map<String, Object> map);
}
