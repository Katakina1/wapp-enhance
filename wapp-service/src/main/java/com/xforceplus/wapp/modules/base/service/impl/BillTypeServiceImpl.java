package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.BillTypeDao;
import com.xforceplus.wapp.modules.base.entity.BillTypeEntity;
import com.xforceplus.wapp.modules.base.service.BillTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jingsong.mao on 2018/08/10.
 */
@Service("billTypeService")
public class BillTypeServiceImpl implements BillTypeService {

    @Autowired
    private BillTypeDao billtypeDao;

    @Override
    public BillTypeEntity queryObject(String schemaLabel, Long roleId) {
        return billtypeDao.queryObject(schemaLabel, roleId);
    }

    @Override
    public List<BillTypeEntity> queryList(String schemaLabel, BillTypeEntity entity) {
        return billtypeDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, BillTypeEntity entity) {
        return billtypeDao.queryTotal(schemaLabel, entity);
    }

    @Override
    public int roleTotal(String schemaLabel, Long[] orgIds) {
        return billtypeDao.roleTotal(schemaLabel, orgIds);
    }

    @Override
    public void save(String schemaLabel, BillTypeEntity entity) {
        billtypeDao.save(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, BillTypeEntity entity) {
        billtypeDao.update(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long roleid) {
        billtypeDao.delete(schemaLabel, roleid);
    }

    @Override
    public int deleteBatch(String schemaLabel, Long[] roleids) {
        return billtypeDao.deleteBatch(schemaLabel, roleids);
    }
    
    @Override
    public int queryByNameAndCode(String schemaLabel, String name, String code,Integer billTypeId){
    	return billtypeDao.queryByNameAndCode(schemaLabel, name, code,billTypeId);
    }
}
