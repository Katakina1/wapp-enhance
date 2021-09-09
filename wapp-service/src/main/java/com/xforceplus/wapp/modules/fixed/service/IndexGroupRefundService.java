package com.xforceplus.wapp.modules.fixed.service;



import com.xforceplus.wapp.modules.fixed.entity.IndexGroupRefundEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface IndexGroupRefundService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<IndexGroupRefundEntity> queryList(Map<String, Object> map);

    /**
     *
     * 查询抵账表明细的信息
     * @param params
     * @return
     */
    List<IndexGroupRefundEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查抵账明细的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询po表明细的信息
     * @param params
     * @return
     */
    List<IndexGroupRefundEntity> getPOList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查po明细的条数
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
     * 整组退
     *
     * @param refundNotes
     */
//    void  inputrefundnotes(String schemaLabel,List<GroupRefundEntity> uuids,String refundNotes,String rebateNo);
//    void  inputrefundnotes(String schemaLabel,String[] uuids,String refundNotes,String rebateNo);
    void  inputrefundnotes(String schemaLabel, String uuid, String refundNotes, String rebateNo);

    //查询最大退单号
    IndexGroupRefundEntity querymaxrebateno();

    //查询UUID
    List<IndexGroupRefundEntity> queryuuid(Long id);


    /**
     * 整组退修改底账表
     *
     * @param id
     */
    void  updaterecordinvoice(String schemaLabel, Long id);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<IndexGroupRefundEntity> queryListAll(String schemaLabel, Map<String, Object> map);

    void  inputrefundyesno(String uuid,String refundReason);
    IndexGroupRefundEntity queryReason(Long id);

}
