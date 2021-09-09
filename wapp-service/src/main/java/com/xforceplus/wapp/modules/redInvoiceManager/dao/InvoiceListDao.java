package com.xforceplus.wapp.modules.redInvoiceManager.dao;

import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Mapper
public interface InvoiceListDao extends BaseDao<InvoiceListEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    List<UploadScarletLetterEntity> queryListAll(@Param("map") Map<String, Object> map);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);


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
    List<InvoiceListEntity> getRedInvoiceDetailList(@Param("invoiceCode") String invoiceCode,@Param("invoiceCode") String invoiceNo);


}
