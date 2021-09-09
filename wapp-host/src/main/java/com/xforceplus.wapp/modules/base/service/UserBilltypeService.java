package com.xforceplus.wapp.modules.base.service;


import java.util.List;

import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
public interface UserBilltypeService {
	
	 /**
     * 查询用户的票据类型
     */
    List<UserBilltypeEntity> getOrgDetail(String schemaLabel, UserBilltypeEntity userBilltypeEntity);
}
