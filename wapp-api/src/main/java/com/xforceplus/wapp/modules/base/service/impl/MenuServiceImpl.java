package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.MenuDao;
import com.xforceplus.wapp.modules.base.entity.MenuEntity;
import com.xforceplus.wapp.modules.base.service.MenuService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Daily.zhang on 2018/04/13.
 */
@Service("menuService")
public class MenuServiceImpl implements MenuService {

    private final MenuDao menuDao;

    @Autowired
    public MenuServiceImpl(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    @Override
    public List<MenuEntity> queryListParentId(String schemaLabel, Long parentId) {
        return menuDao.queryListParentId(schemaLabel,parentId);
    }

    @Override
    public List<MenuEntity> getUserMenuList(String schemaLabel, Long userId, Integer type) {
        List<MenuEntity> list = menuDao.getUserMenuList(schemaLabel, userId);
        if (type == 0) {
            return list;
        }

        MenuEntity rootEntity = null;
        //根节点
        for (MenuEntity menuEntity : list) {
            if (menuEntity.getParentid() == 0) {
                rootEntity = menuEntity;
            }
        }
        //添加一级菜单
        final List<MenuEntity> nodeList = Lists.newArrayList();
        nodeList.add(rootEntity);
        for (MenuEntity menuEntity : list) {
            if (Objects.equals(menuEntity.getParentid(), rootEntity.getMenuid())) {
                //添加二级菜单
                menuEntity.setSubList(menuDao.getUserMenuListOfParentId(schemaLabel, userId, menuEntity.getMenuid().longValue()));
                nodeList.add(menuEntity);
            }
        }

        return nodeList;
    }

    @Override
    public List<MenuEntity> getUserMenuList(String schemaLabel, Long userId) {
        return getUserMenuList(schemaLabel, userId, 0);
    }

    @Override
    public MenuEntity queryObject(String schemaLabel, Long menuId) {
        return menuDao.queryObject(schemaLabel, menuId);
    }

    @Override
    public List<MenuEntity> queryList(String schemaLabel, MenuEntity entity) {
        return menuDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, MenuEntity entity) {
        return menuDao.queryTotal(schemaLabel, entity);
    }

    @Override
    public void save(String schemaLabel, MenuEntity entity) {

        menuDao.save(schemaLabel, entity);

        //当前菜单级别
        final Integer level=entity.getMenulevel();

        //如果为1级菜单，就不更新
        if (level>1){
            // 将上级菜单的是否有下级 (- 无 1 -有0 改为1，有下级
            menuDao.updateBottom(schemaLabel, entity.getParentid());
        }

    }

    @Override
    public void update(String schemaLabel, MenuEntity entity) {
        menuDao.update(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long menuId) {
        menuDao.delete(schemaLabel, menuId);
    }

    @Override
    public int deleteBatch(String schemaLabel, Long[] menuIds) {
        return menuDao.deleteBatch(schemaLabel, menuIds);
    }
}
