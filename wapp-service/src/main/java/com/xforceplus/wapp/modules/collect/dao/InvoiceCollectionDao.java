package com.xforceplus.wapp.modules.collect.dao;

import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 发票采集列表数据层接口
 * @author Colin.hu
 * @date 4/11/2018
 */
@Mapper
public interface InvoiceCollectionDao {

    /**
     * 获得发票采集列表数据的总行数
     * @param map 查询条件(gfName-购方名称,createStartDate-采集开始时间,createEndDate-采集结束时间)
     * @return 总数
     */
    Integer getInvoiceCollectionCount(Map<String, Object> map);

    /**
     * 获得发票采集列表数据集合
     * @param map 查询条件(gfName-购方名称,createStartDate-采集开始时间,createEndDate-采集结束时间)
     * @return 发票采集列表数据集合
     */
    List<CollectListStatistic> selectInvoiceCollection(Map<String, Object> map);

    /**
     * 获取当前登录用户的购方税号和名称
     * @param userId 用户id
     * @return map key-购方税号 value-购方名称
     */
    List<Map<String, String>> getGfNameByUserId(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 根据税号查询发票信息
     * @param map 税号 是否查询异常
     * @return 发票信息集
     */
    List<InvoiceCollectionInfo> getInvoiceInfo(Map<String, Object> map);

    /**
     * 根据税号查询发票数量
     * @param map 税号 是否查询异常
     * @return 数量
     */
    Integer getInvoiceInfoCount(Map<String, Object> map);

    /**
     * 获取所有金额之和，所有税额之和
     * @param map 参数
     * @return 金额之和，税额之和
     */
    Map<String, BigDecimal> getSumAmount(Map<String, Object> map);
}
