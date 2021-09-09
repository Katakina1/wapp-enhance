package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/12 11:53
 */
@Mapper
public interface UploadOpenRedTicketDataDao {
    Integer getRedTicketMatchListCount(@Param("map") Map<String,Object> map);

    List<RedTicketMatch> queryOpenRedTicket(@Param("map")Map<String,Object> map);

    List<FileEntity> queryRedDatalist(@Param("map")Map<String,Object> para);

    int deleteRedData(@Param("map")Map<String,Object> para);
}
