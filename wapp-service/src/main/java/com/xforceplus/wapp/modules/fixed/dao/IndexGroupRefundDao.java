package com.xforceplus.wapp.modules.fixed.dao;

import com.xforceplus.wapp.modules.fixed.entity.IndexGroupRefundEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface IndexGroupRefundDao extends BaseDao<IndexGroupRefundEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<IndexGroupRefundEntity> queryList(@Param("map") Map<String, Object> map);

    /**
     *
     * 查询抵账表明细的信息
     * @param params
     * @return
     */
    List<IndexGroupRefundEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查明细的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询PO表明细的信息
     * @param params
     * @return
     */
    List<IndexGroupRefundEntity> getPOList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查PO明细的条数
     * @param params
     * @return
     */
    Integer getPOListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询索赔表明细的信息
     * @param params
     * @return
     */
    List<IndexGroupRefundEntity> getClaimList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查索赔明细的条数
     * @param params
     * @return
     */
    Integer getClaimListCount(@Param("map") Map<String, Object> params);


    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<IndexGroupRefundEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 整组退
     * @param refundNotes
     * @return
     */
    int inputrefundnotes(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid, @Param("refundNotes") String refundNotes, @Param("rebateNo") String rebateNo);
    //查询最大退单号
    IndexGroupRefundEntity querymaxrebateno();

    /**
     * 查询uuid
     * @param id
     * @return
     */
    List<IndexGroupRefundEntity> queryuuid(@Param("id") Long id);

    IndexGroupRefundEntity queryReason(@Param("id") Long id);


    /**
     * 整组退修改底账表
     * @param id
     * @return
     */
    int updaterecordinvoice(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);
    int inputrefundyesno(@Param("uuid") String uuid,@Param("refundReason") String refundReason);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("map") Map<String, Object> map);


}
