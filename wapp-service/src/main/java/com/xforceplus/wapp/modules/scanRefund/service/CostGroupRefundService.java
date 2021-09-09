package com.xforceplus.wapp.modules.scanRefund.service;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.CostGroupRefundEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface CostGroupRefundService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<CostGroupRefundEntity> queryList(String schemaLabel, Map<String, Object> map);

    /**
     *
     * 查询抵账表明细的信息
     * @param params
     * @return
     */
    List<CostGroupRefundEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查抵账明细的条数
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
     * 整组退
     *
     * @param refundNotes
     */
    void  inputrefundnotes(String schemaLabel, String uuid, String refundNotes, String rebateNo);
    void  inputrefundyesno(String uuid,String refundReason);

    //查询最大退单号
    CostGroupRefundEntity querymaxrebateno();

    //查询UUID
    List<CostGroupRefundEntity> queryuuid( Long id);
    //    GroupRefundEntity queryuuid(String schemaLabel,Long id);
    CostGroupRefundEntity queryReason(Long id);

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
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<CostGroupRefundEntity> queryListAll(String schemaLabel, Map<String, Object> map);



}
