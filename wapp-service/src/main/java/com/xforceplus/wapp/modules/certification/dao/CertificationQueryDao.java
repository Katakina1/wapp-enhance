package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 查询认证数据层接口
 * @author Colin.hu
 * @date 4/13/2018
 */
@Mapper
public interface CertificationQueryDao {

    /**
     * 获取认证总数
     * @param map 查询参数
     * @return 总数
     */
    ReportStatisticsEntity getCertificationListCount(Map<String, Object> map);

    /**
     * 获取认证发票信息集
     * @param map 查询参数
     * @return 认证发票信息集
     */
    List<InvoiceCollectionInfo> selectCertificationList(Map<String, Object> map);
    
    
    List<InvoiceCollectionInfo> selectCertificationListExport(Map<String, Object> map);
    //获取taxCode
    OptionEntity queryHostTaxRate(@Param("value") String value);
    //获取费用TaxCode
    List<OptionEntity> getCostTaxCode(@Param("uuid")String uuid);
    //获取费用业务类型
    String getCostServiceType(@Param("value")String costRate);
    String selectOrgType(@Param("taxNo")String taxNo,@Param("orgName")String orgname);
    List<String> getTaxRateDetail(@Param("invoiceNo")String invoiceNo,@Param("invoiceCode")String invoiceCode);
}
