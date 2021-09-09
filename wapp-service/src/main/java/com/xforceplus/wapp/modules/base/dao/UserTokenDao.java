package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.UserTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户Token
 * <p>
 * Created by Daily.zhang on 2018/04/24
 */
@Mapper
public interface UserTokenDao {

    int saveToken(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTokenEntity t);

    int updateToken(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTokenEntity t);

    UserTokenEntity queryByUserId(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    UserTokenEntity queryByToken(@Param("schemaLabel") String schemaLabel, @Param("token") String token);

}
