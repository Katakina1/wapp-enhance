package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.RoleDao;
import com.xforceplus.wapp.modules.base.entity.RoleEntity;
import com.xforceplus.wapp.modules.base.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/13.
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public RoleEntity queryObject(String schemaLabel, Long roleId) {
        return roleDao.queryObject(schemaLabel, roleId);
    }

    @Override
    public List<RoleEntity> queryList(String schemaLabel, RoleEntity entity) {
        return roleDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, RoleEntity entity) {
        return roleDao.queryTotal(schemaLabel, entity);
    }

    @Override
    public int roleTotal(String schemaLabel, Long[] orgIds) {
        return roleDao.roleTotal(schemaLabel, orgIds);
    }

    @Override
    public void save(String schemaLabel, RoleEntity entity) {
        roleDao.save(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, RoleEntity entity) {
        roleDao.update(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long roleid) {
        roleDao.delete(schemaLabel, roleid);
    }

    @Override
    public int deleteBatch(String schemaLabel, Long[] roleids) {
        return roleDao.deleteBatch(schemaLabel, roleids);
    }
}
