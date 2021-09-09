package com.xforceplus.wapp.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xforceplus.wapp.modules.sys.entity.SysUserTokenEntity;

/**
 * 系统用户Token
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:22:07
 */
@Mapper
public interface SysUserTokenDao extends SysBaseDao<SysUserTokenEntity> {

    SysUserTokenEntity queryByUserId(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    SysUserTokenEntity queryByToken(@Param("schemaLabel") String schemaLabel, @Param("token") String token);

    void saveToken(@Param("schemaLabel") String schemaLabel, @Param("token") SysUserTokenEntity token);

    void updateToken(@Param("schemaLabel") String schemaLabel, @Param("token") SysUserTokenEntity token);

}
