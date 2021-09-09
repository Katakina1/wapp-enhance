package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.UserBilltypeDao;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
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
	public List<UserBilltypeEntity> getOrgDetail(String schemaLabel, UserBilltypeEntity userBilltypeEntity) {
		// TODO Auto-generated method stub
		 return userBilltypeDao.getOrgDetailBill(schemaLabel, userBilltypeEntity);
	}

	
}
