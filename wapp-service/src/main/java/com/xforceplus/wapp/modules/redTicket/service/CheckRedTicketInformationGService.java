package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.modules.redTicket.entity.*;

import java.util.List;
import java.util.Map;

public interface CheckRedTicketInformationGService {

    /**
     * 获取查询开红票分页数据对象
     * @param map 参数
     * @return 分页对象
     */
    List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map);
    /**
     * 获取查询开红票分页数据记录数
     * @param params 参数
     * @return
     */
    Integer getRedTicketMatchListCount(Map<String, Object> params);

    /**
     * 取消生成红票资料
     * @param map
     * @return
     */
    String cancelRedRushInformation(Map<String, Object> map);

    /**
     * 协议红票明细查询协议信息
     */
    List<ProtocolEntity>  protocolList(Map<String, Object> params);
    Integer protocolListCouont(Map<String, Object> params);
    /**
     * 协议红票明细查询蓝票信息
     */
    List<RedTicketMatchMiddle> invoiceList(Map<String, Object> params);
    Integer invoiceListCount(Map<String, Object> params);
    /**
     * 协议红票明细查询发票明细
     */
    List<InvoiceDetail> invoiceDetailList(Map<String, Object> params);
    Integer invoiceDetailListCount(Map<String, Object> params);

    /**
     * 协议红票明细查询红冲明细
     */
    List<RedTicketMatchDetail> redTicketMatchDetailList(Map<String, Object> params);
    Integer redTicketMatchDetailListCount(Map<String, Object> params);
}
