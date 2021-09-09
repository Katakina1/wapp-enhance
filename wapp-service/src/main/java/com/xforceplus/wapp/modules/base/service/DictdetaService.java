package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.DictdetaEntity;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/18.
 */
public interface DictdetaService {

    /**
     * 保存
     * @param entity
     */
    void save(String schemaLabel, DictdetaEntity entity);

    /**
     * 修改
     */
    void update(String schemaLabel, DictdetaEntity entity);

    /**
     * 删除
     */
    void delete(String schemaLabel, Long dictid);

    /**
     * 根据id查询
     */
    DictdetaEntity queryObject(String schemaLabel, Long dictid);

    /**
     * 根据条件查询
     */
    List<DictdetaEntity> queryList(String schemaLabel, DictdetaEntity entity);

    /**
     * 查询总数
     */
    int queryTotal(String schemaLabel, DictdetaEntity entity);
}
