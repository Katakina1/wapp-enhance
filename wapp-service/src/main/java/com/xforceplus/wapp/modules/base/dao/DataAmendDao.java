package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.DataAmendEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataAmendDao {
    /**
     * 根据userId查用户
     *
     * @param
     * @return
     */
    List<DataAmendEntity> queryList(@Param("map") Map<String, Object> map);

    Integer queryListCount(@Param("map") Map<String, Object> map);

    //Boolean update(@Param("entity") Map<String, Object> map);
    Integer updates(DataAmendEntity dataAmendEntity);
    //int update(@Param("schemaLabel") String schemaLabel, @Param("entity") DataAmendEntity query);
}
