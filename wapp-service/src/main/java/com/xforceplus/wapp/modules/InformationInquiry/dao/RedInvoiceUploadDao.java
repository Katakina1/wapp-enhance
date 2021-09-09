package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadEntity;
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
public interface RedInvoiceUploadDao extends BaseDao<RedInvoiceUploadEntity> {
    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<RedInvoiceUploadEntity> queryList(@Param("map") Map<String, Object> map);
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
    List<RedInvoiceUploadEntity> queryListAll(@Param("map") Map<String, Object> map);

    int saveInvoice(@Param("list") List<RedInvoiceUploadEntity> entity);
    int selectCount(@Param("entity") RedInvoiceUploadEntity redInvoiceUploadEntity);
    int updateRedEntity(@Param("entity") RedInvoiceUploadEntity redInvoiceUploadEntity);
}
