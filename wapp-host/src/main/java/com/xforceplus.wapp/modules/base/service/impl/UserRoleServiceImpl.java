package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.UserRoleDao;
import com.xforceplus.wapp.modules.base.entity.UserRoleEntity;
import com.xforceplus.wapp.modules.base.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
@Service("userRoleService")
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleDao userRoleDao;

    @Override
    public List<UserRoleEntity> queryList(String schemaLabel, UserRoleEntity entity) {
        return userRoleDao.queryList(schemaLabel, entity);
    }

    @Override
    public void save(String schemaLabel, UserRoleEntity entity) {
        if (entity.getIds() == null || entity.getIds().length < 1) {
            if (entity.getRoleid() != null) {
                Long[] roleIds = {Long.valueOf(entity.getRoleid())};
                entity.setIds(roleIds);
            } else {
                return;
            }
        }
        userRoleDao.save(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, UserRoleEntity entity) {
        userRoleDao.delete(schemaLabel, entity);
    }

    @Override
    @Transactional
    public void saveOrUpdate(String schemaLabel, UserRoleEntity entity) {

        userRoleDao.delete(schemaLabel, entity);
        userRoleDao.save(schemaLabel, entity);
    }

    @Override
    public List<Long> queryRoleIdList(String schemaLabel, Long userId) {
        return userRoleDao.queryRoleIdList(schemaLabel, userId);
    }

    @Override
    public int queryTotal(String schemaLabel, UserRoleEntity entity) {
        return userRoleDao.queryTotal(schemaLabel, entity);
    }

    @Override
    public void saveRoleUser(String schemaLabel, UserRoleEntity entity) {

        userRoleDao.saveRoleUser(schemaLabel, entity);
    }
}
