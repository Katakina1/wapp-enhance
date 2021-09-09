package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 9:32
 */
@Mapper
public interface PrintCoverDao {


    Integer selectRedTicketListCount(@Param("map") Map<String,Object> map);

    List<RedTicketMatch> selectRedTicketList(@Param("map")Map<String,Object> map);

    RedTicketMatch getRedTicketMatch(@Param("id")long id);

    UserEntity getUserName(@Param("userCode")String userCode);
}
