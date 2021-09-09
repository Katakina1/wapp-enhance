package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
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
public interface PaymentInvoiceQueryDao extends BaseDao<PaymentInvoiceUploadEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryList(@Param("map") Map<String, Object> map);
    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("map") Map<String, Object> map);

    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryListAll(@Param("map") Map<String, Object> map);

}
