package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserScanPathEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 扫描点管理
 *
 * Created by jingsong.mao on 2018/08/10.
 */
@Mapper
public interface ScanPathDao {

    /**
     * 保存扫描点信息
     * @param entity
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") ScanPathEntity entity);
    
    
    /**
     * 保存扫描点信息
     * @param entity
     */
    int saveUserScanPath(@Param("schemaLabel") String schemaLabel, @Param("entity") UserScanPathEntity entity);

    /**
     * 更新扫描点信息
     * @param entity
     * @return
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") ScanPathEntity entity);

    /**
     * 根据扫描点id删除扫描点
     * @param roleid
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("roleid") Long roleid);

    /**
     * 批量删除扫描点
     * @param ids
     * @return
     */
    int deleteBatch(@Param("schemaLabel") String schemaLabel, @Param("ids") Long[] ids);
    
    /**
     * 批量删除扫描点
     * @param uuids
     * @return
     */
    int deleteUserScanpath(@Param("schemaLabel") String schemaLabel, @Param("uuids") Long[] uuids);

    /**
     *根据扫描点id查扫描点信息
     * @param id
     * @return
     */
    ScanPathEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    /**
     * 根据条件查扫描点信息
     * @param query
     * @return
     */
    List<ScanPathEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") ScanPathEntity query);
    
    
    /**
     * 根据机构信息（机构id，orgname，taxno，taxname）查询扫描点
     * @param query
     * @return
     */
    List<ScanPathEntity> queryScanPathBYOrg(@Param("schemaLabel") String schemaLabel, @Param("entity") OrganizationEntity query);
    /**
     * 根据当前登录用户的profit信息查询扫描点
     * @param profit
     * @return
     */
    List<ScanPathEntity> queryByProfit(@Param("schemaLabel") String schemaLabel, @Param("profit") String profit);

    /**
     * 根据用户信息（用户id）查询扫描点，同时会把用户关联机构上的扫描点也查询出来（重复的自动过滤）
     * @param query
     * @return
     */
    List<ScanPathEntity> queryScanPathBYUser(@Param("schemaLabel") String schemaLabel, @Param("entity") UserEntity query);


    /**
     * 根据条件查扫描点信息
     * @param query
     * @return
     */
    List<UserScanPathEntity> queryListU(@Param("schemaLabel") String schemaLabel, @Param("entity") UserScanPathEntity query);

    /**
     * 根据条件查扫描点信息
     * @param query
     * @return
     */
    List<UserScanPathEntity> queryListUNot(@Param("schemaLabel") String schemaLabel, @Param("entity") UserScanPathEntity query);

    /**
     * 根据条件查扫描点信息
     * @param query
     * @return
     */
    List<ScanPathEntity> queryListS(@Param("schemaLabel") String schemaLabel, @Param("entity") ScanPathEntity query);

    /**
     * 根据条件查扫描点总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") ScanPathEntity query);


    /**
     * 根据条件查扫描点总数
     * @param query
     * @return
     */
    int queryTotalU(@Param("schemaLabel") String schemaLabel, @Param("entity") UserScanPathEntity query);

    /**
     * 根据条件查扫描点总数
     * @param query
     * @return
     */
    int queryTotalUNot(@Param("schemaLabel") String schemaLabel, @Param("entity") UserScanPathEntity query);

    /**
     * 统计组织下的扫描点数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int roleTotal(@Param("schemaLabel") String schemaLabel, @Param("orgIds") Long[] orgIds);

}
