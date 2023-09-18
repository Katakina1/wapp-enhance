package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.repository.entity.PoEntity;
import com.xforceplus.wapp.repository.entity.TXfExchangeTicketEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;


/**
* <p>
*  Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2022-06-10
*/
public interface TXfExchangeTicketDao extends BaseMapper<TXfExchangeTicketEntity> {

    public Page<TXfExchangeTicketEntity> queryList(Page<TXfExchangeTicketEntity> page, @Param("entity") TXfExchangeTicketEntity dto);

    Integer cancelPo(@Param("id") Integer id, @Param("changeAmount") BigDecimal changeAmount, @Param("matchStatus") String matchStatus);
    Integer cancelInvoice(@Param("matchno") String matchno);

    Integer cancelMatch(@Param("matchno") String matchno);
    Integer cancelClaim(@Param("matchno") String matchno);


    Integer deletePo(@Param("id") Integer id, @Param("changeAmount")BigDecimal changeAmount,@Param("matchStatus") String matchStatus);

    List<PoEntity> getPoJiLu(@Param("matchno") String matchno);

    void updateRefund(@Param("invoiceNo") String invoiceNo, @Param("invoiceCode") String invoiceCode);
}
