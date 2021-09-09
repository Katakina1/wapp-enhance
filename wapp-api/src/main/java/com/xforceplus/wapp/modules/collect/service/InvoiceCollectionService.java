package com.xforceplus.wapp.modules.collect.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;

import java.util.List;
import java.util.Map;

/**
 * 发票采集列表业务层接口
 * @author Colin.hu
 * @date 4/11/2018
 */
public interface InvoiceCollectionService {

    /**
     * 获取采集发票列表页面数据
     * @param map 查询条件(gfName-购方名称,createStartDate-采集开始时间,createEndDate-束时间)
     * @return 采集发票列表集
     */
    PagedQueryResult<CollectListStatistic> selectInvoiceCollection(Map<String, Object> map);

    /**
     * 获取当前登录用户的购方税号和名称
     * @param userId 用户id
     * @return map key-购方税号 value-购方名称
     */
    List<Map<String, String>> getGfNameByUserId(String schemaLabel, Long userId);

    /**
     * 根据税号查询发票信息
     * @param map 税号map
     * @return 发票信息集
     */
    PagedQueryResult<InvoiceCollectionInfo> getInvoiceInfo(Map<String, Object> map);
}
