package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.DictdetaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/18.
 */
@Mapper
public interface DictdetaDao{
    /**
     * 根据id查询字典明细
     * @param id
     * @return
     */
    DictdetaEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("dictid") Long id);

    /**
     * 根据条件查询字典明细
     * @param query
     * @return
     */
    List<DictdetaEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") DictdetaEntity query);

    /**
     * 查询总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") DictdetaEntity query);

    /**
     * 保存字典明细信息
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") DictdetaEntity entity);

    /**
     * 根据id删除字典明细信息
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("dictid") Long dictid);

    /**
     * 更新字典明细信息
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") DictdetaEntity entity);
}
