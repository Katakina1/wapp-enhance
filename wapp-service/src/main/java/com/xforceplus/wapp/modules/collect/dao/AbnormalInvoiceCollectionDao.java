package com.xforceplus.wapp.modules.collect.dao;

import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 异常发票采集控制层
 * @author Colin.hu
 * @date 4/11/2018
 */
@Mapper
public interface AbnormalInvoiceCollectionDao {

    /**
     * 获得异常发票采集数据的总行数
     * @param map 查询条件(gfName-购方名称,createStartDate-采集开始时间,createEndDate-采集结束时间)
     * @return 总数
     */
    Integer getAbnormalInvoiceCollectionCount(Map<String, Object> map);

    /**
     * 获得异常发票采集数据集合
     * @param map 查询条件(gfName-购方名称,createStartDate-采集开始时间,createEndDate-采集结束时间)
     * @return 获得异常发票采集数据集合
     */
    List<CollectListStatistic> selectAbnormalInvoiceCollection(Map<String, Object> map);

    /**
     * 获取所有金额之和，所有税额之和
     * @param map 参数
     * @return 金额之和，税额之和
     */
    Map<String, BigDecimal> getAbnormalSumAmount(Map<String, Object> map);
}
