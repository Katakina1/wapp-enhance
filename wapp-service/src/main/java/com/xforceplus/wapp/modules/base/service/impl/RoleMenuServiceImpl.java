package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.RoleMenuDao;
import com.xforceplus.wapp.modules.base.entity.RoleMenuEntity;
import com.xforceplus.wapp.modules.base.service.RoleMenuService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/16.
 */
@Service("roleMenuServiceImpl")
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    RoleMenuDao roleMenuDao;

    @Override
    public List<RoleMenuEntity> queryList(String schemaLabel, RoleMenuEntity entity) {
        return roleMenuDao.queryList(schemaLabel, entity);
    }

    @Override
    public void save(String schemaLabel, RoleMenuEntity entity) {
        if (entity.getMenuIdList() == null) {
            if (entity.getMenuid() != null) {
                List<Long> menuList = Lists.newArrayList();
                menuList.add(Long.valueOf(entity.getMenuid()));
                entity.setMenuIdList(menuList);
            } else {
                return;
            }
        }
        roleMenuDao.save(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long roleid) {
        roleMenuDao.delete(schemaLabel, roleid);
    }

    @Override
    @Transactional
    public void saveOrUpdate(String schemaLabel, RoleMenuEntity entity) {
        //要绑定的菜单id
        final List<Long> menuIdList = entity.getMenuIdList();

        roleMenuDao.delete(schemaLabel, Long.valueOf(entity.getRoleid()));

        if (null != menuIdList && !menuIdList.isEmpty()){
            roleMenuDao.save(schemaLabel, entity);
        }
    }

    @Override
    public List<Long> queryMenuIdList(String schemaLabel, Long roleId) {
        return roleMenuDao.queryMenuIdList(schemaLabel, roleId);
    }

    @Override
    public int queryTotal(String schemaLabel, RoleMenuEntity entity) {

        return roleMenuDao.queryTotal(schemaLabel, entity);
    }
}
