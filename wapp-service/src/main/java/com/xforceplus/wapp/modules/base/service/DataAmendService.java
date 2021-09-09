package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.MatchEntity;
import com.xforceplus.wapp.modules.base.entity.DataAmendEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Daily.zhang on 2018/04/17.
 *
 * BaseUserService
 */

public interface DataAmendService{

    /**
     * 查询用户列表
     */
    List<DataAmendEntity> queryList(Map<String, Object> map);


    Integer queryListCount(Map<String, Object> map);
    /**
     * 修改用户
     */
    Integer update(DataAmendEntity dataAmendEntity);
    //int update(@Param("entity")DataAmendEntity  entity,@Param("user")UserEntity user)
   // Boolean update(String schemaLabel, Map<String, Object> params);


}
