package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.DictdetaDao;
import com.xforceplus.wapp.modules.base.entity.DictdetaEntity;
import com.xforceplus.wapp.modules.base.service.DictdetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/18.
 */
@Service("dictdetaService")
public class DictdetaServiceImpl implements DictdetaService {

    @Autowired
    DictdetaDao dictdetaDao;

    @Override
    public void save(String schemaLabel, DictdetaEntity entity) {
        dictdetaDao.save(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, DictdetaEntity entity) {
        dictdetaDao.update(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long dictid) {
        dictdetaDao.delete(schemaLabel, dictid);
    }

    @Override
    public DictdetaEntity queryObject(String schemaLabel, Long dictid) {
        return dictdetaDao.queryObject(schemaLabel, dictid);
    }

    @Override
    public List<DictdetaEntity> queryList(String schemaLabel, DictdetaEntity entity) {
        return dictdetaDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, DictdetaEntity entity) { return dictdetaDao.queryTotal(schemaLabel, entity); }
}
