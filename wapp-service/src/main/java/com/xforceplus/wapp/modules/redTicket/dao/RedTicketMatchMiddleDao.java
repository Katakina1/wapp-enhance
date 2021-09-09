package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

@Mapper
public interface RedTicketMatchMiddleDao {

    /**
     * 添加发票匹配中间信息
     */
    Integer insertRedTicketMatchMiddle(@Param("map")InvoiceEntity map,@Param("redTicketMatchingId")Long redTicketMatchingId,@Param("redRushAmount")BigDecimal redRushAmount);
}
