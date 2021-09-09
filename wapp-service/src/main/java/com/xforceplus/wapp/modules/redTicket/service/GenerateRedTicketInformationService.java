package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import com.xforceplus.wapp.modules.redTicket.entity.GenerateRedRush;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;

import java.util.List;
import java.util.Map;

public interface GenerateRedTicketInformationService {
    /**
     * 查询单位代码
     */
    OrganizationEntity queryGfCode(String gfName);
    /**
     * 查询可用的发票信息
     * @param map
     * @return
     */
    List<InvoiceEntity> getInvoicelist(Map<String, Object> map);
    /**
     * 查询可用的发票信息条数
     */
    Integer invoiceCount(Map<String, Object> map);
    /**
     * 生成红票数据
     */
    String generateRedTicketData(GenerateRedRush generateRedRush,Integer userId,String userName,String userCode);
    /**
     * 获取退货信息
     * @param map 参数
     * @return
     */
    List<ReturngoodsEntity> getReturnGoodsList(Map<String, Object> map);
    /**
     * 获取退货信息条数
     * @param map 参数
     * @return
     */
    Integer getReturnGoodsCount(Map<String, Object> map);
}
