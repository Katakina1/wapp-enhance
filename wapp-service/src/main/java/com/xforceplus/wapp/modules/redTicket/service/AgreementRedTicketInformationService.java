package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AgreementRedTicketInformationService {

    /**
     * 查询可用的发票信息(不分页)
     * @param params
     * @return
     */
    Integer getInvoiceCount(Map<String, Object> params);

    List<InvoiceEntity> getInvoicelist(Map<String, Object> map);

    /**
     * 查询协议信息
     * @param params
     * @return
     */
    List<ProtocolEntity> protocollist(Map<String, Object> params);
    /**
     * 查询协议信息条数
     * @param params
     * @return
     */
    Integer protocolCount(Map<String, Object> params);

    /**
     * 查询协议明细信息
     */
    List<ProtocolDetailEntity> protocoldetaillist(Map<String, Object> params);
    String selectPurchaseInvoiceNo(String PurchaseInvoiceNo);
    List<ProtocolInvoiceDetailEntity> queryInvoiceDetailList(String caseDate,String protocolNo);
    List<RoleEntity> selectRoleCode(long userId);
}
