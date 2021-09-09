package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserScanPathEntity;

import java.util.List;


/**
 * 扫描点
 * <p>
 * Created by jingsong.mao on 2018/08/10.
 */
public interface ScanPathService {

    /**
     * 根据扫描点id查询扫描点信息
     *
     * @param roleId
     * @return
     */
    ScanPathEntity queryObject(String schemaLabel, Long roleId);

    /**
     * 根据条件查询扫描点信息
     *
     * @param entity
     * @return
     */
    List<ScanPathEntity> queryList(String schemaLabel, ScanPathEntity entity);
    
    
    /**
     * 根据机构信息（机构id，orgname，taxno，taxname）查询扫描点
     *
     * @param entity
     * @return
     */
    List<ScanPathEntity> queryScanPathBYOrg(String schemaLabel, OrganizationEntity entity);
    
    
    /**
     * 根据用户信息（用户id）查询扫描点，同时会把用户关联机构上的扫描点也查询出来（重复的自动过滤）
     *
     * @param entity
     * @return
     */
    List<ScanPathEntity> queryScanPathBYUser(String schemaLabel, UserEntity entity);
    
    
    /**
     * 根据条件查询扫描点信息
     *
     * @param entity
     * @return
     */
    List<UserScanPathEntity> queryListU(String schemaLabel, UserScanPathEntity entity);
    
    /**
     * 根据条件查询扫描点信息
     *
     * @param entity
     * @return
     */
    List<UserScanPathEntity> queryListUNot(String schemaLabel, UserScanPathEntity entity);
    
    
    /**
     * 根据条件查询扫描点信息
     *
     * @param entity
     * @return
     */
    List<ScanPathEntity> queryListS(String schemaLabel, ScanPathEntity entity);

    /**
     * 根据条件查询扫描点记录数
     *
     * @param entity
     * @return
     */
    int queryTotal(String schemaLabel, ScanPathEntity entity);
    
    /**
     * 根据条件查询扫描点记录数
     *
     * @param entity
     * @return
     */
    int queryTotalU(String schemaLabel, UserScanPathEntity entity);
    
    /**
     * 根据条件查询扫描点记录数
     *
     * @param entity
     * @return
     */
    int queryTotalUNot(String schemaLabel, UserScanPathEntity entity);

    /**
     * 统计组织下的扫描点数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int roleTotal(String schemaLabel, Long[] orgIds);

    /**
     * 保存扫描点信息
     *
     * @param entity
     */
    void save(String schemaLabel, ScanPathEntity entity);
    
    
    /**
     * 保存扫描点信息
     *
     * @param entity
     */
    void saveUserScanPath(String schemaLabel, UserScanPathEntity entity);

    /**
     * 更新扫描点信息
     *
     * @param entity
     */
    void update(String schemaLabel, ScanPathEntity entity);

    /**
     * 根据扫描点id删除扫描点信息
     *
     * @param roleid
     */
    void delete(String schemaLabel, Long roleid);

    /**
     * 批量删除
     */
    int deleteBatch(String schemaLabel, Long[] roleids);
    
    /**
     * 批量删除
     */
    int deleteUserScanPath(String schemaLabel, Long[] uuids);
}
