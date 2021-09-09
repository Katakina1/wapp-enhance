package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/10/22 18:26
 */
@Mapper
public interface UploadRedNoticeDao {


    List<RedTicketMatch> queryOpenRedTicket(@Param("map") Map<String, Object> map);

    Integer getRedTicketMatchListCount(@Param("map") Map<String, Object> params);


    List<FileEntity> queryRedNoticelist(@Param("map")Map<String,Object> para);

    void updateStatusDelectNotice(@Param("map")Map<String,Object> para);

    void updateMatchStatusDelectNotice(@Param("map")Map<String,Object> para);

    void updateProcolStatusDelectNotice(@Param("map")Map<String,Object> para);
}
