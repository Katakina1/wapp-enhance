package com.xforceplus.wapp.modules.scanRefund.dao;

import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface PrintRefundInformationDao extends BaseDao<EnterPackageNumberEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

   /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);


    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);


    /**
     * 查询退单发票
     * @param id
     * @return
     */
//    List<EnterPackageNumberEntity> queryRefundListAll(@Param("id") Long id);

    EnterPackageNumberEntity queryRefundList(@Param("id") Long id);

    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryListAll(@Param("map") Map<String, Object> map);

    EnterPackageNumberEntity queryPostType(@Param("id") Long id);

	List<EnterPackageNumberEntity> queryCostList(@Param("schemaLabel")String schemaLabel,@Param("map") Map<String, Object> map);

	ReportStatisticsEntity queryTotalCostResult(@Param("schemaLabel")String schemaLabel,@Param("map") Map<String, Object> map);

	EnterPackageNumberEntity queryRefundCostList(@Param("id")Long id);

	List<EnterPackageNumberEntity> queryListCostAll(@Param("map")Map<String, Object> map);
}
