package com.xforceplus.wapp.modules.redInvoiceManager.service;



import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface InvoiceListService {

    /**
     * 分页列表
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryList(String schemaLabel, Map<String, Object> map);
    List<UploadScarletLetterEntity> queryListAll( Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     *
     * 查询红票明细信息
     * @param params
     * @return
     */
    List<InvoiceListEntity> getRedInvoiceList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询红票明细条数
     * @param params
     * @return
     */
    Integer getRedInvoiceCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询详细红票明细信息
     * @param invoiceCode
     * @return
     */
    List<InvoiceListEntity> getRedInvoiceDetailList(String invoiceCode,String invoiceNo);




}
