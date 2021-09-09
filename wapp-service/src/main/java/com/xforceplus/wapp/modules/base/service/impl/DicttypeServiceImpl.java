package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.DicttypeDao;
import com.xforceplus.wapp.modules.base.entity.DicttypeEntity;
import com.xforceplus.wapp.modules.base.service.DicttypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/18.
 */
@Service("ddicttypeService")
public class DicttypeServiceImpl implements DicttypeService {

    @Autowired
    DicttypeDao dicttypeDao;

    @Override
    public void save(String schemaLabel, DicttypeEntity entity) {
        dicttypeDao.save(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, DicttypeEntity entity) {
        dicttypeDao.update(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long dicttypeid) {
        dicttypeDao.delete(schemaLabel, dicttypeid);
    }

    @Override
    public DicttypeEntity queryObject(String schemaLabel, Long dicttypeid) {
        return dicttypeDao.queryObject(schemaLabel, dicttypeid);
    }

    @Override
    public List<DicttypeEntity> queryList(String schemaLabel, DicttypeEntity entity) {
        return dicttypeDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, DicttypeEntity entity) { return dicttypeDao.queryTotal(schemaLabel, entity); }
}
