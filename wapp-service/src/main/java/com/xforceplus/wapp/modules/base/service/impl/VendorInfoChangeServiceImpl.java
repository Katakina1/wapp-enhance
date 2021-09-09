package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.VendorInfoChangeDao;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.VendorInfoChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Transactional
public class VendorInfoChangeServiceImpl implements VendorInfoChangeService {

    @Autowired
    private VendorInfoChangeDao vendorInfoChangeDao;


    @Override
    public void submit(UserEntity userEntity) {
        //判断是否已经有未审核的提交，如果有修改变更信息，如果没有创建之
        boolean isExist = vendorInfoChangeDao.queryVendorInfoChangeIsExist(userEntity.getUsercode()) > 0;
        if(isExist){
            vendorInfoChangeDao.updateVendorInfoChange(userEntity);
        } else{
            vendorInfoChangeDao.submit(userEntity);
        }
    }

    @Override
    public List<UserEntity> queryVendorInfoChangeList(Map<String, Object> map) {
        return vendorInfoChangeDao.queryVendorInfoChangeList(map);
    }

    @Override
    public Integer queryVendorInfoChangeCount(Map<String, Object> map) {
        return vendorInfoChangeDao.queryVendorInfoChangeCount(map);
    }

    @Override
    public void auditAgree(Long[] ids) {
        for (Long id : ids){
            UserEntity changeInfo = vendorInfoChangeDao.queryChangeInfoById(id);
            boolean success = vendorInfoChangeDao.updateVendorInfo(changeInfo) > 0;
            if(success){
                //修改审核状态为已审核
                vendorInfoChangeDao.updateAuditStatus(changeInfo.getUsercode());
            }
        }
    }
}
