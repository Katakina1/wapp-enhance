package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.UserTaxnoDao;
import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;
import com.xforceplus.wapp.modules.base.service.UserTaxnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
@Service("userTaxnoService")
public class UserTaxnoServiceImpl implements UserTaxnoService {

    @Autowired
    UserTaxnoDao userTaxnoDao;

    @Override
    public void save(String schemaLabel, UserTaxnoEntity entity) {
        userTaxnoDao.save(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, UserTaxnoEntity entity) {
        userTaxnoDao.update(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long id) {
        userTaxnoDao.delete(schemaLabel, id);
    }

    @Override
    public void deleteByUserId(String schemaLabel, Long userId) {
        userTaxnoDao.deleteByUserId(schemaLabel, userId);
    }

    @Override
    public List<UserTaxnoEntity> queryList(String schemaLabel, UserTaxnoEntity entity) {
        return userTaxnoDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, UserTaxnoEntity entity) { return userTaxnoDao.queryTotal(schemaLabel, entity); }
}
