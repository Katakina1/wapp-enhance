package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface GenerateRedTicketInformationDao {

    /**
     * 查询单位代码
     */
    OrganizationEntity queryGfCode(@Param("gfName")String gfName);
    /**
     * 查询可用的发票信息
     * @param params
     * @return
     */
    List<InvoiceEntity> getInvoicelist(@Param("map") Map<String, Object> params);

    /**
     * 查询可用的发票信息条数
     */
    Integer invoiceCount(@Param("map") Map<String, Object> map);

    /**
     * 红冲发票
     */
    Integer invoiceRedRush(@Param("uuid") String uuid,@Param("redRushAmountDet")BigDecimal redRushAmountDet);


    List<InvoiceEntity> invoicelist(@Param("map") Map<String, Object> params);

    /**
     * 获取退货信息
     */
    List<ReturngoodsEntity> getReturnGoodsList(@Param("map") Map<String, Object> params);
    /**
     * 获取退货信息条数
     */
    Integer getReturnGoodsCount(@Param("map") Map<String, Object> params);
    /**
     * 查询购方税号
     */
    String getGfTaxNo(String uuid);
    /**
     * 查询公司代码
     * @param orgcode
     * @return
     */
    String getCompanycode(String orgcode);
}
