package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.DicttypeEntity;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/18.
 */
public interface DicttypeService {

    /**
     * 保存
     * @param entity
     */
    void save(String schemaLabel, DicttypeEntity entity);

    /**
     * 修改
     */
    void update(String schemaLabel, DicttypeEntity entity);

    /**
     * 删除
     */
    void delete(String schemaLabel, Long dicttypeid);

    /**
     * 根据id查询
     */
    DicttypeEntity queryObject(String schemaLabel, Long dicttypeid);

    /**
     * 根据条件查询
     */
    List<DicttypeEntity> queryList(String schemaLabel, DicttypeEntity entity);

    /**
     * 查询总数
     */
    int queryTotal(String schemaLabel, DicttypeEntity entity);
}
