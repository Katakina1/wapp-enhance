package com.xforceplus.wapp.modules.scanRefund.dao;

import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.CostGroupRefundEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface CostGroupRefundDao extends BaseDao<CostGroupRefundEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<CostGroupRefundEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     *
     * 查询抵账表明细的信息
     * @param params
     * @return
     */
    List<CostGroupRefundEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查明细的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询税率表明细的信息
     * @param params
     * @return
     */
    List<CostGroupRefundEntity> getRateList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查税率明细的条数
     * @param params
     * @return
     */
    Integer getRateListCount(@Param("map") Map<String, Object> params);

    CostGroupRefundEntity getRateListTotal(@Param("map") Map<String, Object> params);
    /**
     *
     * 查询Cost表明细的信息
     * @param params
     * @return
     */
    List<CostGroupRefundEntity> getCostList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查Cost明细的条数
     * @param params
     * @return
     */
    Integer getCostListCount(@Param("map") Map<String, Object> params);


    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<CostGroupRefundEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 整组退
     * @param refundNotes
     * @return
     */
    int inputrefundnotes(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid, @Param("refundNotes") String refundNotes, @Param("rebateNo") String rebateNo);
    int inputrefundyesno(@Param("uuid") String uuid,@Param("refundReason")String refundReason);
    //查询最大退单号
    CostGroupRefundEntity querymaxrebateno();

    /**
     * 查询uuid
     * @param id
     * @return
     */
    List<CostGroupRefundEntity> queryuuid(@Param("id") Long id);
    //    GroupRefundEntity queryuuid(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);
    CostGroupRefundEntity queryReason(@Param("id") Long id);


    /**
     * 整组退修改底账表
     * @param id
     * @return
     */
    int updaterecordinvoice(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);


    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);


}
