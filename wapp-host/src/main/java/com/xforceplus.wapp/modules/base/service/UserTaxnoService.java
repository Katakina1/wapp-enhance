package com.xforceplus.wapp.modules.base.service;


import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
public interface UserTaxnoService {
    /**
     * 保存
     *
     * @param entity
     */
    void save(String schemaLabel, UserTaxnoEntity entity);

    /**
     * 修改
     */
    void update(String schemaLabel, UserTaxnoEntity entity);

    /**
     * 删除
     */
    void delete(String schemaLabel, Long id);

    /**
     * 删除 - 根据用户
     */
    void deleteByUserId(String schemaLabel, Long userId);

    /**
     * 根据条件查询
     */
    List<UserTaxnoEntity> queryList(String schemaLabel, UserTaxnoEntity entity);

    /**
     * 查询总数
     */
    int queryTotal(String schemaLabel, UserTaxnoEntity entity);
}
