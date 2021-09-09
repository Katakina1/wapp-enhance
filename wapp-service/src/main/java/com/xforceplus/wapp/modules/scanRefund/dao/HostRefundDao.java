package com.xforceplus.wapp.modules.scanRefund.dao;

import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface HostRefundDao extends BaseDao<GroupRefundEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<GroupRefundEntity> queryList(@Param("map") Map<String, Object> map);
    List<GroupRefundEntity> queryRzhList(@Param("map") Map<String, Object> map);

    /**
     * 整组退
     * @param
     * @return
     */

    int inputrefundyesno(@Param("uuid") String uuid,@Param("refundReason") String refundReason);

    /**
     * 查询uuid
     * @param id
     * @return
     */
    List<GroupRefundEntity> queryuuid(@Param("id") Long id);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("map") Map<String, Object> map);
    ReportStatisticsEntity queryRzhTotalResult(@Param("map") Map<String, Object> map);

    GroupRefundEntity queryisdel(@Param("uuid") String uuid);

    Integer getuuidCount(@Param("uuid") String uuid);

    int saveInvoice(@Param("entity")GenerateBindNumberEntity entity);


    GenerateBindNumberEntity queryListUuid(@Param("uuid") String uuid);


}
