package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.DicttypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/18.
 */
@Mapper
public interface DicttypeDao{
    /**
     * 根据字典id查询字典信息
     */
    DicttypeEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("dicttypeid") Long id);

    /**
     * 根据条件查询字典信息
     */
    List<DicttypeEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") DicttypeEntity query);

    /**
     * 查询信息条数
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") DicttypeEntity query);

    /**
     * 保存字典信息
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") DicttypeEntity entity);

    /**
     * 更新字典信息
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") DicttypeEntity entity);

    /**
     * 删除字典信息
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("dicttypeid") Long id);
}
