package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AgreementRedTicketInformationDao {
    /**
     * 查询协议信息
     * @param params
     * @return
     */
    List<ProtocolEntity> protocollist(@Param("map") Map<String, Object> params);
    /**
     * 查询协议信息条数
     * @param params
     * @return
     */
    Integer protocolCount(@Param("map") Map<String, Object> params);
    /**
     * 查询协议明细信息
     */
    List<ProtocolDetailEntity> protocoldetaillist(@Param("map") Map<String, Object> params);

    /**
     * 红冲协议信息
     */
    Integer redRushAgreement(@Param("map")ProtocolEntity map,@Param("userCode")String userCode,@Param("redTicketNumber")String redTicketNumber);

    /**
     * 查询可用的发票信息(不分页)
     * @param params
     * @return
     */
    List<InvoiceEntity> getInvoicelist(@Param("map") Map<String, Object> params);
    /**
     * 查询可红冲协议扣款项目
     * @return
     */
    List<String> getPayItemlist();

    String selectPurchaseInvoiceNo(@Param("PurchaseInvoiceNo")String PurchaseInvoiceNo);
    List<ProtocolInvoiceDetailEntity> queryInvoiceDetailList(@Param("caseDate")String caseDate, @Param("protocolNo") String protocolNo);
    List<RoleEntity> selectRoleCode(@Param("userId") long userId);
}
