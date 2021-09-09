package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.UserBilltypeDao;
import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;
import com.xforceplus.wapp.modules.base.service.UserBilltypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
@Service("userBilltypeService")
public class UserBilltypeServiceImpl implements UserBilltypeService {

    @Autowired
    UserBilltypeDao userBilltypeDao;

    @Override
    public void save(String schemaLabel, UserBilltypeEntity entity) {
        userBilltypeDao.save(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, UserBilltypeEntity entity) {
        userBilltypeDao.update(schemaLabel, entity);
    }
    
    @Override
    public void updateSome(String schemaLabel, UserBilltypeEntity entity) {
        userBilltypeDao.updateSome(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long id) {
        userBilltypeDao.delete(schemaLabel, id);
    }

    @Override
    public void deleteByUserId(String schemaLabel, Long userId) {
        userBilltypeDao.deleteByUserId(schemaLabel, userId);
    }

    @Override
    public List<UserBilltypeEntity> queryList(String schemaLabel, UserBilltypeEntity entity) {
        return userBilltypeDao.queryListBill(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, UserBilltypeEntity entity) { return userBilltypeDao.queryTotal(schemaLabel, entity); }
    
    @Override
    public List<UserBilltypeEntity> getNotAddList(String schemaLabel, String[] billtypeidArr, UserBilltypeEntity userBilltypeEntity, String orgType, String[] orgChildArr) {
        return userBilltypeDao.getNotAddListBill(schemaLabel, billtypeidArr, userBilltypeEntity, orgType, orgChildArr);
    }

	@Override
    public int getNotAddListTotal(String schemaLabel, String[] billtypeidArr , UserBilltypeEntity userBilltypeEntity, String orgType, String[] orgChildArr) {
        return userBilltypeDao.getNotAddListTotalBill(schemaLabel, billtypeidArr, userBilltypeEntity, orgType, orgChildArr);
    }
    
	@Override
	public List<UserBilltypeEntity> getOrgDetail(String schemaLabel, UserBilltypeEntity userBilltypeEntity) {
		// TODO Auto-generated method stub
		 return userBilltypeDao.getOrgDetailBill(schemaLabel, userBilltypeEntity);
	}

	@Override
	public int getOrgDetailCount(String schemaLabel, UserBilltypeEntity userBilltypeEntity) {
		// TODO Auto-generated method stub
		 return userBilltypeDao.getOrgDetailCountBill(schemaLabel, userBilltypeEntity);
	}
	
	
	@Override
	public int queryListBillCount(String schemaLabel, UserBilltypeEntity entity) {
		// TODO Auto-generated method stub
		 return userBilltypeDao.queryListBillCount(schemaLabel, entity);
	}
}
