package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface RedTicketMatchDao {
    /**
     * 添加红票匹配信息
     */
    Integer insertRedTicketMatch(@Param("map")RedTicketMatch redTicketMatch);

    /**
     * 查询最大红票序列号
     */
    RedTicketMatch querymaxredTicketNumber();
    /**
     * 查询红票匹配信息
     */
    List<RedTicketMatch> queryredTicketMatch(String redTicketNumber);

}
