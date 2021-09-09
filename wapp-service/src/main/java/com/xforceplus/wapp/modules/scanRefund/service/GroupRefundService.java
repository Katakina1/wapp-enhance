package com.xforceplus.wapp.modules.scanRefund.service;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface GroupRefundService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<GroupRefundEntity> queryList( Map<String, Object> map);

    /**
     *
     * 查询抵账表明细的信息
     * @param params
     * @return
     */
    List<GroupRefundEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);

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
    List<GroupRefundEntity> getPOList(@Param("map") Map<String, Object> params);

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
    List<GroupRefundEntity> getClaimList(@Param("map") Map<String, Object> params);

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
    void  inputrefundnotes(String schemaLabel,String uuid,String refundNotes,String rebateNo);
    void  inputrefundyesno(String uuid,String refundReason);

    //查询最大退单号
    GroupRefundEntity querymaxrebateno();

    //查询UUID
    List<GroupRefundEntity> queryuuid(Long id);
//    GroupRefundEntity queryuuid(String schemaLabel,Long id);
    GroupRefundEntity queryReason(Long id);

    /**
     * 整组退修改底账表
     *
     * @param id
     */
    void  updaterecordinvoice(String schemaLabel,Long id);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(Map<String, Object> map);

    GroupRefundEntity queryisdel(String uuid);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<GroupRefundEntity> queryListAll(String schemaLabel, Map<String, Object> map);



}
