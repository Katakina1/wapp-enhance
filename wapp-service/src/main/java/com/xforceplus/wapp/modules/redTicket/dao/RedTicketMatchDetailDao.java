package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RedTicketMatchDetailDao {
    /**
     *添加红冲明细
     */
    Integer insertRedRushDetails(@Param("map")InvoiceDetail params, @Param("redTicketNumber") String redTicketNumber);
    /**
     *查询红冲明细
     */
    Integer getRedRushDetails(Map<String, Object> map);
}
