package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.ScanPathDao;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserScanPathEntity;
import com.xforceplus.wapp.modules.base.service.ScanPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jingsong.mao on 2018/08/10.
 */
@Service
public class ScanPathServiceImpl implements ScanPathService {

    @Autowired
    private ScanPathDao scanpathDao;

    @Override
    public ScanPathEntity queryObject(String schemaLabel, Long roleId) {
        return scanpathDao.queryObject(schemaLabel, roleId);
    }

    @Override
    public List<ScanPathEntity> queryList(String schemaLabel, ScanPathEntity entity) {
        return scanpathDao.queryList(schemaLabel, entity);
    }
    
    @Override
    public List<ScanPathEntity> queryScanPathBYOrg(String schemaLabel, OrganizationEntity entity) {
        return scanpathDao.queryScanPathBYOrg(schemaLabel, entity);
//        return scanpathDao.queryByProfit(schemaLabel,((UserEntity) SecurityUtils.getSubject().getPrincipal()).getProfit());
    }
    
    
    @Override
    public List<ScanPathEntity> queryScanPathBYUser(String schemaLabel, UserEntity entity) {
        return scanpathDao.queryScanPathBYUser(schemaLabel, entity);
    }
    
    @Override
    public List<UserScanPathEntity> queryListU(String schemaLabel, UserScanPathEntity entity) {
        return scanpathDao.queryListU(schemaLabel, entity);
    }

    @Override
    public List<UserScanPathEntity> queryListUNot(String schemaLabel, UserScanPathEntity entity) {
        return scanpathDao.queryListUNot(schemaLabel, entity);
    }

    
    /**
     * 根据条件查询扫描点信息
     *
     * @param entity
     * @return
     */
    @Override
    public List<ScanPathEntity> queryListS(String schemaLabel, ScanPathEntity entity) {
        return scanpathDao.queryListS(schemaLabel, entity);
    }


    @Override
    public int queryTotal(String schemaLabel, ScanPathEntity entity) {
        return scanpathDao.queryTotal(schemaLabel, entity);
    }
    
    @Override
    public int queryTotalU(String schemaLabel, UserScanPathEntity entity) {
        return scanpathDao.queryTotalU(schemaLabel, entity);
    }
    
    @Override
    public int queryTotalUNot(String schemaLabel, UserScanPathEntity entity) {
        return scanpathDao.queryTotalUNot(schemaLabel, entity);
    }

    @Override
    public int roleTotal(String schemaLabel, Long[] orgIds) {
        return scanpathDao.roleTotal(schemaLabel, orgIds);
    }

    @Override
    public void save(String schemaLabel, ScanPathEntity entity) {
        scanpathDao.save(schemaLabel, entity);
    }

    
    @Override
    public void saveUserScanPath(String schemaLabel, UserScanPathEntity entity) {
        scanpathDao.saveUserScanPath(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, ScanPathEntity entity) {
        scanpathDao.update(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long roleid) {
        scanpathDao.delete(schemaLabel, roleid);
    }

    @Override
    public int deleteBatch(String schemaLabel, Long[] roleids) {
        return scanpathDao.deleteBatch(schemaLabel, roleids);
    }
    
    @Override
    public int deleteUserScanPath(String schemaLabel, Long[] uuids) {
        return scanpathDao.deleteUserScanpath(schemaLabel, uuids);
    }
}
