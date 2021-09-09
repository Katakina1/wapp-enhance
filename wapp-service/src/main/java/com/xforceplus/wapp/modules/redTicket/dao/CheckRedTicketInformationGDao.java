package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CheckRedTicketInformationGDao {
    /**
     * 取消退货状态
     */
    Integer cancelReturnGoodsStatus(@Param("map") Map<String, Object> map);
    /**
     * 取消协议状态
     */
    Integer cancelAgreementStatus(@Param("map")Map<String, Object> map);
    /**
     * 红票匹配状态作废
     */
    Integer redRushInformationObsolete(@Param("map")Map<String, Object> map);
    /**
     * 清空发票明细红冲数据
     */
    Integer clearTicketInformationData(@Param("map")Map<String, Object> map);
    /**
     * 发票中间表查询
     */
    List<RedTicketMatchMiddle> queryRedTicketMatchMiddle(@Param("map")Map<String, Object> map);
    /**
     * 发票可红冲金额回冲
     */
    Integer invoiceRedRushAmountBackflush(@Param("map")RedTicketMatchMiddle map);

    /**
     *
     * 获取红票匹配表信息
     * @param params
     * @return
     */
    List<RedTicketMatch> getRedTicketMatchList(@Param("map") Map<String, Object> params);
    /**
     *
     * 获取红票匹配表信息条数
     * @param params
     * @return
     */
    Integer getRedTicketMatchListCount(@Param("map") Map<String, Object> params);
    /**
     * 协议红票明细查询协议信息
     */
    List<ProtocolEntity>  protocolList(@Param("map") Map<String, Object> params);
    Integer protocolListCouont(@Param("map") Map<String, Object> params);

    /**
     * 协议红票明细查询蓝票信息
     */
    List<RedTicketMatchMiddle> invoiceList(@Param("map") Map<String, Object> params);
    Integer invoiceListCount(@Param("map") Map<String, Object> params);
    /**
     * 协议红票明细查询发票明细
     */
    List<InvoiceDetail> invoiceDetailList(@Param("map") Map<String, Object> params);
    Integer invoiceDetailListCount(@Param("map") Map<String, Object> params);

    /**
     * 协议红票明细查询红冲明细
     */
    List<RedTicketMatchDetail> redTicketMatchDetailList(@Param("map") Map<String, Object> params);
    Integer redTicketMatchDetailListCount(@Param("map") Map<String, Object> params);
}
